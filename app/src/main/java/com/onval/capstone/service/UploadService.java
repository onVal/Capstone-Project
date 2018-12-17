package com.onval.capstone.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.onval.capstone.repository.DataModel;
import com.onval.capstone.room.Record;
import com.onval.capstone.utility.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.onval.capstone.activities.ManageAccountActivity.UPLOAD_FOLDER_NAME;

public class UploadService extends IntentService {
    public static final String VALUE_ID = "value_id";
    public static final String ADD_UPREC = "add-uprec-action";
    public static final String RMV_UPREC = "rmv-uprec-action";
    public static final String ADD_UPCAT = "add-upcat-action";
    public static final String RMV_UPCAT = "rmv-upcat-action";

    public static boolean isRunning;

    private DataModel model;

    private Toast toast;

    public UploadService() {
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        isRunning = true;
        model = DataModel.getInstance(getApplication());
        createFolderIfNotExists();
    }

    private void createFolderIfNotExists() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        DriveResourceClient resourceClient =  Drive.getDriveResourceClient(this, account);

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, UPLOAD_FOLDER_NAME))
                .build();

        Task<MetadataBuffer> queryTask = resourceClient.query(query);
        queryTask.addOnSuccessListener(metadata -> {
            if (metadata.getCount() == 0) {
                createFolder(UPLOAD_FOLDER_NAME);
            }
        });
    }

    public void uploadRecordingToDrive(Record recording, GoogleSignInAccount account) {
        DriveResourceClient resourceClient =  Drive.getDriveResourceClient(this, account);

        setUploadingValues(recording.getId(), recording.getCategoryId());

        Uri uri = Utility.createUriFromRecording(this, recording);
        File recordingFile = new File(uri.toString());

        // this executor prevents code from being run in the main thread
        int numCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(numCores * 2, numCores *2,
                60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, UPLOAD_FOLDER_NAME))
                .build();

        Task<MetadataBuffer> queryTask = resourceClient.query(query);

        final Task<DriveFolder> rootFolderTask = resourceClient.getRootFolder();
        final Task<DriveContents> createContentsTask = resourceClient.createContents();

        Tasks.whenAll(rootFolderTask, createContentsTask, queryTask)
                .continueWithTask(executor, task -> {
                    DriveFolder parent = rootFolderTask.getResult();
                    DriveContents contents = createContentsTask.getResult();
                    MetadataBuffer metadata = queryTask.getResult();

                    DriveId recFolderId = parent.getDriveId();
                    if (metadata.getCount() != 0) {
                        recFolderId = metadata.get(0).getDriveId();
                    }

                    DriveFolder recFolder = recFolderId.asDriveFolder();

                    OutputStream outputStream = contents.getOutputStream();
                    FileInputStream inputStream = new FileInputStream(recordingFile);

                    byte[] buffer = new byte[512];

                    while (inputStream.read(buffer) != -1) {
                        outputStream.write(buffer);
                    }

                    String title = uri.getLastPathSegment();

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(uri.getLastPathSegment())
                            .setMimeType("audio/mp4")
                            .build();

                    return resourceClient.createFile(recFolder, changeSet, contents);
                })
                .addOnSuccessListener(
                        driveFile -> {
                            showToast(recording.getName() + " uploaded.");
                            unsetUploadingValues(recording.getId(), recording.getCategoryId());
                            recording.setCloudStatus(Record.CLOUD_UPLOADED);
                            model.updateRecordings(recording);
                        })
                .addOnFailureListener(e -> {
                    showToast("Unable to create recording " + recording.getName() + " in google Drive.");
                    unsetUploadingValues(recording.getId(), recording.getCategoryId());
                });
    }

    private void createFolder(String name) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        DriveResourceClient resourceClient =  Drive.getDriveResourceClient(this, account);
        resourceClient.getRootFolder()
                .continueWithTask(task -> {
                    DriveFolder parentFolder = task.getResult();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(name)
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .build();
                    return resourceClient.createFolder(parentFolder, changeSet);
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

    private void setUploadingValues(long recId, int catId) {
        setUploadingRecs(recId, true);
        setUploadingCats(catId, true);
    }

    private void unsetUploadingValues(long recId, int catId) {
        setUploadingRecs(recId, false);
        setUploadingCats(catId, false);
    }

    private void setUploadingRecs(long recId, boolean isUploading) {
        Intent intent = new Intent();
        intent.setAction((isUploading) ? ADD_UPREC : RMV_UPREC);
        intent.putExtra(VALUE_ID, recId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void setUploadingCats(int catId, boolean isUploading) {
        Intent intent = new Intent();
        intent.setAction((isUploading) ? ADD_UPCAT : RMV_UPCAT);
        intent.putExtra(VALUE_ID, catId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
    }
}
