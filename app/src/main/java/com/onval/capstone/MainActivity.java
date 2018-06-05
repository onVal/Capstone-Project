package com.onval.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setCustomTitle(R.layout.actionbar_title);

        FloatingActionButton fab = findViewById(R.id.main_fab);
        fab.setBackground(ContextCompat.getDrawable(this, R.mipmap.ic_launcher_round));

    }

    @OnClick(R.id.main_fab)
    public void record(View view) {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    private void setCustomTitle(@LayoutRes int resource) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View view = inflator.inflate(resource, null);

        ((TextView)view.findViewById(R.id.title)).setText(getTitle());
        actionBar.setCustomView(view);
    }
}
