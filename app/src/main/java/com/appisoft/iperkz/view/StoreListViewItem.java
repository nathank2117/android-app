package com.appisoft.iperkz.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.appisoft.iperkz.activity.PaymentActivity;
import com.appisoft.iperkz.activity.RegistrationActivity;
import com.appisoft.iperkz.activity.RegistrationNewActivity;
import com.appisoft.iperkz.activity.StoreDetailsDialog;
import com.appisoft.iperkz.activity.TipDialog;
import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.activity.ui.login.LoginViewModel;
import com.appisoft.iperkz.activity.ui.login.LoginViewModelFactory;
import com.appisoft.iperkz.callback.ImageLoadCallBack;
import com.appisoft.iperkz.callback.StoreDetailsRequestCallback;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.CustomerEntity;
import com.appisoft.iperkz.entity.Store;
import com.appisoft.iperkz.entity.Timing;
import com.appisoft.perkz.DisplayMessageActivity;
import com.appisoft.perkz.R;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class StoreListViewItem extends LinearLayout implements View.OnClickListener {
    Store store = null;
    Context context;
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    private LoginViewModel loginViewModel;
    private ProgressBar loadingProgressBar;


    public StoreListViewItem(Context context) {
        super(context);
        this.context = context;
        initControl(context);
    }

    public void setValues(Store store) {
        if(store.getLogo()!=null) {
        this.store = store;
        loadingProgressBar = findViewById(R.id.loadingstore);
        loadingProgressBar.setVisibility(View.GONE);
        loginViewModel = ViewModelProviders.of((FragmentActivity) this.getContext(), new LoginViewModelFactory())
                .get(LoginViewModel.class);
        CronetEngine cronetEngine = Cronet.getCronetEngine(this.getContext());
        ImageView view = (ImageView) findViewById(R.id.imageView);
        //ImageView infoView = (ImageView) findViewById(R.id.infoView) ;

        TextView loggedInStatus = (TextView) findViewById(R.id.storeImageViewText);
        loggedInStatus.setText(store.getStoreName());
            view.setOnClickListener(this);
            if((store.getStoreTypeId()==4) && (store.getStoreStatus().equalsIgnoreCase("OPEN"))){
                store.setLoggedIn(true);
                view.performClick();
            }

            TextView storeImageViewText = (TextView) findViewById(R.id.loggedInStatus);
            for (Timing timing : store.getStoreAttributes().getTimings()) {
                if (timing.getIsToday() == 1) {

                    if (timing.getIsOpen() == 1) {

                        if(!store.isLoggedIn()) {
                            // v.setImageAlpha(30);
                            storeImageViewText.setText("Closed now");
                            //storeImageViewText.setTextColor(Color.RED);
                            storeImageViewText.setVisibility(View.VISIBLE);
                        }


                    } else {
                        //view.setImageAlpha(30);
                        storeImageViewText.setText("Closed now");
                        //storeImageViewText.setTextColor(Color.RED);
                        storeImageViewText.setVisibility(View.VISIBLE);

                    }

                }

            }





            UrlRequest.Callback callback = new ImageLoadCallBack(view, this.getContext());
            Executor executor = Executors.newSingleThreadExecutor();
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    store.getLogo(), callback, executor);

            UrlRequest request = requestBuilder.build();
            request.start();
        /*    Drawable infoIcon = getResources().getDrawable(android.R.drawable.ic_dialog_info);
            infoView.hasOverlappingRendering();
            infoView.setBackground(infoIcon);

            infoView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment newFragment = new StoreDetailsDialog(((RegistrationNewActivity)context), store);
                    newFragment.setCancelable(false);
                    newFragment.show(((RegistrationNewActivity)context).getSupportFragmentManager(), "missiles");
                }
            });*/
        }

    }

    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.store_list_item_frame, this);
    }

    @Override
    public void onClick(View v) {

        TextView storeImageViewText = (TextView) findViewById(R.id.loggedInStatus);
        for (Timing timing : store.getStoreAttributes().getTimings()) {
            if (timing.getIsToday() == 1) {

                if (timing.getIsOpen() == 1) {

                    if(!store.isLoggedIn()) {
                       // v.setImageAlpha(30);
                        storeImageViewText.setText("Closed now");
                        //storeImageViewText.setTextColor(Color.RED);
                        storeImageViewText.setVisibility(View.VISIBLE);
                        DialogFragment newFragment = new StoreDetailsDialog(((RegistrationNewActivity)context), store);
                        newFragment.setCancelable(false);
                        newFragment.show(((RegistrationNewActivity)context).getSupportFragmentManager(), "missiles");
                    }
                    else{
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        callGetStoreDetailsService(this.store.getStoreCode());
                    }

                } else {
                    //view.setImageAlpha(30);
                    storeImageViewText.setText("Closed now");
                    //storeImageViewText.setTextColor(Color.RED);
                    storeImageViewText.setVisibility(View.VISIBLE);
                    DialogFragment newFragment = new StoreDetailsDialog(((RegistrationNewActivity)context), store);
                    newFragment.setCancelable(false);
                    newFragment.show(((RegistrationNewActivity)context).getSupportFragmentManager(), "missiles");

                }

            }

        }


    }



    private void callGetStoreDetailsService(String storeCode) {
        try {

            CronetEngine cronetEngine = Cronet.getCronetEngine(getContext());
            StoreDetailsRequestCallback storeDetailsRequestCallback = new StoreDetailsRequestCallback(getContext());

            Executor executor = Executors.newSingleThreadExecutor();
            String url = Data.SERVER_URL + "/api/store/" + storeCode;
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    url, storeDetailsRequestCallback, executor);
            UrlRequest request = requestBuilder.build();
            request.start();

        } catch (Exception e) {

        }
    }

}
