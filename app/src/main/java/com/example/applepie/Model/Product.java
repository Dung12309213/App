package com.example.applepie.Model;

import java.util.List;

public class Product {
    private String id;
    private String name;
    private String cateid;
    private int quantity;
    private float rating;
    private String description;
    private String ingredient;
    private String texture;
    private String uses1;
    private String uses2;
    private String uses3;
    private String uses4;
    private String instruction;
    private List<String> imageUrl;

    public Product() {
    }

    public Product(String id, String name, String cateid, int quantity, float rating, String description, String ingredient, String texture, String uses1, String uses2, String uses3, String uses4, String instruction, List<String> imageUrl) {
        this.id = id;
        this.name = name;
        this.cateid = cateid;
        this.quantity = quantity;
        this.rating = rating;
        this.description = description;
        this.ingredient = ingredient;
        this.texture = texture;
        this.uses1 = uses1;
        this.uses2 = uses2;
        this.uses3 = uses3;
        this.uses4 = uses4;
        this.instruction = instruction;
        this.imageUrl = imageUrl;
    }

    // Getter Setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCateid() {
        return cateid;
    }

    public void setCateid(String cateid) {
        this.cateid = cateid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public String getUses1() {
        return uses1;
    }

    public void setUses1(String uses1) {
        this.uses1 = uses1;
    }

    public String getUses2() {
        return uses2;
    }

    public void setUses2(String uses2) {
        this.uses2 = uses2;
    }

    public String getUses3() {
        return uses3;
    }

    public void setUses3(String uses3) {
        this.uses3 = uses3;
    }

    public String getUses4() {
        return uses4;
    }

    public void setUses4(String uses4) {
        this.uses4 = uses4;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }
}