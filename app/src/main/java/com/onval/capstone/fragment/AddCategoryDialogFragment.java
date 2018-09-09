package com.onval.capstone.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

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

            Category categoryToInsert = new Category(
                    categoryName,
                     fetchNextColor(),
                    false );

            viewModel.insertCategory(categoryToInsert);
        }
    }

    String fetchNextColor() {
        String[] colors = getContext().getResources().getStringArray(R.array.category_colors);

        SharedPreferences prefs = getContext().getSharedPreferences(
                getString(R.string.prefs), Context.MODE_PRIVATE);

        int nextColor = prefs.getInt(getString(R.string.next_color), 0);

        prefs.edit()
                .putInt(getString(R.string.next_color), (nextColor+1) % colors.length)
                .apply();

        return colors[nextColor];
    }
}


