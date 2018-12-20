package com.onval.capstone.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.onval.capstone.R;
import com.onval.capstone.adapter.RecordingsAdapter;
import com.onval.capstone.fragment.RecordingsFragment;
import com.onval.capstone.room.Record;
import com.onval.capstone.service.PlayerService;
import com.onval.capstone.utility.GuiUtility;
import com.onval.capstone.viewmodel.CategoriesViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.onval.capstone.PlayerAppWidget.CATEGORY_COLOR;
import static com.onval.capstone.PlayerAppWidget.REC_DURATION;
import static com.onval.capstone.PlayerAppWidget.REC_NAME;
import static com.onval.capstone.service.PlayerService.START_SERVICE_ACTION;

public class RecordingsActivity extends AppCompatActivity
        implements RecordingsAdapter.RecordingListener {
    public static final String CATEGORY_ID = "category-id-extra";
    public static final String CATEGORY_NAME = "category-name-extra";
    public static final String SELECTED_REC = "selected-rec-extra";
    public static final String FRAGMENT_TAG = "rec-fragment";

    public static final String UPDATE_PLAYER_ACTION = "com.onval.capstone.UPDATE_PLAYER";

    @BindView(R.id.exo_player) PlayerView playerView;
    @BindView(R.id.recording_fab) FloatingActionButton fab;
    @BindView(R.id.ctrl_rec_name) TextView recNameView;
    @BindView(R.id.ctrl_cat_name) TextView catNameView;

    private int categoryId;
    private String categoryName;

    private String recName;
    private String recCategoryName;

    private Intent serviceIntent;
    private PlayerService playerService;

    private Uri playingRecordingUri;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UPDATE_PLAYER_ACTION)) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    recName = extras.getString(REC_NAME);
                    recCategoryName = extras.getString(CATEGORY_NAME);
                    updatePlayerText(recName, recCategoryName);
                }
            }
        }
    };

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

            updatePlayerText(playerService.getRecName(),
                    playerService.getCategoryName());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playerService = null;
            playingRecordingUri = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GuiUtility.initCustomTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);

        categoryId = getIntent().getExtras().getInt(CATEGORY_ID);
        categoryName = getIntent().getExtras().getString(CATEGORY_NAME);

        setToolbar();

        ButterKnife.bind(this);

        IntentFilter filter = new IntentFilter(UPDATE_PLAYER_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        CategoriesViewModel viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        categoryColor = viewModel.getCategoryColor(categoryId);
//        categoryColor.observe(this, this::setInterfaceColor);

        if (PlayerService.isRunning) {
            if (PlayerService.selectedRec != null)
                getIntent().getExtras().putInt(SELECTED_REC, PlayerService.selectedRec);

            Intent intent = new Intent(this, PlayerService.class);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.category_container, new RecordingsFragment(), FRAGMENT_TAG)
                .commit();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.my_rec_toolbar);
        toolbar.setTitle(categoryName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        updatePlayerText(
                savedInstanceState.getString(REC_NAME),
                savedInstanceState.getString(CATEGORY_NAME));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (playerService != null) {
            outState.putString(REC_NAME, playerService.getRecName());
            outState.putString(CATEGORY_NAME, playerService.getCategoryName());
        }
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
        //todo: sometimes this is null and app crashes - can't rely on it
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

    private void updatePlayerText(String recName, String catName) {
        recNameView.setText(recName);
        catNameView.setText("from " + catName);
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
