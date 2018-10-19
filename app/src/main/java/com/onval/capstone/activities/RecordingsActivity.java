package com.onval.capstone.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.onval.capstone.R;
import com.onval.capstone.adapter.RecordingsAdapter;
import com.onval.capstone.fragment.RecordingsFragment;
import com.onval.capstone.service.PlayerService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.onval.capstone.fragment.RecordingsFragment.NO_SELECTED_REC;

public class RecordingsActivity extends AppCompatActivity
        implements RecordingsAdapter.RecordingListener {
    public static final String CATEGORY_ID = "category-id-extra";
    public static final String SELECTED_REC = "selected-rec-extra";

    @BindView(R.id.exo_player) PlayerView playerView;
    @BindView(R.id.recording_fab) FloatingActionButton fab;

    private int categoryId;
    private Integer selectedRec;

    private Intent serviceIntent;
    private PlayerService playerService;

    private Uri playingRecordingUri;
    private boolean isBound;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            playerService = ((PlayerService.PlayerBinder) binder).getService();
            SimpleExoPlayer player = playerService.getPlayer();
            if (!playerService.isPlaying())
                startMediaPlayer();
            else {
                showPlayer(true);
                playerView.showController();
                playerView.setPlayer(playerService.getPlayer());
            }

            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playerService = null;
            playingRecordingUri = null;
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);

        Toolbar toolbar = findViewById(R.id.my_rec_toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        categoryId = getIntent().getExtras().getInt(CATEGORY_ID);

        if (PlayerService.isRunning) {
            if (PlayerService.selectedRec != null)
                getIntent().getExtras().putInt(SELECTED_REC, PlayerService.selectedRec);

            Intent intent = new Intent(this, PlayerService.class);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.category_container, new RecordingsFragment())
                .commit();
    }

    @OnClick
    public void record(View view) {
        serviceIntent = new Intent(this, RecordActivity.class);
        serviceIntent.putExtra(CATEGORY_ID, categoryId);
        startActivity(serviceIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recs, menu);
        return true;
    }

    @Override
    public void onRecordingClicked(Uri recUri, int selectedRec) {
        playingRecordingUri = recUri;

        Intent intent = new Intent(this, PlayerService.class);
        intent.putExtra(CATEGORY_ID, categoryId);
        intent.putExtra(SELECTED_REC, selectedRec);

        if (playerService == null) {
            startService(intent);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        } else {
            startMediaPlayer();
        }
    }

    public void closePlayer(View view) {
        unbindService(serviceConnection);
        Intent intent = new Intent(this, PlayerService.class);
        stopService(intent);
        playerService = null;
        showPlayer(false);
    }

    private void startMediaPlayer() {
        playerService.setUri(playingRecordingUri);
        playerView.setPlayer(playerService.getPlayer());
        playerService.preparePlayer();
        playerService.play();
        showPlayer(true);
    }

    private void showPlayer(boolean isPlayerVisible) {
        playerView.setVisibility((isPlayerVisible) ? View.VISIBLE : View.GONE);
        fab.setVisibility((isPlayerVisible) ? View.GONE : View.VISIBLE);
    }
}
