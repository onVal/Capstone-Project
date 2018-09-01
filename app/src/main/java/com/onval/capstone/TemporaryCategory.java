package com.onval.capstone;

public class TemporaryCategory {
    private String color;
    private String name;
    private int recordings;
    private boolean autoUploading;

    public TemporaryCategory(String color, String name, int recordings, boolean autoupload) {
        this.color = color;
        this.name = name;
        this.recordings = recordings;
        this.autoUploading = autoupload;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public int getRecordings() {
        return recordings;
    }

    public boolean isAutoUploading() {
        return autoUploading;
    }
}
