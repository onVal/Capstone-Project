package com.onval.capstone.utility;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.onval.capstone.room.Record;

import java.io.File;

public class Utility {

    public static Uri createUriFromRecording(Context context, Record recording) {
        long recId = recording.getId();
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

    public static Query matchRecordingDriveQuery(long recId, String recName) {
        return new Query.Builder()
                .addFilter(Filters.and(
                        Filters.contains(SearchableField.TITLE, String.valueOf(recId)),
                        Filters.contains(SearchableField.TITLE, recName),
                        Filters.eq(SearchableField.TRASHED, false)))
                .build();
    }
}
