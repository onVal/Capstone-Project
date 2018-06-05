package com.onval.capstone;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {
    private Context context;

    public CategoriesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(position);
    }

    //TODO: this is a mock method for now, needs proper implementation
    @Override
    public int getItemCount() {
        return 5;
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.colorLabel) View colorLabel;
        @BindView(R.id.category_name) TextView categoryName;
        @BindView(R.id.category_subtext) TextView categorySubtext;
        @BindView(R.id.autoupload_icon) ImageView autouploadIcon;

        CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        //TODO: this is a mock method for now, needs proper implementation
        public void bind(int position) {
            switch (position) {
                case 0:
                    colorLabel.setBackgroundColor(Color.rgb(255, 40, 40));
                    categoryName.setText("Mathematics");
                    categorySubtext.setText("79 recordings");
                    break;
                case 1:
                    colorLabel.setBackgroundColor(Color.rgb(15, 126, 200));
                    categoryName.setText("History");
                    categorySubtext.setText("63 recordings");
                    autouploadIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cloud_autoupload_on));
                    break;
                case 2:
                    colorLabel.setBackgroundColor(Color.rgb(225, 0, 202));
                    categoryName.setText("Memos");
                    categorySubtext.setText("28 recordings");
                    autouploadIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cloud_autoupload_on));
                    break;
                case 3:
                    colorLabel.setBackgroundColor(Color.rgb(30, 70, 235));
                    categoryName.setText("Software Engineering");
                    categorySubtext.setText("33 recordings");
                    break;
                case 4:
                    colorLabel.setBackgroundColor(Color.rgb(2, 170, 67));
                    categoryName.setText("Programming");
                    categorySubtext.setText("14 recordings");
                    autouploadIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cloud_autoupload_on));
                    break;

            }
        }
    }
}
