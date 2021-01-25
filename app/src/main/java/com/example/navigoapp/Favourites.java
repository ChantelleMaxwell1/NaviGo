package com.example.navigoapp;

public class Favourites {

    public String placeName;
    public String placeAddress;
    public String placeLat;
    public String placeLng;
    public String userId;

    public Favourites(){

    }

    public Favourites(String placeName, String placeAddress, String placeLat, String placeLng, String userId) {
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placeLat = placeLat;
        this.placeLng = placeLng;
        this.userId = userId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getPlaceLat() {
        return placeLat;
    }

    public void setPlaceLat(String placeLat) {
        this.placeLat = placeLat;
    }

    public String getPlaceLng() {
        return placeLng;
    }

    public void setPlaceLng(String placeLng) {
        this.placeLng = placeLng;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
