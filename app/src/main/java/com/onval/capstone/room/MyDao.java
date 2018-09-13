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

    @Insert(onConflict =  OnConflictStrategy.IGNORE)
    long insertCategory(Category category);

    @Insert
    void insertCategories(Category... categories);

    @Insert
    void insertRecordings(Record... recordings);

    @Delete
    void deleteCategories(List<Category> categories);

    @Delete
    void deleteRecords(List<Record> recordings);
}
