package com.duung.applepieapp.model;

public class ProfileOption {
    private int iconResId;
    private String title;

    public ProfileOption(int iconResId, String title) {
        this.iconResId = iconResId;
        this.title = title;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getTitle() {
        return title;
    }
}
