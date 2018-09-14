package com.onval.capstone.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.onval.capstone.R;
import com.onval.capstone.activities.RecordActivity;


import java.io.IOException;

public class RecordService extends IntentService {
    public static final String START_RECORDING = "start-recording";
    public static final String PAUSE_RECORDING = "pause=recording";

    private MediaRecorder recorder;
    private static final int NOTIFICATION_ID = 1;
    private Notification foregroundNotification;
    private boolean isPlaying = false;

    public RecordService() {
        super("RecordService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        initializeRecorder();
        initializeNotification();
    }

    private void initializeRecorder() {
        String fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.mp4";

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        try {
            recorder.prepare();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    private void initializeNotification() {
        Intent intent = new Intent(this, RecordActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        foregroundNotification = new Notification.Builder(this)
                .setContentTitle("Recording")
                .setContentText("Hello")
                .setContentText("The app is recording...")
                .setSmallIcon(R.drawable.exo_notification_small_icon)
                .setContentIntent(pendingIntent)
                .build();
    }

    public void startRecording() {
        if (!isPlaying) {
            recorder.start();
            startForeground(NOTIFICATION_ID, foregroundNotification);
            isPlaying = true;
        }
    }

    public void pauseRecording() {
        if (isPlaying) {
            recorder.stop();
            stopForeground(true);
            isPlaying = false;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new RecordBinder(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recorder != null)
            recorder.release();
    }
}
