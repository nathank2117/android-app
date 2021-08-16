package com.appisoft.iperkz.util;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.appisoft.iperkz.entity.FoodItem;
import com.appisoft.iperkz.entity.MenuItem;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Util {

    public static String getFormattedDollarAmt(Double value) {
        DecimalFormat format  = new DecimalFormat("##.00");

        String formattedStr =  format.format(value);
        if(value >= 0 && value < 1) {
            formattedStr = "0" + formattedStr;
        }
        /*
        String[] formattedStrSplit =  formattedStr.split("[.]");
        if (formattedStrSplit.length == 2) { //has decimal
            if (formattedStrSplit[1].length() == 1) { //has only 1 digit
                formattedStr += "0";
            }
        }

         */
        return formattedStr;
    }

    public static ArrayList<MenuItem> insertNoDuplicates(MenuItem itemToAdd, ArrayList<MenuItem> list) {
        if (list != null ) {
            for (MenuItem item : list ) {
                if (item.getMenuId() == itemToAdd.getMenuId() ) {
                    return list;
                }
            }
            list.add(itemToAdd);
        }
        return list;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static long convertDoubleToLong(Double doubleAmount) {
            DecimalFormat format  = new DecimalFormat("##.00");

            String formattedStr =  format.format(doubleAmount);
            String[] formattedStrSplit =  formattedStr.split("[.]");
            if (formattedStrSplit.length == 2) { //has decimal
                if (formattedStrSplit[1].length() == 1) { //has only 1 digit
                    formattedStr += "0";
                }
            }
            formattedStrSplit =  formattedStr.split("[.]");
            String amount = "";
            for (String s : formattedStrSplit) {
                System.out.println(s);
                amount = amount+s;
            }
            return Long.parseLong(amount);
    }

    public static String convertDouble2String(Double doubleValue){

        DecimalFormat decimalFormat = new DecimalFormat("0.#####");
        String stringValue = decimalFormat.format(doubleValue);
        return stringValue;
    }
}
