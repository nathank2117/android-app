package com.appisoft.iperkz.activity.data;

import static com.appisoft.iperkz.activity.RegistrationNewActivity.LOCATIONPREFERENCES;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.appisoft.iperkz.activity.PaymentActivity;
import com.appisoft.iperkz.activity.data.model.LoggedInUser;
import com.appisoft.iperkz.activity.ui.login.LoginResult;
import com.appisoft.iperkz.callback.DrivingDistanceCallbackMenu;
import com.appisoft.iperkz.entity.AppSettings;
import com.appisoft.iperkz.entity.CustomerEntity;
import com.appisoft.iperkz.entity.CustomerOrderCreationRequest;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.entity.OfflinePaymentDetails;
import com.appisoft.iperkz.entity.PastOrderItem;
import com.appisoft.iperkz.entity.PaymentDetails;
import com.appisoft.iperkz.entity.Reward;
import com.appisoft.iperkz.entity.SimpleResponse;
import com.appisoft.iperkz.entity.Store;
import com.appisoft.iperkz.entity.StoreTypes;
import com.appisoft.iperkz.entity.UserLocation;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;
    public Reward reward = null;
    private LoginDataSource dataSource;
    private String phoneNumber = "";
    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;
    private CustomerEntity customerEntity = null;
    private boolean dineInOptionAllowed = true;
    private StoreTypes storeTypes = null;
    private AppSettings appSettings = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public void login(int customerId, String phoneNumber, String pin, Context context, MutableLiveData<LoginResult> loginResult) {
        // handle login
        dataSource.login(customerId, phoneNumber, pin, context, loginResult);
        /*
        NATHAN
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }

         */

    }

    public void processOfflinePayment(OfflinePaymentDetails offlinePaymentDetails,
                                      Context context,
                                      MutableLiveData<SimpleResponse> offlineProcessResult) {
        dataSource.processOfflinePayment(offlinePaymentDetails, context, offlineProcessResult);
    }

    public void requestOTP(String phoneNumber, Context context, MutableLiveData<SimpleResponse> sendOtpResult ) {
        // handle login
        dataSource.requestOTP(phoneNumber, context, sendOtpResult);
        this.phoneNumber = phoneNumber;
    }
    public void requestClientSecret(PaymentDetails paymentDetails, Context context, MutableLiveData<String> sendClientSecret ) {
        // handle login
        dataSource.requestClientSecret(paymentDetails, context, sendClientSecret);
        this.phoneNumber = phoneNumber;
    }


    public void createCustomer(CustomerEntity customerEntity, Context context, MutableLiveData<CustomerEntity> createCustomerResult) {
        // handle login
        dataSource.createCustomer(customerEntity, context, createCustomerResult);
    }

    public void updateCustomer(CustomerEntity customerEntity, Context context, MutableLiveData<CustomerEntity> updateCustomerResult) {
        // handle login[
        dataSource.updateCustomer(customerEntity, context, updateCustomerResult);
    }

    public void updateCustomerStore(CustomerEntity customerEntity, Context context, MutableLiveData<CustomerEntity> updateCustomerResult) {
        // handle login[
        dataSource.updateCustomerStore(customerEntity, context, updateCustomerResult);
    }
    public void requestPaymentOptions(int customerId, Context context, MutableLiveData<Boolean> useExistingPaymentDetails) {
        // handle login
        dataSource.requestPaymentOptions(customerId, context, useExistingPaymentDetails);
    }

    public void requestPastRecords(int customerId, Context context, MutableLiveData<PastOrderItem[]> pastOrdersResult ) {
        dataSource.requestPastRecords(customerId, context, pastOrdersResult);
    }

    public void retrieveRewards( CustomerEntity customerEntity, Context context, MutableLiveData<Reward> reward) {
        // handle login
        dataSource.retrieveRewards(customerEntity, context, reward);
    }

    public void retrieveAllRewards( CustomerEntity customerEntity, Context context, MutableLiveData<Reward[]> rewards) {
        // handle login
        dataSource.retrieveAllRewards(customerEntity, context, rewards);
    }

    public void getInStoreResult(UserLocation userLocation, Context context, MutableLiveData<SimpleResponse> inStoreResult) {

        dataSource.getInStoreResult(userLocation, context, inStoreResult);
    }

    public void getDrivingDistanceResult(Store store, UserLocation userLocation, Context context, MutableLiveData<String> drivingDistanceResult ) {

        dataSource.getDrivingDistanceResult(store, userLocation, context, drivingDistanceResult);
    }

    public void getDrivingDistanceResultMenu(Store store, UserLocation userLocation, Context context, MutableLiveData<String> drivingDistanceResult, DrivingDistanceCallbackMenu drivingDistanceCallbackMenu) {

        dataSource.getDrivingDistanceResultMenu(store, userLocation, context, drivingDistanceResult, drivingDistanceCallbackMenu);
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public CustomerEntity getCustomerEntity() {
        return customerEntity;
    }

    public void setCustomerEntity(CustomerEntity customerEntity) {
        this.customerEntity = customerEntity;
    }

    public boolean isDineInOptionAllowed() {
        return dineInOptionAllowed;
    }

    public void setDineInOptionAllowed(boolean dineInOptionAllowed) {
        this.dineInOptionAllowed = dineInOptionAllowed;
    }

    public StoreTypes getStoreTypes() {
        return storeTypes;
    }

    public void setStoreTypes(StoreTypes storeTypes) {
        this.storeTypes = storeTypes;
    }

    public AppSettings getAppSettings() {
        /*
         if (appSettings == null) {
             //try to get from memory
             SharedPreferences sharedpreferences = getContext().getSharedPreferences(LOCATIONPREFERENCES, Context.MODE_PRIVATE);
             String appsettingsJson = sharedpreferences.getString("APPSETTINGS", null);
             ObjectMapper mapper11 = new ObjectMapper();
             AppSettings appSettings = null;
             try {
                 if (appsettingsJson != null)
                     appSettings = mapper11.readValue(appsettingsJson, AppSettings.class);


             } catch (IOException e) {
                 e.printStackTrace();
             }
             return appSettings;
         }
         */
        return appSettings;
    }

    public void setAppSettings(AppSettings appSettings) {
        this.appSettings = appSettings;
    }
}
