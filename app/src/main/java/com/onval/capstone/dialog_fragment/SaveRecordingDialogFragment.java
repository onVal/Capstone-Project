package com.onval.capstone.dialog_fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;

import com.onval.capstone.R;
import com.onval.capstone.room.Record;
import com.onval.capstone.viewmodel.RecordingsViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import static com.onval.capstone.activities.RecordingsActivity.CATEGORY_ID;

public class SaveRecordingDialogFragment extends DialogFragment {
    private EditText editText;
    private RecordingsViewModel viewModel;
    private Bundle recInfoBundle;
    private OnSaveCallback callback;
    private Context context;
    private FragmentManager fm;

    public interface OnSaveCallback {
        void onSaveRecording(long id, String name);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (OnSaveCallback) context;
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(RecordingsViewModel.class);
        recInfoBundle = getArguments();
        fm = getActivity().getSupportFragmentManager();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        editText = new EditText(getActivity());
        editText.setTextColor(Color.BLACK);
        String recStartTime = recInfoBundle.getString("REC_START_TIME");
        String recDate = recInfoBundle.getString("REC_DATE");
        editText.setText(String.format("Recording %s %s", recDate, recStartTime));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        builder.setView(editText)
                .setPositiveButton("Confirm", new SaveRecordingListener())
                .setNeutralButton("Delete", new AskConfirmationListener(fm))
                .setNegativeButton("Cancel", (dialogInterface, i) -> getDialog().cancel());

        return builder.create();
    }


    class SaveRecordingListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String recName = editText.getText().toString();
            String recDuration = recInfoBundle.getString("REC_DURATION");
            String recStartTime = recInfoBundle.getString("REC_START_TIME");
            String recDate = recInfoBundle.getString("REC_DATE");
            int categoryId = recInfoBundle.getInt(CATEGORY_ID);

            Record recording = new Record(
                    recName,
                    recDuration,
                    recDate,
                    recStartTime,
                    ".wav",
                    null,
                    categoryId);
            viewModel.insertRecording(recording, callback);
        }
    }
}
