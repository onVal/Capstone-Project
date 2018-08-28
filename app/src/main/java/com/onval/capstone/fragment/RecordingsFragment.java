package com.onval.capstone.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onval.capstone.R;
import com.onval.capstone.adapter.RecordingsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordingsFragment extends Fragment {
    private RecordingsAdapter adapter;
    @BindView(R.id.recordings_rv) RecyclerView recordings;

    public RecordingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_recordings, container, false);
        ButterKnife.bind(this, view);

        recordings.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecordingsAdapter(getContext());
        recordings.setAdapter(adapter);

        return view;
    }
}
