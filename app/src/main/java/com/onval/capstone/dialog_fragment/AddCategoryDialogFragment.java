package com.onval.capstone.dialog_fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;

import com.onval.capstone.R;
import com.onval.capstone.room.Category;
import com.onval.capstone.utility.GuiUtility;
import com.onval.capstone.viewmodel.CategoriesViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

public class AddCategoryDialogFragment extends DialogFragment {
    public static final String ADD_CATEGORY_TAG = "ADD_CATEGORY";

    private EditText editText;
    private CategoriesViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        editText = new EditText(getActivity());

        int dialogTheme, editTextColor;

        if (GuiUtility.isLightTheme(getContext())) {
            dialogTheme = R.style.DialogTheme;
            editTextColor = Color.BLACK;
        } else {
            dialogTheme = R.style.DialogThemeDark;
            editTextColor = Color.WHITE;
        }

        editText.setTextColor(editTextColor);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), dialogTheme);
        builder.setTitle(R.string.add_category)
                .setView(editText)
                .setPositiveButton(R.string.confirm_btn, new AddCategoriesListener())
                .setNegativeButton(R.string.cancel_btn, (dialogInterface, i) -> getDialog().cancel());

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

    private String fetchNextColor() {
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


