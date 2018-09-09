package com.onval.capstone.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.onval.capstone.fragment.AddCategoryDialogFragment;
import com.onval.capstone.fragment.CategoriesFragment;

import com.onval.capstone.viewmodel.CategoriesViewModel;
import com.onval.capstone.fragment.EmptyFragment;
import com.onval.capstone.R;

import butterknife.OnClick;
import dagger.android.AndroidInjection;

public class MainActivity extends AppCompatActivity implements Observer<Integer> {
    private static final String ADD_CATEGORY_TAG = "ADD_CATEGORY";
    private CategoriesViewModel viewModel;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        setCustomTitle(R.layout.actionbar_title);

        fm = getSupportFragmentManager();

        if (savedInstanceState == null) {
            viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);

//        Category cat1 = new Category("Python", "#ff00ff", true);
//        Category cat2 = new Category("Java", "#33ff00", false);
//        Category cat3 = new Category("PHP", "#1100aa", false);
//        viewModel.insertCategories(cat1, cat2, cat3);

            LiveData<Integer> liveNumCategories = viewModel.getNumOfCategories();
            liveNumCategories.observe(this, this);
        }
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
            case R.id.action_add_category:
                AddCategoryDialogFragment addCatFragment = new AddCategoryDialogFragment();
                addCatFragment.show(fm, ADD_CATEGORY_TAG);
                break;
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

    @Override
    public void onChanged(@Nullable Integer numberOfCategories) {
        FragmentTransaction ft = fm.beginTransaction();

        assert numberOfCategories != null;

        if (numberOfCategories > 0)
            ft.replace(R.id.fragment_container, CategoriesFragment.newInstance());
        else
            ft.replace(R.id.fragment_container, EmptyFragment.newInstance());
        ft.commit();
    }
}
