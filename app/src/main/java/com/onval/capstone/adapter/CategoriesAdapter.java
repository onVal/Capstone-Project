package com.onval.capstone.adapter;

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

import com.onval.capstone.TemporaryCategory;
import com.onval.capstone.R;
import com.onval.capstone.activities.RecordingsActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>
    implements View.OnClickListener {
    private Context context;
    private List<TemporaryCategory> data;

    public CategoriesAdapter(Context context, List<TemporaryCategory> data) {
        this.context = context;
        this.data = data;
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
        return (data == null) ? 0 : data.size();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, RecordingsActivity.class);
        context.startActivity(intent);
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

        public void bind(int position) {
            TemporaryCategory category = data.get(position);

            colorLabel.setBackgroundColor(Color.parseColor(category.getColor()));
            categoryName.setText(category.getName());

            int recordingNumber = category.getRecordings();
            String subtext = recordingNumber + ((recordingNumber == 1 ) ? " recording" : " recordings");
            categorySubtext.setText(subtext);

            if (category.isAutoUploading()) // todo: i need to check if g.drive is enabled as well
                autouploadIcon.setImageDrawable(cloudAutouploadingIconOn);
            else
                autouploadIcon.setImageDrawable(cloudAutouploadingIconOff);
        }
    }
}
