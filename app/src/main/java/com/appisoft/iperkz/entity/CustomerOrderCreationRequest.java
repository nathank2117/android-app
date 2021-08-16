package com.appisoft.iperkz.entity;

import com.appisoft.iperkz.activity.DeliveryDialog;
import com.fasterxml.jackson.annotation.JsonFormat;

import org.chromium.net.UploadDataProvider;
import org.chromium.net.UploadDataSink;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CustomerOrderCreationRequest {
    private Integer storeId;
    private Integer customerId;

    private String orderCreationTime;

    private Date orderReadyTime;
    private Date orderClosedTime;
    private Integer isASAP;
    private Integer isCustomerNotified;
    private String specialInstructions;
    private String orderStatus;
    private Double totalSalePrice;
    private ArrayList<CustomerOrderMenuItemRequest> orderItems;
    private String paymentMode;
    private boolean allowFutureUse;
    private Double tax;
    private Double transactionFee;
    private List<Reward> perkzRewards;
    private boolean isBillable;
    private boolean isTakeOut;
    private String tableNumber;
    private Double tipAmount;
    private String deliveryDate;
    private Double deliveryCharge;
    private String address;
    private int orderCompletionMethod = -1;

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getOrderCreationTime() {
        return orderCreationTime;
    }

    public void setOrderCreationTime(String orderCreationTime) {
        this.orderCreationTime = orderCreationTime;
    }

    public Date getOrderReadyTime() {
        return orderReadyTime;
    }

    public void setOrderReadyTime(Date orderReadyTime) {
        this.orderReadyTime = orderReadyTime;
    }

    public Date getOrderClosedTime() {
        return orderClosedTime;
    }

    public void setOrderClosedTime(Date orderClosedTime) {
        this.orderClosedTime = orderClosedTime;
    }

    public Integer getIsASAP() {
        return isASAP;
    }

    public void setIsASAP(Integer isASAP) {
        this.isASAP = isASAP;
    }

    public Integer getIsCustomerNotified() {
        return isCustomerNotified;
    }

    public void setIsCustomerNotified(Integer isCustomerNotified) {
        this.isCustomerNotified = isCustomerNotified;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
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

    public ArrayList<CustomerOrderMenuItemRequest> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<CustomerOrderMenuItemRequest> orderItems) {
        this.orderItems = orderItems;
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

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(Double transactionFee) {
        this.transactionFee = transactionFee;
    }

    public void setPerkzRewards(List<Reward> perkzRewards) {
        this.perkzRewards = perkzRewards;
    }

    public List<Reward> getPerkzRewards() {
        return perkzRewards;
    }

    public boolean getIsBillable() {
        return isBillable;
    }

    public void setIsBillable(boolean billable) {
        isBillable = billable;
    }

    public boolean getIsTakeOut() {
        return isTakeOut;
    }

    public void setIsTakeOut(boolean takeOut) {
        isTakeOut = takeOut;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
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
    public Double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(Double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getOrderCompletionMethod() {
        return orderCompletionMethod;
    }

    public void setOrderCompletionMethod(int orderCompletionMethod) {
        this.orderCompletionMethod = orderCompletionMethod;
    }
}
