package com.onval.capstone.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.onval.capstone.R;
import com.onval.capstone.activities.RecordingsActivity;

import static com.onval.capstone.activities.RecordingsActivity.CATEGORY_ID;
import static com.onval.capstone.activities.RecordingsActivity.SELECTED_REC;

public class PlayerService extends Service {
    private SimpleExoPlayer player;
    private MediaSource source;
    private Notification foregroundNotification;

    public static boolean isRunning;
    public static Integer selectedRec;

    private boolean isPlaying;
    private static final int NOTIFICATION_ID = 2;
    private static final String NOTIFICATION_CHANNEL_ID = "channel-2";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int categoryId = intent.getExtras().getInt(CATEGORY_ID);
        selectedRec = intent.getExtras().getInt(SELECTED_REC);

        initializePlayer();
        initializeNotification(categoryId);
        isRunning = true;
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayerBinder();
    }

    private void initializeNotification(int categoryId) {
        createNotificationChannel();

        Intent intent = new Intent(this, RecordingsActivity.class);
        intent.putExtra(CATEGORY_ID, categoryId);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        foregroundNotification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Player")
                .setContentText("You are playing a recording")
                .setSmallIcon(R.drawable.ic_play_circle_filled_white_24dp)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] {0L})
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "AudioChannel", NotificationManager.IMPORTANCE_LOW);
            channel.enableVibration(false);
            channel.setSound(null, null);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void initializePlayer() {
        if (player == null)
            player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
    }

    public void setUri(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "yourApplicationName"));

        source = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    public void preparePlayer() {
        player.prepare(source);
    }

    public void play() {
        player.setPlayWhenReady(true);
        isPlaying = true;
        startForeground(NOTIFICATION_ID, foregroundNotification);
    }

    public void pause() {
        player.setPlayWhenReady(false);
        isPlaying = false;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        pause();
        player.release();
        player = null;
        isRunning = false;
    }

    public class PlayerBinder extends Binder {
       public PlayerService getService() {
            return PlayerService.this;
       }
    }
}
