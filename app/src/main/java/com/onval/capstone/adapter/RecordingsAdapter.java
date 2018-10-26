package com.onval.capstone.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onval.capstone.R;

import com.onval.capstone.room.Record;
import com.onval.capstone.utility.UserInterfaceUtility;
import com.onval.capstone.utility.Utility;
import com.onval.capstone.viewmodel.CategoriesViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.RecordingVH> {
    private Context context;
    private List<Record> recordings;

    private int currentlySelected;
    private boolean multiselect;
    private List<Integer> selectedPositions = new ArrayList<>();
    private CategoriesViewModel viewModel;

    private String categoryColor;

    private RecordingListener listener;

    ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.add("Delete");
            multiselect = true;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            ArrayList<Record> selectedRecList = new ArrayList<>();
            Record[] rArray;

            for (Integer pos : selectedPositions) {
                selectedRecList.add(recordings.get(pos));
            }

            rArray = selectedRecList.toArray(new Record[selectedRecList.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
            String msg = "CAUTION: You will lose PERMANENTLY all recordings " +
                    "inside the selected category.";

            builder.setTitle("Delete Categories")
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.yes, (d, w)-> {
                        viewModel.deleteRecordings(rArray);
                    })
                    .setNegativeButton(android.R.string.no, null);

            Dialog dialog = builder.create();
            dialog.show();

            mode.finish();
            return true;

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            selectedPositions = new ArrayList<>();
            multiselect = false;
        }
    };

    public interface RecordingListener {
        void onRecordingClicked(Uri recUri, int selectedRec, Record recording);
    }

    public RecordingsAdapter(Context context, int selectedRecording, ViewModel viewModel) {
        this.context = context;
        this.viewModel = (CategoriesViewModel) viewModel;
        listener = (RecordingListener) context;
        recordings = Collections.emptyList();
        currentlySelected = selectedRecording;
        multiselect = false;
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

        void bind(int position) {
            Record recording = recordings.get(position);

            name.setText(recording.getName());
            time.setText(recording.getRecDate() + " - " + recording.getRecTime());
            duration.setText(recording.getDuration());

            cloud_icon.setImageDrawable(cloudAutouploadingIconOff);

            if (multiselect) {
                if (selectedPositions.contains(position))
                    selectToDelete(true);
                else
                    selectToDelete(false);
            } else {
                if (position != currentlySelected)
                    selectToPlay(false);
                else
                    selectToPlay(true);
            }

            itemView.setOnClickListener((v) -> {
                    if (multiselect) {
                        if (selectedPositions.contains(position)) {
                            selectedPositions.remove(selectedPositions.indexOf(position));
                            selectToDelete(false);
                        }
                        else {
                            selectToDelete(true);
                            selectedPositions.add(position);
                        }
                    } else {
                        if (position != currentlySelected) {
                            currentlySelected = position;
                            selectToPlay(true);
                            notifyDataSetChanged();

                            Uri recUri = Utility.createUriFromRecording(context, recording);
                            listener.onRecordingClicked(recUri, currentlySelected, recording);
                        }
                    }
                });

            itemView.setOnLongClickListener(v -> {
                ((AppCompatActivity) v.getContext()).startSupportActionMode(actionModeCallback);
                selectedPositions.add(position);
                selectToDelete(true);
                return true;
            });
        }

        private void selectToDelete(boolean selected) {
            int LITEGRAY = Color.parseColor("#eeeeee");
            int DEEPBLUE = Color.parseColor("#129fe5");

            int bgColor = (selected) ? DEEPBLUE : Color.WHITE;
            int textColor = (selected) ? Color.WHITE : Color.BLACK;
            int subColor = (selected) ? LITEGRAY : Color.DKGRAY;

            itemView.setBackgroundColor(bgColor);
            name.setTextColor(textColor);
            time.setTextColor(subColor);
            duration.setTextColor(subColor);
            cloud_icon.setImageTintList(ColorStateList.valueOf(textColor));
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
