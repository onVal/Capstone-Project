package com.onval.capstone.activities;

import android.os.Bundle;

import com.onval.capstone.R;
import com.onval.capstone.fragment.SettingsFragment;
import com.onval.capstone.utility.UserInterfaceUtility;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (UserInterfaceUtility.getTheme(this).equals("Light"))
            setTheme(R.style.SettingsTheme);
        else
            setTheme(R.style.SettingsDarkTheme);

        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        setToolbar();
    }

    private void setToolbar() {
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
