package com.onval.capstone.viewmodel;

import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.onval.capstone.dialog_fragment.SaveRecordingDialogFragment.OnSaveCallback;
import com.onval.capstone.repository.DataModel;
import com.onval.capstone.room.Record;
import com.onval.capstone.utility.Utility;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class RecordingsViewModel extends AndroidViewModel {
    private DataModel model;
    private Application application;

    public RecordingsViewModel(@NonNull Application application) {
        super(application);
        model = DataModel.getInstance(application);
        this.application = application;
    }

    public void updateRecordings(Record... recordings) {
        model.updateRecordings(recordings);
    }

    public void insertRecording(Record recording, OnSaveCallback callback) {
        model.insertRecording(recording, callback);
    }

    public void uploadRecording(Record recording) {
        if (Utility.isSignedIn(application)) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(application);

            model.startUploadService();
            model.getServiceLiveData().observeForever(
                     (uploadService -> uploadService.uploadRecordingToDrive(recording, account))
            );
        }
    }

    public LiveData<List<Record>> getRecordingsFromCategory(int categoryId) {
        return model.getRecordingsFromCategory(categoryId);
    }

    public LiveData<ArrayList<Long>> getUploadingRecordingsIds() {
        return model.getRecordingsIds();
    }

    public void deleteRecordings(Record... recordings) {
        model.deleteRecordings(recordings);
    }
}
