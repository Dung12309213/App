package com.example.applepie.Model;

public class OrderItem {
    private String orderId;
    private String quantity;
    private String price;
    private int imageResId;  // R.drawable.sample_product
    private String actionText;

    public OrderItem(String orderId, String quantity, String price, int imageResId, String actionText) {
        this.orderId = orderId;
        this.quantity = quantity;
        this.price = price;
        this.imageResId = imageResId;
        this.actionText = actionText;
    }

    // Getter methods...
}
