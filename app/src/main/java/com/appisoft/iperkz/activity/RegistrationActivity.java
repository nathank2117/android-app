package com.appisoft.iperkz.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.activity.data.model.RegistrationFormState;
import com.appisoft.iperkz.activity.ui.login.LoginResult;
import com.appisoft.iperkz.activity.ui.login.LoginViewModel;
import com.appisoft.iperkz.activity.ui.login.LoginViewModelFactory;
import com.appisoft.iperkz.activity.ui.login.ValidateOtp;
import com.appisoft.iperkz.callback.SendOtpRequestCallback;
import com.appisoft.iperkz.callback.StoreDetailsRequestCallback;
import com.appisoft.iperkz.callback.VerifyOtpRequestCallback;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.Credentials;
import com.appisoft.iperkz.entity.CustomerEntity;
import com.appisoft.iperkz.entity.Store;
import com.appisoft.iperkz.entity.uploader.ByteBufferUploadProvider;
import com.appisoft.iperkz.util.Util;
import com.appisoft.perkz.DisplayMessageActivity;
import com.appisoft.perkz.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodiebag.pinview.Pinview;

import org.chromium.net.CronetEngine;
import org.chromium.net.UploadDataProvider;
import org.chromium.net.UrlRequest;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegistrationActivity extends AppCompatActivity  implements View.OnClickListener {

    private  ProgressBar loadingProgressBar;
    private Button confirmButton;
    private TextView storeDetails;


    public CheckBox termsCheckBox;
    CustomerEntity customerItem = new CustomerEntity();
    Store store = new Store();
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    private LoginViewModel loginViewModel;
    //LiveData<CustomerEntity> formData =  new MutableLiveData<>();
    private MutableLiveData<RegistrationFormState> loginFormState = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        loginFormState.setValue(new RegistrationFormState(false));
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        loadingProgressBar = findViewById(R.id.loading);
        final Pinview pinEntryEditText = findViewById(R.id.storeCode);

        confirmButton = findViewById(R.id.storeConfirmButton);
        storeDetails = findViewById(R.id.storeDetails);

        confirmButton.setOnClickListener(this);

        SpannableString content = new SpannableString("Terms & Conditions");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);


        pinEntryEditText.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                if (pinview.getValue().toString().length() == 4) {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    callGetStoreDetailsService(pinEntryEditText.getValue().toString());
                }
            }

        });

        loginViewModel.getCreateCustomerResult().observe(this, new Observer<CustomerEntity>() {
            @Override
            public void onChanged(@Nullable CustomerEntity customer) {
                if (customer == null) {
                    return;
                }

                if (customer.getCustomerId() <= 0 ) {
                    createCustomerFailed();
                }
                if (customer.getCustomerId() > 0) {
                    updateUiWithOtpSendSuccess(customer);
                }

                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful

            }
        });

    }

    private void createCustomerFailed() {
        Toast.makeText(getApplicationContext(), "Could not create customer", Toast.LENGTH_SHORT).show();
    }

    private void updateUiWithOtpSendSuccess(CustomerEntity customer) {
        customer.setStore(this.store);
        loginRepository.setCustomerEntity(customer);

        SharedPreferences.Editor editor = getSharedPreferences("CUSTOMER_DETAILS", MODE_PRIVATE).edit();
        editor.putBoolean("REGISTERED_USER", false);
        ObjectMapper mapper = new ObjectMapper();
        String customerJson = "";
        try {
            customerJson = mapper.writeValueAsString(customer);
        } catch (Exception e) {

        }
        editor.putString("CUSTOMER", customerJson);
        editor.commit();
        // Toast.makeText(getApplicationContext(), "OTP Successfuly sent", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        startActivity(intent);
    }

    private void callGetStoreDetailsService(String storeCode) {
        try {

            CronetEngine cronetEngine = Cronet.getCronetEngine(this);
            StoreDetailsRequestCallback otpRequestCallback = new StoreDetailsRequestCallback(this );

            Executor executor = Executors.newSingleThreadExecutor();
            String url = Data.SERVER_URL + "/api/store/"+storeCode;
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    url, otpRequestCallback, executor);
            UrlRequest request = requestBuilder.build();
            request.start();

        } catch (Exception e) {

        }
    }

    public void showStoreDetails(Store store) {
        this.store = store;
        loadingProgressBar.setVisibility(View.GONE);
        String details = store.getStoreName() + "\n";
        details += store.getAddressOne();
        storeDetails.setText(details);
        storeDetails.setVisibility(View.VISIBLE);
        confirmButton.setVisibility(View.VISIBLE);
        Util.hideKeyboard(this);
    }

    public void showNoMatchingStoreDetails () {
        loadingProgressBar.setVisibility(View.GONE);

        storeDetails.setText("No Matches! Enter correct store code");
        storeDetails.setVisibility(View.VISIBLE);
        confirmButton.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.storeConfirmButton) {

            if ( loginRepository.getCustomerEntity() != null &&
                    loginRepository.getCustomerEntity().getCustomerId() > 0 ) {
                loginRepository.getCustomerEntity().setStoreId(store.getStoreId());
                loginRepository.getCustomerEntity().setStore(this.store);

                Intent intent = new Intent(this, DisplayMessageActivity.class);
                startActivity(intent);
            } else {
                CustomerEntity customerEntity = populateCustomerDetails();
                loginViewModel.creatCustomer(customerEntity, RegistrationActivity.this);
            }
        }

    }

    private CustomerEntity populateCustomerDetails() {

      //  item.setFirstName();

        RegistrationFormState formState = loginFormState.getValue();
        customerItem.setFirstName("Guest");
        customerItem.setLastName("LN");
        customerItem.setEmail("guest@iperkz.com");
        customerItem.setPhoneNumber("");
        //customerItem.setPhoneNumber(loginRepository.getPhoneNumber());
        customerItem.setStoreId(store.getStoreId());
        //customerItem.setPhoneNumber("2222");
        customerItem.setCompany("wip");
        return customerItem;
    }

}
