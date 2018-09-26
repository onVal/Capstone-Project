package com.onval.capstone.service;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.onval.capstone.R;
import com.onval.capstone.activities.RecordActivity;

import java.io.IOException;


public class RecordService extends IntentService {
    public static final String DEFAULT_REC_NAME = "/audiorecord.mp4";

    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "channel-1";
    private Notification foregroundNotification;

    private MediaRecorder recorder;

    private boolean isPlaying = false;
    private ResultReceiver timerReceiver;
    private Handler handler;
    private Bundle bundle;

    private String fileName;

    private long currentTimeMillis, startTime, timeElapsed;

    public RecordService() {
        super("RecordService");
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            currentTimeMillis = SystemClock.uptimeMillis() - startTime + timeElapsed;
            bundle.putLong("current-time", currentTimeMillis);
            timerReceiver.send(0, bundle);
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        initializeRecorder();
        initializeNotification();
        timerReceiver = intent.getExtras().getParcelable("timer");
        handler = new Handler(Looper.getMainLooper());
        bundle = new Bundle();

        startRecording();
    }

    public String getFileName() {
        return fileName;
    }

    private void initializeRecorder() {
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += DEFAULT_REC_NAME;

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
        createNotificationChannel();

        Intent intent = new Intent(this, RecordActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        foregroundNotification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Record")
                .setContentText("You are recording a voice memo")
                .setSmallIcon(R.drawable.ic_mic_black_24dp)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] {0L})
                .build();
        }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Record", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(false);
            channel.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void startRecording() {
        recorder.start();
        startForeground(NOTIFICATION_ID, foregroundNotification);
        startChrono();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void resumeRecording() {
        recorder.resume();
        startChrono();
    }

    public void stopRecording() {
        recorder.stop();
        stopForeground(true);
        isPlaying = false;
        handler.removeCallbacks(timerRunnable);

    }

    @TargetApi(Build.VERSION_CODES.N)
    public void pauseRecording() {
        recorder.pause();
        isPlaying = false;
        handler.removeCallbacks(timerRunnable);
        timeElapsed = currentTimeMillis;
    }

    private void startChrono() {
        startTime = SystemClock.uptimeMillis();
        handler.post(timerRunnable);
        isPlaying = true;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getTimeElapsed() {
        return timeElapsed;
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
