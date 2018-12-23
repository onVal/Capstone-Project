package com.onval.capstone.dialog_fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.onval.capstone.R;
import com.onval.capstone.room.Record;
import com.onval.capstone.utility.GuiUtility;
import com.onval.capstone.viewmodel.RecordingsViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import static com.onval.capstone.activities.RecordingsActivity.CATEGORY_ID;

public class SaveRecordingDialogFragment extends DialogFragment {
    private EditText editText;

    private RecordingsViewModel recViewModel;

    private Bundle recInfoBundle;
    private OnSaveCallback callback;
    private FragmentManager fm;

    public interface OnSaveCallback {
        void onSaveRecording(Record recording);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (OnSaveCallback) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recViewModel = ViewModelProviders.of(this).get(RecordingsViewModel.class);

        recInfoBundle = getArguments();
        fm = getActivity().getSupportFragmentManager();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        editText = new EditText(getActivity());
        String recStartTime = recInfoBundle.getString("REC_START_TIME");
        String recDate = recInfoBundle.getString("REC_DATE");
        editText.setText(String.format("Recording %s %s", recDate, recStartTime));
        editText.setSelection(0, editText.length());

        editText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        int editTextColor, dialogTheme;
        if (GuiUtility.isLightTheme(getContext())) {
            editTextColor = Color.BLACK;
            dialogTheme = R.style.DialogTheme;
        } else {
            editTextColor = Color.WHITE;
            dialogTheme = R.style.DialogThemeDark;
        }

        editText.setTextColor(editTextColor);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), dialogTheme);
        builder.setView(editText)
                .setPositiveButton(R.string.confirm_btn, new SaveRecordingListener())
                .setNeutralButton(R.string.delete_btn, new AskConfirmationListener(fm))
                .setNegativeButton(R.string.cancel_btn, (dialogInterface, i) -> getDialog().cancel());

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
                    Record.CLOUD_NOT_UPLOADED,
                    categoryId);

            recViewModel.insertRecording(recording, callback);
        }
    }
}
