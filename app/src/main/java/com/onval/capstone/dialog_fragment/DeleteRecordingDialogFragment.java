package com.onval.capstone.dialog_fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.onval.capstone.R;
import com.onval.capstone.activities.RecordActivity;
import com.onval.capstone.utility.GuiUtility;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DeleteRecordingDialogFragment extends DialogFragment {
    OnDeleteCallback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (RecordActivity) context;
    }

    public interface OnDeleteCallback {
        void onDeleteRecording();
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int dialogTheme = (GuiUtility.isLightTheme(getContext()) ? R.style.DialogTheme : R.style.DialogThemeDark);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), dialogTheme);

        builder.setTitle("Delete Recording")
                .setMessage(R.string.delete_rec_msg)
                .setPositiveButton(android.R.string.yes, new DeleteRecordingListener())
                .setNegativeButton(android.R.string.no, null);

        return builder.create();
    }

    class DeleteRecordingListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            callback.onDeleteRecording();
        }
    }
}
