package com.appisoft.iperkz.entity;

public class OfflinePaymentDetails {
    private PaymentDetails paymentDetails;
    private CustomerOrderCreationRequest orderCreationRequest;

    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public CustomerOrderCreationRequest getOrderCreationRequest() {
        return orderCreationRequest;
    }

    public void setOrderCreationRequest(CustomerOrderCreationRequest orderCreationRequest) {
        this.orderCreationRequest = orderCreationRequest;
    }
}
