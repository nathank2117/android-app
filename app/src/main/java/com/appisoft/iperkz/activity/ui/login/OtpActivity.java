package com.appisoft.iperkz.activity.ui.login;

import android.app.Activity;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.appisoft.iperkz.activity.ViewCartActivity;
import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.activity.ui.login.LoginViewModel;
import com.appisoft.iperkz.activity.ui.login.LoginViewModelFactory;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.CustomerEntity;
import com.appisoft.iperkz.entity.SimpleResponse;
import com.appisoft.iperkz.util.Util;
import com.appisoft.perkz.DisplayMessageActivity;
import com.appisoft.perkz.R;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class OtpActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());

    private Button sendOtpButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        //SharedPreferences prefs = this.getSharedPreferences("CUSTOMER_DETAILS", Context.MODE_PRIVATE);
       // boolean isRegisteredUser = prefs.getBoolean("REGISTERED_USER", false );

        final androidx.appcompat.app.ActionBar supportActionBar = getSupportActionBar();
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        try {
            String title = loginRepository.getCustomerEntity().getStore().getStoreName();
            supportActionBar.setTitle(title);
        } catch (Exception e){

        }

        SharedPreferences prefs = this.getSharedPreferences("CUSTOMER_DETAILS", Context.MODE_PRIVATE);
        boolean isRegisteredUser = prefs.getBoolean("REGISTERED_USER", false );

        if ( isRegisteredUser) {
            /*
            String customerJson = prefs.getString("CUSTOMER", null);
            ObjectMapper mapper = new ObjectMapper();
            try {
                CustomerEntity entity = mapper.readValue(customerJson, CustomerEntity.class);
                loginRepository.setCustomerEntity(entity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            Intent intent = new Intent(this, DisplayMessageActivity.class);
            startActivity(intent);
            finish();
        }


        final EditText phonenumberEditText = findViewById(R.id.phonenumber);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        final EditText phoneNumber = findViewById(R.id.phonenumber);

        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        phonenumberEditText.requestFocus();

        /*
        View mainView = phonenumberEditText.getRootView();
        mainView.getBackground().setAlpha(10);
        */
        TextWatcher afterTextChangedListener = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore

            }

            @Override
            public void afterTextChanged(Editable s) {
               // loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                 //       passwordEditText.getText().toString());
                if (s.length() == 14) {
                    Util.hideKeyboard(OtpActivity.this);
                    sendOtpButton.setEnabled(true);
                } else {
                    sendOtpButton.setEnabled(false);
                }
            }
        };
        phonenumberEditText.addTextChangedListener(afterTextChangedListener);

        loginViewModel.getSendOtpResult().observe(this, new Observer<SimpleResponse>() {
            @Override
            public void onChanged(@Nullable SimpleResponse sendOtpResult) {
                if (sendOtpResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (sendOtpResult.isResult() == false) {
                    showSendOTPFailed(sendOtpResult);
                }
                if (sendOtpResult.isResult() == true) {
                    updateUiWithOtpSendSuccess();
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
              //  finish();
            }
        });
        final Context context = this;
        phonenumberEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                sendOtpButton.setEnabled(false);
                loginViewModel.requestOTP(phoneNumber.getText().toString(), context);
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        //Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void updateUiWithOtpSendSuccess() {
        sendOtpButton.setEnabled(true);
        Intent intent = new Intent(this, ValidateOtp.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void showSendOTPFailed(SimpleResponse response) {
        Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
