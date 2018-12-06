package com.onval.capstone.repository;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.widget.Toast;

import com.onval.capstone.R;
import com.onval.capstone.room.AppDatabase;
import com.onval.capstone.room.Category;
import com.onval.capstone.room.MyDao;
import com.onval.capstone.room.Record;
import com.onval.capstone.service.UploadService;
import com.onval.capstone.utility.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import static com.onval.capstone.dialog_fragment.SaveRecordingDialogFragment.OnSaveCallback;

public class DataModel {
    private MyDao dao;
    private Application app;
    private UploadService serviceInstance;
    private MutableLiveData<UploadService> serviceLiveData;
    private MutableLiveData<List<Record>> uploadingRecordings;

    private static volatile DataModel sInstance;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            UploadService.UploadBinder binder = (UploadService.UploadBinder) iBinder;
            serviceInstance = binder.getService();
            serviceLiveData.setValue(serviceInstance);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceInstance = null;
        }
    };

    private DataModel(Application app) {
        AppDatabase db = AppDatabase.getDatabase(app);
        dao = db.getDao();
        this.app = app;

        serviceLiveData = new MutableLiveData<>();

        uploadingRecordings = new MutableLiveData<>();
        uploadingRecordings.setValue(new ArrayList<>());
    }

    public static DataModel getInstance(Application app) {
        if (sInstance == null) {
            synchronized (DataModel.class) {
                if (sInstance == null) {
                    sInstance = new DataModel(app);
                }
            }
        }
        return sInstance;
    }

    public LiveData<List<Record>> getUploadingRecordings() {
        if (UploadService.isRunning) {
            return serviceInstance.getUploadingRecs();
        }

        return new MediatorLiveData<>();
    }

    public void startUploadService() {
        Intent intent = new Intent(app, UploadService.class);
        app.startService(intent);
        app.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public LiveData<UploadService> getServiceLiveData() {
        return serviceLiveData;
    }

    public LiveData<List<Category>> getCategories() {
        return dao.loadCategories();
    }

    public LiveData<Integer> getNumOfCategories() {
        return dao.numOfCategories();
    }

    public LiveData<List<Record>> getRecordingsFromCategory(int categoryId) {
        return dao.loadRecordingsFromCategory(categoryId);
    }

    public LiveData<Integer> getRecNumberInCategory(int categoryId) {
        return dao.numberOfRecordingsInCategory(categoryId);
    }

    public LiveData<String> getCategoryColor(int categoryId) {
        return dao.getCategoryColor(categoryId);
    }

    public void insertCategories(final Category category) {
        new CategoriesInsertAsyncTask().execute(category);
    }

    public void deleteCategories(final Category... categories) {
        runInBackground(() -> dao.deleteCategories(categories));
    }

    public void insertRecording(final Record recs, OnSaveCallback onSaveCallback) {
        new RecordingsInsertAsyncTask(onSaveCallback).execute(recs);
    }

    public void updateCategories(Category... categories) {
        runInBackground(()->dao.updateCategories(categories));
    }

    public void deleteRecordings(Record... recordings) {
        deleteRecordingsFiles(Arrays.asList(recordings));
        deleteRecordingsEntries(recordings);
    }

    private void deleteRecordingsFiles(List<Record> recordings) {
        if (recordings != null && recordings.size() != 0) {
            for (Record rec : recordings)
                Utility.deleteRecordingFromFilesystem(app, rec);
        }
    }

    private void deleteRecordingsEntries(final Record... recs) {
        runInBackground(()->dao.deleteRecordings(recs));
    }

    private void runInBackground(Runnable runnable) {
        HandlerThread handlerThread = new HandlerThread("Background-thread");
        handlerThread.start();
        new Handler(handlerThread.getLooper()).post(runnable);
    }

    private class CategoriesInsertAsyncTask extends AsyncTask<Category, Void, Long> {
        @Override
        protected Long doInBackground(Category... cats) {
            return dao.insertCategory(cats[0]);
        }

        @Override
        protected void onPostExecute(Long rowId) {
            super.onPostExecute(rowId);

            if (rowId < 0)
                Toast.makeText(app.getApplicationContext(),
                        R.string.cant_insert,
                        Toast.LENGTH_SHORT).show();
        }
    }

    private class RecordingsInsertAsyncTask extends AsyncTask<Record, Void, Long> {
        private OnSaveCallback callback;
        private String recName;

        RecordingsInsertAsyncTask(OnSaveCallback callback) {
            this.callback = callback;
        }
        @Override
        protected Long doInBackground(Record... recs) {
            recName = recs[0].getName();
            return dao.insertRecording(recs[0]);
        }

        @Override
        protected void onPostExecute(Long rowId) {
            super.onPostExecute(rowId);

            if (rowId < 0)
                Toast.makeText(app.getApplicationContext(),
                        R.string.cant_insert,
                        Toast.LENGTH_SHORT).show();
            else
                callback.onSaveRecording(rowId, recName);
        }
    }
}
