package com.onval.capstone.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.onval.capstone.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSummary();
    }

    private void updateSummary() {
        Preference pref = findPreference("pref_account");
        pref.setSummary(R.string.account_default_summary);

        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(getActivity());

        if (googleAccount != null)
            pref.setSummary(googleAccount.getEmail());
        else
            pref.setSummary(R.string.account_default_summary);
    }
}
