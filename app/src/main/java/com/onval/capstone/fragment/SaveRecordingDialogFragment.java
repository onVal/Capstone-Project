package com.onval.capstone.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

import com.onval.capstone.R;
import com.onval.capstone.viewmodel.CategoriesViewModel;

public class SaveRecordingDialogFragment extends DialogFragment {
    private EditText editText;
    private CategoriesViewModel viewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        editText = new EditText(getActivity());
        editText.setTextColor(Color.BLACK);
        editText.setText("Recording-1");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        builder.setTitle("")
                .setView(editText)
                .setPositiveButton("Confirm", null)
                .setNegativeButton("Cancel", (dialogInterface, i) -> getDialog().cancel());

        return builder.create();

    }
}
