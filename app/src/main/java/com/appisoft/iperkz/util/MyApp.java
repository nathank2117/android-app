package com.appisoft.iperkz.util;

import android.app.Application;

import com.stripe.android.PaymentConfiguration;

public class MyApp extends Application {
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
    }
}
