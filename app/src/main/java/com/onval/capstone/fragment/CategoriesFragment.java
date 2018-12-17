package com.onval.capstone.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.onval.capstone.R;
import com.onval.capstone.adapter.CategoriesAdapter;
import com.onval.capstone.viewmodel.CategoriesViewModel;

import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class CategoriesFragment extends Fragment {
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
        prefs = getActivity().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);

        ButterKnife.bind(this, view);

        adapter = new CategoriesAdapter(getActivity(), viewModel);
        boolean sortByName = prefs.getBoolean(getString(R.string.sort_by_name), false);

        categories.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        categories.setLayoutManager(layoutManager);

        viewModel.getCategories().observe(this,
                cats -> {
                    adapter.setCategories(cats != null ? cats : Collections.EMPTY_LIST);
                    if (sortByName) adapter.sortCategoriesByName();
                    else adapter.sortCategoriesById();
                }
        );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
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
