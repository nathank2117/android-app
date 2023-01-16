package com.appisoft.iperkz.activity.ui.login;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.appisoft.iperkz.activity.PaymentActivity;
import com.appisoft.iperkz.activity.RegistrationActivity;
import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.activity.data.Result;
import com.appisoft.iperkz.activity.data.model.LoggedInUser;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.SimpleResponse;
import com.appisoft.perkz.DisplayMessageActivity;
import com.appisoft.perkz.R;

public class ValidateOtp extends AppCompatActivity  {

    private TextView resendOTPTextView;
    private TextView resendSuccessTextView;

    private LoginViewModel loginViewModel;
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    private String otpVerifyResultMessage = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_otp);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        resendOTPTextView = findViewById(R.id.resendOTP);
        resendSuccessTextView = findViewById(R.id.resendSuccess);

        final TextView titleMessageEditText = findViewById(R.id.title_message);
        final EditText pinEntryEditText = findViewById(R.id.pinEntry);
        final TextView failureMessgTextView = findViewById(R.id.failureMessg);

        titleMessageEditText.setText("Enter the 4-digit code sent to you at \n" + loginRepository.getPhoneNumber());
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final Context context = this;

        final androidx.appcompat.app.ActionBar supportActionBar = getSupportActionBar();
        try {
            String title = Data.PROJECT_NAME + " - " +
                    loginRepository.getCustomerEntity().getStore().getStoreName();
            supportActionBar.setTitle(title);
        } catch (Exception e){

        }
        /*
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });
        */
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                otpVerifyResultMessage = loginResult.getSuccess().getPerkzStatus();
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful

            }
        });


        pinEntryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {}

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() == 4) {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    loginViewModel.login(loginRepository.getCustomerEntity().getCustomerId(),loginRepository.getPhoneNumber(),
                            pinEntryEditText.getText().toString(), context);
                }
            }
        });

        loginViewModel.getSendOtpResult().observe(this, new Observer<SimpleResponse>() {
            @Override
            public void onChanged(@Nullable SimpleResponse sendOtpResult) {
                if (sendOtpResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                otpVerifyResultMessage = sendOtpResult.getMessage();

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

        resendOTPTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                resendOTPTextView.setClickable(false);
                resendSuccessTextView.setVisibility(View.GONE);
                loginViewModel.requestOTP(loginRepository.getPhoneNumber(), context);
            }
        });


    }

    private void updateUiWithUser(LoggedInUserView model) {
        SharedPreferences.Editor editor = getSharedPreferences("CUSTOMER_DETAILS", MODE_PRIVATE).edit();
        editor.putBoolean("REGISTERED_USER", true);
        editor.commit();
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("isPerkzUsed", otpVerifyResultMessage);

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        final TextView failureMessgTextView = findViewById(R.id.failureMessg);
        failureMessgTextView.setVisibility(View.VISIBLE);
    }

    private void showSendOTPFailed(SimpleResponse response) {

        //Nathan: use response to show error message
        Toast.makeText(getApplicationContext(), "Error Sending OTP", Toast.LENGTH_SHORT).show();
        resendSuccessTextView.setVisibility(View.VISIBLE);
    }

    private void updateUiWithOtpSendSuccess() {
        resendOTPTextView.setClickable(true);
        resendSuccessTextView.setVisibility(View.VISIBLE);
    }

}
