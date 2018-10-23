package com.onval.capstone.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.onval.capstone.utility.UserInterfaceUtility;
import com.onval.capstone.utility.Utility;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.RecordingVH> {
    private Context context;
    private List<Record> recordings;

    private int currentlySelected;
    private String categoryColor;

    private RecordingListener listener;

    public interface RecordingListener {
        void onRecordingClicked(Uri recUri, int selectedRec, Record recording);
    }

    public RecordingsAdapter(Context context, int selectedRecording) {
        this.context = context;
        listener = (RecordingListener) context;
        recordings = Collections.emptyList();
        currentlySelected = selectedRecording;
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
        return (recordings == null) ? 0 : recordings.size();
    }

    public void setRecordings(List<Record> recordings) {
        this.recordings = recordings;
        notifyDataSetChanged();
    }

    public void setColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }

    public void setSelected(int position) {
        currentlySelected = position;
        notifyDataSetChanged();
    }

    public class RecordingVH extends RecyclerView.ViewHolder {
        @BindView(R.id.cloud_icon) ImageView cloud_icon;
        @BindView(R.id.recording_name) TextView name;
        @BindView(R.id.recording_time) TextView time;
        @BindView(R.id.recording_duration) TextView duration;

        final Drawable cloudAutouploadingIconOff =
                ContextCompat.getDrawable(context, R.drawable.ic_cloud_upload_off);

        RecordingVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        //TODO filled with fake data for now
        void bind(int position) {
            Record recording = recordings.get(position);

            name.setText(recording.getName());
            time.setText(recording.getRecDate() + " - " + recording.getRecTime());
            duration.setText(recording.getDuration());

            cloud_icon.setImageDrawable(cloudAutouploadingIconOff);

            if (position != currentlySelected)
                selectToPlay(false);
            else
                selectToPlay(true);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != currentlySelected) {
                        currentlySelected = position;
                        selectToPlay(true);
                        notifyDataSetChanged();

                        Uri recUri = Utility.createUriFromRecording(context, recording);
                        listener.onRecordingClicked(recUri, currentlySelected, recording);
                    }
                }
            });
        }

        private void selectToPlay(boolean selected) {
            int LITEGRAY = Color.parseColor("#eeeeee");

            int darkenedColor = UserInterfaceUtility.darkenColor(Color.parseColor(categoryColor), 0.7f);
            int bgColor = (selected) ? darkenedColor : Color.WHITE;
            int textColor = (selected) ? Color.WHITE : Color.BLACK;
            int subColor = (selected) ? LITEGRAY : Color.DKGRAY;

            itemView.setBackgroundColor(bgColor);
            name.setTextColor(textColor);
            time.setTextColor(subColor);
            duration.setTextColor(subColor);
            cloud_icon.setImageTintList(ColorStateList.valueOf(textColor));
        }
    }
}
