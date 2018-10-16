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

public class RecordingsActivity extends AppCompatActivity
        implements RecordingsAdapter.RecordingListener {
    public static final String CATEGORY_ID = "category-id-extra";

    @BindView(R.id.exo_player) PlayerView playerView;
    @BindView(R.id.recording_fab) FloatingActionButton fab;

    private int categoryId;

    private Intent serviceIntent;
    private PlayerService playerService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            playerService = ((PlayerService.PlayerBinder) binder).getService();
            SimpleExoPlayer player = playerService.getPlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playerService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);

        Toolbar toolbar = findViewById(R.id.my_rec_toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        categoryId = getIntent().getExtras().getInt("CATEGORY_ID");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.category_container, new RecordingsFragment())
                .commit();

        //todo: should probably delay startService
        Intent intent = new Intent(this, PlayerService.class);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
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
    public void onRecordingClicked(Uri recUri) {
        playerService.setUri(recUri);
        playerView.setPlayer(playerService.getPlayer());
        playerService.preparePlayer();
        playerService.play();

        playerView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.INVISIBLE);
    }

}
