package com.appisoft.iperkz.entity;

import java.util.List;

public class CustomerEntity {

    private int customerId;
    private String email;
    private String firstName;
    private String middleName = "";
    private String lastName;
    private String company;
    private String phoneNumber;
    private int storeId = -1;
    private String token;
    private Store store;
    private boolean allowCardFutureUse = false;
    private List<Reward> perkzRewards;
    private String udidString;
    private UserLocation userLocation;
    private boolean isBillable;
    private boolean isTakeOut;
    private boolean isGPSEnabled;
    private String tableNumber;
    private boolean instoreCheck;
    private Double tipAmount;
    private String deliveryDate;

    private String deliveryAddress;
    private Double deliveryCharge;
    private int orderCompletionMethod = -1;
    private AppSettings appSettings;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public boolean isAllowCardFutureUse() {
        return allowCardFutureUse;
    }

    public void setAllowCardFutureUse(boolean futureUse) {
        this.allowCardFutureUse = futureUse;
    }

    public List<Reward> getPerkzRewards() {
        return perkzRewards;
    }

    public void setPerkzRewards(List<Reward> perkzRewards) {
        this.perkzRewards = perkzRewards;
    }

    public String getUdidString() {
        return udidString;
    }

    public void setUdidString(String udId) {
        this.udidString = udId;
    }

    public UserLocation getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(UserLocation userLocation) {
        this.userLocation = userLocation;
    }

    public boolean isBillable() {
        return isBillable;
    }

    public void setBillable(boolean billable) {
        isBillable = billable;
    }

    public boolean isTakeOut() {
        return isTakeOut;
    }

    public void setTakeOut(boolean takeOut) {
        isTakeOut = takeOut;
    }

    public boolean isGPSEnabled() {
        return isGPSEnabled;
    }

    public void setGPSEnabled(boolean GPSEnabled) {
        isGPSEnabled = GPSEnabled;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public boolean isInstoreCheck() {
        return instoreCheck;
    }

    public void setInstoreCheck(boolean instoreCheck) {
        this.instoreCheck = instoreCheck;
    }

    public Double getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(Double tipAmount) {
        this.tipAmount = tipAmount;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(Double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public int getOrderCompletionMethod() {
        return orderCompletionMethod;
    }

    public void setOrderCompletionMethod(int orderCompletionMethod) {
        this.orderCompletionMethod = orderCompletionMethod;
    }

    public AppSettings getAppSettings() {
        return appSettings;
    }

    public void setAppSettings(AppSettings appSettings) {
        this.appSettings = appSettings;
    }
}
