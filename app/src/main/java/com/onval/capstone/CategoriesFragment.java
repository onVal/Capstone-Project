package com.onval.capstone;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesFragment extends Fragment {
    @Inject Context context;
    private CategoriesViewModel viewModel;

    @BindView(R.id.categories) RecyclerView categories;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        AndroidModuleComponent component =
                DaggerAndroidModuleComponent.builder()
                        .androidModule(new AndroidModule(getContext()))
                        .build();

        context = component.context();

        viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);

        ButterKnife.bind(this, view);

        CategoriesAdapter adapter = new CategoriesAdapter(context, viewModel.getData().getValue());
        categories.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        categories.setLayoutManager(layoutManager);

        return view;
    }
}
