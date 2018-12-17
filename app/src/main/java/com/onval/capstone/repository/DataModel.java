package com.onval.capstone.repository;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.onval.capstone.dialog_fragment.SaveRecordingDialogFragment.OnSaveCallback;
import static com.onval.capstone.service.UploadService.*;

public class DataModel {
    private MyDao dao;
    private Application app;
    private UploadService serviceInstance;
    private MutableLiveData<UploadService> serviceLiveData;

    private MutableLiveData<ArrayList<Long>> uploadingRecordingsId;
    private MutableLiveData<ArrayList<Integer>> uploadingCategoriesId;

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

        uploadingRecordingsId = new MutableLiveData<>();
        uploadingRecordingsId.setValue(new ArrayList<>());
        uploadingCategoriesId = new MutableLiveData<>();
        uploadingCategoriesId.setValue(new ArrayList<>());

        IntentFilter filter = new IntentFilter();
        filter.addAction(ADD_UPREC);
        filter.addAction(RMV_UPREC);
        filter.addAction(ADD_UPCAT);
        filter.addAction(RMV_UPCAT);

        LocalBroadcastManager.getInstance(app)
                .registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        Bundle extras = intent.getExtras();

                        assert action != null;
                        assert extras != null;

                        switch (action) {
                            case ADD_UPREC:
                                updateRecValue(extras.getLong(VALUE_ID), true);
                                break;
                            case RMV_UPREC:
                                updateRecValue(extras.getLong(VALUE_ID), false);
                                break;
                            case ADD_UPCAT:
                                updateCatValue(extras.getInt(VALUE_ID), true);
                                break;
                            case RMV_UPCAT:
                                updateCatValue(extras.getInt(VALUE_ID), false);
                                break;
                        }
                    }
                }, filter);
    }

    private void updateRecValue(long value, boolean add) {
        ArrayList<Long> list = uploadingRecordingsId.getValue();
        if (add)
            list.add(value);
        else
            list.remove(value);
        uploadingRecordingsId.setValue(list);
    }

    private void updateCatValue(Integer value, boolean add) {
        ArrayList<Integer> list = uploadingCategoriesId.getValue();
        if (add)
            list.add(value);
        else
            list.remove(value);
        uploadingCategoriesId.setValue(list);
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


    public LiveData<ArrayList<Integer>> getCategoriesIds() {
        return uploadingCategoriesId;
    }

    public LiveData<ArrayList<Long>> getRecordingsIds() {
        return uploadingRecordingsId;
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

    public LiveData<Boolean> categoryAutoupload(int categoryId) {
        return dao.categoryAutoupload(categoryId);
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

    public void updateRecordings(Record... recordings) {
        runInBackground(()-> dao.updateRecordings(recordings));
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
        private Record recording;

        RecordingsInsertAsyncTask(OnSaveCallback callback) {
            this.callback = callback;
        }
        @Override
        protected Long doInBackground(Record... recs) {
            recording = recs[0];
            return dao.insertRecording(recording);
        }

        @Override
        protected void onPostExecute(Long rowId) {
            super.onPostExecute(rowId);

            if (rowId < 0)
                Toast.makeText(app.getApplicationContext(),
                        R.string.cant_insert,
                        Toast.LENGTH_SHORT).show();
            else {
                recording.setId(rowId);
                callback.onSaveRecording(recording);
            }
        }
    }
}
