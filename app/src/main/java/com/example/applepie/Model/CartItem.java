package com.example.applepie.Model;

public class CartItem {
    private String name;
    private String size;
    private int quantity;
    private int unitPrice;
    private int imageResId;

    public CartItem(String name, String size, int quantity, int unitPrice, int imageResId) {
        this.name = name;
        this.size = size;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getSize() { return size; }
    public int getQuantity() { return quantity; }
    public int getUnitPrice() { return unitPrice; }
    public int getImageResId() { return imageResId; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getPrice() {
        return unitPrice;
    }


    public int getTotalPrice() {
        return unitPrice * quantity;
    }
}
