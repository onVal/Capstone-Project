package com.onval.capstone.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.onval.capstone.MyRepository;
import com.onval.capstone.R;
import com.onval.capstone.room.Category;

import java.util.List;

public class CategoriesViewModel extends AndroidViewModel {
    private Application application;
    private LiveData<List<Category>> categories;
    private MyRepository repository;

    public CategoriesViewModel(Application application) {
        super(application);
        this.application = application;
        repository = new MyRepository(application);
    }

    public LiveData<List<Category>> getCategories() {
//        String[] colors = application.getBaseContext()
//                .getResources().getStringArray(R.array.category_colors);

        return repository.getCategories();

//        categories.add(new TemporaryCategory(colors[0], "Mathematics", 78, false));
//        categories.add(new TemporaryCategory(colors[1], "History", 18, false));
//        categories.add(new TemporaryCategory(colors[2], "Programming", 28, true));
//        categories.add(new TemporaryCategory(colors[3], "Operating Systems", 55, false));
//        categories.add(new TemporaryCategory(colors[4], "Chinese", 1, true));
//        categories.add(new TemporaryCategory(colors[5], "Statistics", 21, false));
//        categories.add(new TemporaryCategory(colors[6], "Algebra", 32, true));

    }

    public void insertCategories(Category... categories) {
        repository.insertCategories(categories);
    }

    public LiveData<Integer> getNumOfCategories() {
        return repository.getNumOfCategories();
    }
}
