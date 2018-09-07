package com.onval.capstone;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.os.Handler;

import com.onval.capstone.room.AppDatabase;
import com.onval.capstone.room.Category;
import com.onval.capstone.room.MyDao;
import com.onval.capstone.room.Record;

import java.lang.ref.WeakReference;
import java.util.List;

public class MyRepository {
    private MyDao dao;

    public MyRepository(Application app) {
        AppDatabase db = AppDatabase.getDatabase(app);
        dao = db.getDao();
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

    public void insertCategories(final Category... category) {
        new CategoriesAsyncTask(dao).execute(category);
    }

    public void insertRecordings(final Record... recs) {
        new RecordingsAsyncTask(dao).execute(recs);
    }

    private static class CategoriesAsyncTask extends AsyncTask<Category, Void, Void> {
        WeakReference<MyDao> weakDao;

        CategoriesAsyncTask(MyDao dao) {
            weakDao = new WeakReference<>(dao);
        }

        @Override
        protected Void doInBackground(Category... cats) {
            if (weakDao != null)
                weakDao.get().insertCategories(cats);
            return null;
        }
    }

    private static class RecordingsAsyncTask extends AsyncTask<Record, Void, Void> {
        WeakReference<MyDao> weakDao;

        RecordingsAsyncTask(MyDao dao) {
            weakDao = new WeakReference<>(dao);
        }
        @Override
        protected Void doInBackground(Record... recs) {
            if (weakDao != null)
                weakDao.get().insertRecordings(recs);
            return null;
        }
    }
}
