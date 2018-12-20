package com.onval.capstone.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.onval.capstone.R;
import com.onval.capstone.activities.RecordingsActivity;
import com.onval.capstone.room.Category;
import com.onval.capstone.utility.GuiUtility;
import com.onval.capstone.utility.Utility;
import com.onval.capstone.viewmodel.CategoriesViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.onval.capstone.activities.RecordingsActivity.CATEGORY_ID;
import static com.onval.capstone.activities.RecordingsActivity.CATEGORY_NAME;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {
    private Context context;
    private List<Category> categories;
    private CategoriesViewModel viewModel;

    private int themedBackgroundColor;
    private int themedPrimaryTextColor;
    private int themedSecondaryTextColor;
    private int dialogTheme;
    private int accentColor;

    private MyActionModeCallback actionModeCallback = new MyActionModeCallback() {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            ArrayList<Category> selectedCatList = new ArrayList<>();

            for (Integer pos : getSelectedPositions())
                selectedCatList.add(categories.get(pos));

            Category[] cArray = selectedCatList.toArray(new Category[selectedCatList.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(context, dialogTheme);
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
    };

    public CategoriesAdapter(Context context, CategoriesViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
        categories = Collections.emptyList();
        setThemedColors();
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

    private void setThemedColors() {
        Resources resources = context.getResources();

        if (GuiUtility.isLightTheme(context)) {
            themedBackgroundColor = Color.WHITE;
            themedPrimaryTextColor = Color.BLACK;
            themedSecondaryTextColor = resources.getColor(R.color.colorSubtextDark);
            dialogTheme = R.style.DialogTheme;
            accentColor = resources.getColor(R.color.colorAccent);

        } else {
            themedBackgroundColor = resources.getColor(R.color.itemBgDark);
            themedPrimaryTextColor = Color.WHITE;
            themedSecondaryTextColor = resources.getColor(R.color.colorSubtextLight);
            dialogTheme = R.style.DialogThemeDark;
            accentColor = resources.getColor(R.color.darkAccent);
        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.constraint_layout) ConstraintLayout layout;
        @BindView(R.id.colorLabel) View colorLabel;
        @BindView(R.id.category_name) TextView categoryName;
        @BindView(R.id.category_subtext) TextView categorySubtext;
        @BindView(R.id.autoupload_icon) ImageView autouploadIcon;
        @BindView(R.id.upload_progress) ProgressBar progressBar;

        final Drawable cloudAutouploadingIconOn = ContextCompat.getDrawable(context, R.drawable.ic_cloud_upload_on);
        final Drawable cloudAutouploadingIconOff = ContextCompat.getDrawable(context, R.drawable.ic_cloud_upload_off);

        CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setThemedColors();
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

            viewModel.getUploadingCategoryIds().observeForever(catIds -> {
                boolean categoryIsUploading = catIds.contains(category.getId());
                showProgressBar(categoryIsUploading);
            });


//            GoogleSignInAccount
            if (Utility.isSignedIn(context)) {
                autouploadIcon.setImageDrawable(
                        (category.isAutoUploading()) ? cloudAutouploadingIconOn : cloudAutouploadingIconOff);
            } else {
                autouploadIcon.setImageDrawable(null);
            }

            // This is to prevent incorrect item selection when RecyclerView does its thing
            if (actionModeCallback.getSelectedPositions().contains(position)) {
                layout.setBackgroundColor(accentColor);
                colorLabel.setBackgroundColor(accentColor);
            } else {
                layout.setBackgroundColor(themedBackgroundColor);
                colorLabel.setBackgroundColor(Color.parseColor(category.getColor()));
            }

            //add listeners
            itemView.setOnLongClickListener(view -> {
                ((AppCompatActivity) view.getContext()).startSupportActionMode(actionModeCallback);
                selectItem(position);
                return true;
            });

            itemView.setOnClickListener(view -> {
                if (actionModeCallback.isMultiselect())
                    selectItem(position);
                else {
                    Intent intent = new Intent(context, RecordingsActivity.class);
                    intent.putExtra(CATEGORY_ID, category.getId());
                    intent.putExtra(CATEGORY_NAME, category.getName());

                    context.startActivity(intent);
                }
            });

            autouploadIcon.setOnLongClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, dialogTheme);
                String msg =  (category.isAutoUploading()) ? "Turn off auto uploading for this category?"
                                                            : "Turn on auto uploading for this category?";

                builder.setTitle("Google Drive Sync")
                        .setMessage(msg)
                        .setPositiveButton(android.R.string.yes, (d, w) -> {
                            boolean autouploadIsSet = !category.isAutoUploading();

                            if (autouploadIsSet)
                                viewModel.uploadRecordings(category.getId());

                            //todo: this should be done after upload recording finishes
                            category.setAutoUploading(autouploadIsSet);
                            viewModel.updateCategories(category);

                        })
                        .setNegativeButton(android.R.string.no, null);

                Dialog dialog = builder.create();
                dialog.show();

                return true;
            });
        }

        private void selectItem(Integer position) {
            boolean itemIsSelected = actionModeCallback.selectItemAtPosition(position);

            int categoryLabelColor = Color.parseColor(categories.get(position).getColor());

            layout.setBackgroundColor(itemIsSelected ? accentColor : themedBackgroundColor );
            categoryName.setTextColor(itemIsSelected ? Color.WHITE : themedPrimaryTextColor);
            categorySubtext.setTextColor(itemIsSelected ? Color.WHITE : themedSecondaryTextColor );
            colorLabel.setBackgroundColor(itemIsSelected ? accentColor : categoryLabelColor);
            autouploadIcon.setImageTintList(itemIsSelected ?
                        ColorStateList.valueOf(Color.WHITE)
                        : ColorStateList.valueOf(themedSecondaryTextColor));
        }

        private void showProgressBar(boolean show) {
            progressBar.setVisibility((show) ? View.VISIBLE : View.INVISIBLE);
            autouploadIcon.setVisibility((show) ? View.INVISIBLE : View.VISIBLE);
        }
    }
}
