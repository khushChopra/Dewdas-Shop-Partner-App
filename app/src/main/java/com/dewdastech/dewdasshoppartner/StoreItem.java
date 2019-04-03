package com.dewdastech.dewdasshoppartner;

public class StoreItem {
    private String storeItemID;     //  equals push key
    private String brand;          //  equals owners authID
    private String name;
    private String description;
    private String photoURL;
    private float mrp;
    private float price;
    private int stock;

    public StoreItem() {
    }

    @Override
    public String toString() {
        return  brand+" "+name;
    }

    public StoreItem(String storeItemID, String brand, String name, String description, String photoURL, float mrp, float price, int stock) {
        this.storeItemID = storeItemID;
        this.brand = brand;
        this.name = name;
        this.description = description;
        this.photoURL = photoURL;
        this.mrp = mrp;
        this.price = price;
        this.stock = stock;
    }

    public String getStoreItemID() {
        return storeItemID;
    }

    public void setStoreItemID(String storeItemID) {
        this.storeItemID = storeItemID;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public float getMrp() {
        return mrp;
    }

    public void setMrp(float mrp) {
        this.mrp = mrp;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
