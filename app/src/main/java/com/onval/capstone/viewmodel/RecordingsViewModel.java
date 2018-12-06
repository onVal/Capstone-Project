package com.onval.capstone.viewmodel;

import android.app.Application;

import com.onval.capstone.dialog_fragment.SaveRecordingDialogFragment.OnSaveCallback;
import com.onval.capstone.repository.DataModel;
import com.onval.capstone.room.Record;

import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class RecordingsViewModel extends AndroidViewModel {
    private DataModel model;
    private MediatorLiveData<HashSet<Integer>> recordingsIds;

    public RecordingsViewModel(@NonNull Application application) {
        super(application);
        model = DataModel.getInstance(application);

        recordingsIds = new MediatorLiveData<>();
        recordingsIds.addSource(model.getUploadingRecordings(), recordings -> {
            HashSet<Integer> recIds = new HashSet<>();

            for (Record r : recordings)
                recIds.add(r.getId());

            recordingsIds.setValue(recIds);
        });
    }

    public void updateRecordings(Record... recordings) {
        model.updateRecordings(recordings);
    }

    public void insertRecording(Record recording, OnSaveCallback callback) {
        model.insertRecording(recording, callback);
    }

    public LiveData<List<Record>> getRecordingsFromCategory(int categoryId) {
        return model.getRecordingsFromCategory(categoryId);
    }

    public LiveData<HashSet<Integer>> getUploadingRecordingsIds() {
        return recordingsIds;
    }

    public void deleteRecordings(Record... recordings) {
        model.deleteRecordings(recordings);
    }
}
