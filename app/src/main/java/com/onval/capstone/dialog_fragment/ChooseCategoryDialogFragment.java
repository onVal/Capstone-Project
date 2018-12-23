package com.onval.capstone.dialog_fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.onval.capstone.R;
import com.onval.capstone.adapter.MiniCategoriesAdapter;
import com.onval.capstone.utility.GuiUtility;
import com.onval.capstone.viewmodel.CategoriesViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.onval.capstone.activities.RecordingsActivity.CATEGORY_ID;
import static com.onval.capstone.adapter.MiniCategoriesAdapter.NO_CATEGORIES;
import static com.onval.capstone.dialog_fragment.AddCategoryDialogFragment.ADD_CATEGORY_TAG;


public class ChooseCategoryDialogFragment extends DialogFragment {

    private static final String SAVE_RECORDING_TAG = "save-recording-tag";

    @BindView(R.id.mini_cat_rv) RecyclerView categories;
    @BindView(R.id.mini_add_cat) ImageView addCategory;

    private CategoriesViewModel viewModel;
    private MiniCategoriesAdapter adapter;
    private Bundle recInfoBundle;
    private FragmentManager fm;

    private View layout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recInfoBundle = getArguments();

        layout = LayoutInflater.from(getContext()).inflate(R.layout.category_list_mini, null);
        ButterKnife.bind(this, layout);

        assert getActivity() != null;
        fm = getActivity().getSupportFragmentManager();

        adapter = new MiniCategoriesAdapter(getContext());

        categories.setLayoutManager(new LinearLayoutManager(getContext()));
        categories.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        viewModel.getCategories().observe(this, adapter::setCategories);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int dialogTheme = (GuiUtility.isLightTheme(getContext()) ? R.style.DialogTheme : R.style.DialogThemeDark);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), dialogTheme);
        builder.setView(layout)
                .setPositiveButton(R.string.save_btn, new ChooseCategoryListener())
                .setNeutralButton(R.string.delete_btn, new AskConfirmationListener(fm))
                .setNegativeButton(R.string.cancel_btn, (dialogInterface, i) -> getDialog().cancel());

        return builder.create();
    }

    class ChooseCategoryListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            int categoryId = adapter.getSelectedCategoryId();

            if (categoryId != NO_CATEGORIES) {
                recInfoBundle.putInt(CATEGORY_ID, categoryId);

                SaveRecordingDialogFragment saveRecording = new SaveRecordingDialogFragment();
                saveRecording.setArguments(recInfoBundle);
                saveRecording.show(fm, SAVE_RECORDING_TAG);
            } else {
                Toast.makeText(getContext(),
                        "Create a category in order to save a recording",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.mini_add_cat)
    public void addCategory() {
        AddCategoryDialogFragment addCatFragment = new AddCategoryDialogFragment();
        addCatFragment.show(fm, ADD_CATEGORY_TAG);
    }
}
