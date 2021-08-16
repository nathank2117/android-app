package com.appisoft.iperkz.entity;


import java.util.HashMap;
import java.util.Map;

public class DeliveryOptions {

    private Boolean deliveryEnabled;
    private String deliveryBy;
    private DeliveryByStore deliveryByStore;

    public Boolean getDeliveryEnabled() {
        return deliveryEnabled;
    }

    public void setDeliveryEnabled(Boolean deliveryEnabled) {
        this.deliveryEnabled = deliveryEnabled;
    }

    public String getDeliveryBy() {
        return deliveryBy;
    }

    public void setDeliveryBy(String deliveryBy) {
        this.deliveryBy = deliveryBy;
    }

    public DeliveryByStore getDeliveryByStore() {
        return deliveryByStore;
    }

    public void setDeliveryByStore(DeliveryByStore store) {
        this.deliveryByStore = store;
    }

}
