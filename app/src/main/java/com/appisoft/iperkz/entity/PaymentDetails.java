package com.appisoft.iperkz.entity;

public class PaymentDetails {
    private long amount;
    private String currency;
    private int customerId;
    private String paymentMode;
    private boolean allowFutureUse;

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public boolean isAllowFutureUse() {
        return allowFutureUse;
    }

    public void setAllowFutureUse(boolean allowFutureUse) {
        this.allowFutureUse = allowFutureUse;
    }
}
