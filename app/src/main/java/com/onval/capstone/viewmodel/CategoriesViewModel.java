package com.onval.capstone.viewmodel;

import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.onval.capstone.repository.DataModel;
import com.onval.capstone.room.Category;
import com.onval.capstone.room.Record;
import com.onval.capstone.utility.Utility;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

public class CategoriesViewModel extends AndroidViewModel {
    private Application application;
    private DataModel model;

    private MediatorLiveData<Set<Integer>> uploadingCategoryIds;

    private final Observer<List<Record>> deleteRecordObs =
            this::deleteRecordingsFiles;

    public CategoriesViewModel(Application application) {
        super(application);
        this.application = application;
        model = DataModel.getInstance(application);

        uploadingCategoryIds = new MediatorLiveData<>();
        uploadingCategoryIds.addSource(model.getUploadingRecordings(), (recordings) -> {
            Set<Integer> catIdsHashSet = new HashSet<>();

            for (Record rec : recordings) {
                catIdsHashSet.add(rec.getCategoryId());
            }

            uploadingCategoryIds.setValue(catIdsHashSet);
        });
    }

    public LiveData<List<Category>> getCategories() {
        return model.getCategories();
    }

    public void insertCategory(Category category) {
        model.insertCategories(category);
    }

    public void updateCategories(Category... categories) {
        model.updateCategories(categories);
    }

    public LiveData<Set<Integer>> getUploadingCategoryIds() {
        return uploadingCategoryIds;
    }

    public void uploadRecordings(int categoryId) {
        if (Utility.isSignedIn(application)) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(application);
            LiveData<List<Record>> recordings = model.getRecordingsFromCategory(categoryId);

            model.startUploadService();

            recordings.observeForever(new Observer<List<Record>>() {
                @Override
                public void onChanged(List<Record> records) {
                    for (Record rec : records) {
                        model.getServiceLiveData().observeForever(
                                (uploadService -> uploadService.uploadRecordingToDrive(rec, account))
                        );
                    }
                    recordings.removeObserver(this);
                }
            });
        }
    }

    public void deleteCategories(Category... categories) {
        deleteRecFilesOfCategories(categories);
        model.deleteCategories(categories);
    }

    private void deleteRecordingsFiles(List<Record> recordings) {
        if (recordings != null && recordings.size() != 0) {
            for (Record rec : recordings)
                Utility.deleteRecordingFromFilesystem(application, rec);
        }
    }

    private void deleteRecFilesOfCategories(Category... categories) {
        LiveData<List<Record>> recLiveData;

        for(Category category : categories) {
            recLiveData = model.getRecordingsFromCategory(category.getId());
            recLiveData.observeForever(deleteRecordObs);
        }
    }

    public LiveData<Integer> getNumOfCategories() {
        return model.getNumOfCategories();
    }

    public LiveData<Integer> getRecNumberInCategory(int categoryId) {
        return model.getRecNumberInCategory(categoryId);
    }

    public LiveData<String> getCategoryColor(int categoryId) {
        return model.getCategoryColor(categoryId);
    }
}