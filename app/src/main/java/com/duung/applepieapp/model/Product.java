package com.duung.applepieapp.model;

public class Product {
    private String name;
    private String price;
    private String oldPrice;
    private String discountPercent;
    private String imageUrl; // hoặc int drawable nếu bạn dùng ảnh trong resources

    public Product(String name, String price, String oldPrice, String discountPercent, String imageUrl) {
        this.name = name;
        this.price = price;
        this.oldPrice = oldPrice;
        this.discountPercent = discountPercent;
        this.imageUrl = imageUrl;
    }

    // Getter và Setter (nếu cần)
    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getOldPrice() { return oldPrice; }
    public String getDiscountPercent() { return discountPercent; }
    public String getImageUrl() { return imageUrl; }
}

