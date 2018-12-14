package com.onval.capstone.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.onval.capstone.repository.DataModel;
import com.onval.capstone.room.Record;
import com.onval.capstone.utility.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

public class UploadService extends IntentService {
    public static boolean isRunning;

    private DataModel model;

    private static List<Record> uploadingRecList;
    private static MutableLiveData<List<Record>> uploadingRecs;

    private Toast toast;

    public UploadService() {
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        isRunning = true;
        initializeUploadingRecs();
        model = DataModel.getInstance(getApplication());
    }

    public void uploadRecordingToDrive(Record recording, GoogleSignInAccount account) {
        setUploadingRecs(recording, true);

        Uri uri = Utility.createUriFromRecording(this, recording);
        File recordingFile = new File(uri.toString());

        // this executor prevents code from being run in the main thread
        int numCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(numCores * 2, numCores *2,
                60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        DriveResourceClient resourceClient =  Drive.getDriveResourceClient(this, account);

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
                            showToast(recording.getName() + " uploaded.");
                            setUploadingRecs(recording, false);
                            recording.setCloudStatus(Record.CLOUD_UPLOADED);
                            model.updateRecordings(recording);
                        })
                .addOnFailureListener(e -> {
                    showToast("Unable to create recording " + recording.getName() + " in google Drive.");
                    setUploadingRecs(recording, false);
                });
    }

    private void showToast(CharSequence text) {
        if (toast == null) {
            toast = new Toast(this);
        }
        toast.cancel();
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void initializeUploadingRecs() {
        uploadingRecs = new MutableLiveData<>();
        uploadingRecList = new ArrayList<>();
        uploadingRecs.postValue(uploadingRecList);
    }

    private void setUploadingRecs(Record rec, boolean isUploading) {
        if (isUploading)
            uploadingRecList.add(rec);
        else
            uploadingRecList.remove(rec);

        uploadingRecs.setValue(uploadingRecList);
    }

    public MutableLiveData<List<Record>> getUploadingRecs() {
        return uploadingRecs;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new UploadBinder();
    }

    public class UploadBinder extends Binder {
        public UploadService getService() {
            return UploadService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        uploadingRecs.setValue(new ArrayList<>());
    }
}
