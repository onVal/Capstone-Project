package com.onval.capstone.utility;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;

import com.onval.capstone.room.Record;

import java.io.File;

public class Utility {

    public static Uri createUriFromRecording(Context context, Record recording) {
        int recId = recording.getId();
        String recName = recording.getName();

        String filePath =  context.getExternalCacheDir().getAbsolutePath();
        String recFullName = "/" + recId + "_" + recName.replace(":", "_") + ".mp4";
        return Uri.parse(filePath + recFullName);
    }

    public static String getPathFromRecording(Context context, Record recording) {
        Uri uri = createUriFromRecording(context, recording);
        return uri.toString();
    }

    public static boolean deleteRecordingFromFilesystem(Context context, Record recording) {
        String path = Utility.getPathFromRecording(context, recording);
        File file = new File(path);
        return file.delete();
    }
}
