package com.appisoft.iperkz.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreAttributes {

    private List<MealType> mealTypes = null;
    private String startDate;
    private Integer showTips;
    private List<Double> tips = null;
    private String description;
    private String storeFoodType;
    private String storeMealTypes;
    private Double distance;
    private Double ratings;
    private long reviews;
    private String closingMessage;
    private List<Timing> timings = null;
    private List<String> paymentOptions;
    private List<String> serviceOptions;
    private boolean cardOnly;
    private boolean dineInAllowed;
    private Integer allowFutureOrders;
    private DeliveryOptions deliveryOptions;


    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public List<MealType> getMealTypes() {
        return mealTypes;
    }

    public void setMealTypes(List<MealType> mealTypes) {
        this.mealTypes = mealTypes;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Integer getShowTips() {
        return showTips;
    }

    public void setShowTips(Integer showTips) {
        this.showTips = showTips;
    }

    public List<Double> getTips() {
        return tips;
    }

    public void setTips(List<Double> tips) {
        this.tips = tips;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStoreFoodType() {
        return storeFoodType;
    }

    public void setStoreFoodType(String storeFoodType) {
        this.storeFoodType = storeFoodType;
    }

    public String getStoreMealTypes() {
        return storeMealTypes;
    }

    public void setStoreMealTypes(String storeMealTypes) {
        this.storeMealTypes = storeMealTypes;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getRatings() {
        return ratings;
    }

    public void setRatings(Double ratings) {
        this.ratings = ratings;
    }

    public long getReviews() {
        return reviews;
    }

    public void setReviews(long reviews) {
        this.reviews = reviews;
    }

    public String getClosingMessage() {
        return closingMessage;
    }

    public void setClosingMessage(String closingMessage) {
        this.closingMessage = closingMessage;
    }

    public List<Timing> getTimings() {
        return timings;
    }

    public void setTimings(List<Timing> timings) {
        this.timings = timings;
    }

    public List<String> getPaymentOptions() {
        return paymentOptions;
    }

    public void setPaymentOptions(List<String> paymentOptions) {
        this.paymentOptions = paymentOptions;
    }

    public List<String> getServiceOptions() {
        return serviceOptions;
    }

    public void setServiceOptions(List<String> serviceOptions) {
        this.serviceOptions = serviceOptions;
    }

    public boolean isCardOnly() {
        return cardOnly;
    }

    public void setCardOnly(boolean cardOnly) {
        this.cardOnly = cardOnly;
    }

    public boolean isDineInAllowed() {
        return dineInAllowed;
    }

    public void setDineInAllowed(boolean dineInAllowed) {
        this.dineInAllowed = dineInAllowed;
    }

    public Integer isAllowFutureOrders() {
        return allowFutureOrders;
    }

    public void setAllowFutureOrders(Integer allowFutureOrders) {
        this.allowFutureOrders = allowFutureOrders;
    }

    public DeliveryOptions getDeliveryOptions() {
        return deliveryOptions;
    }

    public void setDeliveryOptions(DeliveryOptions deliveryOptions) {
        this.deliveryOptions = deliveryOptions;
    }
}
