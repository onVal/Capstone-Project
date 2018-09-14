package com.onval.capstone;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.onval.capstone.room.AppDatabase;
import com.onval.capstone.room.Category;
import com.onval.capstone.room.MyDao;
import com.onval.capstone.room.Record;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

public class MyRepository {
    private MyDao dao;
    private Application app;

    public MyRepository(Application app) {
        AppDatabase db = AppDatabase.getDatabase(app);
        dao = db.getDao();
        this.app = app;
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

    public void insertCategories(final Category category) {
        new CategoriesInsertAsyncTask().execute(category);
    }

//    public void insertRecordings(final Record... recs) {
//        new RecordingsAsyncTask().execute(recs);
//    }

    public void deleteCategories(final Category... categories) {
        new CategoriesDeleteAsyncTask().execute(categories);
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
                        R.string.cant_insert_cat,
                        Toast.LENGTH_SHORT).show();
        }
    }

    private class CategoriesDeleteAsyncTask extends AsyncTask<Category, Void, Void> {
        @Override
        protected Void doInBackground(Category... categories) {
            dao.deleteCategories(categories);
            return null;
        }
    }

//    private static class RecordingsAsyncTask extends AsyncTask<Record, Void, Void> {
//        @Override
//        protected Void doInBackground(Record... recs) {
//                dao.get().insertRecordings(recs);
//            return null;
//        }
//    }
}
