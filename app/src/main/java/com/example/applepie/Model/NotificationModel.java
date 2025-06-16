package com.example.applepie.Model;

public class NotificationModel {
    private final String title;
    private final String message;
    private final String time;
    private final boolean isRead;
    private final String section; // "Today" or "Yesterday"

    public NotificationModel(String title, String message, String time, boolean isRead, String section) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.isRead = isRead;
        this.section = section;
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public boolean isRead() { return isRead; }
    public String getSection() { return section; }
}
