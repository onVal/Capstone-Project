package com.onval.capstone.adapter;

import android.content.Context;
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

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.RecordingVH> {
    private Context context;
    private List<Record> recordings;

    private final static int NO_REC_SELECTED = -1;
    private int currentlySelected;

    private RecordingListener listener;

    public interface RecordingListener {
        void onRecordingClicked(Uri recUri);
    }

    public RecordingsAdapter(Context context) {
        this.context = context;
        listener = (RecordingListener) context;
        recordings = Collections.emptyList();
        currentlySelected = NO_REC_SELECTED;
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

    class RecordingVH extends RecyclerView.ViewHolder {
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
//                        selectToPlay(false);
                        currentlySelected = position;
                        selectToPlay(true);
                        notifyDataSetChanged();

                        int recId = recording.getId();
                        String recName = recording.getName();
                        Uri recUri = createUriFromRecording(recId, recName);
                        listener.onRecordingClicked(recUri);
                    }
                }
            });
        }

        private void selectToPlay(boolean selected) {
            int bgColor = (selected) ? Color.BLUE : Color.WHITE;
            int textColor = (selected) ? Color.WHITE : Color.BLACK;
            int subColor = (selected) ? Color.LTGRAY : Color.DKGRAY;

            itemView.setBackgroundColor(bgColor);
            name.setTextColor(textColor);
            time.setTextColor(subColor);
            duration.setTextColor(subColor);
//            cloud_icon.setColorFilter(subColor);
        }



        private Uri createUriFromRecording(int recId, String recName) {
            String filePath =  context.getExternalCacheDir().getAbsolutePath();
            String recFullName = "/" + recId + "_" + recName.replace(":", "_") + ".mp4";
            return Uri.parse(filePath + recFullName);
        }
    }
}
