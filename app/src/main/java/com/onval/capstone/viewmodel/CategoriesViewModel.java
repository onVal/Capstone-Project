package com.onval.capstone.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.onval.capstone.repository.MyRepository;
import com.onval.capstone.dialog_fragment.SaveRecordingDialogFragment.OnSaveCallback;
import com.onval.capstone.room.Category;
import com.onval.capstone.room.Record;
import com.onval.capstone.utility.Utility;

import java.io.File;
import java.util.List;

public class CategoriesViewModel extends AndroidViewModel {
    private Application application;
    private LiveData<List<Category>> categories;
    private MyRepository repository;

    private final Observer<List<Record>> deleteRecordObs = new Observer<List<Record>>() {
        @Override
        public void onChanged(@Nullable List<Record> recordings) {
            if (recordings != null && recordings.size() != 0) {
                for (Record rec : recordings)
                    Utility.deleteRecordingFromFilesystem(application, rec);
            }
        }
    };

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
        deleteRecFilesOfCategories(categories);
        repository.deleteCategories(categories);
    }

    public LiveData<Integer> getNumOfCategories() {
        return repository.getNumOfCategories();
    }

    public LiveData<Integer> getRecNumberInCategory(int categoryId) {
        return repository.getRecNumberInCategory(categoryId);
    }

    public LiveData<String> getCategoryColor(int categoryId) {
        return repository.getCategoryColor(categoryId);
    }

    public void setOnSaveCallback(OnSaveCallback callback) {
        repository = new MyRepository(application, callback);
    }

    private void deleteRecFilesOfCategories(Category... categories) {
        LiveData<List<Record>> recLiveData;

        for(Category category : categories) {
            recLiveData = getRecordingsFromCategory(category.getId());
            recLiveData.observeForever(deleteRecordObs);
        }
    }
}
