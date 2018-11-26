package com.onval.capstone.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onval.capstone.R;
import com.onval.capstone.activities.RecordingsActivity;
import com.onval.capstone.room.Category;
import com.onval.capstone.utility.Utility;
import com.onval.capstone.viewmodel.CategoriesViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.onval.capstone.activities.RecordingsActivity.CATEGORY_ID;
import static com.onval.capstone.activities.RecordingsActivity.CATEGORY_NAME;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {
    private Context context;
    private List<Category> categories;
    private CategoriesViewModel viewModel;

    private boolean multiselect = false;
    private List<Integer> selectedPositions;

    private Category[] cArray;

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action, menu);
            multiselect = true;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            ArrayList<Category> selectedCatList = new ArrayList<>();

            for (Integer pos : selectedPositions) {
                selectedCatList.add(categories.get(pos));
            }

            cArray = selectedCatList.toArray(new Category[selectedCatList.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
            String msg = "CAUTION: You will lose PERMANENTLY all recordings " +
                        "inside the selected categories.";

            builder.setTitle("Delete Categories")
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.yes, (d, w)-> viewModel.deleteCategories(cArray))
                    .setNegativeButton(android.R.string.no, null);

            Dialog dialog = builder.create();
            dialog.show();

            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiselect = false;
            selectedPositions.clear();
            notifyDataSetChanged();
        }
    };

    public CategoriesAdapter(Context context, CategoriesViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
        categories = Collections.emptyList();
        selectedPositions = new ArrayList<>();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, final int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return (categories == null) ? 0 : categories.size();
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;

        SharedPreferences prefs = context.getSharedPreferences(
                context.getString(R.string.prefs), Context.MODE_PRIVATE);

        notifyDataSetChanged();
    }

    public void sortCategoriesByName() {
        Collections.sort(categories,
                (c1, c2) -> c1.getName().compareTo(c2.getName()));
    }

    public void sortCategoriesById() {
        Collections.sort(categories,
                (c1, c2) -> c1.getId() - (c2.getId()));
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.constraint_layout) ConstraintLayout layout;
        @BindView(R.id.colorLabel) View colorLabel;
        @BindView(R.id.category_name) TextView categoryName;
        @BindView(R.id.category_subtext) TextView categorySubtext;
        @BindView(R.id.autoupload_icon) ImageView autouploadIcon;

        final Drawable cloudAutouploadingIconOn = ContextCompat.getDrawable(context, R.drawable.ic_cloud_upload_on);
        final Drawable cloudAutouploadingIconOff = ContextCompat.getDrawable(context, R.drawable.ic_cloud_upload_off);

        CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int position) {
            Category category = categories.get(position);

            colorLabel.setBackgroundColor(Color.parseColor(category.getColor()));
            categoryName.setText(category.getName());

            viewModel.getRecNumberInCategory(category.getId())
                    .observeForever((Integer num) -> {
                        String subtext = num + ((num == 1) ? " recording" : " recordings");
                        categorySubtext.setText(subtext);
                    });

//            GoogleSignInAccount
            if (Utility.isSignedIn(context)) {
                autouploadIcon.setImageDrawable(
                        (category.isAutoUploading()) ? cloudAutouploadingIconOn : cloudAutouploadingIconOff
                );
            } else {
                autouploadIcon.setImageDrawable(null);
            }

            // This is to prevent incorrect item selection when RecyclerView does its thing
            if (selectedPositions.contains(position)) {
                layout.setBackgroundColor(Color.LTGRAY);
            } else {
                layout.setBackgroundColor(Color.WHITE);
            }

            //add listeners
            itemView.setOnLongClickListener(view -> {
                ((AppCompatActivity) view.getContext()).startSupportActionMode(actionModeCallbacks);
                selectItem(position);
                return true;
            });

            itemView.setOnClickListener(view -> {
                if (multiselect)
                    selectItem(position);
                else {
                    Intent intent = new Intent(context, RecordingsActivity.class);
                    intent.putExtra(CATEGORY_ID, category.getId());
                    intent.putExtra(CATEGORY_NAME, category.getName());

                    context.startActivity(intent);
                }
            });



            autouploadIcon.setOnLongClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
                String msg =  (category.isAutoUploading()) ? "Turn off auto uploading for this category?"
                                                            : "Turn on auto uploading for this category?";

                builder.setTitle("Google Drive Sync")
                        .setMessage(msg)
                        .setPositiveButton(android.R.string.yes, (d, w) -> {
                            category.setAutoUploading(!category.isAutoUploading());
                            viewModel.updateCategories(category);
                            viewModel.uploadRecordings(category.getId());
                        })
                        .setNegativeButton(android.R.string.no, null);

                Dialog dialog = builder.create();
                dialog.show();

                return true;
            });
        }

        private void selectItem(Integer position) {
            if (selectedPositions.contains(position)) {
                selectedPositions.remove(position);
                layout.setBackgroundColor(Color.WHITE);
            } else {
                selectedPositions.add(position);
                layout.setBackgroundColor(Color.LTGRAY);
            }
        }
    }
}
