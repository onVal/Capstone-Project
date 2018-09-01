package com.onval.capstone.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.onval.capstone.TemporaryCategory;
import com.onval.capstone.R;

import java.util.ArrayList;
import java.util.List;

public class CategoriesViewModel extends AndroidViewModel {
    private Application application;

    public CategoriesViewModel(Application application) {
        super(application);
        this.application = application;
    }

    public LiveData<List<TemporaryCategory>> getData() {
        String[] colors = application.getBaseContext()
                .getResources().getStringArray(R.array.category_colors);

        ArrayList<TemporaryCategory> categories = new ArrayList<>();

        categories.add(new TemporaryCategory(colors[0], "Mathematics", 78, false));
        categories.add(new TemporaryCategory(colors[1], "History", 18, false));
        categories.add(new TemporaryCategory(colors[2], "Programming", 28, true));
        categories.add(new TemporaryCategory(colors[3], "Operating Systems", 55, false));
        categories.add(new TemporaryCategory(colors[4], "Chinese", 1, true));
        categories.add(new TemporaryCategory(colors[5], "Statistics", 21, false));
        categories.add(new TemporaryCategory(colors[6], "Algebra", 32, true));

        MutableLiveData<List<TemporaryCategory>> data = new MutableLiveData<>();
        data.setValue(categories);
        return data;
    }
}
