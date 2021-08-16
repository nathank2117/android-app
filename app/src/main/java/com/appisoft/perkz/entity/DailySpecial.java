package com.appisoft.perkz.entity;

import com.appisoft.iperkz.entity.FoodItem;

public class DailySpecial extends FoodItem {

    private boolean isPlaceHolder=false;

    public boolean isPlaceHolder() {
        return isPlaceHolder;
    }

    public void setPlaceHolder(boolean placeHolder) {
        isPlaceHolder = placeHolder;
    }
}
