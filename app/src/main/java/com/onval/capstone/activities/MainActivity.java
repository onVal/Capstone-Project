package com.onval.capstone.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.onval.capstone.fragment.CategoriesFragment;
import com.onval.capstone.fragment.SettingsFragment;
import com.onval.capstone.viewmodel.CategoriesViewModel;
import com.onval.capstone.fragment.EmptyFragment;
import com.onval.capstone.R;

import butterknife.OnClick;
import dagger.android.AndroidInjection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        setCustomTitle(R.layout.actionbar_title);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        CategoriesViewModel categoriesViewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);

        if (!categoriesViewModel.getData().getValue().isEmpty())
            ft.replace(R.id.fragment_container, CategoriesFragment.newInstance());
        else
            ft.replace(R.id.fragment_container, EmptyFragment.newInstance());
        ft.commit();
    }

    @OnClick(R.id.main_fab)
    public void record(View view) {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }

        return true;
    }

    private void setCustomTitle(@LayoutRes int resource) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(resource, null);

        ((TextView)view.findViewById(R.id.title)).setText(getTitle());
        actionBar.setCustomView(view);
    }
}
