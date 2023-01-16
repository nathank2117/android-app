package com.appisoft.iperkz.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavArgs;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.activity.data.model.RegistrationFormState;
import com.appisoft.iperkz.activity.ui.login.LoginViewModel;
import com.appisoft.iperkz.activity.ui.login.LoginViewModelFactory;
import com.appisoft.iperkz.callback.CreateCustomAttributeRequestCallback;
import com.appisoft.iperkz.callback.CreateOrderRequestCallback;
import com.appisoft.iperkz.callback.FetchCustomerAttributeRequestCallback;
import com.appisoft.iperkz.converters.MenuSelectionToCustomerOrderConverter;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.AddressDetail;
import com.appisoft.iperkz.entity.CustomerEntity;
import com.appisoft.iperkz.entity.CustomerOrderCreationRequest;
import com.appisoft.iperkz.entity.DeliveryOptions;
import com.appisoft.iperkz.entity.DeliveryPricing;
import com.appisoft.iperkz.entity.DeliveryRules;
import com.appisoft.iperkz.entity.FoodItem;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.entity.OfflinePaymentDetails;
import com.appisoft.iperkz.entity.PaymentDetails;
import com.appisoft.iperkz.entity.Reward;
import com.appisoft.iperkz.entity.SimpleResponse;
import com.appisoft.iperkz.entity.StoreSummary;
import com.appisoft.iperkz.entity.UserLocation;
import com.appisoft.iperkz.entity.uploader.ByteBufferUploadProvider;
import com.appisoft.iperkz.util.DateUtils;
import com.appisoft.iperkz.util.PaymentUtil;
import com.appisoft.iperkz.util.Util;
import com.appisoft.perkz.DisplayMessageActivity;
import com.appisoft.perkz.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import org.chromium.net.CronetEngine;
import org.chromium.net.UploadDataProvider;
import org.chromium.net.UrlRequest;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.appisoft.iperkz.util.DateUtils.getFormattedDateAndTime;
import static com.appisoft.perkz.DisplayMessageActivity.MEAL_TYPE;
import com.google.android.material.textfield.*;

public class PaymentActivity extends AppCompatActivity {
    private String paymentIntentClientSecret = "";
    private Stripe stripe;
    private LoginViewModel loginViewModel;
    private final LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    private final MutableLiveData<RegistrationFormState> loginFormState = new MutableLiveData<>();
    private CardInputWidget cardInputWidget;

    public AlertDialog progressDialog;
    AlertDialog.Builder builder;
    private RadioGroup radioGroup;
    private RadioGroup radioGroupDineIn;
    public RadioButton radioButtonTakeOut;
    private Button payButton;
    private Button orderButton;
    private CheckBox futureUserCheckBox;
    PaymentDetails paymentDetails = new PaymentDetails();
    private TextView transactionFeeMessg;
    CustomerEntity customerItem = new CustomerEntity();
    private EditText tableNumber;
    private Double customerTip = 0.0d;
    private TextView tip1Amt;
    private TextView tip2Amt;
    private TextView tip3Amt;
    private LinearLayout noTip;
    private LinearLayout Tip1Lt;
    private LinearLayout Tip2Lt;
    private LinearLayout Tip3Lt;
    private LinearLayout Tip4Lt;
    private Boolean tipFlag = true;
    ReviewManager manager;
    ReviewInfo reviewInfo;

    private TextInputEditText splInstructions;
    private RadioButton deliveryRadioBtn;
    private ArrayList<String> arrayList;

    private ArrayAdapter<String> arrayAdapter;
    private List<AddressDetail> addressDetailsList;
    private Double deliveryOrderAmt;
    public Spinner addressSpinner;
    private Double deliveryCharge = 0.00d;
    private Double deliveryChargeCalculated = 0.0d;

    LocationManager locationManager;
    LocationListener locationListener;
    UserLocation userLocation;
    public ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        String isPerkzUsed = getIntent().getStringExtra("isPerkzUsed");
        System.out.println(" ******************** ");
        System.out.println("isPerkzUsed: " + isPerkzUsed);
        if (isPerkzUsed != null && isPerkzUsed.length() > 0 ) {
            System.out.println("1");
            if ( isPerkzUsed.contains("A"))
            {
                System.out.println("2");
                if (loginRepository.reward != null && loginRepository.reward.getRewardType().equalsIgnoreCase("APP_DOWNLOAD")) {
                    loginRepository.reward = null;
                    System.out.println("3");
                }
            }

            if ( isPerkzUsed.contains("B"))
            {
                System.out.println("4");
                if (loginRepository.reward != null && loginRepository.reward.getRewardType().equalsIgnoreCase("FIRST_GROCERY_ORDER")) {
                    loginRepository.reward = null;
                    System.out.println("5");
                }
            }
            PerkzRedeemedDialog newFragment =
                    new PerkzRedeemedDialog( "Welcome Back",
                            "Few Perkz for this account have already been redeemed",
                            "Close", this);
            newFragment.setCancelable(false);
            newFragment.show(getSupportFragmentManager(), "Welcome back");
            System.out.println("6");
        }
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                reviewInfo = task.getResult();
            } else {
                // There was some problem, continue regardless of the result.
            }
        });

        progressDialog = getDialogProgressBar("You order is being placed...", this).create();
        progressDialog.setCancelable(false);

        loginFormState.setValue(new RegistrationFormState(false));
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final TextView tableLabel = findViewById(R.id.tableLabel);
        tableNumber = findViewById(R.id.tableNumber);
        final EditText regCustFirstname = findViewById(R.id.regCustomerFirstname);
        final EditText regCustLastname = findViewById(R.id.regCustomerLastname);
        final EditText regCustEmail = findViewById(R.id.regCustomerEmail);
        splInstructions = findViewById(R.id.spl_instructions_edittext);
        final TextView delvrydatelabel = findViewById(R.id.deldatelabel);

        final LinearLayout addTips = findViewById(R.id.addTip);
        loginRepository.getCustomerEntity().setTipAmount(0.0);
        loginRepository.getCustomerEntity().setDeliveryDate("");
        loginRepository.getCustomerEntity().setDeliveryCharge(0.0);
        noTip = findViewById(R.id.noTipLT);
        Tip1Lt = findViewById(R.id.Tip1Lt);
        Tip2Lt = findViewById(R.id.Tip2Lt);
        Tip3Lt = findViewById(R.id.Tip3Lt);
        Tip4Lt = findViewById(R.id.Tip4LT);
        final TextView tip1 = findViewById(R.id.Tip1);
        final TextView tip2 = findViewById(R.id.Tip2);
        final TextView tip3 = findViewById(R.id.Tip3);
        tip1Amt = findViewById(R.id.Tip1Amt);
        tip2Amt = findViewById(R.id.Tip2Amt);
        tip3Amt = findViewById(R.id.Tip3Amt);

        TextView freemaxOrderTxtView = findViewById(R.id.freemaxOrderTxt);
        freemaxOrderTxtView.setText("Order more than "+getMaxFreeDelOrderAmt()+", get Free Delivery.");

        int customerId= loginRepository.getCustomerEntity().getCustomerId();
        StoreSummary storeSummary = new StoreSummary();
        storeSummary.setId(loginRepository.getCustomerEntity().getStoreId());
        storeSummary.setLatitude(loginRepository.getCustomerEntity().getStore().getLatitude());
        storeSummary.setLongitude(loginRepository.getCustomerEntity().getStore().getLongitude());
        loadingProgressBar = findViewById(R.id.loading);

        radioButtonTakeOut = findViewById(R.id.radioButtonTakeout);

        final RadioButton cashRadioBtn = findViewById(R.id.radioButtonCash);
        final RadioButton cardRadioBtn = findViewById(R.id.radioButtonCard);
        if (loginRepository.getCustomerEntity().getStore().getStoreAttributes().getPaymentOptions().
                get(0).equalsIgnoreCase("Card")) {

            cardRadioBtn.setText(loginRepository.getCustomerEntity().getStore().getStoreAttributes().getPaymentOptions().get(0));
            if(loginRepository.getCustomerEntity().getStore().getStoreAttributes().getPaymentOptions().size()>1)
            {
                cashRadioBtn.setText(loginRepository.getCustomerEntity().getStore().getStoreAttributes().getPaymentOptions().get(1));

            }
            if (loginRepository.getCustomerEntity().getStore().getStoreAttributes().isCardOnly()) {
                cashRadioBtn.setEnabled(false);
                cashRadioBtn.setVisibility(View.GONE);
            }


        } else {
            cardRadioBtn.setText(loginRepository.getCustomerEntity().getStore().getStoreAttributes().getPaymentOptions().get(1));
            cashRadioBtn.setText(loginRepository.getCustomerEntity().getStore().getStoreAttributes().getPaymentOptions().get(0));
            if (loginRepository.getCustomerEntity().getStore().getStoreAttributes().isCardOnly()) {
                cashRadioBtn.setEnabled(false);
            }
        }
        final RadioButton dineInRadioBtn = findViewById(R.id.radioButtonDineIn);
        final RadioButton takeOutRadioBtn = findViewById(R.id.radioButtonTakeout);
        deliveryRadioBtn = findViewById(R.id.radioButtonDelivery);
        LinearLayout datetimepickerlinearLayout = findViewById(R.id.datetimepicker);
        LinearLayout addressBarLT = findViewById(R.id.addressBar);
        payButton = findViewById(R.id.payButton);
        orderButton = findViewById(R.id.placeOrderButton);
        cardInputWidget = findViewById(R.id.cardInputWidget);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroupDineIn = findViewById(R.id.radioGroup1);
        futureUserCheckBox = findViewById(R.id.futureUserCheckBox);
        transactionFeeMessg = findViewById(R.id.transactionFeeMessg);


        takeOutRadioBtn.setVisibility(View.GONE);
        dineInRadioBtn.setVisibility(View.GONE);
        deliveryRadioBtn.setVisibility(View.GONE);
        tableLabel.setVisibility(View.GONE);
        tableNumber.setVisibility(View.GONE);
        for (String serviceOption : loginRepository.getCustomerEntity().getStore().getStoreAttributes().getServiceOptions()) {
            if (serviceOption.equalsIgnoreCase("Dine In")) {
                dineInRadioBtn.setText(serviceOption);
                dineInRadioBtn.setVisibility(View.VISIBLE);
                if(loginRepository.getCustomerEntity().getStore().getStoreAttributes().isDineInAllowed()) {

                    dineInRadioBtn.setVisibility(View.VISIBLE);
                    tableLabel.setVisibility(View.GONE);
                    tableNumber.setVisibility(View.GONE);
                }
                else{
                    dineInRadioBtn.setVisibility(View.GONE);
                    tableLabel.setVisibility(View.GONE);
                    tableNumber.setVisibility(View.GONE);
                }

            } else if ((serviceOption.equalsIgnoreCase("Take Out"))
                    ||(serviceOption.equalsIgnoreCase("Pickup"))) {
                takeOutRadioBtn.setText(serviceOption);
                takeOutRadioBtn.setVisibility(View.VISIBLE);
                takeOutRadioBtn.setChecked(true);
                addressBarLT.setVisibility(View.INVISIBLE);
                loginRepository.getCustomerEntity().setTakeOut(true);
                delvrydatelabel.setText(serviceOption+" Date:");

            } else if (serviceOption.equalsIgnoreCase("Delivery")) {
                deliveryRadioBtn.setText(serviceOption);
                deliveryRadioBtn.setVisibility(View.VISIBLE);
                //deliveryRadioBtn.setChecked(true);
                //delvrydatelabel.setText(serviceOption+" Date:");
                if(loginRepository.getCustomerEntity().getStore().getStoreAttributes().getServiceOptions().size()==1){
                    deliveryRadioBtn.setChecked(true);
                    //delivery fun code.
                    getStoredCustomerAddress(customerId, storeSummary);
                    addressBarLT.setVisibility(View.VISIBLE);
                    tableLabel.setVisibility(View.GONE);
                    tableNumber.setVisibility(View.GONE);
                    cashRadioBtn.setEnabled(false);
                    loginRepository.getCustomerEntity().setBillable(true);
                    deliveryCharge=deliveryChargeCalculated;
                    if (loginRepository.getCustomerEntity().getStore().getStoreAttributes().isAllowFutureOrders()==1) {
                        datetimepickerlinearLayout.setVisibility(View.VISIBLE);
                    }
                    showCardOptions();
                    if(deliveryCharge==0){
                        payButton.setEnabled(false);
                        payButton.setBackgroundColor(Color.LTGRAY);
                    }
                    delvrydatelabel.setText(serviceOption+" Date:");
                }
            }


        }

        //review this logic


        loginRepository.getCustomerEntity().setDeliveryAddress(null);
        loginRepository.getCustomerEntity().setOrderCompletionMethod(-1);

        if (loginRepository.getCustomerEntity().getStore().getStoreAttributes().isAllowFutureOrders()==1) {
            datetimepickerlinearLayout.setVisibility(View.VISIBLE);
        }



        if (!loginRepository.getCustomerEntity().getFirstName().equalsIgnoreCase("Guest")) {
            regCustFirstname.setText(loginRepository.getCustomerEntity().getFirstName());
        }

        if (!loginRepository.getCustomerEntity().getLastName().equalsIgnoreCase("LN")) {
            regCustLastname.setText(loginRepository.getCustomerEntity().getLastName());
        }

        if (!loginRepository.getCustomerEntity().getEmail().equalsIgnoreCase("guest@iperkz.com")) {
            regCustEmail.setText(loginRepository.getCustomerEntity().getEmail());
        }

        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        //regCustFirstname.requestFocus();



        if (loginRepository.getCustomerEntity().getStore().getChargeMode().equalsIgnoreCase("CUSTOMER")) {
            transactionFeeMessg.setVisibility(View.VISIBLE);
        } else {
            transactionFeeMessg.setVisibility(View.GONE);
        }





    /*    if (loginRepository.isDineInOptionAllowed() == true) {

            //datetimepickerlinearLayout.setVisibility(View.GONE);

        } else {
            for (int i = 0; i < radioGroupDineIn.getChildCount(); i++) {
                radioGroupDineIn.getChildAt(i).setEnabled(false);
            }
        }*/




        radioGroupDineIn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb1 = group.findViewById(checkedId);
                if (null != rb1 && checkedId == R.id.radioButtonDineIn) {
                    //
                    //check dine in allowed?
                    checkDineInOption();
                    //
                    loginRepository.getCustomerEntity().setTakeOut(false);
                    loginRepository.getCustomerEntity().setOrderCompletionMethod(0);
                    tableNumber.setText(loginRepository.getCustomerEntity().getTableNumber());
                    tableLabel.setVisibility(View.VISIBLE);
                    tableNumber.setVisibility(View.VISIBLE);
                    addressBarLT.setVisibility(View.INVISIBLE);
                    cashRadioBtn.setEnabled(true);
                    payButton.setEnabled(true);
                    payButton.setBackgroundColor(Color.parseColor("#3F51B5"));
                    deliveryCharge=0.0;
                    showCardOptions();
                    //hide

                    datetimepickerlinearLayout.setVisibility(View.GONE);
                    loginRepository.getCustomerEntity().setDeliveryAddress(null);

                } else if (null != rb1 && checkedId == R.id.radioButtonTakeout) {
                    loginRepository.getCustomerEntity().setTakeOut(true);
                    loginRepository.getCustomerEntity().setOrderCompletionMethod(1);
                    loginRepository.getCustomerEntity().setBillable(true);
                    tableLabel.setVisibility(View.GONE);
                    tableNumber.setVisibility(View.GONE);
                    //loginRepository.getCustomerEntity().setTableNumber("");
                    addressBarLT.setVisibility(View.INVISIBLE);
                    cashRadioBtn.setEnabled(true);
                    payButton.setEnabled(true);
                    payButton.setBackgroundColor(Color.parseColor("#3F51B5"));
                    deliveryCharge=0.0;
                    if (loginRepository.getCustomerEntity().getStore().getStoreAttributes().isAllowFutureOrders()==1) {
                        datetimepickerlinearLayout.setVisibility(View.VISIBLE);

                        delvrydatelabel.setText(takeOutRadioBtn.getText()+" Date:");
                    }
                    else{

                        datetimepickerlinearLayout.setVisibility(View.GONE);
                    }
                    loginRepository.getCustomerEntity().setDeliveryAddress(null);
                    showCardOptions();
                } else {

                    getStoredCustomerAddress(customerId, storeSummary);
                    addressBarLT.setVisibility(View.VISIBLE);

                    tableLabel.setVisibility(View.GONE);
                    tableNumber.setVisibility(View.GONE);
                    cashRadioBtn.setEnabled(false);

                    loginRepository.getCustomerEntity().setBillable(true);

                    deliveryCharge=deliveryChargeCalculated;

                    if (loginRepository.getCustomerEntity().getStore().getStoreAttributes().isAllowFutureOrders()==1) {
                        datetimepickerlinearLayout.setVisibility(View.VISIBLE);
                    }

                    showCardOptions();
                    if(deliveryCharge==0){
                        payButton.setEnabled(false);
                        payButton.setBackgroundColor(Color.LTGRAY);
                    }
                    delvrydatelabel.setText("Delivery Date:");


                }
            }
        });

        showCardOptions();
        setTipVisible();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = group.findViewById(checkedId);
                if (null != rb && checkedId == R.id.radioButtonCard) {
                    // Toast.makeText(PaymentActivity.this, "Card Clicked", Toast.LENGTH_SHORT).show();
                    showCardOptions();
                    setTipVisible();
                    if (loginRepository.getCustomerEntity().getStore().getStoreTypeId() == 2){
                        //nathan
                       addressBarLT.setVisibility(View.INVISIBLE);
                    }
                    if(loginRepository.getCustomerEntity().getStore().getStoreAttributes().getServiceOptions().contains("Delivery"))
                    {
                        deliveryRadioBtn.setVisibility(View.VISIBLE);
                    }
                }

                if (null != rb && checkedId == R.id.radioButtonCash) {
                    showCashOptions();
                    setTipInVisible();
                    deliveryRadioBtn.setVisibility(View.GONE);
                }

            }


        });

        loginViewModel.getOfflineProcessResult().observe(this, new Observer<SimpleResponse>() {

            @Override
            public void onChanged(@Nullable SimpleResponse simpleResponse) {
                if (simpleResponse == null) {return; }
              //  loadingProgressBar.setVisibility(View.GONE);
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

                    fragment.show(PaymentActivity.this.getSupportFragmentManager(), "test");
                } else  {
                    progressDialog = PaymentUtil.getInstance().getDialogProgressBar(simpleResponse.getMessage(),
                            PaymentActivity.this).create();
                    progressDialog.setCancelable(true);
                    //progressDialog.get
                    progressDialog.show();
                }
                setResult(Activity.RESULT_OK);
            }
        });

        payButton.setOnClickListener((View view) -> {
            System.out.println("TEST : PAYBUTTON");
            if (regCustFirstname.length() == 0 || regCustLastname.length() == 0 || regCustEmail.length() == 0 || !isEmailValid(regCustEmail.getText().toString())) {
                loginDataChanged(regCustFirstname.getText().toString(),
                        regCustLastname.getText().toString(),
                        regCustEmail.getText().toString());
                return;
            }
            payButton.setEnabled(false);
            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            progressDialog = getDialogProgressBar("You order is being placed...", PaymentActivity.this).create();

            //PaymentActivity.this.showProgressBar();


            switch (checkedRadioButtonId) {
                case R.id.radioButtonCash:
                    paymentDetails = PaymentUtil.getInstance().generatePaymentDetails("CASH",
                            futureUserCheckBox.isChecked()
                    );
                    progressDialog.show();
                    submitOrder();
                    break;
                case R.id.radioButtonCard:
                    if(loginRepository.getCustomerEntity().isAllowCardFutureUse()){

                         paymentDetails =
                                PaymentUtil.getInstance().generatePaymentDetails( "CARD",
                                        true

                                );

                        CustomerOrderCreationRequest order = new MenuSelectionToCustomerOrderConverter()
                                .convert(paymentDetails.getPaymentMode(), paymentDetails.isAllowFutureUse());
                        order.setSpecialInstructions(splInstructions.getText().toString());
                        //CustomerOrderCreationRequest orderCreationRequest = new CustomerOrderCreationRequest();
                        OfflinePaymentDetails offlinePaymentDetails = new OfflinePaymentDetails();
                        offlinePaymentDetails.setOrderCreationRequest(order);
                        offlinePaymentDetails.setPaymentDetails(paymentDetails);
                        loginViewModel.processOfflinePayment(offlinePaymentDetails, PaymentActivity.this);
                    }
                    else {
                        paymentDetails = PaymentUtil.getInstance().generatePaymentDetails("CARD",
                                futureUserCheckBox.isChecked()
                        );

                        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
                        if (params == null) {
                            payButton.setEnabled(true);
                            return;
                        }
                        progressDialog.show();
                        System.out.println("TEST : requesting client secret");
                        loginViewModel.requestClientSecret(paymentDetails, this);
                    }
                    break;
            }

        });

        orderButton.setOnClickListener((View view) -> {
            System.out.println("TEST : ORDER BUTTON" );
            if (regCustFirstname.length() == 0 || regCustLastname.length() == 0 || regCustEmail.length() == 0 || !isEmailValid(regCustEmail.getText().toString())) {
                loginDataChanged(regCustFirstname.getText().toString(),
                        regCustLastname.getText().toString(),
                        regCustEmail.getText().toString());
                return;
            }
            orderButton.setEnabled(false);
            showProgressBar();
            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            switch (checkedRadioButtonId) {
                case R.id.radioButtonCash:
                    paymentDetails = PaymentUtil.getInstance().generatePaymentDetails("CASH",
                            futureUserCheckBox.isChecked()
                    );
                    submitOrder();
                    break;
                    /*
                case R.id.radioButtonCard :
                    paymentDetails =PaymentUtil.getInstance().generatePaymentDetails( "CARD",
                            futureUserCheckBox.isChecked()
                    );
                    loginViewModel.requestClientSecret(paymentDetails, this);
                    break;

                     */
            }

        });

        loginViewModel.getPaymentIntentClientSecret().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String clientSecret) {
                System.out.println("TEST : client secret response");
                if (clientSecret == null) {
                    return;
                }

                if (clientSecret != null && clientSecret.length() > 0) {
                    paymentIntentClientSecret = clientSecret;
                    System.out.println("TEST : start checkout");
                    startCheckout(futureUserCheckBox.isChecked());
                } else {
                    Toast.makeText(getApplicationContext(), "Error contacting iPerkz server", Toast.LENGTH_SHORT).show();
                }
                setResult(Activity.RESULT_OK);
            }
        });

        regCustFirstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                loginDataChanged(regCustFirstname.getText().toString(),
                        regCustLastname.getText().toString(),
                        regCustEmail.getText().toString());
                customerItem.setFirstName(s.toString());
            }
        });


        regCustLastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                customerItem.setLastName(s.toString());
                loginDataChanged(regCustFirstname.getText().toString(),
                        regCustLastname.getText().toString(),
                        regCustEmail.getText().toString());
            }
        });

        regCustEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                customerItem.setEmail(s.toString());
                loginDataChanged(regCustFirstname.getText().toString(),
                        regCustLastname.getText().toString(),
                        regCustEmail.getText().toString());
            }
        });

        loginFormState.observe(this, new Observer<RegistrationFormState>() {
            @Override
            public void onChanged(@Nullable RegistrationFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                //submitButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getFirstnameError() != null) {
                    regCustFirstname.setError(getString(loginFormState.getFirstnameError()));
                }
                if (loginFormState.getLastnameError() != null) {
                    regCustLastname.setError(getString(loginFormState.getLastnameError()));
                }
                if (loginFormState.getEmailError() != null) {
                    regCustEmail.setError(getString(loginFormState.getEmailError()));
                }
            }
        });

        loginViewModel.getUpdateCustomerResult().observe(this, new Observer<CustomerEntity>() {
            @Override
            public void onChanged(@Nullable CustomerEntity customer) {
                if (customer == null) {
                    return;
                }

                if (!customer.getFirstName().equalsIgnoreCase("Guest")) {
                    loginRepository.getCustomerEntity().setFirstName(customer.getFirstName());
                    loginRepository.getCustomerEntity().setLastName((customer.getFirstName()));
                    loginRepository.getCustomerEntity().setEmail((customer.getEmail()));
                }

            }
        });

        //tips


        Tip4Lt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTotalWithTip("0.0");
                highlight("Tip4Lt");
                DialogFragment newFragment = new TipDialog((PaymentActivity) v.getContext());
                newFragment.setCancelable(false);
                newFragment.show(getSupportFragmentManager(), "missiles");
            }
        });

        Tip1Lt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlight("Tip1Lt");
                updateTotalWithTip(((String) tip1Amt.getText()).substring(1));
            }
        });

        Tip2Lt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlight("Tip2Lt");
                updateTotalWithTip(((String) tip2Amt.getText()).substring(1));
            }
        });

        Tip3Lt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlight("Tip3Lt");
                updateTotalWithTip(((String) tip3Amt.getText()).substring(1));
            }
        });

        noTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlight("noTip");
                updateTotalWithTip("0.0");
            }
        });


        if (loginRepository.getCustomerEntity().getStore().getStoreAttributes().getShowTips() == 1) {
            addTips.setVisibility(View.VISIBLE);

            if (loginRepository.getCustomerEntity().getStore().getStoreAttributes().getTips() != null) {

                List<Double> tips = loginRepository.getCustomerEntity().getStore().getStoreAttributes().getTips();
                tip1.setText(Util.convertDouble2String(tips.get(0)) + "%");
                tip2.setText(Util.convertDouble2String(tips.get(1)) + "%");
                tip3.setText(Util.convertDouble2String(tips.get(2)) + "%");
            }
        }

        SingleDateAndTimePicker dateTimePicker = findViewById(R.id.single_day_picker);

        if (loginRepository.getCustomerEntity().getStore().getStoreTypeId() == 4 ) {

            LinearLayout delTextLV = findViewById(R.id.delText);
            delTextLV.setVisibility(View.VISIBLE);
            dateTimePicker.setDefaultDate(DateUtils.incrementDateByOne(new Date()));
            dateTimePicker.setDisplayMinutes(false);
            dateTimePicker.setDisplayHours(false);
            dateTimePicker.setDisplayYears(false);
            dateTimePicker.mustBeOnFuture();
            dateTimePicker.setMinDate(DateUtils.incrementDateByOne(new Date()));
            dateTimePicker.setMaxDate(DateUtils.addMonthByOne(new Date(), 1));
            String formattedDate = DateUtils.getFormattedDateAndTime(DateUtils.incrementDateByOne(new Date()));
            loginRepository.getCustomerEntity().setDeliveryDate(formattedDate);

        }
        else {

            if (loginRepository.getCustomerEntity().getStore().getStoreTypeId() == 2){
                dateTimePicker.setDisplayMinutes(false);
                dateTimePicker.setDisplayHours(false);
                dateTimePicker.mustBeOnFuture();
                dateTimePicker.setDisplayYears(false);
                dateTimePicker.setMaxDate(DateUtils.addMonthByOne(new Date(), 1));
                addressBarLT.setVisibility(View.INVISIBLE);

            }

        }

        if (loginRepository.getCustomerEntity().getStore().getStoreTypeId() == 2) {
            if (loginRepository.getCustomerEntity().getStore().getStoreAttributes().isAllowFutureOrders()==1) {
                datetimepickerlinearLayout.setVisibility(View.VISIBLE);
            }
            futureUserCheckBox.setChecked(false);
           // futureUserCheckBox.setVisibility(View.GONE);
            tableNumber.setVisibility(View.GONE);
            tableLabel.setVisibility(View.GONE);
            loginRepository.getCustomerEntity().setTakeOut(true);
        }

        dateTimePicker.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                String formattedDate = DateUtils.getFormattedDateAndTime(date);
                loginRepository.getCustomerEntity().setDeliveryDate(formattedDate);
            }
        });

        //delivery



        addressSpinner = findViewById(R.id.addressSpinner);
        arrayList = new ArrayList<>();
        arrayList.add("Select Address");

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addressSpinner.setAdapter(arrayAdapter);

        //Google places auto complete
        String apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(this.getApplicationContext(), apiKey);
        }

        PaymentActivity pactivity = this;
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                this.getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment2);
        autocompleteFragment.setHint(" Enter Address ");
        //autocompleteFragment.setText(Color.parseColor("#3F51B5"));

        EditText input = autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input);
        input.setText(" Add ");
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        input.setTextSize(14f);
        input.setGravity(Gravity.CENTER);
        input.setTextColor(Color.BLUE);
        //input.setTextColor(Color.parseColor("#3F51B5"));
        autocompleteFragment.getView().findViewById(R.id.places_autocomplete_clear_button).setPadding(0, 0, 0, 55555);
        autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button).setVisibility(View.GONE);
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {


                AddressDetail addressDetail = new AddressDetail();
                addressDetail.setId(-7);
                AddressComponents addressComponents = place.getAddressComponents();
                for(AddressComponent addressComponent  : addressComponents.asList()){
                   if(addressComponent.getTypes().contains("street_number")){
                        addressDetail.setFieldOne(addressComponent.getName()!=null ? addressComponent.getName() : "");
                    }
                   else if(addressComponent.getTypes().contains("route")){
                       addressDetail.setFieldTwo(addressComponent.getName()!=null ? addressComponent.getName() : "");
                   }
                   else if(addressComponent.getTypes().contains("locality")){
                       addressDetail.setCity(addressComponent.getName()!=null ? addressComponent.getName() : "");
                   }
                   else if(addressComponent.getTypes().contains("administrative_area_level_1")){
                       addressDetail.setState(addressComponent.getShortName()!=null ? addressComponent.getShortName() : "");
                   }
                   else if(addressComponent.getTypes().contains("country")){
                       addressDetail.setCountry(addressComponent.getShortName()!=null ? addressComponent.getShortName() : "");
                   }
                    else if(addressComponent.getTypes().contains("postal_code")){
                        addressDetail.setZip((addressComponent.getName()!=null) ? addressComponent.getName() : "");
                    }

                   }

                addressDetail.setLatitude(place.getLatLng().latitude);
                addressDetail.setLongitude(place.getLatLng().longitude);

                //popup
                if(addressDetailsList==null){
                    addressDetailsList = new ArrayList<>();
                }
                DeliveryDialog newFragment = new DeliveryDialog(storeSummary, customerId, addressDetail, addressDetailsList, pactivity);
                newFragment.setCancelable(false);
                newFragment.show(getSupportFragmentManager(), "delivery");

            }


            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });


        addressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String deliveryAddress = parent.getItemAtPosition(position).toString();
//nathan
                if(deliveryAddress.equals("Select Address")){
                    deliveryCharge=0.0;
                    showCardOptions();
                    if ( takeOutRadioBtn.isChecked() == false ) {
                        payButton.setEnabled(false);
                        payButton.setBackgroundColor(Color.LTGRAY);
                    }
                    return;
                }
                else if (deliveryAddress.equals("Add New")) {
                    deliveryCharge=0.0;
                    showCardOptions();
                    payButton.setEnabled(false);
                    payButton.setBackgroundColor(Color.LTGRAY);
                    input.performClick();
                }
                else {
                    setDeliveryCharge(deliveryAddress);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                    //input.performClick();
            }
        });


        //loc instore logic
        userLocation = new UserLocation();
        userLocation.setStoreId(loginRepository.getCustomerEntity().getStoreId());
        locationManager = (LocationManager) this.getSystemService(getApplicationContext().LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try{
                    locationManager.removeUpdates(this);
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

        //loc logic

    }


    public void loginDataChanged(String firstname, String lastname, String email) {
        if (!isFirstnameValid(firstname)) {
            loginFormState.setValue(
                    new RegistrationFormState(R.string.invalid_firstname, null, null)
            );
        } else if (!isLastnameValid(lastname)) {
            loginFormState.setValue(
                    new RegistrationFormState(null, R.string.invalid_lastname, null)
            );
        } else if (!isEmailValid(email)) {
            loginFormState.setValue(
                    new RegistrationFormState(null, null, R.string.invalid_email)
            );
        } else {
            loginFormState.setValue(new RegistrationFormState(true));
        }
    }

    private boolean isFirstnameValid(String firstname) {
        if (firstname == null) {
            return false;
        }
        if (firstname.contains("@")) {
            return false;
        } else {
            return !firstname.trim().isEmpty();
        }
    }

    private boolean isLastnameValid(String lastname) {
        if (lastname == null) {
            return false;
        }
        if (lastname.contains("@")) {
            return false;
        } else {
            return !lastname.trim().isEmpty();
        }
    }

    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        if (email.trim().isEmpty()) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public AlertDialog.Builder getDialogProgressBar(String title, Context context) {

        if (builder == null) {
            builder = new AlertDialog.Builder(context);

            builder.setTitle(title);

            final ProgressBar progressBar = new ProgressBar(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(lp);
            builder.setView(progressBar);
        }
        return builder;
    }

    public void showProgressBar() {
        if (progressDialog == null) {
            progressDialog = getDialogProgressBar("You order is being placed...", this).create();
        }
        progressDialog.show();
    }

    private void showCashOptions() {
        loginRepository.getCustomerEntity().setTipAmount(0.0);
        payButton.setVisibility(View.GONE);
        cardInputWidget.setVisibility(View.GONE);
        futureUserCheckBox.setVisibility(View.GONE);
        transactionFeeMessg.setVisibility(View.GONE);
        orderButton.setVisibility(View.VISIBLE);
        Double grandTotal = Data.getInstance().getGrandTotalAsDouble();
        if (loginRepository.reward != null && loginRepository.reward.getClaimedReward() != null) {
            grandTotal = grandTotal - (loginRepository.reward.getClaimedReward() - loginRepository.reward.getPreviousClaim());
        }

        //tips logic
        orderButton.setText("Place Order & Pay at Store : $ " + Util.getFormattedDollarAmt(grandTotal));
    }

    public void showCardOptions() {
        payButton.setVisibility(View.VISIBLE);
        cardInputWidget.setVisibility(View.VISIBLE);
        futureUserCheckBox.setVisibility(View.VISIBLE);
        if(loginRepository.getCustomerEntity().isAllowCardFutureUse()){
            futureUserCheckBox.setVisibility(View.GONE);
            //futureUserCheckBox.setChecked(true);
            cardInputWidget.setVisibility(View.GONE);
            RadioButton cardRadioBtn = findViewById(R.id.radioButtonCard);
            cardRadioBtn.setText("Card on file");
        }
        //deliveryRadioBtn.setVisibility(View.VISIBLE);

        Double grandTotal = Data.getInstance().getGrandTotalAsDouble();

        Double transactionFee = ((grandTotal * loginRepository.getCustomerEntity().getStore().getChargeRate()) / 100)
                + loginRepository.getCustomerEntity().getStore().getTransactionFee();
        grandTotal = grandTotal + transactionFee;
        grandTotal = grandTotal + deliveryCharge;
        if (loginRepository.reward != null && loginRepository.reward.getClaimedReward() != null) {
            grandTotal = grandTotal - (loginRepository.reward.getClaimedReward() - loginRepository.reward.getPreviousClaim());
        }
        transactionFeeMessg.setText("As per the store owner policy, you will be charged a credit card transaction fee of $" +
                Util.getFormattedDollarAmt(transactionFee));
        if (loginRepository.getCustomerEntity().getStore().getChargeMode().equalsIgnoreCase("CUSTOMER")) {
            transactionFeeMessg.setVisibility(View.VISIBLE);
            //
            prepareTips(grandTotal);
            grandTotal = grandTotal + customerTip;
            //grandTotal = grandTotal + deliveryCharge;
            //
            if(deliveryCharge >0 && deliveryRadioBtn.isChecked()){
                payButton.setText("Pay : $ " + Util.getFormattedDollarAmt(grandTotal) + " + $ "+Util.getFormattedDollarAmt(deliveryCharge)+" Delivery Charge");
            }
            else {
                payButton.setText("Pay : $ " + Util.getFormattedDollarAmt(grandTotal));
            }
            deliveryOrderAmt = grandTotal;
        } else {
            Double grandTotall = Data.getInstance().getGrandTotalAsDouble() + customerTip;
            if (loginRepository.reward != null && loginRepository.reward.getClaimedReward() != null) {
                grandTotall = grandTotall - (loginRepository.reward.getClaimedReward() - loginRepository.reward.getPreviousClaim());

                prepareTips(grandTotall);

                if(deliveryCharge >0 && deliveryRadioBtn.isChecked()){
                    payButton.setText("Pay : $ " + Util.getFormattedDollarAmt(grandTotall) + " + $ "+Util.getFormattedDollarAmt(deliveryCharge)+" Delivery Charge");
                }
                else {
                    payButton.setText("Pay : $ " + Util.getFormattedDollarAmt(grandTotall));
                }
                deliveryOrderAmt = grandTotall;
            } else {
                Double gtotal = Double.valueOf(Data.getInstance().getGrandTotal().substring(1));
                prepareTips(gtotal);
                gtotal = gtotal + customerTip;
                if(deliveryCharge >0 && deliveryRadioBtn.isChecked()){
                    payButton.setText("Pay : $ " + Util.getFormattedDollarAmt(gtotal) + " + $ "+Util.getFormattedDollarAmt(deliveryCharge)+" Delivery Charge");
                }
                else {
                    payButton.setText("Pay : $ " + Util.getFormattedDollarAmt(gtotal));
                }
                deliveryOrderAmt = gtotal;
            }
        }
        // payButton.setText("Pay : " + Data.getInstance().getGrandTotal());
        orderButton.setVisibility(View.GONE);
        if (grandTotal < .50) {
            if (loginRepository.getCustomerEntity().getStore().getChargeMode().equalsIgnoreCase("CUSTOMER")) {
                if ((grandTotal - transactionFee) > 0) {
                    transactionFeeMessg.setText("At least .50 cents charge needed for credit card. \n" +
                            "As per the store owner policy, you will be charged a credit card transaction fee of $ " +
                            Util.getFormattedDollarAmt(transactionFee));
                    payButton.setText("Pay : $ 0.00");
                }
            } else {
                transactionFeeMessg.setText("At least .50 cents charge needed for credit card");
            }
            transactionFeeMessg.setTextColor(Color.RED);
            payButton.setEnabled(false);
            payButton.setBackgroundColor(Color.LTGRAY);
            futureUserCheckBox.setVisibility(View.GONE);
        }


    }
    /*
    public AlertDialog.Builder getDialogProgressBar() {

        if (builder == null) {
            builder = new AlertDialog.Builder(this);

            builder.setTitle("You order is being placed...");

            final ProgressBar progressBar = new ProgressBar(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(lp);
            builder.setView(progressBar);
        }
        return builder;
    }
    */


    private void startCheckout(boolean allowFutureUse) {
        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
        if (params != null) {
            Map<String, String> extraParams = new HashMap<>();
            if (allowFutureUse == true) {
                extraParams.put("setup_future_usage", "off_session");
            } else {
                extraParams.put("setup_future_usage", "on_session");
            }
            ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                    .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret, null, false, extraParams);
            stripe = new Stripe(getApplicationContext(), PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey());
            stripe.confirmPayment(this, confirmParams);
        }
        loginRepository.getCustomerEntity().setAllowCardFutureUse(allowFutureUse);
    }

    public void displayAlert(String title, String message, boolean result) {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment
        if (stripe != null)
            stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));

    }

    public void submitOrder() {
        if (loginRepository.getCustomerEntity().isTakeOut()) {
            loginRepository.getCustomerEntity().setTableNumber("");
        } else
            loginRepository.getCustomerEntity().setTableNumber(tableNumber.getText().toString());

        updateCustomer();

        CronetEngine cronetEngine = Cronet.getCronetEngine(this);
        CreateOrderRequestCallback createOrderCallback = new CreateOrderRequestCallback(this);

        Executor executor = Executors.newSingleThreadExecutor();
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                Data.SERVER_URL + "/api/order", createOrderCallback, executor);
        requestBuilder.addHeader("Content-Type", "application/json");
        requestBuilder.setHttpMethod("POST");

        ObjectMapper mapper = new ObjectMapper();
        try {
            CustomerOrderCreationRequest order = new MenuSelectionToCustomerOrderConverter()
                    .convert(paymentDetails.getPaymentMode(), paymentDetails.isAllowFutureUse());
            order.setSpecialInstructions(splInstructions.getText().toString());
            String jsonValue = mapper.writeValueAsString(order);
            Log.println(Log.INFO, "ViewCartActivity", jsonValue);
            byte[] bytes = mapper.writeValueAsBytes(order);
            UploadDataProvider provider = ByteBufferUploadProvider.create(bytes);
            requestBuilder.setUploadDataProvider(provider, executor);

            UrlRequest request = requestBuilder.build();
            request.start();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (reviewInfo != null) {
            Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
            flow.addOnCompleteListener(task -> {
                // The flow has finished. The API does not indicate whether the user
                // reviewed or not, or even whether the review dialog was shown. Thus, no
                // matter the result, we continue our app flow.
            });
        }

    }

    public void showFailureMessage() {
        SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(new String[]{"Failed to place order"});
        fragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme);
        fragment.setCancelable(false);
        fragment.show(getSupportFragmentManager(), "test");
    }

    public void showSuccessMessage() {
        MenuItem[] breakfastList = Data.getInstance().getBreakfastList();
        MenuItem[] lunchList = Data.getInstance().getLunchList();
        MenuItem[] allDayList = Data.getInstance().getAllDayList();

        if (breakfastList != null) {
            for (FoodItem item : breakfastList) {
                item.setSelected(false);
            }
        }
        if (lunchList != null) {
            for (FoodItem item : lunchList) {
                item.setSelected(false);
            }
        }
        if (allDayList != null) {
            for (FoodItem item : allDayList) {
                item.setSelected(false);
            }
        }

        //remove the progress bar and make the Avitivty respond to user input
        // loadingProgressBar.setVisibility(View.GONE);
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressDialog.hide();
        SuccessDialogFragment fragment =
                SuccessDialogFragment.newInstance(
                        new String[]{
                                loginRepository.getCustomerEntity().getFirstName()
                                        + ", "
                                        + "Your order is placed."}
                );
        fragment.show(getSupportFragmentManager(), "test");

    }

    public void moveToHomePage(View view) {
        if (clearCart() == true) {

            Intent intent;
            if (loginRepository.getCustomerEntity().getStore().getStoreTypeId() != 4) {
                 intent = new Intent(this, IperkzHomeActivity.class);
                intent.putExtra(MEAL_TYPE, loginRepository.getCustomerEntity().getStore().getMealTypes().get(0).getMealTypeId().toString());
                startActivity(intent);

            }
            else{
                 intent = new Intent(this, IperkzHomeActivity.class);
                 startActivity(intent);
            }


            //   finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    private boolean clearCart() {
        Data.getInstance().setSelectedMenuItems(new ArrayList<FoodItem>());
        Data.getInstance().clearTotalCost();
        return true;
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<PaymentActivity> activityRef;

        PaymentResultCallback(@NonNull PaymentActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final PaymentActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            System.out.println("TEST : hide the Progress Dialog" );
          //  activity.progressDialog.hide();
            System.out.println("TEST : After hide the Progress Dialog" );
            if (status == PaymentIntent.Status.Succeeded) {
                activity.submitOrder();
                /*
                activity.displayAlert(
                        "Payment Succeed",
                        "ALL GOOD",
                        true
                );
                */

            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage(),
                        false
                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final PaymentActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString(), false);
        }
    }

    private void updateCustomer() {
        customerItem.setCustomerId(loginRepository.getCustomerEntity().getCustomerId());
        loginRepository.getCustomerEntity().setFirstName(customerItem.getFirstName() != null ? customerItem.getFirstName() : loginRepository.getCustomerEntity().getFirstName());
        loginRepository.getCustomerEntity().setLastName(customerItem.getLastName() != null ? customerItem.getLastName() : loginRepository.getCustomerEntity().getLastName());
        loginRepository.getCustomerEntity().setEmail(customerItem.getEmail() != null ? customerItem.getEmail() : loginRepository.getCustomerEntity().getEmail());
        loginViewModel.updateCustomer(customerItem, this);
        SharedPreferences.Editor editor = getSharedPreferences("CUSTOMER_DETAILS", MODE_PRIVATE).edit();
        ObjectMapper mapper = new ObjectMapper();
        String customerJson = "";
        try {
            customerJson = mapper.writeValueAsString(loginRepository.getCustomerEntity());
        } catch (Exception e) {

        }
        editor.putString("CUSTOMER", customerJson);
        editor.commit();

    }

    private void prepareTips(Double grandTotal) {
        if (tipFlag) {
            if (loginRepository.getCustomerEntity().getStore().getStoreAttributes().getTips() != null) {

                List<Double> tips = loginRepository.getCustomerEntity().getStore().getStoreAttributes().getTips();
                tip1Amt.setText("$" + Util.getFormattedDollarAmt(tips.get(0) * grandTotal / 100));
                tip2Amt.setText("$" + Util.getFormattedDollarAmt(tips.get(1) * grandTotal / 100));
                tip3Amt.setText("$" + Util.getFormattedDollarAmt(tips.get(2) * grandTotal / 100));
            }
            tipFlag = false;
        }
    }

    private void setTipVisible() {
        noTip.setVisibility(View.VISIBLE);
        Tip1Lt.setVisibility(View.VISIBLE);
        Tip2Lt.setVisibility(View.VISIBLE);
        Tip3Lt.setVisibility(View.VISIBLE);
        Tip4Lt.setVisibility(View.VISIBLE);

    }

    private void setTipInVisible() {
        noTip.setVisibility(View.GONE);
        Tip1Lt.setVisibility(View.GONE);
        Tip2Lt.setVisibility(View.GONE);
        Tip3Lt.setVisibility(View.GONE);
        Tip4Lt.setVisibility(View.GONE);

    }

    public void updateTotalWithTip(String tipAmount) {

        customerTip = Double.valueOf(tipAmount);
        loginRepository.getCustomerEntity().setTipAmount(customerTip);

        RadioButton cardButton = findViewById(R.id.radioButtonCard);
        if (cardButton.isChecked()) {
            showCardOptions();
        }
    }

    private void highlight(String tiplt) {

        switch (tiplt) {
            case "noTip":
                Tip1Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip1Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip2Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip2Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip3Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip3Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip4Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip4Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                noTip.setBackgroundResource(R.drawable.tip_box_border_highlight);
                noTip.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                break;
            case "Tip1Lt":
                Tip1Lt.setBackgroundResource(R.drawable.tip_box_border_highlight);
                Tip1Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip2Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip2Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip3Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip3Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip4Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip4Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                noTip.setBackgroundResource(R.drawable.tip_box_border);
                noTip.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                break;
            case "Tip2Lt":
                Tip1Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip1Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip2Lt.setBackgroundResource(R.drawable.tip_box_border_highlight);
                Tip2Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip3Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip3Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip4Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip4Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                noTip.setBackgroundResource(R.drawable.tip_box_border);
                noTip.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                break;
            case "Tip3Lt":
                Tip1Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip1Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip2Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip2Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip3Lt.setBackgroundResource(R.drawable.tip_box_border_highlight);
                Tip3Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip4Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip4Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                noTip.setBackgroundResource(R.drawable.tip_box_border);
                noTip.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                break;
            case "Tip4Lt":
                Tip1Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip1Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip2Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip2Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip3Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip3Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip4Lt.setBackgroundResource(R.drawable.tip_box_border_highlight);
                Tip4Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                noTip.setBackgroundResource(R.drawable.tip_box_border);
                noTip.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                break;

        }

    }

    private int dptopx(int padding_in_dp) {
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
        return padding_in_px;
    }

    /*public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showSingleDateTimePicker(View v) {

        new SingleDateAndTimePickerDialog.Builder(this)
                //.bottomSheet()
                //.curved()
                //.stepSizeMinutes(15)
                //.displayHours(false)
                //.displayMinutes(false)
                //.todayText("aujourd'hui")
                .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                    @Override
                    public void onDisplayed(SingleDateAndTimePicker picker) {
                        // Retrieve the SingleDateAndTimePicker
                    }

                    //@Override
                    public void onClosed(SingleDateAndTimePicker picker) {
                        Log.i("2", "3");
                    }
                })
                .title("Pickup Date")
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        //TextView textView = (TextView) findViewById(R.id.pickupdate);
                        //textView.setText(date.toString());
                        Log.i("Selected Date: ", date.toString());
                    }
                }).display();
    }*/

    public void setArrayAdapter(List<AddressDetail> addressDetailsList){

        AddressDetail addressDetail = addressDetailsList.get(0);
            String address = Util.getFormattedDollarAmt(addressDetail.getDistance())+"-"+addressDetail.getTag()+"-"+(addressDetail.getFieldOne()!=null?addressDetail.getFieldOne():"")+","+addressDetail.getFieldTwo()+","+addressDetail.getCity()+","+addressDetail.getState()+","+addressDetail.getCountry()+"-"+addressDetail.getZip();
            address = address.replace("null,", "");
            address = address.replace(",null", "");
            address = address.replace("-null", "");
            address = address.replace("null-", "");

        this.addressDetailsList.add(addressDetail);
        if (!this.arrayList.contains(address)) {

            this.arrayList.add(address);
            addressSpinner.setSelection(arrayList.size()-1);
            arrayAdapter.notifyDataSetChanged();
        }

           /* if(!isDistanceInRange(addressDetail.getDistance())) {

                ErrorDialog newFragment = new ErrorDialog("Sorry, Delivery Address is located outside the delivery area of the store", this);
                newFragment.setCancelable(false);
                newFragment.show(getSupportFragmentManager(), "error delivery");
            }*/

    }

    public void loadArrayAdapter(List<AddressDetail> addressDetailsList){

        this.arrayList.remove("Add New");
        for(AddressDetail addressDetail : addressDetailsList) {
            String address = Util.getFormattedDollarAmt(addressDetail.getDistance()) + "-" + addressDetail.getTag() + "-" + (addressDetail.getFieldOne() != null ? addressDetail.getFieldOne() : "") + "," + addressDetail.getFieldTwo() + "," + addressDetail.getCity() + "," + addressDetail.getState() + "," + addressDetail.getCountry() + "-" + addressDetail.getZip();
            address = address.replace("null,", "");
            address = address.replace(",null", "");
            address = address.replace("-null", "");
            address = address.replace("null-", "");
            //if (isDistanceInRange(addressDetail.getDistance())) {

                if (!this.arrayList.contains(address)) {

                    this.arrayList.add(address);
                    arrayAdapter.notifyDataSetChanged();
                }
                if(this.addressDetailsList==null){
                    this.addressDetailsList = new ArrayList<>();
                }
                this.addressDetailsList.add(addressDetail);
         //   } else {

          //  }
        }

        if(!this.arrayList.contains("Add New")){

            this.arrayList.add("Add New");
        }

        arrayAdapter.notifyDataSetChanged();

    }

    private void getStoredCustomerAddress(int customerId, StoreSummary storeSummary){

        try {
            CronetEngine cronetEngine = Cronet.getCronetEngine(this);
            FetchCustomerAttributeRequestCallback fetchCustomAttributeRequestCallback = new FetchCustomerAttributeRequestCallback(this);

            Executor executor = Executors.newSingleThreadExecutor();
            String url = Data.SERVER_URL + "/api/customer/attributes/" + customerId;
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    url, fetchCustomAttributeRequestCallback, executor);


            requestBuilder.addHeader("Content-Type", "application/json");
            requestBuilder.setHttpMethod("POST");

            ObjectMapper mapper = new ObjectMapper();
            byte[] bytes = mapper.writeValueAsBytes(storeSummary);

            UploadDataProvider provider = ByteBufferUploadProvider.create(bytes);
            requestBuilder.setUploadDataProvider(provider, executor);

            UrlRequest request = requestBuilder.build();
            request.start();

        } catch (Exception e) {

        }


    }

    private void setDeliveryCharge(String deliveryAddress){

            boolean addressInRangeFlag = false;
        if(!deliveryAddress.equalsIgnoreCase("Add New")){
            loginRepository.getCustomerEntity().setDeliveryAddress(deliveryAddress);
            loginRepository.getCustomerEntity().setDeliveryCharge(0.0);
            for(AddressDetail addressDetail : addressDetailsList){


                String address = Util.getFormattedDollarAmt(addressDetail.getDistance())+"-"+addressDetail.getTag()+"-"+(addressDetail.getFieldOne()!=null?addressDetail.getFieldOne():"")+","+addressDetail.getFieldTwo()+","+addressDetail.getCity()+","+addressDetail.getState()+","+addressDetail.getCountry()+"-"+addressDetail.getZip();
                address = address.replace("null,", "");
                address = address.replace(",null", "");
                address = address.replace("-null", "");
                address = address.replace("null-", "");

                if(address.equalsIgnoreCase(deliveryAddress)){

                    Double distance = addressDetail.getDistance();

                    DeliveryOptions deliveryOptions = loginRepository.getCustomerEntity().getStore().getStoreAttributes().getDeliveryOptions();
                    outerloop:
                    for(DeliveryPricing deliveryPricing :deliveryOptions.getDeliveryByStore().getPricing()){
                        if(distance > deliveryPricing.getMinDistance() && distance < deliveryPricing.getMaxDistance()){

                            for(DeliveryRules deliveryRules: deliveryPricing.getRules()){
                                if(deliveryOrderAmt >= deliveryRules.getMinAmt() && deliveryOrderAmt < deliveryRules.getMaxAmt())
                                {
                                    loginRepository.getCustomerEntity().setDeliveryCharge(deliveryRules.getDeliveryCharge());
                                    deliveryCharge=deliveryRules.getDeliveryCharge();
                                    deliveryChargeCalculated = deliveryCharge;
                                    payButton.setEnabled(true);
                                    payButton.setBackgroundColor(Color.parseColor("#3F51B5"));
                                    loginRepository.getCustomerEntity().setOrderCompletionMethod(2);
                                    showCardOptions();
                                    addressInRangeFlag=true;
                                    break outerloop;
                                }

                            }

                        }

                    }

                }
            }

           if(!addressInRangeFlag){
             //  arrayList.remove(deliveryAddress);
                ErrorDialog newFragment = new ErrorDialog("Sorry, Delivery Address is located outside the delivery area of the store", this);
                newFragment.setCancelable(false);
                newFragment.show(getSupportFragmentManager(), "error delivery");

            }

        }



    }

    private boolean isDistanceInRange(Double distance){

        //Double distance = addressDetail.getDistance();

        DeliveryOptions deliveryOptions = loginRepository.getCustomerEntity().getStore().getStoreAttributes().getDeliveryOptions();

        for(DeliveryPricing deliveryPricing :deliveryOptions.getDeliveryByStore().getPricing()){
            if(distance > deliveryPricing.getMinDistance() && distance < deliveryPricing.getMaxDistance()){
             return true;
            }

            }
        return false;

    }

    public void checkDineInOption(){
        loadingProgressBar.setVisibility(View.VISIBLE);
       // view=v;

        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);
        else {
            loginRepository.getCustomerEntity().setGPSEnabled(false);

            if(loginRepository.getCustomerEntity().getStore().getStoreAttributes().isDineInAllowed()) {
                loginRepository.setDineInOptionAllowed(true);
                //Please enable GPS
               DialogFragment newFragment = new DineInAllowedDialog(loginRepository.getCustomerEntity(), this);
                newFragment.setCancelable(false);
                newFragment.show(this.getSupportFragmentManager(), "missiles");
             /////   processOrder(view);
            }
            return;
        }

    }

    private void getInStoreResult(UserLocation userLocation){
        loginViewModel.getInStoreResult(userLocation,this);
    }

    private void callPopup(){

        loadingProgressBar.setVisibility(View.GONE);
        if(loginRepository.getCustomerEntity().isInstoreCheck()) {

            if(loginRepository.getCustomerEntity().getStore().getStoreAttributes().isDineInAllowed()) {
                loginRepository.setDineInOptionAllowed(true);
            }

        }
        else{
            loginRepository.setDineInOptionAllowed(false);

            //Dine in not allowed
            DialogFragment newFragment = new DineInErrorDialog("", this);
            newFragment.setCancelable(false);
            newFragment.show(this.getSupportFragmentManager(), "missiles");
        }
    }

    public void allow() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        else
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100, locationListener);


    }

    private String getMaxFreeDelOrderAmt() {
        DeliveryOptions deliveryOptions = loginRepository.getCustomerEntity().getStore().getStoreAttributes().getDeliveryOptions();
    if(deliveryOptions!=null) {
        for (DeliveryPricing deliveryPricing : deliveryOptions.getDeliveryByStore().getPricing()) {
            for (DeliveryRules deliveryRules : deliveryPricing.getRules()) {
                if (deliveryRules.getDeliveryCharge() == 0) {
                    return "$" + deliveryRules.getMinAmt().intValue();
                }
            }
        }
        return "$30";
    }
    else
        return "";
    }

}
