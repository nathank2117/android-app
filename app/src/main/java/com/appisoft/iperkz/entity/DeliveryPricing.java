package com.appisoft.iperkz.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryPricing {

    private Double maxDistance;
    private Double minDistance;
    private List<DeliveryRules> rules = null;

    public Double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(Double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public Double getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(Double minDistance) {
        this.minDistance = minDistance;
    }

    public List<DeliveryRules> getRules() {
        return rules;
    }

    public void setDeliveryRules(List<DeliveryRules> rules) {
        this.rules = rules;
    }

}