package com.example.applepie.Model;

public class Product {
    private String id;
    private String name;
    private String cateid;
    private int price;
    private int secondprice;
    private int quantity;
    private float rating;
    private String description;
    private String ingredient;
    private String texture;
    private String uses;
    private String instruction;
    private int discountPercent;
    private String imageUrl;

    public Product(String name, int price, int secondprice, int discountPercent, String imageUrl) {
        this.name = name;
        this.price = price;
        this.secondprice = secondprice;
        this.discountPercent = discountPercent;
        this.imageUrl = imageUrl;
    }

    public Product() {
    }


    // Getter Setter

    public String getId() {
        return id;
    }

    public float getRating() {
        return rating;
    }

    public String getName() {
        return name;
    }

    public String getCateid() {
        return cateid;
    }

    public int getPrice() {
        return price;
    }

    public int getSecondprice() {
        return secondprice;
    }

    public String getDescription() {
        return description;
    }

    public String getIngredient() {
        return ingredient;
    }

    public String getTexture() {
        return texture;
    }

    public String getUses() {
        return uses;
    }

    public String getInstruction() {
        return instruction;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }
}

