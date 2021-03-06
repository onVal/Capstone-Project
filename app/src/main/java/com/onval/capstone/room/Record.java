package com.onval.capstone.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(indices = { @Index(value = "name", unique = true),
                    @Index(value = "category_id")},
        foreignKeys = @ForeignKey(entity = Category.class,
                                    parentColumns = "id",
                                    childColumns = "category_id",
                                    onDelete = CASCADE))
public class Record {
    public static final String CLOUD_UPLOADED = "UPLOADED";
    public static final String CLOUD_NOT_UPLOADED = "NOT UPLOADED";

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String duration;

    @ColumnInfo(name="rec_date")
    private String recDate;

    @ColumnInfo(name="rec_time")
    private String recTime;

    private String format;

    @ColumnInfo(name="cloud_status")
    private String cloudStatus;

    @ColumnInfo(name="category_id")
    private int categoryId;

    @Ignore
    public Record(String name, int categoryId) {
        this(name, "", "", "", "", "", categoryId);
    }

    public Record(String name, String duration,
                  String recDate, String recTime, String format, String cloudStatus, int categoryId) {
        this.name = name;
        this.duration = duration;
        this.recDate = recDate;
        this.recTime = recTime;
        this.format = format;
        this.cloudStatus = cloudStatus;
        this.categoryId = categoryId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRecDate() {
        return recDate;
    }

    public void setRecDate(String recDate) {
        this.recDate = recDate;
    }

    public String getRecTime() {
        return recTime;
    }

    public void setRecTime(String recTime) {
        this.recTime = recTime;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getCloudStatus() {
        return cloudStatus;
    }

    public void setCloudStatus(String cloudStatus) {
        this.cloudStatus = cloudStatus;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}

