package com.onval.capstone.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.Toast;

import com.onval.capstone.R;
import com.onval.capstone.room.AppDatabase;
import com.onval.capstone.room.Category;
import com.onval.capstone.room.MyDao;
import com.onval.capstone.room.Record;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import static com.onval.capstone.dialog_fragment.SaveRecordingDialogFragment.OnSaveCallback;

public class MyRepository {
    private MyDao dao;
    private Application app;
    private OnSaveCallback onSaveCallback;

    public MyRepository(Application app, @Nullable OnSaveCallback onSaveCallback) {
        AppDatabase db = AppDatabase.getDatabase(app);
        dao = db.getDao();
        this.app = app;
        this.onSaveCallback = onSaveCallback;
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

    public void insertRecording(final Record recs) {
        new RecordingsInsertAsyncTask(onSaveCallback).execute(recs);
    }

    public void updateCategories(Category... categories) {
        runInBackground(()->dao.updateCategories(categories));

    }

    public void deleteRecordings(final Record... recs) {
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
