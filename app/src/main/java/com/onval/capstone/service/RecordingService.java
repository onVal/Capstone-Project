package com.onval.capstone.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.onval.capstone.R;
import com.onval.capstone.activities.RecordActivity;
import com.onval.capstone.utility.Utility;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.onval.capstone.activities.RecordActivity.PAUSE_ACTION;
import static com.onval.capstone.activities.RecordActivity.PLAY_ACTION;
import static com.onval.capstone.activities.RecordActivity.RESET_ACTION;

public class RecordingService extends Service {
    public static final String REC_EXTENSION = ".mp4";
    public static final String DEFAULT_REC_NAME = "/audiorecord" + REC_EXTENSION;

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
        } else {
            updateTimerTV();

            if (isPlaying) {
                updateUIButton(PAUSE_ACTION);
            } else {
                updateUIButton(PLAY_ACTION);
            }
        }
        return Service.START_STICKY;
    }

    private void initializeRecorder() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bitrateString = prefs.getString(getString(R.string.pref_save_quality),
                                    getString(R.string.normal_quality));
        int bitrate = Integer.parseInt(bitrateString);

        fileName = Utility.getRecordingAbsolutePath(this);
        fileName += DEFAULT_REC_NAME;

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioEncodingBitRate(bitrate);

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
                .setContentTitle(getString(R.string.record_title))
                .setContentText(getString(R.string.record_content_txt))
                .setSmallIcon(R.drawable.ic_mic_black_24dp)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] {0L})
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    getString(R.string.record_channel), NotificationManager.IMPORTANCE_LOW);
            channel.enableVibration(false);
            channel.setSound(null, null);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void updateTimerTV() {
        timer.updateTimer();
    }
    private void updateUIButton(String action) {
        Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void startRecording() {
        recorder.start();
        startForeground(NOTIFICATION_ID, foregroundNotification);
        startTimer();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            updateUIButton(PAUSE_ACTION);
        else
            updateUIButton(RESET_ACTION);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void resumeRecording() {
        recorder.resume();
        startTimer();
        updateUIButton(PAUSE_ACTION);
    }

    public void stopRecording() {
        recorder.stop();
        stopForeground(true);
        isPlaying = false;
        timer.pauseTimer();

        updateUIButton(RESET_ACTION);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void pauseRecording() {
        recorder.pause();
        isPlaying = false;
        timer.pauseTimer();
        updateUIButton(PLAY_ACTION);
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
        updateUIButton(RESET_ACTION);

        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }
}
