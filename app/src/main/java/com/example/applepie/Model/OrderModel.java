package com.example.applepie.Model;

public class OrderModel {
    private String orderCode;
    private int itemCount;
    private String totalPrice;
    private int imageResId;
    private String actionText;
    private String orderType;

    public OrderModel(String orderCode, int itemCount, String totalPrice, int imageResId, String actionText, String orderType) {
        this.orderCode = orderCode;
        this.itemCount = itemCount;
        this.totalPrice = totalPrice;
        this.imageResId = imageResId;
        this.actionText = actionText;
        this.orderType = orderType;
    }

    public String getOrderCode() { return orderCode; }
    public int getItemCount() { return itemCount; }
    public String getTotalPrice() { return totalPrice; }
    public int getImageResId() { return imageResId; }
    public String getActionText() { return actionText; }
    public String getOrderType() { return orderType; }
}
