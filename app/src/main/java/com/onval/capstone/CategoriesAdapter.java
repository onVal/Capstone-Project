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
            String[] allColors = context.getResources().getStringArray(R.array.category_colors);

            switch (position) {
                case 0:
                    colorLabel.setBackgroundColor(Color.parseColor(allColors[position]));
                    categoryName.setText("Mathematics");
                    categorySubtext.setText("79 recordings");
                    autouploadIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cloud_upload_off));

                    break;
                case 1:
                    colorLabel.setBackgroundColor(Color.parseColor(allColors[position]));
                    categoryName.setText("History");
                    categorySubtext.setText("63 recordings");
                    autouploadIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cloud_upload_on));
                    break;
                case 2:
                    colorLabel.setBackgroundColor(Color.parseColor(allColors[position]));
                    categoryName.setText("Memos");
                    categorySubtext.setText("28 recordings");
                    autouploadIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cloud_upload_off));
                    break;
                case 3:
                    colorLabel.setBackgroundColor(Color.parseColor(allColors[position]));
                    categoryName.setText("Software Engineering");
                    categorySubtext.setText("33 recordings");
                    autouploadIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cloud_upload_on));
                    break;
                case 4:
                    colorLabel.setBackgroundColor(Color.parseColor(allColors[position]));
                    categoryName.setText("Programming");
                    categorySubtext.setText("14 recordings");
                    autouploadIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cloud_upload_on));
                    break;
                case 5:
                    colorLabel.setBackgroundColor(Color.parseColor(allColors[position]));
                    categoryName.setText("Logic");
                    categorySubtext.setText("40 recordings");
                    autouploadIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cloud_upload_off));
                    break;
                case 6:
                    colorLabel.setBackgroundColor(Color.parseColor(allColors[position]));
                    categoryName.setText("Chinese II");
                    categorySubtext.setText("100.000.000 recordings");
                    break;

            }
        }
    }
}
