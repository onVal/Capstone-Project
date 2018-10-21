package com.onval.capstone.fragment;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onval.capstone.R;
import com.onval.capstone.activities.RecordingsActivity;
import com.onval.capstone.adapter.RecordingsAdapter;
import com.onval.capstone.room.Record;
import com.onval.capstone.viewmodel.CategoriesViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.onval.capstone.activities.RecordingsActivity.CATEGORY_ID;
import static com.onval.capstone.activities.RecordingsActivity.SELECTED_REC;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordingsFragment extends Fragment
        implements RecordingsActivity.AdapterListener {
    private RecordingsAdapter adapter;
    private CategoriesViewModel viewModel;

    public static final int NO_SELECTED_REC = -1;

    @BindView(R.id.recordings_rv) RecyclerView recordingsRv;

    public RecordingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModel =  ViewModelProviders.of(this).get(CategoriesViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_recordings, container, false);
        ButterKnife.bind(this, view);

        int categoryId = getActivity().getIntent().getExtras().getInt(CATEGORY_ID);
        int selectedRec = getActivity().getIntent().getExtras().getInt(SELECTED_REC, NO_SELECTED_REC);

        recordingsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecordingsAdapter(getContext(), selectedRec);

        CategoriesViewModel viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        LiveData<String> categoryColor = viewModel.getCategoryColor(categoryId);
        categoryColor.observe(this, (color) -> {
            adapter.setColor(color);
        });

        recordingsRv.setAdapter(adapter);

        LiveData<List<Record>> liveRecordings = viewModel.getRecordingsFromCategory(categoryId);
        liveRecordings.observe(this, (records -> adapter.setRecordings(records)));

        return view;
    }

    @Override
    public void setSelected(int position) {
        adapter.setSelected(position);
    }
}
