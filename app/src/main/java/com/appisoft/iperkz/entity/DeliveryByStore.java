package com.appisoft.iperkz.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryByStore {

    private List<DeliveryPricing> pricing = null;

    public List<DeliveryPricing> getPricing() {
        return pricing;
    }

    public void setPricing(List<DeliveryPricing> pricing) {
        this.pricing = pricing;
    }

}
