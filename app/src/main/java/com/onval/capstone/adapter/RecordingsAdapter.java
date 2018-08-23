package com.onval.capstone.adapter;

import android.content.Context;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.RecordingVH> {
    private Context context;

    public RecordingsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecordingVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_rec, parent, false);
        return new RecordingVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordingVH holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class RecordingVH extends RecyclerView.ViewHolder {
        @BindView(R.id.cloud_icon) ImageView icon;
        @BindView(R.id.recording_name) TextView name;
        @BindView(R.id.recording_time) TextView time;
        @BindView(R.id.recording_duration) TextView duration;

        final Drawable cloudAutouploadingIconOff =
                ContextCompat.getDrawable(context, R.drawable.ic_cloud_upload_off);


        RecordingVH(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        //TODO filled with fake data for now
        void bind(int position) {
            switch (position) {
                case 0:
                    name.setText("First meeting");
                    time.setText("12/03/1991 - 12:01");
                    duration.setText("02:33:18");
                    break;
                case 1:
                    name.setText("Second meeting");
                    time.setText("12/03/1992 - 18:33");
                    duration.setText("01:13:00");
                    break;
                case 2:
                    name.setText("Another meeting with Carlos");
                    time.setText("12/03/2010 - 17:21");
                    duration.setText("00:54:26");
                    break;
            }

            icon.setImageDrawable(cloudAutouploadingIconOff);
        }
    }
}
