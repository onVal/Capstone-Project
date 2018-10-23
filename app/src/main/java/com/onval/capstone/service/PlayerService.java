package com.onval.capstone.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.google.android.exoplayer2.ExoPlayerFactory;
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

import static com.onval.capstone.activities.RecordingsActivity.CATEGORY_ID;
import static com.onval.capstone.activities.RecordingsActivity.CATEGORY_NAME;
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

    private String categoryName;
    private String categoryColor;
    private String recName;
    private String recDuration;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int categoryId = intent.getExtras().getInt(CATEGORY_ID);

        selectedRec = intent.getExtras().getInt(SELECTED_REC);

        categoryName = intent.getExtras().getString(CATEGORY_NAME);
        categoryColor = intent.getExtras().getString("cat-color");
        recName = intent.getExtras().getString("rec-name");
        recDuration = intent.getExtras().getString("rec-duration");

        initializePlayer();
        initializeNotification(categoryId, categoryName);
        isRunning = true;

        return Service.START_STICKY;
    }

    private void notifyWidget() {
        Intent intent = new Intent(getApplicationContext(), PlayerAppWidget.class);
        intent.setAction(PlayerAppWidget.WIDGET_MANUAL_UPDATE);
        intent.putExtra(CATEGORY_NAME, categoryName);
        intent.putExtra("cat-color", categoryColor);
        intent.putExtra("rec-name", recName);
        intent.putExtra("rec-duration", recDuration);
        intent.putExtra("status", isPlaying);

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.player_app_widget);
        views.setTextViewText(R.id.playing_rec, recName);
        views.setTextViewText(R.id.from_category, "from " + categoryName);
        views.setTextViewText(R.id.rec_duration, recDuration);
        views.setInt(R.id.cat_color, "setBackgroundColor", Color.parseColor(categoryColor));

        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayerBinder();
    }

    private void initializeNotification(int categoryId, String categoryName) {
        createNotificationChannel();

        Intent intent = new Intent(this, RecordingsActivity.class);
        intent.putExtra(CATEGORY_ID, categoryId);
        intent.putExtra(CATEGORY_NAME, categoryName);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        foregroundNotification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Player")
                .setContentText(recName)
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
        notifyWidget();
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
