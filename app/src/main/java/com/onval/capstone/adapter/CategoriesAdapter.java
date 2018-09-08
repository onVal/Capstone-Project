package com.onval.capstone.adapter;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onval.capstone.R;
import com.onval.capstone.activities.RecordingsActivity;
import com.onval.capstone.room.Category;
import com.onval.capstone.viewmodel.CategoriesViewModel;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>
    implements View.OnClickListener {
    private Context context;
    private List<Category> categories;
    private CategoriesViewModel viewModel;

    public CategoriesAdapter(Context context, CategoriesViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
        categories = Collections.emptyList();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_category, parent, false);
        view.setOnClickListener(this);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return (categories == null) ? 0 : categories.size();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, RecordingsActivity.class);
        context.startActivity(intent);
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
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

            LiveData<Integer> recordings = viewModel.getRecNumberInCategory(category.getId());
            recordings.observeForever((Integer r)->{
                    String subtext = r + ((r == 1) ? " recording" : " recordings");
                    categorySubtext.setText(subtext);
            });

            if (category.isAutoUploading()) // todo: i need to check if g.drive is enabled as well
                autouploadIcon.setImageDrawable(cloudAutouploadingIconOn);
            else
                autouploadIcon.setImageDrawable(cloudAutouploadingIconOff);
        }
    }
}
