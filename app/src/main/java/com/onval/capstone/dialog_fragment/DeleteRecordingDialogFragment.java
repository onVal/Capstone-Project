package com.onval.capstone.dialog_fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.onval.capstone.R;
import com.onval.capstone.activities.RecordActivity;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

        builder.setTitle("Delete Recording")
                .setMessage("Do you really want to permanently delete the recording? (this operation is irreversible!)")
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
