package com.appisoft.iperkz.entity;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<FoodItem> cartItems = null;
    private int customerId;

    public ArrayList<FoodItem> getCartItems() {
        return (ArrayList<FoodItem>)  cartItems;
    }
    public void setCartItems(List<FoodItem> cartItems) {
        this.cartItems = cartItems;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
