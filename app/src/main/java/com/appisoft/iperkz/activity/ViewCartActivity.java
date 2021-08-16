package com.appisoft.iperkz.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.activity.ui.login.LoginViewModel;
import com.appisoft.iperkz.activity.ui.login.LoginViewModelFactory;
import com.appisoft.iperkz.activity.ui.login.OtpActivity;
import com.appisoft.iperkz.adapter.OrderListAdapter;
import com.appisoft.iperkz.callback.CreateOrderRequestCallback;
import com.appisoft.iperkz.callback.InStoreCheckCallback;
import com.appisoft.iperkz.callback.MenuListServiceRequestCallback;
import com.appisoft.iperkz.converters.MenuSelectionToCustomerOrderConverter;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.FoodItem;
import com.appisoft.iperkz.entity.MenuFilterCriteria;
import com.appisoft.iperkz.entity.OfflinePaymentDetails;
import com.appisoft.iperkz.entity.PaymentDetails;
import com.appisoft.iperkz.entity.Reward;
import com.appisoft.iperkz.entity.SimpleResponse;
import com.appisoft.iperkz.entity.UserLocation;
import com.appisoft.iperkz.entity.uploader.ByteBufferUploadProvider;
import com.appisoft.iperkz.entity.CustomerOrderCreationRequest;
import com.appisoft.iperkz.util.PaymentUtil;
import com.appisoft.iperkz.util.Util;
import com.appisoft.perkz.DisplayMessageActivity;
import com.appisoft.perkz.R;
import com.appisoft.perkz.databinding.ActivityViewCartBinding;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.chromium.net.CronetEngine;
import org.chromium.net.UploadDataProvider;
import org.chromium.net.UrlRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ViewCartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    AlertDialog.Builder builder;
    AlertDialog progressDialog;
    private LoginViewModel loginViewModel;
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    public ProgressBar loadingProgressBar;
    public ProgressBar loadingProgressBar1;
    private Double rewardAmount;
    double claimedReward =0.0;
    TextView rewardsTextAmt ;
    TextView orderTextView ;
    LinearLayout rewardsLT ;
    LinearLayout remainingCreditsLT;
    TextView rewardsRemainingTextAmt;
    private boolean includeTransactionFee;
    LocationManager locationManager;
    LocationListener locationListener;
    UserLocation userLocation;
    View view;
    private String wallpaper;
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 10, locationListener);
        }
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_view_cart);
        ActivityViewCartBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_view_cart);
        //default
        loginRepository.setDineInOptionAllowed(true);

        binding.setMyData(Data.getInstance());
        loadingProgressBar = findViewById(R.id.loading);
        loadingProgressBar1 = findViewById(R.id.loading1);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        rewardsTextAmt = (TextView) findViewById(R.id.rewardsTextAmt);
        orderTextView = (TextView) findViewById(R.id.cartValue);
        rewardsLT = (LinearLayout) findViewById(R.id.rewardsLT);
        remainingCreditsLT = (LinearLayout) findViewById(R.id.remainingCreditLT);
        rewardsRemainingTextAmt = (TextView) findViewById(R.id.remaingingCreditAmt);

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView storeTitle = (TextView) findViewById(R.id.toolbar_title);
        try {
            String title = loginRepository.getCustomerEntity().getStore().getStoreName() + "  ";
            storeTitle.setText(title);

        } catch (Exception e){

        }

        ImageView infoView = (ImageView)findViewById(R.id.infoView);
        ViewCartActivity viewCartActivity = this;
        infoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new StoreDetailsDialogatMenu(loginRepository.getCustomerEntity().getStore());
                newFragment.setCancelable(false);
                newFragment.show((viewCartActivity).getSupportFragmentManager(), "missiles");
            }
        });

        Intent intent = getIntent();
        wallpaper = intent.getStringExtra("wallPaper");

        Bitmap myImage = getBitmapFromURL(wallpaper);
        Drawable dr = new BitmapDrawable(myImage);
        ConstraintLayout linearLayout = (ConstraintLayout) findViewById(R.id.viewcartlt);
        linearLayout.setBackground(dr);

      //  ActionBar actionBar = getSupportActionBar();
   //     actionBar.setDisplayHomeAsUpEnabled(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new OrderListAdapter(Data.getInstance().getOrderedItems(), this);
        recyclerView.setAdapter(mAdapter);

     /*   if(loginRepository.getCustomerEntity().getStore().getStoreTypeId()==2) {
            rewardsLT.setVisibility(View.GONE);
        }*/

       // if(Data.getInstance().getTransactionFee().equalsIgnoreCase("0.0")){
        LinearLayout cardTrasactionView = (LinearLayout) findViewById(R.id.cardTransactionFeeLT);

        if ( loginRepository.getCustomerEntity().getStore().getChargeMode().equalsIgnoreCase("CUSTOMER")
                && (loginRepository.getCustomerEntity().isAllowCardFutureUse()) ) {
            cardTrasactionView.setVisibility(View.VISIBLE);
            includeTransactionFee = true;
        } else {
            cardTrasactionView.setVisibility(View.GONE);
            includeTransactionFee = false;
        }

        loginViewModel.getUseExistingPaymentDetails().observe(this, new Observer<Boolean>() {

            @Override
            public void onChanged(@Nullable Boolean useExistingCard) {
                loadingProgressBar1.setVisibility(View.GONE);
                if (useExistingCard == null) {return; }

                if (useExistingCard == true) {
                    PaymentDetails paymentDetails =
                            PaymentUtil.getInstance().generatePaymentDetails( "CARD",
                            true

                    );
                    loginRepository.getCustomerEntity().setAllowCardFutureUse(true);
                    loadingProgressBar.setVisibility(View.GONE);


                    Intent intent;

                        intent = new Intent(ViewCartActivity.this, PaymentActivity.class);
                        startActivity(intent);


                   /*CustomerOrderCreationRequest order = new MenuSelectionToCustomerOrderConverter()
                            .convert(paymentDetails.getPaymentMode(), paymentDetails.isAllowFutureUse());

                    //CustomerOrderCreationRequest orderCreationRequest = new CustomerOrderCreationRequest();
                    OfflinePaymentDetails offlinePaymentDetails = new OfflinePaymentDetails();
                    offlinePaymentDetails.setOrderCreationRequest(order);
                    offlinePaymentDetails.setPaymentDetails(paymentDetails);
                    loginViewModel.processOfflinePayment(offlinePaymentDetails, ViewCartActivity.this);*/
                    /*
                    progressDialog = PaymentUtil.getInstance().getDialogProgressBar("Need to be implemented..working",
                            ViewCartActivity.this).create();
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    */
                } else  {
                    loginRepository.getCustomerEntity().setAllowCardFutureUse(false);
                    loadingProgressBar.setVisibility(View.GONE);
                    SharedPreferences prefs = getBaseContext().getSharedPreferences("CUSTOMER_DETAILS", Context.MODE_PRIVATE);
                    boolean isRegisteredUser = prefs.getBoolean("REGISTERED_USER", false );
                    Intent intent;
                    if ( isRegisteredUser ) {
                         intent = new Intent(ViewCartActivity.this, PaymentActivity.class);
                    }
                    else{
                     intent = new Intent(ViewCartActivity.this, OtpActivity.class);
                    }
                    startActivity(intent);
                }
                setResult(Activity.RESULT_OK);
            }

        });

        /*loginViewModel.getOfflineProcessResult().observe(this, new Observer<SimpleResponse>() {

            @Override
            public void onChanged(@Nullable SimpleResponse simpleResponse) {
                if (simpleResponse == null) {return; }
                loadingProgressBar.setVisibility(View.GONE);
                if (simpleResponse.isResult() == true) { //Nathan change

                    //clear selections
                    MenuItem[] breakfastList = Data.getInstance().getBreakfastList();
                    MenuItem[] lunchList = Data.getInstance().getLunchList();
                    MenuItem[] allDayList = Data.getInstance().getAllDayList();

                    if ( breakfastList != null) {
                        for (FoodItem item : breakfastList) {
                            item.setSelected(false);
                        }
                    }
                    if ( lunchList != null) {
                        for (FoodItem item : lunchList) {
                            item.setSelected(false);
                        }
                    }
                    if ( allDayList != null) {
                        for (FoodItem item : allDayList) {
                            item.setSelected(false);
                        }
                    }

                    SuccessDialogFragment fragment =
                            SuccessDialogFragment.newInstance(
                                    new String[]{
                                            loginRepository.getCustomerEntity().getFirstName()
                                                    + ", "
                                                    + "Your order is placed."}
                            );

                    fragment.show(ViewCartActivity.this.getSupportFragmentManager(), "test");
                } else  {
                    progressDialog = PaymentUtil.getInstance().getDialogProgressBar(simpleResponse.getMessage(),
                            ViewCartActivity.this).create();
                    progressDialog.setCancelable(true);
                    //progressDialog.get
                    progressDialog.show();
                }
                setResult(Activity.RESULT_OK);
            }
        });*/

        //rewards
        loginViewModel.retrieveRewards(loginRepository.getCustomerEntity(), this);


        CheckBox rewardsCheck = (CheckBox) findViewById(R.id.rewardCheckBox);
        rewardsCheck.setChecked(true);
        Double originalAmount= Data.getInstance().getGrandTotalAsDouble();


        rewardsCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                processRewards(isChecked);
            }
        }
        );
        loginViewModel.getReward().observe(this, new Observer<Reward>() {
            @Override
            public void onChanged(Reward reward) {

                if (reward == null) {
                    if(loginRepository.getCustomerEntity().getPerkzRewards().size()>0) {
                        loginRepository.getCustomerEntity().getPerkzRewards().remove(0);
                    }
                    rewardsLT.setVisibility(View.GONE);
                    remainingCreditsLT.setVisibility(View.GONE);
                    orderTextView.setText(Data.getInstance().grandTotalWithTransFee());
                    return;
                }

                if((reward.getReward()-reward.getClaimedReward())>0) {
                    if((loginRepository.getCustomerEntity().getStore().getStoreTypeId()!=2)&&(loginRepository.getCustomerEntity().getStore().getStoreTypeId()!=4)) {
                        rewardAmount = reward.getReward() - reward.getClaimedReward();
                        claimedReward = reward.getClaimedReward();
                        loginRepository.getCustomerEntity().getPerkzRewards().get(0).setPreviousClaim(reward.getClaimedReward());
                        processRewards(rewardsCheck.isChecked());
                    }
                    else{
                        rewardsLT.setVisibility(View.GONE);
                        remainingCreditsLT.setVisibility(View.GONE);
                        rewardsTextAmt.setText("$ 0.00");
                        //processRewards(false);
                    }

                }
                else{
                    rewardsLT.setVisibility(View.GONE);
                    remainingCreditsLT.setVisibility(View.GONE);
                    rewardsTextAmt.setText("$ 0.00");
                }
            }
        });

/*        //loc instore logic
        userLocation = new UserLocation();
        userLocation.setStoreId(loginRepository.getCustomerEntity().getStoreId());
        locationManager = (LocationManager) this.getSystemService(getApplicationContext().LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try{
                    userLocation.setLongitude(location.getLongitude());
                    userLocation.setLatitude(location.getLatitude());
                    getInStoreResult(userLocation);
                }
                catch(Exception e){
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

        loginViewModel.getInStoreResult().observe(this, new Observer<SimpleResponse>() {
            @Override
            public void onChanged(SimpleResponse simpleResponse) {

                loginRepository.getCustomerEntity().setInstoreCheck(simpleResponse.isResult());
                loginRepository.getCustomerEntity().setGPSEnabled(true);
                loginRepository.getCustomerEntity().setBillable(!simpleResponse.isResult());

                    callPopup();

            }
        });

        //loc logic*/
    }

    private void processRewards(boolean isChecked) {
        Double localClaimedReward=claimedReward;
        Double localOrderAmount =Data.getInstance().getGrandTotalAsDouble();
        Double transactionFee =  Data.getInstance().getTransactionAsDouble();

        Double subtotal = (Data.getInstance().getGrandTotalAsDouble() + transactionFee) -rewardAmount;

        Double originalAmount= Data.getInstance().getGrandTotalAsDouble();
        originalAmount = originalAmount + transactionFee;

        if(!isChecked){
            rewardsTextAmt.setText("$ 0.00");
            Data.getInstance().recalculateTotalCost();
            orderTextView.setText("$ "+Util.getFormattedDollarAmt(originalAmount));
           // orderTextView.setText(Data.getInstance().grandTotalWithTransFee());
            remainingCreditsLT.setVisibility(View.GONE);
        }
        else{
            rewardsTextAmt.setText("- $ " + Util.getFormattedDollarAmt(rewardAmount));

            if(subtotal<0){
                remainingCreditsLT.setVisibility(View.VISIBLE);
                rewardsRemainingTextAmt.setText("- $ " + Util.getFormattedDollarAmt(rewardAmount- originalAmount));
                localClaimedReward = localClaimedReward + Data.getInstance().getGrandTotalAsDouble();
                orderTextView.setText("$ 0.00");
            }
            else{
                if(rewardAmount>0) {
                    localClaimedReward = localClaimedReward + rewardAmount;
                    remainingCreditsLT.setVisibility(View.GONE);
                    orderTextView.setText("$ "+Util.getFormattedDollarAmt(subtotal));
                }

            }

        }
        loginRepository.getCustomerEntity().getPerkzRewards().get(0).setClaimedReward(localClaimedReward);
    }

    private boolean clearCart() {
        Data.getInstance().setSelectedMenuItems(new ArrayList<FoodItem>());
        Data.getInstance().clearTotalCost();
        return true;
    }

    public void moveToHomePage(View view) {
        if ( clearCart() == true) {
            Intent intent = new Intent(this, DisplayMessageActivity.class);
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    private boolean checkIfCartIsEmpty() {
        if (Data.getInstance().getSelectedMenuItems() == null || Data.getInstance().getSelectedMenuItems().size() ==0) {
            return  true;
        }
        return false;
    }

    private void showCaryEmptyMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your have not selected any items for the order")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog

                    }
                });
        // Create the AlertDialog object and return it
        builder.show();
    }


    public void processOrder(View view) {
        loadingProgressBar1.setVisibility(View.VISIBLE);
        if (checkIfCartIsEmpty() == true) {
            showCaryEmptyMessage();
        } else {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.requestPaymentOptions(loginRepository.getCustomerEntity().getCustomerId(), this);
        }

    }

    private void getInStoreResult(UserLocation userLocation){
        loginViewModel.getInStoreResult(userLocation,this);
    }

    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}
