package com.onval.capstone.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.onval.capstone.R;
import com.onval.capstone.activities.RecordActivity;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class RecordingService extends Service {
    public static final String DEFAULT_REC_NAME = "/audiorecord.mp4";

    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "channel-1";
    private Notification foregroundNotification;

    private RecordingTimer timer;
    private MediaRecorder recorder;
    private Date startDate;

    private boolean isPlaying = false;

    private boolean hasStarted;
    private String fileName;

    public RecordingService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeRecorder();
        initializeNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!hasStarted) {
            timer = new RecordingTimer(this);
            startRecording();
            startDate = Calendar.getInstance().getTime();
            hasStarted = true;
        }
        return Service.START_STICKY;
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
        startTimer();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void resumeRecording() {
        recorder.resume();
        startTimer();
    }

    public void stopRecording() {
        recorder.stop();
        stopForeground(true);
        isPlaying = false;
        timer.removeCallback();

    }

    @TargetApi(Build.VERSION_CODES.N)
    public void pauseRecording() {
        recorder.pause();
        isPlaying = false;
        timer.pauseTimer();
    }

    private void startTimer() {
        timer.startTimer();
        isPlaying = true;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getFileName() {
        return fileName;
    }

    public long getStartTime() {
        return timer.getStartTime();
    }

    public long getTimeElapsed() {
        return timer.getTimeElapsed();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new RecordingBinder(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }
}
