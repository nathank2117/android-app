package com.appisoft.iperkz.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class FoodItem implements Cloneable, Serializable {
    private int menuId;
    private String menuCode;
    private int storeId;
    private String menuItemName;
    private String menuItemDesc;
    private Double orgPrice;
    private Double salePrice;
    private String mealType;
    private String mealCategory;
    private Timestamp availableStartTime;
    private Timestamp availableEndTime;
    private String imageUrl;
    private String startTime;
    private String endTime;
    private boolean isSelected = false;
    private String specialInstructions;
    private int timeToMake;
    private int quantity;
    private Integer hasAdditions;
    private List<MenuItemAddition> additions = null;
    private boolean isDailySpecial = false;
    private int availableQuantity;
    private boolean quantityApplicable =false;

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public String getMenuItemDesc() {
        return menuItemDesc;
    }

    public void setMenuItemDesc(String menuItemDesc) {
        this.menuItemDesc = menuItemDesc;
    }

    public Double getOrgPrice() {
        return orgPrice;
    }

    public void setOrgPrice(Double orgPrice) {
        this.orgPrice = orgPrice;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getMealCategory() {
        return mealCategory;
    }

    public void setMealCategory(String mealCategory) {
        this.mealCategory = mealCategory;
    }

    public Timestamp getAvailableStartTime() {
        return availableStartTime;
    }

    public void setAvailableStartTime(Timestamp availableStartTime) {
        this.availableStartTime = availableStartTime;
    }

    public Timestamp getAvailableEndTime() {
        return availableEndTime;
    }

    public void setAvailableEndTime(Timestamp availableEndTime) {
        this.availableEndTime = availableEndTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getTimeToMake() {
        return timeToMake;
    }

    public void setTimeToMake(int timeToMake) {
        this.timeToMake = timeToMake;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Integer getHasAdditions() {
        return hasAdditions;
    }

    public void setHasAdditions(Integer hasAdditions) {
        this.hasAdditions = hasAdditions;
    }

    public List<MenuItemAddition> getAdditions() {
        return additions;
    }

    public void setAdditions(List<MenuItemAddition> additions) {
        this.additions = additions;
    }

    public boolean isDailySpecial() {
        return isDailySpecial;
    }

    public void setDailySpecial(boolean isDailySpecial) {
        this.isDailySpecial = isDailySpecial;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodItem foodItem = (FoodItem) o;
        return menuId == foodItem.menuId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuId);
    }

    public boolean getQuantityApplicable(){
        return quantityApplicable;
    }
    public void setQuantityApplicable(boolean quantityApplicable) {
        this.quantityApplicable = quantityApplicable;
    }
}
