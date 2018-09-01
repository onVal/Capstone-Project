package com.onval.capstone.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index(value = "name", unique = true)})

public class Category {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name; //UNIQUE
    private String color;
    @ColumnInfo(name = "auto_upload")
    private boolean autoUploading;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isAutoUploading() {
        return autoUploading;
    }

    public void setAutoUploading(boolean autoUploading) {
        this.autoUploading = autoUploading;
    }
}
