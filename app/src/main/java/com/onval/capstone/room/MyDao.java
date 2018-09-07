package com.onval.capstone.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;

import java.util.List;

@Dao
public interface MyDao {
    @Query("SELECT * FROM category")
    LiveData<List<Category>> loadCategories();

    @Query("SELECT COUNT(*) FROM category")
    LiveData<Integer> numOfCategories();

    @Query("SELECT * FROM record AS R WHERE R.category_id = :categoryId")
    LiveData<List<Record>> loadRecordingsFromCategory(int categoryId);

    @Query ("SELECT COUNT(*) FROM record AS R WHERE R.category_id = :categoryId")
    LiveData<Integer> numberOfRecordingsInCategory(int categoryId);

    @Insert
    void insertCategories(Category... category);

    @Insert
    void insertRecordings(Record... record);

    @Delete
    void deleteCategories(Category... category);

    @Delete
    void deleteRecords(Record... record);
}
