package com.appisoft.iperkz.entity;

import java.math.BigDecimal;

public class CustomerOrderMenuItemRequest {
    private Integer customerOrderId;
    private Integer menuItemId;
    private String specialInstructions;
    private Double salePrice;
    private Integer count;
    private String additions;
    private boolean isDailySpecial;

    public Integer getCustomerOrderId() {
        return customerOrderId;
    }

    public void setCustomerOrderId(Integer customerOrderId) {
        this.customerOrderId = customerOrderId;
    }

    public Integer getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Integer menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setAdditions(String additions) {
        this.additions = additions;
    }

    public String getAdditions() {
        return additions;
    }

    public boolean isDailySpecial() {
        return isDailySpecial;
    }

    public void setDailySpecial(boolean isDailySpecial) {
        this.isDailySpecial = isDailySpecial;
    }
}
