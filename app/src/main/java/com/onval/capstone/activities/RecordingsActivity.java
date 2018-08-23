package com.onval.capstone.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.onval.capstone.R;
import com.onval.capstone.fragment.RecordingsFragment;

import butterknife.OnClick;

public class RecordingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.category_container, new RecordingsFragment())
                .commit();
    }

    @OnClick
    public void record(View view) {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }
}
