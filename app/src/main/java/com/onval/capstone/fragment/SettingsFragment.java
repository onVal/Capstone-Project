package com.onval.capstone.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.onval.capstone.R;
import com.onval.capstone.utility.GuiUtility;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {
    //need to hold a reference to this boy otherwise app crashes
    //trying to call method on null parent activity reference
    private FragmentActivity activity;
    private Context context;

    private SharedPreferences sharedPreferences;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        activity = getActivity();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        PrefChangeListener listener = new PrefChangeListener();
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSummaries();
    }

    private void updateSummaries() {
        updateAccountSummary();
        updateThemeSummary();
    }

    private void updateThemeSummary() {
        Preference pref = findPreference("pref_theme");
        pref.setSummary(GuiUtility.getTheme(getContext()));
    }

    private void updateAccountSummary() {
        Preference pref = findPreference("pref_account");
        pref.setSummary(R.string.account_default_summary);

        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(getActivity());

        if (googleAccount != null)
            pref.setSummary(googleAccount.getEmail());
        else
            pref.setSummary(R.string.account_default_summary);
    }

    private class PrefChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("pref_theme") &&
                    sharedPreferences.getString("pref_theme", "Light")
                            .equals(GuiUtility.getTheme(context))) {
                activity.recreate();
            }
        }
    }
}
