package com.onval.capstone.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class PlayerService extends Service {
    private SimpleExoPlayer player;
    private MediaSource source;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initializePlayer();
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayerBinder();
    }

    private void initializePlayer() {
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
    }

    public void pause() {
        player.setPlayWhenReady(false);
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
        player = null;
    }

    public class PlayerBinder extends Binder {
       public PlayerService getService() {
            return PlayerService.this;
       }
    }
}
