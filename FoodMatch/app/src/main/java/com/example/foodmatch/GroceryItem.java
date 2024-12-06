package com.example.foodmatch;

public class GroceryItem {
    private String name;
    private double price;
    private String store;
    private String imageUrl;
    private String key; // Clave única para identificar el elemento en Firebase

    // Constructor vacío para Firebase
    public GroceryItem() {
    }

    public GroceryItem(String name, double price, String store, String imageUrl) {
        this.name = name;
        this.price = price;
        this.store = store;
        this.imageUrl = imageUrl;
    }

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
