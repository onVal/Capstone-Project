package com.onval.capstone.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
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
