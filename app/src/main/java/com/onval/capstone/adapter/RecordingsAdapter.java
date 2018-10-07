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
import com.onval.capstone.room.Record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.RecordingVH> {
    private Context context;
    private List<Record> recordings;

    public RecordingsAdapter(Context context) {
        this.context = context;
        recordings = Collections.emptyList();
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

    public void setRecordings(List<Record> recordings) {
        this.recordings = recordings;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (recordings == null) ? 0 : recordings.size();
    }

    class RecordingVH extends RecyclerView.ViewHolder {
        @BindView(R.id.cloud_icon) ImageView cloud_icon;
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
            Record recording = recordings.get(position);

            name.setText(recording.getName());
            time.setText(recording.getRecDate() + " - " + recording.getRecTime());
            duration.setText(recording.getDuration());

            cloud_icon.setImageDrawable(cloudAutouploadingIconOff);
        }
    }
}
