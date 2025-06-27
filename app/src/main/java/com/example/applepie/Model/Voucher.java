package com.example.applepie.Model;

// src/main/java/com/example.yourapp/Voucher.java (Thay example.yourapp bằng package của bạn)

public class Voucher {
    private String id;
    private String code;
    private double amount;
    private String description;

    public Voucher() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
