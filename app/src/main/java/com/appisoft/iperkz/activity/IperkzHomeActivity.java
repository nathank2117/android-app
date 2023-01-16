package com.appisoft.iperkz.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.activity.ui.login.LoginViewModel;
import com.appisoft.iperkz.callback.AppSettingsRequestCallback;
import com.appisoft.iperkz.callback.ImageButtonLoadCallBack;
import com.appisoft.iperkz.callback.ImageLoadCallBack;
import com.appisoft.iperkz.callback.StoreDetailsRequestCallback;
import com.appisoft.iperkz.callback.StoreTypesRequestCallback;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.AppSettings;
import com.appisoft.iperkz.entity.Item;
import com.appisoft.iperkz.entity.Setting;
import com.appisoft.iperkz.entity.StoreTypes;
import com.appisoft.iperkz.entity.UserLocation;
import com.appisoft.perkz.DisplayMessageActivity;
import com.appisoft.perkz.R;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class IperkzHomeActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ProgressBar loadingProgressBar;
    private ProgressBar loadingProgressBar2;
    private ProgressBar loadingProgressBar1;
    private ProgressBar loadingProgressBar3;
    private ProgressBar loadingProgressBar4;

    LocationManager locationManager;
    LocationListener locationListener;
    UserLocation userLocation;
    Boolean popupFlag = true;
    public static final String LOCATIONPREFERENCES = "LocPrefs" ;
    public static final String userPreviousLocation = "nameKey";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());

    private StoreTypes storeTypes;
    private int storeTypeId = -1;
    private String storeTypeName;
    private String wallpaper;
    private Toolbar toolbar;
    private byte[] byteArray;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            else
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
            }
            else{

                TextView textView23 = (TextView) this.findViewById(R.id.text23);
                textView23.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView23.setText("Phone Location services turned off! Please enable or enter address to continue!");
                textView23.setTextColor(Color.RED);
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            }

            loadingProgressBar.setVisibility(View.GONE);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iperkz_home);
        loadingProgressBar = findViewById(R.id.loading);
        loadingProgressBar1 = findViewById(R.id.loading1);
        loadingProgressBar2 = findViewById(R.id.loading2);
        loadingProgressBar3 = findViewById(R.id.loading3);
        loadingProgressBar4 = findViewById(R.id.loading4);
        TextView locationAddress = (TextView) findViewById(R.id.locAddress);
        sharedpreferences = getSharedPreferences(LOCATIONPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();


        String userlocationJson = sharedpreferences.getString("USERLOCATION", null);
        ObjectMapper mapper1 = new ObjectMapper();
        UserLocation userLocationPref = null;
        try {
            if (userlocationJson != null)
                userLocationPref = mapper1.readValue(userlocationJson, UserLocation.class);


        } catch (IOException e) {
            e.printStackTrace();
        }
        TextView textView = (TextView) findViewById(R.id.locAddress);
        ImageView locAddressBtn = (ImageView) findViewById(R.id.locAddressBtn);
        textView.setVisibility(View.GONE);
        locAddressBtn.setVisibility(View.GONE);

        if (userLocationPref != null) {
            popupFlag = false;
            textView.setVisibility(View.VISIBLE);
            locAddressBtn.setVisibility(View.VISIBLE);

            TextView textWarning = (TextView) findViewById(R.id.text23);
            textWarning.setVisibility(View.GONE);
            locationAddress.setText(userLocationPref.getPlace());
            userLocation = userLocationPref;

        }



        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageView infoView = (ImageView)findViewById(R.id.infoView);
        infoView.setVisibility(View.GONE);

        //get Appsettings
        getAppsettings();
        //get storeTypes
        getStroreTypes();


        locAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationPopUp(-1);
            }
        });

        //TextView textView = (TextView) findViewById(R.id.locAddress);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationPopUp(-1);
            }
        });

        locationManager = (LocationManager) this.getSystemService(getApplicationContext().LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationManager.removeUpdates(this);
                loadingProgressBar.setVisibility(View.VISIBLE);
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                //update current location
                TextView locAddressTxt = findViewById(R.id.locAddress);
                locAddressTxt.setText("Current Location");

                TextView gpsWarning = (TextView) findViewById(R.id.text23);
                gpsWarning.setVisibility(View.GONE);

                try {
                    userLocation = new UserLocation();
                    userLocation.setLongitude(location.getLongitude());
                    userLocation.setLatitude(location.getLatitude());
                    userLocation.setTypeId(storeTypeId);
                    userLocation.setSpinnerPosition(4);
                    userLocation.setDistance(25);
                    userLocation.setPlace("Current Location");

                   /* List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (listAddresses != null && listAddresses.size() > 0) {
                        Log.i("place Info", listAddresses.get(0).getAddressLine(0).toString());
                        userLocation.setPlace(listAddresses.get(0).getAddressLine(0).toString().substring(0, listAddresses.get(0).getAddressLine(0).toString().indexOf(',')));
                    }*/
                   /* if(loginRepository.getCustomerEntity()!=null) {
                        loginRepository.getCustomerEntity().setUserLocation(userLocation);
                    }*/

                    //save preferences
                    ObjectMapper mapper = new ObjectMapper();
                    String userLocationJson = "";
                    try {
                        userLocationJson = mapper.writeValueAsString(userLocation);
                    } catch (Exception e) {

                    }
                    editor.putString("USERLOCATION", userLocationJson);
                    editor.commit();
                    //save preferences
                    if(storeTypeId!=-1) {
                        searchStores(storeTypeId, storeTypeName);
                    }
                    loadingProgressBar.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.i("PPK Location", location.toString());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };



    }

    private void getStroreTypes(){
        CronetEngine cronetEngine = Cronet.getCronetEngine(this);
        StoreTypesRequestCallback storeTypesRequestCallback = new StoreTypesRequestCallback(this );
        Executor executor = Executors.newSingleThreadExecutor();
        String url = Data.SERVER_URL + "/api/storetypes";
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                url, storeTypesRequestCallback, executor);
        UrlRequest request = requestBuilder.build();
        request.start();
    }

    public  void setStoreTypes(StoreTypes storeTypes){
        this.storeTypes = storeTypes;
        loginRepository.setStoreTypes(storeTypes);
        CronetEngine cronetEngine = Cronet.getCronetEngine(this);
        int i=0;
        for(Item item : storeTypes.getItems()){
            i++;
            if(i==1){
                CardView cv1 = findViewById(R.id.cv1);

                cv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());
                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });

                cv1.setVisibility(View.VISIBLE);
                ImageButton storeType1 = (ImageButton) findViewById(R.id.storeType1);
                UrlRequest.Callback callbackAllDayImg = new ImageButtonLoadCallBack(storeType1, this);
                Executor executorAllDay = Executors.newSingleThreadExecutor();
                UrlRequest.Builder requestBuilderAllDay = cronetEngine.newUrlRequestBuilder(
                        item.getImageURL(), callbackAllDayImg, executorAllDay);
                UrlRequest requestAllDay = requestBuilderAllDay.build();
                requestAllDay.start();
                TextView tv1 = (TextView) findViewById(R.id.storeTypeText1);
                tv1.setText(item.getStoreTypeName());

                storeType1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingProgressBar1.setVisibility(View.VISIBLE);
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());

                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });

            }
            if(i==2){
                CardView cv2 = findViewById(R.id.cv2);
                cv2.setVisibility(View.VISIBLE);
                cv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());

                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });
                ImageButton storeType2 = (ImageButton) findViewById(R.id.storeType2);
                UrlRequest.Callback callbackAllDayImg = new ImageButtonLoadCallBack(storeType2, this);
                Executor executorAllDay = Executors.newSingleThreadExecutor();
                UrlRequest.Builder requestBuilderAllDay = cronetEngine.newUrlRequestBuilder(
                        item.getImageURL(), callbackAllDayImg, executorAllDay);
                UrlRequest requestAllDay = requestBuilderAllDay.build();
                requestAllDay.start();
                TextView tv2 = (TextView) findViewById(R.id.storeTypeText2);
                tv2.setText(item.getStoreTypeName());

                storeType2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingProgressBar2.setVisibility(View.VISIBLE);
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());

                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });
            }
            if(i==3){
                CardView cv3 = findViewById(R.id.cv3);
                cv3.setVisibility(View.VISIBLE);
                cv3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());

                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });
                ImageButton storeType3 = (ImageButton) findViewById(R.id.storeType3);
                UrlRequest.Callback callbackAllDayImg = new ImageButtonLoadCallBack(storeType3, this);
                Executor executorAllDay = Executors.newSingleThreadExecutor();
                UrlRequest.Builder requestBuilderAllDay = cronetEngine.newUrlRequestBuilder(
                        item.getImageURL(), callbackAllDayImg, executorAllDay);
                UrlRequest requestAllDay = requestBuilderAllDay.build();
                requestAllDay.start();
                TextView tv3 = (TextView) findViewById(R.id.storeTypeText3);
                tv3.setText(item.getStoreTypeName());

                storeType3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingProgressBar3.setVisibility(View.VISIBLE);
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());

                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });
            }
            if(i==4){

                CardView cv4 = findViewById(R.id.cv4);
                cv4.setVisibility(View.VISIBLE);
                cv4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());

                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });
                ImageButton storeType4 = (ImageButton) findViewById(R.id.storeType4);
                UrlRequest.Callback callbackAllDayImg = new ImageButtonLoadCallBack(storeType4, this);
                Executor executorAllDay = Executors.newSingleThreadExecutor();
                UrlRequest.Builder requestBuilderAllDay = cronetEngine.newUrlRequestBuilder(
                        item.getImageURL(), callbackAllDayImg, executorAllDay);
                UrlRequest requestAllDay = requestBuilderAllDay.build();
                requestAllDay.start();
                TextView tv4 = (TextView) findViewById(R.id.storeTypeText4);
                tv4.setText(item.getStoreTypeName());

                storeType4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingProgressBar4.setVisibility(View.VISIBLE);
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());
                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                            //loadingProgressBar4.setVisibility(View.GONE);
                        }
                    }
                });
            }
            if(i==5){
                CardView cv5 = findViewById(R.id.cv5);
                cv5.setVisibility(View.VISIBLE);
                cv5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());
                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });
                ImageButton storeType5 = (ImageButton) findViewById(R.id.storeType5);
                UrlRequest.Callback callbackAllDayImg = new ImageButtonLoadCallBack(storeType5, this);
                Executor executorAllDay = Executors.newSingleThreadExecutor();
                UrlRequest.Builder requestBuilderAllDay = cronetEngine.newUrlRequestBuilder(
                        item.getImageURL(), callbackAllDayImg, executorAllDay);
                UrlRequest requestAllDay = requestBuilderAllDay.build();
                requestAllDay.start();
                TextView tv5 = (TextView) findViewById(R.id.storeTypeText5);
                tv5.setText(item.getStoreTypeName());

                storeType5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());
                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });
            }
            if(i==6){
                CardView cv6 = findViewById(R.id.cv6);
                cv6.setVisibility(View.VISIBLE);
                cv6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());
                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });
                ImageButton storeType6 = (ImageButton) findViewById(R.id.storeType6);
                UrlRequest.Callback callbackAllDayImg = new ImageButtonLoadCallBack(storeType6, this);
                Executor executorAllDay = Executors.newSingleThreadExecutor();
                UrlRequest.Builder requestBuilderAllDay = cronetEngine.newUrlRequestBuilder(
                        item.getImageURL(), callbackAllDayImg, executorAllDay);
                UrlRequest requestAllDay = requestBuilderAllDay.build();
                requestAllDay.start();
                TextView tv6 = (TextView) findViewById(R.id.storeTypeText6);
                tv6.setText(item.getStoreTypeName());

                storeType6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());
                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });
            }
            if(i==7){
                CardView cv7 = findViewById(R.id.cv7);
                cv7.setVisibility(View.VISIBLE);
                cv7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());
                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });
                ImageButton storeType7 = (ImageButton) findViewById(R.id.storeType7);
                UrlRequest.Callback callbackAllDayImg = new ImageButtonLoadCallBack(storeType7, this);
                Executor executorAllDay = Executors.newSingleThreadExecutor();
                UrlRequest.Builder requestBuilderAllDay = cronetEngine.newUrlRequestBuilder(
                        item.getImageURL(), callbackAllDayImg, executorAllDay);
                UrlRequest requestAllDay = requestBuilderAllDay.build();
                requestAllDay.start();
                TextView tv7 = (TextView) findViewById(R.id.storeTypeText7);
                tv7.setText(item.getStoreTypeName());

                storeType7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());
                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });
            }
            if(i==8){
                CardView cv8 = findViewById(R.id.cv8);
                cv8.setVisibility(View.VISIBLE);
                cv8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());
                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });
                ImageButton storeType8 = (ImageButton) findViewById(R.id.storeType8);
                UrlRequest.Callback callbackAllDayImg = new ImageButtonLoadCallBack(storeType8, this);
                Executor executorAllDay = Executors.newSingleThreadExecutor();
                UrlRequest.Builder requestBuilderAllDay = cronetEngine.newUrlRequestBuilder(
                        item.getImageURL(), callbackAllDayImg, executorAllDay);
                UrlRequest requestAllDay = requestBuilderAllDay.build();
                requestAllDay.start();
                TextView tv8 = (TextView) findViewById(R.id.storeTypeText8);
                tv8.setText(item.getStoreTypeName());

                storeType8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());
                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });
            }
            if(i==9){
                CardView cv9 = findViewById(R.id.cv9);
                cv9.setVisibility(View.VISIBLE);
                cv9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());
                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });

                ImageButton storeType9 = (ImageButton) findViewById(R.id.storeType9);
                UrlRequest.Callback callbackAllDayImg = new ImageButtonLoadCallBack(storeType9, this);
                Executor executorAllDay = Executors.newSingleThreadExecutor();
                UrlRequest.Builder requestBuilderAllDay = cronetEngine.newUrlRequestBuilder(
                        item.getImageURL(), callbackAllDayImg, executorAllDay);
                UrlRequest requestAllDay = requestBuilderAllDay.build();
                requestAllDay.start();
                TextView tv9 = (TextView) findViewById(R.id.storeTypeText9);
                tv9.setText(item.getStoreTypeName());

                storeType9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());
                        } else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });
            }
            if(i==10){
                CardView cv10 = findViewById(R.id.cv10);
                cv10.setVisibility(View.VISIBLE);
                cv10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());
                        }
                        else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });
                ImageButton storeType10 = (ImageButton) findViewById(R.id.storeType10);
                UrlRequest.Callback callbackAllDayImg = new ImageButtonLoadCallBack(storeType10, this);
                Executor executorAllDay = Executors.newSingleThreadExecutor();
                UrlRequest.Builder requestBuilderAllDay = cronetEngine.newUrlRequestBuilder(
                        item.getImageURL(), callbackAllDayImg, executorAllDay);
                UrlRequest requestAllDay = requestBuilderAllDay.build();
                requestAllDay.start();
                TextView tv10 = (TextView) findViewById(R.id.storeTypeText10);
                tv10.setText(item.getStoreTypeName());

                storeType10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupFlag) {
                            storeTypeId= item.getStoreTypeId();
                            storeTypeName=item.getStoreTypeName();
                            locationPopUp(item.getStoreTypeId());
                        }
                        else{
                            searchStores(item.getStoreTypeId(), item.getStoreTypeName());
                        }
                    }
                });
            }


        }

    }

    private void locationPopUp(int storeTypeId){
        DialogFragment newFragment = new LocationDialog(this, storeTypeId);
        newFragment.setCancelable(false);
        newFragment.show(getSupportFragmentManager(), "homepage");

    }

    private void searchStores(int storeTypeId, String storeTypeName){
        userLocation.setTypeId(storeTypeId);

        //
        //save preferences
        ObjectMapper mapper = new ObjectMapper();
        String userLocationJson = "";
        try {
            userLocationJson = mapper.writeValueAsString(userLocation);
        } catch (Exception e) {

        }
        editor.putString("USERLOCATION", userLocationJson);
        editor.commit();
        //
        loadingProgressBar.setVisibility(View.GONE);
        Intent intent = new Intent(this, RegistrationNewActivity.class);
        intent.putExtra("storeTypeName",storeTypeName);
        intent.putExtra("wallPaper", wallpaper);
        startActivity(intent);

    }

    private void getAppsettings(){
        CronetEngine cronetEngine = Cronet.getCronetEngine(this);
        AppSettingsRequestCallback storeTypesRequestCallback = new AppSettingsRequestCallback(this );
        Executor executor = Executors.newSingleThreadExecutor();
        String url = Data.SERVER_URL + "/api/appsettings";
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                url, storeTypesRequestCallback, executor);
        UrlRequest request = requestBuilder.build();
        request.start();
    }

    public  void setAppSettings(AppSettings appsettings) {
            String bannerUrl ="";

            List<Setting> settings = appsettings.getSettings();

        for(Setting setting : settings){

            if(setting.getKey().equals("main-banner-android")){
                bannerUrl = setting.getValue();
            }

            if(setting.getKey().equals("main-background")){
                wallpaper = setting.getValue();
            }
        }

        //save preferences
        ObjectMapper mapper = new ObjectMapper();
        String appSettingsJson = "";
        try {
            appSettingsJson = mapper.writeValueAsString(appsettings);
        } catch (Exception e) {

        }
        editor.putString("APPSETTINGS", appSettingsJson);
        editor.commit();
        //



        CronetEngine cronetEngine = Cronet.getCronetEngine(this);
        ImageView view = (ImageView)findViewById(R.id.banner);

        UrlRequest.Callback callback = new ImageLoadCallBack(view, this);
        Executor executor = Executors.newSingleThreadExecutor();
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                bannerUrl, callback, executor);

        UrlRequest request = requestBuilder.build();
        request.start();

        StrictMode.ThreadPolicy policy0 = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy0);
        try {
            URL url = new URL(wallpaper);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(true);
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) connection.getContent());


               //Bitmap bitmap = (Bitmap)response;

            //Bitmap bitmap = BitmapFactory.decodeStream((InputStream) connection.getContent());
             Drawable backgroundImge = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.homelt);
            linearLayout.setBackground(backgroundImge);


        }
        catch(IOException io){

        }


    }

}
