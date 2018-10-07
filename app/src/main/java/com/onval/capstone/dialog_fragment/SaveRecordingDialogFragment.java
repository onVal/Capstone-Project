package com.onval.capstone.dialog_fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.widget.EditText;

import com.onval.capstone.R;
import com.onval.capstone.room.Record;
import com.onval.capstone.viewmodel.CategoriesViewModel;

import static com.onval.capstone.activities.RecordingsActivity.CATEGORY_ID;

public class SaveRecordingDialogFragment extends DialogFragment {
    private EditText editText;
    private CategoriesViewModel viewModel;
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
        viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        viewModel.setOnSaveCallback(callback);
        recInfoBundle = getArguments();
        fm = getActivity().getSupportFragmentManager();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        editText = new EditText(getActivity());
        editText.setTextColor(Color.BLACK);
        editText.setText("Recording-1");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        builder.setTitle("")
                .setView(editText)
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

            Record recording = new Record(recName, recDuration, recDate, recStartTime, ".wav", null, categoryId);
            viewModel.insertRecording(recording);
        }
    }
}
