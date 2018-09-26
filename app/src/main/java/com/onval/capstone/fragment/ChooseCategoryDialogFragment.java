package com.onval.capstone.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.onval.capstone.R;
import com.onval.capstone.adapter.MiniCategoriesAdapter;
import com.onval.capstone.viewmodel.CategoriesViewModel;


public class ChooseCategoryDialogFragment extends DialogFragment {

    private RecyclerView categories;
    private CategoriesViewModel viewModel;
    private MiniCategoriesAdapter adapter;
    private Bundle recInfoBundle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recInfoBundle = getArguments();

        categories = (RecyclerView) LayoutInflater.from(getContext()).inflate(R.layout.category_list_mini, null);
        adapter = new MiniCategoriesAdapter(getContext());
        categories.setLayoutManager(new LinearLayoutManager(getContext()));
        categories.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        viewModel.getCategories().observe(this, adapter::setCategories);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        builder.setTitle("Choose category to save to...")
                .setView(categories)
                .setPositiveButton("Save", new ChooseCategoryListener())
                .setNegativeButton("Cancel", (dialogInterface, i) -> getDialog().cancel());

        return builder.create();
    }

    class ChooseCategoryListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            int categoryId = adapter.getSelectedCategoryId();
            recInfoBundle.putInt("CATEGORY_ID", categoryId);

            SaveRecordingDialogFragment saveRecording = new SaveRecordingDialogFragment();
            saveRecording.setArguments(recInfoBundle);
            saveRecording.show(getActivity().getSupportFragmentManager(), "derpo");
        }
    }
}
