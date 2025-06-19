package com.example.applepie.Model;

public class Variant {
    private String id;
    private String variant;
    private int price;
    private int secondprice;
    private int quantity;
    public Variant() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSecondprice() {
        return secondprice;
    }

    public void setSecondprice(int secondprice) {
        this.secondprice = secondprice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
