package com.example.applepie.UI;

/**
 * Lớp đại diện cho một mục Câu hỏi thường gặp (FAQ)
 * Gồm: câu hỏi, câu trả lời, danh mục, và trạng thái mở rộng
 */
public class FaqItem {
    // Thuộc tính câu hỏi
    private String question;

    // Thuộc tính câu trả lời
    private String answer;

    // Danh mục (ví dụ: General, Services, Account,...)
    private String category;

    // Cờ trạng thái xem phần trả lời đang mở hay đóng
    private boolean expanded;

    /**
     * Constructor khởi tạo một FAQ mới
     * @param question Câu hỏi
     * @param answer Câu trả lời
     * @param category Danh mục
     */
    public FaqItem(String question, String answer, String category) {
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.expanded = false; // Mặc định chưa mở rộng
    }

    // Getter và setter cho câu hỏi
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    // Getter và setter cho câu trả lời
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    // Getter và setter cho danh mục
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Getter và setter cho trạng thái mở rộng (hiển thị hay ẩn phần trả lời)
    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
