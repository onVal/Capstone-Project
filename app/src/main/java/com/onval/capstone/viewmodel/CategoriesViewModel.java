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
    private DataModel dataModel;

    private MediatorLiveData<Set<Integer>> uploadingCategoryIds;

    private final Observer<List<Record>> deleteRecordObs =
            this::deleteRecordingsFiles;

    public CategoriesViewModel(Application application) {
        super(application);
        this.application = application;
        dataModel = DataModel.getInstance(application);

        uploadingCategoryIds = new MediatorLiveData<>();
        uploadingCategoryIds.addSource(dataModel.getUploadingRecordings(), (recordings) -> {
            Set<Integer> catIdsHashSet = new HashSet<>();

            for (Record rec : recordings) {
                catIdsHashSet.add(rec.getCategoryId());
            }

            uploadingCategoryIds.setValue(catIdsHashSet);
        });
    }

    public LiveData<List<Category>> getCategories() {
        return dataModel.getCategories();
    }

    public void insertCategory(Category category) {
        dataModel.insertCategories(category);
    }

    public void updateCategories(Category... categories) {
        dataModel.updateCategories(categories);
    }

    public LiveData<Set<Integer>> getUploadingCategoryIds() {
        return uploadingCategoryIds;
    }

    public void uploadRecordings(int categoryId) {
        if (Utility.isSignedIn(application)) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(application);
            LiveData<List<Record>> recordings = dataModel.getRecordingsFromCategory(categoryId);

            dataModel.startUploadService();

            recordings.observeForever(records -> {
                for (Record rec : records) {
                    dataModel.getServiceLiveData().observeForever(
                            (uploadService -> uploadService.uploadRecordingToDrive(rec, account))
                    );
                }
            });
        }
    }

    public void deleteCategories(Category... categories) {
        deleteRecFilesOfCategories(categories);
        dataModel.deleteCategories(categories);
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
            recLiveData = dataModel.getRecordingsFromCategory(category.getId());
            recLiveData.observeForever(deleteRecordObs);
        }
    }

    public LiveData<Integer> getNumOfCategories() {
        return dataModel.getNumOfCategories();
    }

    public LiveData<Integer> getRecNumberInCategory(int categoryId) {
        return dataModel.getRecNumberInCategory(categoryId);
    }

    public LiveData<String> getCategoryColor(int categoryId) {
        return dataModel.getCategoryColor(categoryId);
    }
}