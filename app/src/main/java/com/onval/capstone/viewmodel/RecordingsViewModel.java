package com.onval.capstone.viewmodel;

import android.app.Application;

import com.onval.capstone.dialog_fragment.SaveRecordingDialogFragment.OnSaveCallback;
import com.onval.capstone.repository.DataModel;
import com.onval.capstone.room.Record;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class RecordingsViewModel extends AndroidViewModel {
    private DataModel repository;

    public RecordingsViewModel(@NonNull Application application) {
        super(application);
        repository = DataModel.getInstance(application);
    }

    public void insertRecording(Record recording, OnSaveCallback callback) {
        repository.insertRecording(recording, callback);
    }

    public LiveData<List<Record>> getRecordingsFromCategory(int categoryId) {
        return repository.getRecordingsFromCategory(categoryId);
    }

    public void deleteRecordings(Record... recordings) {
        repository.deleteRecordings(recordings);
    }
}
