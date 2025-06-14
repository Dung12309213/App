package com.example.applepie.Model;

// src/main/java/com/example.yourapp/Voucher.java (Thay example.yourapp bằng package của bạn)

public class Voucher {
    private String code;
    private String condition;
    private String discount;

    // Constructor
    public Voucher(String code, String condition, String discount) {
        this.code = code;
        this.condition = condition;
        this.discount = discount;
    }

    // Getters
    public String getCode() {
        return code;
    }

    public String getCondition() {
        return condition;
    }

    public String getDiscount() {
        return discount;
    }

    // Setters (Tùy chọn, nếu bạn cần thay đổi dữ liệu sau khi tạo đối tượng)
    public void setCode(String code) {
        this.code = code;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
