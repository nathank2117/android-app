package com.appisoft.iperkz.data;

import static com.appisoft.iperkz.activity.RegistrationNewActivity.LOCATIONPREFERENCES;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import android.content.SharedPreferences;
import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.callback.SaveShoppingCartCallback;
import com.appisoft.iperkz.callback.StoreTypesRequestCallback;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.AppSettings;
import com.appisoft.iperkz.entity.FoodItem;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.entity.ShoppingCart;
import com.appisoft.iperkz.entity.ShoppingCartTest;
import com.appisoft.iperkz.entity.uploader.ByteBufferUploadProvider;
import com.appisoft.iperkz.util.Util;
import com.appisoft.perkz.BR;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.chromium.net.CronetEngine;
import org.chromium.net.UploadDataProvider;
import org.chromium.net.UrlRequest;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Data extends BaseObservable {

    private static Data data;
    private Double totalCost = 0.0;
    private Double taxes = 0.0;
    private Double grandTotal = 0.0;
    private Double transactionFee =0.0;

    public static final String BREAKFAST = "BREAKFAST";
    public static final String LUNCH = "LUNCH";
    public static final String DINNER = "DINNER";
    public static final String ALL_DAY = "ALL DAY";
    public static final String PROJECT_NAME = "iPerkz";

    public MenuItem[] breakfastList = null;
    public MenuItem[] lunchList = null;
    public MenuItem[] allDayList = null;
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());

    private ArrayList<FoodItem> selectedMenuItems = new ArrayList<>();
    public static final String ENVIRONMENT = "";
    SharedPreferences.Editor editor;
    private Context context ;
    private SharedPreferences sharedpreferences;

    //public static final String SERVER_URL="http://10.0.2.2:8080";

    //public static final String SERVER_URL="http://192.168.1.233:8080";

  // public static final String SERVER_URL="http://localhost:8080";
   // public static final String SERVER_URL="http://192.168.1.156:8080";
    //public static final String SERVER_URL="http://3.20.229.224:8080";
    //DEV server
    //public static final String SERVER_URL="http://52.14.171.13:8080";
    public static final String SERVER_URL="https://portal.iperkz.com";

    //DEMO server

    //PROD server
    //public static final String SERVER_URL="https://www.iperkz.com";

    public static synchronized Data getInstance() {
         if (data == null) {
             data = new Data();
         }
         return data;
    }

    public static synchronized Data getInstance(Context context) {
        if (data == null) {
            data = new Data();
        }
        data.context = context;
        try {
            data.sharedpreferences = data.context.getSharedPreferences(LOCATIONPREFERENCES, Context.MODE_PRIVATE);
        } catch (Exception e) {

        }
        return data;
    }



    public void saveShoppingCartToDisk() {
        //save preferences

        editor = sharedpreferences.edit();
        ObjectMapper mapper = new ObjectMapper();
        String shoppintCarJson = "";
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setCartItems(selectedMenuItems);

        System.out.println( "VVVV: S: Saving Shopping cart with size" + selectedMenuItems.size());
        try {
            //selectedMenuItems
            shoppintCarJson = mapper.writeValueAsString(shoppingCart);
            System.out.println( "VVVV: S: Saving Shopping cart JSON" + shoppintCarJson);
        } catch (Exception e) {
            System.out.println( "VVVV: S: Saving Shopping cart JSON FAILED");
        }
        editor.putString("SHOPPING_CART", shoppintCarJson);
        editor.commit();
        System.out.println( "VVVV: S: Saving Shopping cart JSON SUCCESS");
    }


    public void saveShoppingCartToServer() {
        CronetEngine cronetEngine = Cronet.getCronetEngine(data.context);
        SaveShoppingCartCallback saveShoppingCartCallback = new SaveShoppingCartCallback(data.context);

        Executor executor = Executors.newSingleThreadExecutor();
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                Data.SERVER_URL + "/api/shoppingcart/save", saveShoppingCartCallback, executor);
        requestBuilder.addHeader("Content-Type", "application/json");
        //requestBuilder.addHeader("content-length", "54138");
        requestBuilder.setHttpMethod("POST");
        ObjectMapper mapper = new ObjectMapper();
        try {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setCustomerId(loginRepository.getCustomerEntity().getCustomerId());
            for (FoodItem fItem : selectedMenuItems) {
                fItem.setStoreId(loginRepository.getCustomerEntity().getStoreId());
            }
            shoppingCart.setCartItems(selectedMenuItems);
            String jsonValue = mapper.writeValueAsString(shoppingCart);
            System.out.println("SHOPPING_CART : JSON " +  jsonValue);
            byte[] bytes = mapper.writeValueAsBytes(shoppingCart);

            UploadDataProvider provider = ByteBufferUploadProvider.create(bytes);
            requestBuilder.setUploadDataProvider(provider, executor);

            UrlRequest request = requestBuilder.build();
            request.start();
        } catch (JsonProcessingException e) {
            System.out.println("SHOPPING_CART :" + e.getMessage() );
        }
    }

    @Bindable
    public String getTotalCost() {
        if (totalCost == 0) {
            return "$ " + "0.00";
        }

        String formatterString = "$ "
                    + Util.getFormattedDollarAmt(totalCost);

        return formatterString;
    }

    public Double getTotalCostAsDouble() {
        return totalCost;
    }

    public void clearTotalCost() {
        totalCost = 0.0;
        double saleTax = loginRepository.getCustomerEntity().getStore().getSalesTaxes();
        taxes = totalCost * saleTax/100;
        grandTotal = totalCost + taxes;
        notifyPropertyChanged(BR.totalCost);
    }

    public void setTotalCost(Double price) {
        totalCost = totalCost + price;
        double saleTax = loginRepository.getCustomerEntity().getStore().getSalesTaxes();

        taxes = totalCost * saleTax/100;
        grandTotal = totalCost + taxes;
        notifyPropertyChanged(BR.totalCost);
    }
    public void refreshTotalCost(Double price) {
        totalCost =  price;
        double saleTax = loginRepository.getCustomerEntity().getStore().getSalesTaxes();
        taxes = totalCost *  saleTax/100;
        Double grandTotal = totalCost + taxes;
        setGrandTotal(grandTotal);
        transactionFee = getTransactionAsDouble();
        notifyPropertyChanged(BR.totalCost);
        notifyPropertyChanged(BR.grandTotal);
        notifyPropertyChanged(BR.taxes);
    }

    @Bindable
    public String getTransactionFee() {

        if (totalCost == 0) {
            return "0.0";
        }

        if (loginRepository.getCustomerEntity().getStore().getChargeMode().equalsIgnoreCase("CUSTOMER")&&(loginRepository.getCustomerEntity().isAllowCardFutureUse())) {
            transactionFee = (grandTotal * loginRepository.getCustomerEntity().getStore().getChargeRate() / 100)
                    + loginRepository.getCustomerEntity().getStore().getTransactionFee();
            String formatterString = "$ "
                    + Util.getFormattedDollarAmt(transactionFee);

            return formatterString;
        }
        else
            return "0.0";
    }

    public void updateAllMenuListSelections() {

    }


    public void recalculateTotalCost() {
        if ( selectedMenuItems != null) {
            totalCost = 0.0;
            for (FoodItem item : selectedMenuItems) {
                totalCost += (item.getSalePrice() * item.getQuantity());
            }
            refreshTotalCost(totalCost);
            saveShoppingCartToDisk();
            saveShoppingCartToServer();
        }
    }

    public void recalculateTotalCostWithoutSaving() {
        if ( selectedMenuItems != null) {
            totalCost = 0.0;
            for (FoodItem item : selectedMenuItems) {
                totalCost += (item.getSalePrice() * item.getQuantity());
            }
            try {
                refreshTotalCost(totalCost);
            } catch (Exception e) {
                System.out.println("VVVV:**** : Refresh total cost exception");
            }
            //saveShoppingCartToDisk();
        }
    }

    @Bindable
    public String getTaxes() {

        if (totalCost == 0) {
            return "";
        }

        String formatterString = "$ "
                + Util.getFormattedDollarAmt(taxes);

        return formatterString;
    }

    @Bindable
    public String getGrandTotal() {
        if (totalCost == 0) {
            return "";
        }
        String formatterString = "$ "
                + Util.getFormattedDollarAmt(grandTotal);

        return formatterString;

    }

    public Double getTransactionAsDouble() {
        if (loginRepository.getCustomerEntity().getStore().getChargeMode().equalsIgnoreCase("CUSTOMER")&&(loginRepository.getCustomerEntity().isAllowCardFutureUse())) {

            transactionFee = (grandTotal * loginRepository.getCustomerEntity().getStore().getChargeRate() / 100)
                    + loginRepository.getCustomerEntity().getStore().getTransactionFee();
        } else {
            transactionFee=0.0;
        }
        return transactionFee;
    }

    public String grandTotalWithTransFee() {
        if (totalCost == 0) {
            return "";
        }

        if (loginRepository.getCustomerEntity().getStore().getChargeMode().equalsIgnoreCase("CUSTOMER")&&(loginRepository.getCustomerEntity().isAllowCardFutureUse())) {

            transactionFee = (grandTotal * loginRepository.getCustomerEntity().getStore().getChargeRate() / 100)
                    + loginRepository.getCustomerEntity().getStore().getTransactionFee();
        }
        else
            transactionFee=0.0;

        String formatterString = "$ "
                + Util.getFormattedDollarAmt(grandTotal+transactionFee);

        return formatterString;

    }

    @Bindable
    public long getGrandTotalAsLong() {

        DecimalFormat format  = new DecimalFormat("##.00");

        String formattedStr =  format.format(grandTotal);
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

    @Bindable
    public Double getGrandTotalAsDouble() {
      return  grandTotal;
    }

    public void setGrandTotal(Double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public MenuItem[] getBreakfastList() {
        return breakfastList;
    }

    public void setBreakfastList(MenuItem[] breakfastList) {
        this.breakfastList = breakfastList;
    }

    public MenuItem[] getLunchList() {
        return lunchList;
    }

    public void setLunchList(MenuItem[] lunchList) {
        this.lunchList = lunchList;
    }

    public MenuItem[] getAllDayList() {
        return allDayList;
    }

    public void setAllDayList(MenuItem[] allDayList) {
        this.allDayList = allDayList;
    }

    public ArrayList<FoodItem> getSelectedMenuItems() {
        return selectedMenuItems;
    }


    public FoodItem[] getOrderedItems() {
        return selectedMenuItems.toArray(new FoodItem[selectedMenuItems.size()]);
    }

    public void setSelectedMenuItems(ArrayList<FoodItem> selectedMenuItems) {
        this.selectedMenuItems = selectedMenuItems;
      // saveShoppingCartToDisk();
    }

    public void removeItemFromAllSelections(int index, FoodItem item) {
        selectedMenuItems.remove(index);
        if (breakfastList != null ) {
            for (MenuItem menuItem : breakfastList) {
                if (menuItem.getMenuId() == item.getMenuId()) {
                    menuItem.setSelected(false);
                }
            }
        }
        if (lunchList != null) {
            for (MenuItem menuItem : lunchList) {
                if (menuItem.getMenuId() == item.getMenuId()) {
                    menuItem.setSelected(false);
                }
            }
        }
        if (allDayList != null) {
            for (MenuItem menuItem : allDayList) {
                if (menuItem.getMenuId() == item.getMenuId()) {
                    menuItem.setSelected(false);
                }
            }
        }
        saveShoppingCartToDisk();
        saveShoppingCartToServer();
    }
}
