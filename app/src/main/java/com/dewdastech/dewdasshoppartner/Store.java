package com.dewdastech.dewdasshoppartner;

public class Store {
    private String storeID;          //  equals owners authID
    private String storeName;
    private String phoneNumber;
    private String emailID;
    private String ownerName;
    private float latitude;
    private float longitude;
    private String description;
    private String photoURL;
    private String area;

    public Store() {
    }

    public Store(String storeID, String storeName, String phoneNumber, String emailID, String ownerName, float latitude, float longitude, String description, String photoURL, String area) {
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

    public String getStoreID() {
        return storeID;
    }

    @Override
    public String toString() {
        return "Store{" +
                "storeID='" + storeID + '\'' +
                ", storeName='" + storeName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailID='" + emailID + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", description='" + description + '\'' +
                ", photoURL='" + photoURL + '\'' +
                ", area='" + area + '\'' +
                '}';
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

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
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
