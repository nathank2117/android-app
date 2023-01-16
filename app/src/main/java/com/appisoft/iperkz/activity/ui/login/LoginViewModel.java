package com.appisoft.iperkz.activity.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.util.Patterns;

import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.activity.data.Result;
import com.appisoft.iperkz.activity.data.model.LoggedInUser;
import com.appisoft.iperkz.callback.DrivingDistanceCallbackMenu;
import com.appisoft.iperkz.entity.AddressDetail;
import com.appisoft.iperkz.entity.CustomerEntity;
import com.appisoft.iperkz.entity.CustomerOrderCreationRequest;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.entity.OfflinePaymentDetails;
import com.appisoft.iperkz.entity.PastOrderItem;
import com.appisoft.iperkz.entity.PaymentDetails;
import com.appisoft.iperkz.entity.Reward;
import com.appisoft.iperkz.entity.SimpleResponse;
import com.appisoft.iperkz.entity.Store;
import com.appisoft.iperkz.entity.UserLocation;
import com.appisoft.perkz.R;


public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private MutableLiveData<SimpleResponse> sendOtpResult = new MutableLiveData<>();
    private MutableLiveData<CustomerEntity> createCustomerResult = new MutableLiveData<>();
    private MutableLiveData<CustomerEntity> updateCustomerResult = new MutableLiveData<>();

    private MutableLiveData<String> paymentIntentClientSecret = new MutableLiveData<>();
    private MutableLiveData<Boolean> useExistingPaymentDetails = new MutableLiveData<>();
    private MutableLiveData<SimpleResponse> offlineProcessResult = new MutableLiveData<>();
    private MutableLiveData<PastOrderItem[]> pastOrdersResult = new MutableLiveData<>();
    private MutableLiveData<Reward> reward = new MutableLiveData<>();
    private MutableLiveData<Reward[]> perkzList = new MutableLiveData<>();
    private MutableLiveData<AddressDetail> addressDetail = new MutableLiveData<>();
    private MutableLiveData<SimpleResponse> inStoreResult = new MutableLiveData<>();
    private MutableLiveData<String> drivingDistanceResult = new MutableLiveData<>();

    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    LiveData<SimpleResponse> getSendOtpResult() {
        return sendOtpResult;
    }

    public MutableLiveData<PastOrderItem[]> getPastOrdersResult() {
        return pastOrdersResult;
    }

    public void setPastOrdersResult(MutableLiveData<PastOrderItem[]> pastOrdersResult) {
        this.pastOrdersResult = pastOrdersResult;
    }

    public void requestPaymentOptions(int customerId, Context context) {
        loginRepository.requestPaymentOptions(customerId, context, useExistingPaymentDetails);
    }
    public MutableLiveData<Boolean> getUseExistingPaymentDetails() {
        return useExistingPaymentDetails;
    }

    public void setUseExistingPaymentDetails(MutableLiveData<Boolean> useExistingPaymentDetails) {
        this.useExistingPaymentDetails = useExistingPaymentDetails;
    }

    public MutableLiveData<String> getPaymentIntentClientSecret() {
        return paymentIntentClientSecret;
    }

    public void setPaymentIntentClientSecret(MutableLiveData<String> paymentIntentClientSecret) {
        this.paymentIntentClientSecret = paymentIntentClientSecret;
    }

    public MutableLiveData<SimpleResponse> getOfflineProcessResult() {
        return offlineProcessResult;
    }

    public void setOfflineProcessResult(MutableLiveData<SimpleResponse> offlineProcessResult) {
        this.offlineProcessResult = offlineProcessResult;
    }

    public LiveData<CustomerEntity> getCreateCustomerResult() {
        return createCustomerResult;
    }

    public LiveData<CustomerEntity> getUpdateCustomerResult() {
        return updateCustomerResult;
    }

    public MutableLiveData<Reward> getReward() {
        return reward;
    }

    public MutableLiveData<Reward[]> getPerkzList() {
        return perkzList;
    }

    public MutableLiveData<AddressDetail> getAddressDetails() {
        return addressDetail;
    }

    public LiveData<SimpleResponse> getInStoreResult() {
        return inStoreResult;
    }

    public LiveData<String> getDrivingDistanceResult() {
        return drivingDistanceResult;
    }

    public void requestOTP(String phoneNumber, Context context) {
        // can be launched in a separate asynchronous job
     loginRepository.requestOTP(phoneNumber, context, sendOtpResult);
     /*
        if (result == true) {
            sendOtpResult.setValue(true);
        } else {
            sendOtpResult.setValue(false);
        }

      */
    }

    public void requestClientSecret(PaymentDetails paymentDetails, Context context) {
        // can be launched in a separate asynchronous job
        loginRepository.requestClientSecret(paymentDetails, context, paymentIntentClientSecret);

    }

/*
    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }
*/


    public void login(int customerId, String phoneNumber, String pin, Context context) {
        // can be launched in a separate asynchronous job
        loginRepository.login(customerId, phoneNumber, pin, context, loginResult);
    }

    public void processOfflinePayment(OfflinePaymentDetails offlinePaymentDetails,
                                      Context context) {
        // can be launched in a separate asynchronous job
        loginRepository.processOfflinePayment(offlinePaymentDetails,
                            context,
                            offlineProcessResult);
    }

    public void creatCustomer(CustomerEntity customerEntity, Context context) {
        // can be launched in a separate asynchronous job
        loginRepository.createCustomer(customerEntity, context, createCustomerResult);
    }

    public void updateCustomer(CustomerEntity customerEntity, Context context) {
        // can be launched in a separate asynchronous job
        loginRepository.updateCustomer(customerEntity, context, updateCustomerResult);
    }

    public void updateCustomerStore(CustomerEntity customerEntity, Context context) {
        // can be launched in a separate asynchronous job
        loginRepository.updateCustomerStore(customerEntity, context, updateCustomerResult);
    }
/*
    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }
    */

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    public void requestPastRecords(int customerId, Context context) {
        // can be launched in a separate asynchronous job
        loginRepository.requestPastRecords(customerId, context, pastOrdersResult);
     /*
        if (result == true) {
            sendOtpResult.setValue(true);
        } else {
            sendOtpResult.setValue(false);
        }

      */
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }


    public void setUpdateCustomerResult(MutableLiveData<CustomerEntity> updateCustomerResult) {
        this.updateCustomerResult = updateCustomerResult;
    }

    public void retrieveRewards( CustomerEntity customerEntity, Context context) {
        loginRepository.retrieveRewards(customerEntity, context, reward);
    }

    public void retrieveAllRewards( CustomerEntity customerEntity, Context context) {
        loginRepository.retrieveAllRewards(customerEntity, context, perkzList);
    }

    public void getInStoreResult(UserLocation userLocation, Context context){
        loginRepository.getInStoreResult(userLocation, context, inStoreResult);
    }

    public void getDrivingDistanceResult(Store store, UserLocation userLocation, Context context){
        loginRepository.getDrivingDistanceResult(store, userLocation, context, drivingDistanceResult);
    }

    public void getDrivingDistanceResultMenu(Store store, UserLocation userLocation, Context context){
        loginRepository.getDrivingDistanceResultMenu(store, userLocation, context, drivingDistanceResult, new DrivingDistanceCallbackMenu(context, drivingDistanceResult));
    }
}
