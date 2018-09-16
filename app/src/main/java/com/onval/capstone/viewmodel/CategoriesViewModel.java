package com.onval.capstone.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.database.sqlite.SQLiteConstraintException;

import com.onval.capstone.MyRepository;
import com.onval.capstone.R;
import com.onval.capstone.room.Category;

import java.util.List;

public class CategoriesViewModel extends AndroidViewModel {
    private Application application;
    private LiveData<List<Category>> categories;
    private MyRepository repository;

    public CategoriesViewModel(Application application) {
        super(application);
        this.application = application;
        repository = new MyRepository(application);
    }

    public LiveData<List<Category>> getCategories() {
        return repository.getCategories();
    }

    public void insertCategory(Category category) {
        repository.insertCategories(category);
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
}
