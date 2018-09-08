package com.onval.capstone.fragment;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onval.capstone.adapter.CategoriesAdapter;
import com.onval.capstone.room.Category;
import com.onval.capstone.viewmodel.CategoriesViewModel;
import com.onval.capstone.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesFragment extends Fragment {
    Context context;
    private CategoriesViewModel viewModel;

    @BindView(R.id.categories) public RecyclerView categories;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        LiveData<List<Category>> liveCategories = viewModel.getCategories();

        context = getContext();

        ButterKnife.bind(this, view);

        CategoriesAdapter adapter = new CategoriesAdapter(context, viewModel);
        categories.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        categories.setLayoutManager(layoutManager);

        liveCategories.observe(this,
                cats -> adapter.setCategories(cats != null ? cats : Collections.EMPTY_LIST));

//        Category category = new Category("Physics", "#00ff00", false);
//        viewModel.insertCategories(category);

        return view;
    }
}
