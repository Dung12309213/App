package com.example.applepie.Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatModel {

    private String content;     // Nội dung tin nhắn (text hoặc URI image)
    private boolean isFromBot;  // True nếu là tin nhắn từ bot
    private boolean isImage;
    private long timestamp;// True nếu là ảnh

    // Constructor dùng cho văn bản
    public ChatModel(String content, boolean isFromBot) {
        this.content = content;
        this.isFromBot = isFromBot;
        this.isImage = false;
        this.timestamp = System.currentTimeMillis();
    }

    // Constructor dùng cho ảnh (URI + loại gửi từ ai)
    public ChatModel(String content, boolean isFromBot, boolean isImage) {
        this.content = content;
        this.isFromBot = isFromBot;
        this.isImage = isImage;
        this.timestamp = System.currentTimeMillis();
    }
    public ChatModel(String content, boolean isFromBot, boolean isImage, long timestamp) {
        this.content = content;
        this.isFromBot = isFromBot;
        this.isImage = isImage;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return content;
    }

    public boolean isFromBot() {
        return isFromBot;
    }

    public boolean isImage() {
        return isImage;
    }
    // Getter cho timestamp
    public long getTimestamp() {
        return timestamp;
    }

    // Helper method để định dạng thời gian thành chuỗi (ví dụ: "08:04 pm")
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault()); // "hh" cho 12 giờ, "a" cho AM/PM
        return sdf.format(new Date(timestamp));
    }
}
