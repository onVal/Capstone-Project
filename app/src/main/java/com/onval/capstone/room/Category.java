package com.onval.capstone.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = "name", unique = true)})

public class Category {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name; //UNIQUE
    private String color;
    @ColumnInfo(name = "auto_upload")
    private boolean autoUploading;

    public Category(String name, String color, boolean autoUploading) {
        this.name = name;
        this.color = color;
        this.autoUploading = autoUploading;
    }

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
