package com.onval.capstone.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
import com.onval.capstone.room.Record;
import com.onval.capstone.service.PlayerService;
import com.onval.capstone.utility.UserInterfaceUtility;
import com.onval.capstone.utility.Utility;
import com.onval.capstone.viewmodel.CategoriesViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.onval.capstone.PlayerAppWidget.CATEGORY_COLOR;
import static com.onval.capstone.PlayerAppWidget.REC_DURATION;
import static com.onval.capstone.PlayerAppWidget.REC_NAME;
import static com.onval.capstone.fragment.RecordingsFragment.NO_SELECTED_REC;
import static com.onval.capstone.service.PlayerService.START_SERVICE_ACTION;

public class RecordingsActivity extends AppCompatActivity
        implements RecordingsAdapter.RecordingListener {
    public static final String CATEGORY_ID = "category-id-extra";
    public static final String CATEGORY_NAME = "category-name-extra";
    public static final String SELECTED_REC = "selected-rec-extra";
    public static final String FRAGMENT_TAG = "rec-fragment";

    @BindView(R.id.exo_player) PlayerView playerView;
    @BindView(R.id.recording_fab) FloatingActionButton fab;

    private int categoryId;
    private String categoryName;
    private Integer selectedRec;

    private Intent serviceIntent;
    private PlayerService playerService;

    private Uri playingRecordingUri;
    private boolean isBound;

    Toolbar toolbar;

    private RecordingsFragment fragment;
    private LiveData<String> categoryColor;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            playerService = ((PlayerService.PlayerBinder) binder).getService();
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

        toolbar = findViewById(R.id.my_rec_toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        categoryId = getIntent().getExtras().getInt(CATEGORY_ID);
        categoryName = getIntent().getExtras().getString(CATEGORY_NAME);

        CategoriesViewModel viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        categoryColor = viewModel.getCategoryColor(categoryId);
        categoryColor.observe(this, this::setInterfaceColor);

        if (PlayerService.isRunning) {
            if (PlayerService.selectedRec != null)
                getIntent().getExtras().putInt(SELECTED_REC, PlayerService.selectedRec);

            Intent intent = new Intent(this, PlayerService.class);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }

        int commit = getSupportFragmentManager().beginTransaction()
                .replace(R.id.category_container, new RecordingsFragment(), FRAGMENT_TAG)
                .commit();
    }

    private void setInterfaceColor(String colorStr) {
        int color = Color.parseColor(colorStr);
        int darkenedColor = UserInterfaceUtility.darkenColor(color, 0.9f);

        fab.setBackgroundTintList(ColorStateList.valueOf(darkenedColor));
        toolbar.setBackgroundColor(darkenedColor);
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
    public void onRecordingClicked(Uri recUri, int selectedRec, Record recording) {
        playingRecordingUri = recUri;
        String recName = recording.getName();
        String recDuration = recording.getDuration();
        String catColor = categoryColor.getValue();

        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(START_SERVICE_ACTION);

        intent.putExtra(CATEGORY_ID, categoryId);
        intent.putExtra(CATEGORY_NAME, categoryName);

        intent.putExtra(SELECTED_REC, selectedRec);
        intent.putExtra(REC_NAME, recName);
        intent.putExtra(REC_DURATION, recDuration);
        intent.putExtra(CATEGORY_COLOR, catColor);

        startService(intent);

        if (playerService == null) {
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        } else {
            startMediaPlayer();
        }
    }

    public void closePlayer(View view) {
        fragment = ((RecordingsFragment) getSupportFragmentManager()
                .findFragmentByTag(FRAGMENT_TAG));
        AdapterListener listener = fragment;


        unbindService(serviceConnection);
        Intent intent = new Intent(this, PlayerService.class);
        stopService(intent);
        playerService = null;
        showPlayer(false);
        listener.setSelected(RecordingsFragment.NO_SELECTED_REC);
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

    public interface AdapterListener {
        void setSelected(int position);
    }
}
