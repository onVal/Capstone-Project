package com.onval.capstone.dialog_fragment;

import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;

public class AskConfirmationListener implements DialogInterface.OnClickListener {
    private FragmentManager fm;
    private static final String DELETE_FRAGMENT_TAG = "delete-fragment";

    AskConfirmationListener(FragmentManager fm) {
        this.fm = fm;
    }
    @Override
    public void onClick(DialogInterface dialog, int which) {
        DeleteRecordingDialogFragment deleteFragment = new DeleteRecordingDialogFragment();
        deleteFragment.show(fm, DELETE_FRAGMENT_TAG);
    }
}
