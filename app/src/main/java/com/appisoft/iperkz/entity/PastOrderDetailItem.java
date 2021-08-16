package com.appisoft.iperkz.entity;

import java.util.List;

public class PastOrderDetailItem {
    private String menuItemName;
    private String splInstructions;
    private Double salePrice;
    private int quantity;
    private String additionsNames;

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public String getSplInstructions() {
        return splInstructions;
    }

    public void setSplInstructions(String splInstructions) {
        this.splInstructions = splInstructions;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public String getAdditionsNames() {
        return additionsNames;
    }

    public void setAdditionsNames(String additionsNames) {
        this.additionsNames = additionsNames;
    }
}
