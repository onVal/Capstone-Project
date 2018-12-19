package com.onval.capstone.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Task;
import com.onval.capstone.R;
import com.onval.capstone.room.Record;
import com.onval.capstone.utility.UserInterfaceUtility;
import com.onval.capstone.utility.Utility;
import com.onval.capstone.viewmodel.RecordingsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.RecordingVH> {
    private Context context;
    private List<Record> recordings;

    private int currentlySelected;
    private RecordingsViewModel viewModel;

    private String categoryColor;

    private RecordingListener listener;
    private DriveResourceClient driveClient;

    private MyActionModeCallback actionModeCallback = new MyActionModeCallback() {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            ArrayList<Record> selectedRecList = new ArrayList<>();

            for (Integer pos : getSelectedPositions())
                selectedRecList.add(recordings.get(pos));

            Record[] rArray = selectedRecList.toArray(new Record[selectedRecList.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
            String msg = "CAUTION: You will lose PERMANENTLY all selected recordings.";

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
    };

    public interface RecordingListener {
        void onRecordingClicked(Uri recUri, int selectedRec, Record recording);
    }

    public RecordingsAdapter(Context context, int selectedRecording, ViewModel viewModel) {
        this.context = context;
        this.viewModel = (RecordingsViewModel) viewModel;
        listener = (RecordingListener) context;
        recordings = Collections.emptyList();
        currentlySelected = selectedRecording;

        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(context.getApplicationContext());
        driveClient = Drive.getDriveResourceClient(context, account);
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
        @BindView(R.id.upload_progress_rec) ProgressBar progressBar;
        @BindView(R.id.recording_name) TextView name;
        @BindView(R.id.recording_time) TextView time;
        @BindView(R.id.recording_duration) TextView duration;

        final Drawable cloudUploadedOff =
                ContextCompat.getDrawable(context, R.drawable.ic_cloud_upload_off);
        final Drawable cloudUploadedOn =
                ContextCompat.getDrawable(context, R.drawable.ic_cloud_upload_on);

        RecordingVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int position) {
            Record recording = recordings.get(position);

            name.setText(recording.getName());
            time.setText(recording.getRecDate() + " - " + recording.getRecTime());
            duration.setText(recording.getDuration());

            Query query = new Query.Builder()
                    .addFilter(Filters.and(Filters.contains(SearchableField.TITLE, String.valueOf(recording.getId())),
                            Filters.eq(SearchableField.TRASHED, false)))
                    .build();

            Task<MetadataBuffer> queryTask = driveClient.query(query);
            queryTask.addOnSuccessListener(metadataBuffer -> {
                if (metadataBuffer.getCount() != 0) {
                    cloud_icon.setImageDrawable(cloudUploadedOn);
                } else {
                    cloud_icon.setImageDrawable(cloudUploadedOff);
                }
            });

                    viewModel.getUploadingRecordingsIds().observeForever(recordings -> {
                        boolean recIsUploading = recordings.contains(recording.getId());
                        showProgressBar(recIsUploading);
                    });

            if (actionModeCallback.isMultiselect())
                multiSelectItem(position);
            else
                selectToPlay(position == currentlySelected);

            itemView.setOnClickListener((v) -> {
                if (actionModeCallback.isMultiselect()) {
                    multiSelectItem(position);
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
                multiSelectItem(position);
                return true;
            });
        }

        private void showProgressBar(boolean isUploading) {
            cloud_icon.setVisibility(isUploading ? View.INVISIBLE : View.VISIBLE);
            progressBar.setVisibility(isUploading ? View.VISIBLE : View.INVISIBLE);
        }

        private void multiSelectItem(Integer position) {
            boolean isSelected = actionModeCallback.selectItemAtPosition(position);

            final int LITEGRAY = Color.parseColor("#eeeeee");
            final int DEEPBLUE = Color.parseColor("#304a60");

            int themedBackground; int themedTextColor; int themedSubColor;

            if (UserInterfaceUtility.getTheme(context).equals("Light")) {
                themedBackground = Color.WHITE;
                themedTextColor = Color.BLACK;
                themedSubColor = Color.DKGRAY;
            } else {
                themedBackground = Color.parseColor("#323232");
                themedTextColor = Color.WHITE;
                themedSubColor = LITEGRAY;
            }


            int bgColor = (isSelected) ? DEEPBLUE : themedBackground;
            int textColor = (isSelected) ? Color.WHITE : themedTextColor;
            int subColor = (isSelected) ? LITEGRAY : themedSubColor;

            itemView.setBackgroundColor(bgColor);
            name.setTextColor(textColor);
            time.setTextColor(subColor);
            duration.setTextColor(subColor);
            cloud_icon.setImageTintList(ColorStateList.valueOf(textColor));
        }

        private void selectToPlay(boolean selected) {
            final int LITEGRAY = Color.parseColor("#eeeeee");

            int themedBackground; int themedTextColor; int themedSubColor;

            if (UserInterfaceUtility.getTheme(context).equals("Light")) {
                themedBackground = Color.WHITE;
                themedTextColor = Color.BLACK;
                themedSubColor = Color.DKGRAY;
            } else {
                themedBackground = Color.parseColor("#323232");
                themedTextColor = Color.WHITE;
                themedSubColor = LITEGRAY;
            }

            // sometimes it crashes here...for some reason
            int darkenedColor = UserInterfaceUtility.darkenColor(Color.parseColor(categoryColor), 0.7f);
            int bgColor = (selected) ? darkenedColor : themedBackground;
            int textColor = (selected) ? Color.WHITE : themedTextColor;
            int subColor = (selected) ? LITEGRAY : themedSubColor;

            itemView.setBackgroundColor(bgColor);
            name.setTextColor(textColor);
            time.setTextColor(subColor);
            duration.setTextColor(subColor);
            cloud_icon.setImageTintList(ColorStateList.valueOf(textColor));
        }
    }
}
