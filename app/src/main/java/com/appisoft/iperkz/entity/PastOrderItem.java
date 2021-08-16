package com.appisoft.iperkz.entity;

import java.sql.Timestamp;
import java.util.ArrayList;

public class PastOrderItem implements Cloneable {
    private int orderId;
    private String orderTime;
    private String orderStatus;
    private Double totalSalePrice;
    private String paymentMode;
    private String storeName;
    private String storeAddress1;
    private Double salesTax;
    private Double transactionFee;
    private Double perkzDeducted;
    private Double menuAmount;
    private Double perkzRemaining;
    private Double tipAmount;
    private Integer takeOut;
    private String requestedDeliveryDate;
    private Double deliveryAmount;
    private String deliveryAddress;


    private ArrayList<PastOrderDetailItem> details = new ArrayList<>();

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Double getTotalSalePrice() {
        return totalSalePrice;
    }

    public void setTotalSalePrice(Double totalSalePrice) {
        this.totalSalePrice = totalSalePrice;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public ArrayList<PastOrderDetailItem> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<PastOrderDetailItem> details) {
        this.details = details;
    }

    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress1(){
        return storeAddress1;
    }

    public void setStoreAddress1(String storeAddress1) {
        this.storeAddress1 = storeAddress1;
    }

    public Double getMenuAmount() {
        this.menuAmount = this.totalSalePrice;
        return menuAmount;
    }

    public void setMenuAmount(Double menuAmount) {
        this.menuAmount = menuAmount;
    }

    public Double getPerkzDeducted() {
        return perkzDeducted;
    }

    public Double getPerkzRemaining() {
        if (getPerkzDeducted() > 0)
            this.perkzRemaining = 5 - perkzDeducted;
        else
            this.perkzRemaining = 0.00;
        return perkzRemaining;
    }

    public Double getSalesTax() {
        return salesTax;
    }

    public Double getTransactionFee() {
        return transactionFee;
    }

    public void setSalesTax(Double salesTax) {
        this.salesTax = salesTax;
    }

    public void setPerkzDeducted(Double perkzDeducted) {
        this.perkzDeducted = perkzDeducted;
    }

    public void setPerkzRemaining(Double perkzRemaining) {
        this.perkzRemaining = perkzRemaining;
    }

    public void setTransactionFee(Double transactionFee) {
        this.transactionFee = transactionFee;
    }

    public Double getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(Double tipAmount) {
        this.tipAmount = tipAmount;
    }

    public Integer getTakeOut() {
        return takeOut;
    }

    public void setTakeOut(Integer takeOut) {
        this.takeOut = takeOut;
    }

    public String getRequestedDeliveryDate() {
        return requestedDeliveryDate;
    }

    public void setRequestedDeliveryDate(String requestedDeliveryDate) {
        this.requestedDeliveryDate = requestedDeliveryDate;
    }

    public Double getDeliveryAmount() {
        return deliveryAmount;
    }

    public void setDeliveryAmount(Double deliveryAmount) {
        this.deliveryAmount = deliveryAmount;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
