package com.appisoft.iperkz.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.callback.LoadShoppingCartCallback;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.CustomerEntity;
import com.appisoft.iperkz.entity.ShoppingCart;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.android.PaymentConfiguration;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyApp extends Application {
    public static final String LOCATIONPREFERENCES = "LocPrefs" ;

    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        initializeStripePayment();
    }

    private void initializeStripePayment() {
        PaymentConfiguration.init(
                getApplicationContext(),
                //"pk_live_6ynncLjBPUW8k5fEs7upT7lh00ZCMAWieQ"
               "pk_test_TrG92etxjtc8DPRg5tpoBapk000OD7ugFv"
        );
       //getShoppingCart();
        getShoppingCartFromServer();
    }

    private void getShoppingCartFromServer () {
        SharedPreferences prefs = this.getSharedPreferences("CUSTOMER_DETAILS", Context.MODE_PRIVATE);
        LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
        Data data = Data.getInstance(this.getApplicationContext());
        boolean isCustomerCreated = prefs.getBoolean("CUSTOMER_CREATED", false );
        if (isCustomerCreated) {
            String customerJson = prefs.getString("CUSTOMER", null);
            System.out.println("SHOPPING_CART :" + customerJson);
            ObjectMapper mapper = new ObjectMapper();
            try {
                CustomerEntity entity = mapper.readValue(customerJson, CustomerEntity.class);
                loginRepository.setCustomerEntity(entity);

            } catch (IOException e) {
                e.printStackTrace();
            }

            CronetEngine cronetEngine = Cronet.getCronetEngine(this);
            LoadShoppingCartCallback storeTypesRequestCallback = new LoadShoppingCartCallback(this );
            Executor executor = Executors.newSingleThreadExecutor();
            String url = Data.SERVER_URL + "/api/shoppingcart/load/" + loginRepository.getCustomerEntity().getCustomerId();
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    url, storeTypesRequestCallback, executor);
            UrlRequest request = requestBuilder.build();
            request.start();

        }


    }


    public void getShoppingCart() {
        SharedPreferences sharedpreferences = getSharedPreferences(LOCATIONPREFERENCES, Context.MODE_PRIVATE);
        String shoppingCartString = sharedpreferences.getString("SHOPPING_CART", null);
        Data data = Data.getInstance(this.getApplicationContext());
        ObjectMapper mapper = new ObjectMapper();
        ShoppingCart shoppingCart = null;

        try {
            if (shoppingCartString != null) {
                System.out.println("VVVV:**** 1: loading Shopping cart JSON : " + shoppingCartString );
                shoppingCart = mapper.readValue(shoppingCartString, ShoppingCart.class);
                if (shoppingCart != null ) {
                    System.out.println("VVVV:****: Shopping cart is not null");
                    data.setSelectedMenuItems(shoppingCart.getCartItems());
                    System.out.println("VVVV:**** Loaded records: " + shoppingCart.getCartItems().size());

                } else {
                    System.out.println("VVVV:****Shopping cart IS null");
                }

            } else {
                System.out.println("VVVV: JSON IS null");
            }
        } catch (IOException e) {
            System.out.println("VVVV:**** conversion failed: " + e.toString() ) ;
        }
    }
}
