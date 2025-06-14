package com.example.applepie.Model;

public class CartItem {
    private String name;
    private String size;
    private int quantity;
    private int price;
    private int imageResId;

    public CartItem(String name, String size, int quantity, int price, int imageResId) {
        this.name = name;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getSize() { return size; }
    public int getQuantity() { return quantity; }
    public int getPrice() { return price; }
    public int getImageResId() { return imageResId; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
}
