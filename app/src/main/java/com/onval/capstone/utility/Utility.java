package com.onval.capstone.utility;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.onval.capstone.room.Record;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

public class Utility {

    public static Uri createUriFromRecording(Context context, Record recording) {
        int recId = recording.getId();
        String recName = recording.getName();

        String filePath =  context.getExternalCacheDir().getAbsolutePath();
        String recFullName = "/" + recId + "_" + recName.replace(":", "_") + ".mp4";
        return Uri.parse(filePath + recFullName);
    }

    private static String getPathFromRecording(Context context, Record recording) {
        Uri uri = createUriFromRecording(context, recording);
        return uri.toString();
    }

    public static void deleteRecordingFromFilesystem(Context context, Record recording) {
        String path = Utility.getPathFromRecording(context, recording);
        File file = new File(path);
        file.delete();
    }

    public static boolean isSignedIn(Context context) {
        GoogleSignInAccount signedInAccount = GoogleSignIn.getLastSignedInAccount(context);
        return signedInAccount != null;
    }
}
