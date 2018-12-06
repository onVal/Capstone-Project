package com.onval.capstone.adapter;

import android.view.Menu;
import android.view.MenuItem;

import com.onval.capstone.R;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.view.ActionMode;

public abstract class MyActionModeCallback implements ActionMode.Callback {
    private ActionMode mode;
    private boolean multiselect;
    private List<Integer> selectedPositions = new ArrayList<>();

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        this.mode = mode;
        this.mode.getMenuInflater().inflate(R.menu.menu_action, menu);

        multiselect = true;
        return true;
    }

    boolean selectItemAtPosition(Integer position) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position);

            if (selectedPositions.size() == 0)
                mode.finish();

            return false;

        } else {
            selectedPositions.add(position);
            return true;
        }
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        multiselect = false;
        selectedPositions.clear();
    }

    boolean isMultiselect() {
        return multiselect;
    }

    List<Integer> getSelectedPositions() {
        return selectedPositions;
    }
}
