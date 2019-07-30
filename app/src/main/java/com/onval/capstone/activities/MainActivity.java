package com.onval.capstone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.onval.capstone.R;
import com.onval.capstone.dialog_fragment.AddCategoryDialogFragment;
import com.onval.capstone.fragment.CategoriesFragment;
import com.onval.capstone.fragment.EmptyFragment;
import com.onval.capstone.utility.GuiUtility;
import com.onval.capstone.viewmodel.CategoriesViewModel;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.OnClick;
import dagger.android.AndroidInjection;

import static com.onval.capstone.dialog_fragment.AddCategoryDialogFragment.ADD_CATEGORY_TAG;

public class MainActivity extends AppCompatActivity implements Observer<Integer> {
    private CategoriesViewModel viewModel;
    private FragmentManager fm;
    private String currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentTheme = GuiUtility.getTheme(this);
        setTheme(currentTheme.equals(getString(R.string.light_theme_name)) ? R.style.LightTheme : R.style.DarkTheme);
        setAnimation();

        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        setCustomTitle(R.layout.actionbar_title);

        fm = getSupportFragmentManager();

        viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);

        viewModel.getNumOfCategories()
                .observe(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!currentTheme.equals(GuiUtility.getTheme(this))) {
            recreate();
        }
    }

    private void setAnimation() {
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.START);
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);
    }

    @OnClick(R.id.main_fab)
    public void record(View view) {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
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
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void setCustomTitle(@LayoutRes int resource) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(resource, null);

        ((TextView)view.findViewById(R.id.title)).setText(getString(R.string.main_toolbar_title));
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
