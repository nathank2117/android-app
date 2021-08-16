package com.appisoft.iperkz.entity;

public class UpdateCustomerAttributesRequest {
    private CustomerAttributes customerAttributes;
    private int customerId;
    private StoreSummary storeSummary;

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public CustomerAttributes getCustomerAttributes() {
        return customerAttributes;
    }

    public void setCustomerAttributes(CustomerAttributes customerAttributes) {
        this.customerAttributes = customerAttributes;
    }

    public StoreSummary getStoreSummary() {
        return storeSummary;
    }

    public void setStoreSummary(StoreSummary storeSummary) {
        this.storeSummary = storeSummary;
    }
}