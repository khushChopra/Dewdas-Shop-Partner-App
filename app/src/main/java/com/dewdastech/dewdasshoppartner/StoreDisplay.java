package com.dewdastech.dewdasshoppartner;

import java.util.List;

public class StoreDisplay {
    private String storeID;          //  equals owners authID
    private String storeName;
    private String phoneNumber;
    private String emailID;
    private String ownerName;
    private double latitude;
    private double longitude;
    private String description;
    private String photoURL;
    private String area;


    public StoreDisplay() {
    }

    public StoreDisplay(String storeID, String storeName, String phoneNumber, String emailID, String ownerName, double latitude, double longitude, String description, String photoURL, String area) {
        this.storeID = storeID;
        this.storeName = storeName;
        this.phoneNumber = phoneNumber;
        this.emailID = emailID;
        this.ownerName = ownerName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.photoURL = photoURL;
        this.area = area;
    }

    public StoreDisplay(Store store){
        this.storeID = store.getStoreID();
        this.storeName = store.getStoreName();
        this.phoneNumber = store.getPhoneNumber();
        this.emailID = store.getEmailID();
        this.ownerName = store.getOwnerName();
        this.latitude = store.getLatitude();
        this.longitude = store.getLongitude();
        this.description = store.getDescription();
        this.photoURL = store.getPhotoURL();
        this.area = store.getArea();
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
