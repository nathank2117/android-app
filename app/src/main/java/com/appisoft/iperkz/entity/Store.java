package com.appisoft.iperkz.entity;

import java.util.List;

public class Store {
    private int storeId;
    private String storeName;
    private String storeDesc;
    private String addressOne;
    private String addressTwo;
    private String city;
    private String state;
    private String country;
    private String website;
    private String phone;
    private String fax;
    private String email;
    private String storeStatus;
    private String notes;
    private String storeCode;
    private double salesTaxes;
    private String chargeMode;
    private double chargeRate;
    private double transactionFee;
    private String logo;
    private boolean loggedIn;
    private String firstImageUrl;
    private String secondImageUrl;
    private String thirdImageUrl;
    private String drivingDistance;
    private int storeTypeId;

    private String[] breakfastCategories;
    private String[] lunchCategories;
    private String[] dinnerCategories;
    private String[] allDayCategories;
    private List<MealType> mealTypes;
    private Double latitude;
    private Double longitude;
    private StoreAttributes storeAttributes;
    private boolean cardOnly;


    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreDesc() {
        return storeDesc;
    }

    public void setStoreDesc(String storeDesc) {
        this.storeDesc = storeDesc;
    }

    public String getAddressOne() {
        return addressOne;
    }

    public void setAddressOne(String addressOne) {
        this.addressOne = addressOne;
    }

    public String getAddressTwo() {
        return addressTwo;
    }

    public void setAddressTwo(String addressTwo) {
        this.addressTwo = addressTwo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStoreStatus() {
        return storeStatus;
    }

    public void setStoreStatus(String storeStatus) {
        this.storeStatus = storeStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public double getSalesTaxes() {
        return salesTaxes;
    }

    public void setSalesTaxes(double salesTaxes) {
        this.salesTaxes = salesTaxes;
    }

    public String getChargeMode() {
        return chargeMode;
    }

    public void setChargeMode(String chargeMode) {
        this.chargeMode = chargeMode;
    }

    public double getChargeRate() {
        return chargeRate;
    }

    public void setChargeRate(double chargeRate) {
        this.chargeRate = chargeRate;
    }

    public double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(double transactionFee) {
        this.transactionFee = transactionFee;
    }

    public String[] getBreakfastCategories() {
        return breakfastCategories;
    }

    public void setBreakfastCategories(String[] breakfastCategories) {
        this.breakfastCategories = breakfastCategories;
    }

    public String[] getLunchCategories() {
        return lunchCategories;
    }

    public void setLunchCategories(String[] lunchCategories) {
        this.lunchCategories = lunchCategories;
    }

    public String[] getDinnerCategories() {
        return dinnerCategories;
    }

    public void setDinnerCategories(String[] dinnerCategories) {
        this.dinnerCategories = dinnerCategories;
    }

    public String[] getAllDayCategories() {
        return allDayCategories;
    }

    public void setAllDayCategories(String[] allDayCategories) {
        this.allDayCategories = allDayCategories;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getFirstImageUrl() {
        return firstImageUrl;
    }

    public String getSecondImageUrl() {
        return secondImageUrl;
    }

    public String getThirdImageUrl() {
        return thirdImageUrl;
    }

    public void setFirstImageUrl(String firstImageUrl) {
        this.firstImageUrl = firstImageUrl;
    }

    public void setSecondImageUrl(String secondImageUrl) {
        this.secondImageUrl = secondImageUrl;
    }

    public void setThirdImageUrl(String thirdImageUrl) {
        this.thirdImageUrl = thirdImageUrl;
    }

    public List<MealType> getMealTypes() {
        return mealTypes;
    }

    public void setMealTypes(List<MealType> mealTypes) {
        this.mealTypes = mealTypes;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public StoreAttributes getStoreAttributes() {
        return storeAttributes;
    }

    public void setStoreAttributes(StoreAttributes storeAttributes) {
        this.storeAttributes = storeAttributes;
    }

    public String getDrivingDistance() {
        return drivingDistance;
    }

    public void setDrivingDistance(String drivingDistance) {
        this.drivingDistance = drivingDistance;
    }


    public int getStoreTypeId() {
        return storeTypeId;
    }

    public void setStoreTypeId(int storeTypeId) {
        this.storeTypeId = storeTypeId;
    }
    public boolean isCardOnly() {
        return cardOnly;
    }

    public void setCardOnly(boolean cardOnly) {
        this.cardOnly = cardOnly;

    }
}


