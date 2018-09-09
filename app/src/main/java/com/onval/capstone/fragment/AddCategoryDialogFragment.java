package com.onval.capstone.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.Toast;

import com.onval.capstone.R;
import com.onval.capstone.room.Category;
import com.onval.capstone.viewmodel.CategoriesViewModel;

public class AddCategoryDialogFragment extends DialogFragment {
    EditText editText;
    CategoriesViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        editText = new EditText(getActivity());
        editText.setTextColor(Color.parseColor("#000000"));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        builder.setTitle(R.string.add_category)
                .setView(editText)
                .setPositiveButton("Confirm", new AddCategoriesListener())
                .setNegativeButton("Cancel", (dialogInterface, i) -> getDialog().cancel());

        return builder.create();

    }

    class AddCategoriesListener implements OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            String categoryName = editText.getText().toString();

            try {
                viewModel.insertCategories(
                        new Category(categoryName, "#CCEE00", false));
            } catch (Exception exc) {
                Toast.makeText(getContext(), R.string.cant_insert_cat, Toast.LENGTH_SHORT).show();
            }
        }
    }
}



