package com.onval.capstone.fragment;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.onval.capstone.activities.SettingsActivity;
import com.onval.capstone.adapter.CategoriesAdapter;
import com.onval.capstone.room.Category;
import com.onval.capstone.viewmodel.CategoriesViewModel;
import com.onval.capstone.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class CategoriesFragment extends Fragment {
    private Context context;
    private CategoriesViewModel viewModel;
    private CategoriesAdapter adapter;
    private SharedPreferences prefs;

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
        setHasOptionsMenu(true);

        viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        LiveData<List<Category>> liveCategories = viewModel.getCategories();

        context = getContext();
        prefs = getActivity().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);

        ButterKnife.bind(this, view);

        adapter = new CategoriesAdapter(context, viewModel);
        boolean sortByName = prefs.getBoolean(getString(R.string.sort_by_name), false);

        categories.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        categories.setLayoutManager(layoutManager);

        liveCategories.observe(this,
                cats -> {
                    adapter.setCategories(cats != null ? cats : Collections.EMPTY_LIST);
                    if (sortByName) adapter.sortCategoriesByName();
                    else adapter.sortCategoriesById();
                }
        );

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        boolean sortByName = prefs.getBoolean(getString(R.string.sort_by_name), false);

        inflater.inflate(R.menu.menu_cat, menu);
        menu.findItem(R.id.action_sort_by_name).setTitle(
                (sortByName) ? getString(R.string.sort_by_creation) : getString(R.string.sort_by_name));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_name:
                toggleSort(item);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void toggleSort(MenuItem item) {
        boolean sortByName = prefs.getBoolean(getString(R.string.sort_by_name), false);

        prefs.edit().putBoolean(getString(R.string.sort_by_name), !sortByName).apply();
        item.setTitle((sortByName) ? getString(R.string.sort_by_name) : getString(R.string.sort_by_creation));

        if (sortByName)
            adapter.sortCategoriesById();
        else
            adapter.sortCategoriesByName();

        adapter.notifyDataSetChanged();
    }
}
