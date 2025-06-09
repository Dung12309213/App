package com.example.applepie.Model;

public class ChatModel {

    private String content;     // Nội dung tin nhắn (text hoặc URI image)
    private boolean isFromBot;  // True nếu là tin nhắn từ bot
    private boolean isImage;    // True nếu là ảnh

    // Constructor dùng cho văn bản
    public ChatModel(String content, boolean isFromBot) {
        this.content = content;
        this.isFromBot = isFromBot;
        this.isImage = false;
    }

    // Constructor dùng cho ảnh (URI + loại gửi từ ai)
    public ChatModel(String content, boolean isFromBot, boolean isImage) {
        this.content = content;
        this.isFromBot = isFromBot;
        this.isImage = isImage;
    }

    // Getter cho message
    public String getMessage() {
        return content;
    }

    public boolean isFromBot() {
        return isFromBot;
    }

    public boolean isImage() {
        return isImage;
    }
}
