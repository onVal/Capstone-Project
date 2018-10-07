package com.onval.capstone.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.onval.capstone.MyRepository;
import com.onval.capstone.dialog_fragment.SaveRecordingDialogFragment.OnSaveCallback;
import com.onval.capstone.room.Category;
import com.onval.capstone.room.Record;

import java.util.List;

public class CategoriesViewModel extends AndroidViewModel {
    private Application application;
    private LiveData<List<Category>> categories;
    private MyRepository repository;

    public CategoriesViewModel(Application application) {
        super(application);
        this.application = application;
        repository = new MyRepository(application, null);
    }

    public LiveData<List<Category>> getCategories() {
        return repository.getCategories();
    }

    public LiveData<List<Record>> getRecordingsFromCategory(int categoryId) {
        return repository.getRecordingsFromCategory(categoryId);
    }

    public void insertCategory(Category category) {
        repository.insertCategories(category);
    }

    public void insertRecording(Record recording) {
        repository.insertRecording(recording);
    }

    public void deleteCategories(Category... categories) {
        repository.deleteCategories(categories);
    }

    public LiveData<Integer> getNumOfCategories() {
        return repository.getNumOfCategories();
    }

    public LiveData<Integer> getRecNumberInCategory(int categoryId) {
        return repository.getRecNumberInCategory(categoryId);
    }

    public void setOnSaveCallback(OnSaveCallback callback) {
        repository = new MyRepository(application, callback);
    }
}
