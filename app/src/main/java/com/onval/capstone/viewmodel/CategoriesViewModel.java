package com.onval.capstone.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.onval.capstone.Model;
import com.onval.capstone.dialog_fragment.SaveRecordingDialogFragment.OnSaveCallback;
import com.onval.capstone.repository.MyRepository;
import com.onval.capstone.room.Category;
import com.onval.capstone.room.Record;
import com.onval.capstone.utility.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class CategoriesViewModel extends AndroidViewModel {
    private Application application;
    private MyRepository repository;

    private Model model;

    private List<Record> recs;
//    private MutableLiveData<List<Record>> recsBeingUploaded;

    private final Observer<List<Record>> deleteRecordObs =
            recordings -> deleteRecordingsFiles(recordings);

    private void deleteRecordingsFiles(List<Record> recordings) {
        if (recordings != null && recordings.size() != 0) {
            for (Record rec : recordings)
                Utility.deleteRecordingFromFilesystem(application, rec);
        }
    }

    public CategoriesViewModel(Application application) {
        super(application);
        this.application = application;
        repository = new MyRepository(application, null);
        model = Model.newInstance();
//        if (recsBeingUploaded == null) {
//            recsBeingUploaded = new MutableLiveData<>();
//        }
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

    public void updateCategories(Category... categories) {
        repository.updateCategories(categories);
    }

    public LiveData<List<Record>> getUploadingRecordings() {
        return model.getRecordsBeingUploaded();
    }

//    private void setUploadingRecs() {
//        if (recsBeingUploaded == null || recsBeingUploaded.getValue() == null)
//            recs = new ArrayList<>();
//        else
//            recs = recsBeingUploaded.getValue();
//    }

//    public LiveData<List<Record>> getUploadingRecordings() {
//        List<Record> recs = model.getRecordsBeingUploaded();
//        recsBeingUploaded.setValue(recs);
//        return recsBeingUploaded;
////        if (recsBeingUploaded.getValue() == null) {
////            setUploadingRecs();
////            recsBeingUploaded.setValue(recs);
////        }
////        return recsBeingUploaded;
//    }

    public void uploadRecordings(int categoryId) {
        if (Utility.isSignedIn(application)) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(application);

            LiveData<List<Record>> recordings = getRecordingsFromCategory(categoryId);

            recordings.observeForever(records -> {
                for (Record rec : records) {
                    model.addRecording(rec);
                    uploadRecordingToDrive(rec, account);
                }
            });
        }
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

    public void deleteRecordings(Record... recordings) {
        deleteRecordingsFiles(Arrays.asList(recordings));
        repository.deleteRecordings(recordings);
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

    private void uploadRecordingToDrive(Record recording, GoogleSignInAccount account) {
        Uri uri = Utility.createUriFromRecording(application, recording);
        File recordingFile = new File(uri.toString());

        // this executor prevents code from being run in the main thread
        int numCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(numCores * 2, numCores *2,
                60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        DriveResourceClient resourceClient =  Drive.getDriveResourceClient(application, account);

        final Task<DriveFolder> rootFolderTask = resourceClient.getRootFolder();
        final Task<DriveContents> createContentsTask = resourceClient.createContents();
        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask(executor, task -> {
                    DriveFolder parent = rootFolderTask.getResult();
                    DriveContents contents = createContentsTask.getResult();

                    OutputStream outputStream = contents.getOutputStream();
                    FileInputStream inputStream = new FileInputStream(recordingFile);
                    int readByte;
                    while ((readByte = inputStream.read()) != -1) {
                        outputStream.write(readByte);
                    }

                    String title = uri.getLastPathSegment();

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(uri.getLastPathSegment())
                            .setMimeType("audio/mp4")
                            .build();

                    return resourceClient.createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener(
                        driveFile -> {
                            Toast.makeText(application, "Recording uploaded.", Toast.LENGTH_SHORT).show();
                            Log.d("debug", "One file uploaded");
                            model.removeRecording(recording);

                        })
                .addOnFailureListener(e -> {
                    Toast.makeText(application, "Unable to create file in google Drive.", Toast.LENGTH_SHORT).show();
                    model.removeRecording(recording);
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d("debug", "onCleared is being executed!");
    }
}