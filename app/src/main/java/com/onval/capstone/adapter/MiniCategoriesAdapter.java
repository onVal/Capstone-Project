package com.onval.capstone.adapter;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onval.capstone.R;
import com.onval.capstone.room.Category;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MiniCategoriesAdapter extends RecyclerView.Adapter<MiniCategoriesAdapter.MiniViewHolder> {
    private Context context;
    private List<Category> categories;
    private int selected, lastSelected;

    public MiniCategoriesAdapter(Context context) {
        this.context = context;
        categories = Collections.emptyList();
        selected = 0;
    }

    @NonNull
    @Override
    public MiniViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_category_mini, parent, false);
        return new MiniViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MiniViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected != position) {
                    lastSelected = selected;
                    selected = position;

                    notifyItemChanged(lastSelected);
                    notifyItemChanged(selected);
                }
            }
        });
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

    public int getSelectedCategoryId() {
        return categories.get(selected).getId();
    }

    class MiniViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.mini_colorLabel) View colorLabel;
        @BindView(R.id.mini_category_name) TextView categoryName;

        MiniViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int position) {
            Category category = categories.get(position);
            colorLabel.setBackgroundColor(Color.parseColor(category.getColor()));
            categoryName.setText(category.getName());

            if (selected == position) {
                categoryName.setTextColor(Color.WHITE);
                itemView.setBackgroundColor(Color.GRAY);
            } else {
                categoryName.setTextColor(Color.BLACK);
                itemView.setBackgroundColor(Color.WHITE);
            }
        }
    }
}
