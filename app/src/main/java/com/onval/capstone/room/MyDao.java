package com.onval.capstone.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;

import java.util.List;

@Dao
public interface MyDao {
    @Query("SELECT * FROM category")
    public LiveData<List<Category>> loadCategories();

    @Query("SELECT * FROM record AS R WHERE R.category_id = :categoryId")
    public LiveData<List<Record>> loadRecordingsFromCategory(int categoryId);

    @Query ("SELECT COUNT(*) FROM record AS R WHERE R.category_id = :categoryId")
    public LiveData<Integer> numberOfRecordingsInCategory(int categoryId);

    @Insert
    public void insertCategories(Category... category);

    @Insert
    public void insertRecordings(Record... record);

    @Delete
    public void deleteCategories(Category... category);

    @Delete
    public void deleteRecords(Record... record);
}
