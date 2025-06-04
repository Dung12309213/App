package com.duung.applepieapp;

public class FaqItem {
    private String question;
    private String answer;
    private String category;
    private boolean expanded;

    public FaqItem(String question, String answer, String category) {
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.expanded = false;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
