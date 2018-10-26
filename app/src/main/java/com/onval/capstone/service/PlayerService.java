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
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;


import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.onval.capstone.PlayerAppWidget;
import com.onval.capstone.R;
import com.onval.capstone.activities.RecordingsActivity;

import static com.onval.capstone.PlayerAppWidget.CATEGORY_COLOR;
import static com.onval.capstone.PlayerAppWidget.PLAYER_STATUS;
import static com.onval.capstone.PlayerAppWidget.REC_DURATION;
import static com.onval.capstone.PlayerAppWidget.REC_NAME;
import static com.onval.capstone.activities.RecordingsActivity.CATEGORY_ID;
import static com.onval.capstone.activities.RecordingsActivity.CATEGORY_NAME;
import static com.onval.capstone.activities.RecordingsActivity.SELECTED_REC;
import static com.onval.capstone.activities.RecordingsActivity.UPDATE_PLAYER_ACTION;

public class PlayerService extends Service {
    public static final String START_SERVICE_ACTION = "start-action";
    public static final String PLAY_PLAYER_ACTION = "play-action";
    public static final String PAUSE_PLAYER_ACTION = "stop-action";

    private SimpleExoPlayer player;
    private MediaSource source;
    private Notification foregroundNotification;

    public static boolean isRunning;
    public static Integer selectedRec;

    private boolean isPlaying;
    private static final int NOTIFICATION_ID = 2;
    private static final String NOTIFICATION_CHANNEL_ID = "channel-2";

    private int categoryId;
    private String categoryName;
    private String categoryColor;
    private String recName;
    private String recDuration;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        assert action != null;

        switch (action) {
            case START_SERVICE_ACTION:
                startService(intent.getExtras());
                break;
            case PLAY_PLAYER_ACTION:
                play();
                break;
            case PAUSE_PLAYER_ACTION:
                pause();
                break;
        }

        return Service.START_STICKY;
    }

    private void startService(Bundle extras) {
        updateServiceState(
                extras.getInt(CATEGORY_ID),
                extras.getInt(SELECTED_REC),
                extras.getString(CATEGORY_NAME),
                extras.getString(CATEGORY_COLOR),
                extras.getString(REC_NAME),
                extras.getString(REC_DURATION));

        notifyPlayer();

        initializeNotification();
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, foregroundNotification);

        if (!isRunning) {
            initializePlayer();
            isRunning = true;
        }
    }

    private void notifyPlayer() {
        Intent intent = new Intent();
        intent.setAction(UPDATE_PLAYER_ACTION);
        intent.putExtra(REC_NAME, recName);
        intent.putExtra(CATEGORY_NAME, categoryName);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void notifyWidget() {
        Intent intent = new Intent(getApplicationContext(), PlayerAppWidget.class);
        intent.setAction(PlayerAppWidget.WIDGET_MANUAL_UPDATE);
        intent.putExtra(CATEGORY_NAME, categoryName);
        intent.putExtra(CATEGORY_COLOR, categoryColor);
        intent.putExtra(REC_NAME, recName);
        intent.putExtra(REC_DURATION, recDuration);
        intent.putExtra(PLAYER_STATUS, isPlaying);

        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayerBinder();
    }

    public void updateServiceState(int catId, int selRec, String catName, String catColor,
                                   String recName, String recDuration) {
        categoryId = catId;
        selectedRec = selRec;

        categoryName = catName;
        categoryColor = catColor;
        this.recName = recName;
        this.recDuration = recDuration;
    }

    private void initializeNotification() {
        createNotificationChannel();

        Intent intent = new Intent(this, RecordingsActivity.class);
        intent.putExtra(CATEGORY_ID, categoryId);
        intent.putExtra(CATEGORY_NAME, categoryName);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        foregroundNotification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setContentTitle(recName)
                .setContentText("from " + categoryName)
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
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());

            player.addListener(new Player.DefaultEventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == Player.STATE_ENDED) {
                        player.setPlayWhenReady(false);
                        player.seekTo(0);
                    }

                    isPlaying = playWhenReady;
                    notifyWidget();
                }
            });
        }
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
        startForeground(NOTIFICATION_ID, foregroundNotification);
    }

    public void pause() {
        player.setPlayWhenReady(false);
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getRecName() {
        return recName;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        pause();
        player.release();
        player = null;
        isRunning = false;
        notifyWidget();
    }

    public class PlayerBinder extends Binder {
       public PlayerService getService() {
            return PlayerService.this;
       }
    }
}
