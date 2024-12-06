package com.example.foodmatch;

public class Product {
    private String name;
    private double price;
    private double pricePerOunce;
    private String store;
    private String imageUrl;
    private String key;

    public Product() {} // Constructor vacío para Firebase

    public Product(String name, double price, double pricePerOunce, String store, String imageUrl) {
        this.name = name;
        this.price = price;
        this.pricePerOunce = pricePerOunce;
        this.store = store;
        this.imageUrl = imageUrl;
        this.key = key;
    }

    // Getters
    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getPricePerOunce() { return pricePerOunce; }
    public String getStore() { return store; }
    public String getImageUrl() { return imageUrl; }
    public String getKey() { return key; }


}
