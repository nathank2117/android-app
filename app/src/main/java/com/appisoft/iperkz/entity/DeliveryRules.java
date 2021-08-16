package com.appisoft.iperkz.entity;

import java.util.HashMap;
import java.util.Map;

public class DeliveryRules {

    private Double minAmt;
    private Double maxAmt;
    private Double deliveryCharge;

    public Double getMinAmt() {
        return minAmt;
    }

    public void setMinAmt(Double minAmt) {
        this.minAmt = minAmt;
    }

    public Double getMaxAmt() {
        return maxAmt;
    }

    public void setMaxAmt(Double maxAmt) {
        this.maxAmt = maxAmt;
    }

    public Double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(Double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

}
