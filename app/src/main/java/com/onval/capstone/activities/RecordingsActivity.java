package com.onval.capstone.activities;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.onval.capstone.R;
import com.onval.capstone.fragment.RecordingsFragment;

import butterknife.OnClick;

public class RecordingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);

        Toolbar toolbar = findViewById(R.id.my_rec_toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.category_container, new RecordingsFragment())
                .commit();
    }

    @OnClick
    public void record(View view) {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recs, menu);
        return true;
    }
}
