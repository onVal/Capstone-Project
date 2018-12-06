package com.onval.capstone.room;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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

    @Query("SELECT color FROM category WHERE id = :categoryId")
    LiveData<String> getCategoryColor(int categoryId);

    @Update
    void updateCategories(Category... categories);

    @Update
    void updateRecordings(Record... recordings);

    @Insert(onConflict =  OnConflictStrategy.IGNORE)
    long insertCategory(Category category);

    @Insert
    void insertCategories(Category... categories);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertRecording(Record recording);

    @Insert
    void insertRecordings(Record... recordings);

    @Delete
    void deleteCategories(Category... categories);

    @Delete
    void deleteRecordings(Record... recordings);
}
