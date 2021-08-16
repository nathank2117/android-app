package com.appisoft.iperkz.entity;

import java.io.Serializable;

public class UserLocation implements Serializable {
    private int distance = 25;
    private double longitude;
    private double latitude;
    private int storeId;
    private String place;
    private int typeId;
    private int spinnerPosition;

    public UserLocation() {
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getDistance() {
        return distance;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getSpinnerPosition() {
        return spinnerPosition;
    }

    public void setSpinnerPosition(int spinnerPosition) {
        this.spinnerPosition = spinnerPosition;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
}
