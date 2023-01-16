package com.appisoft.iperkz.activity.data;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.appisoft.iperkz.activity.data.model.LoggedInUser;
import com.appisoft.iperkz.activity.ui.login.LoginResult;
import com.appisoft.iperkz.callback.CreateCustomerRequestCallback;
import com.appisoft.iperkz.callback.DrivingDistanceCallback;
import com.appisoft.iperkz.callback.DrivingDistanceCallbackMenu;
import com.appisoft.iperkz.callback.GetPaymentIntentRequestCallback;
import com.appisoft.iperkz.callback.InStoreCheckCallback;
import com.appisoft.iperkz.callback.MenuListServiceRequestCallback;
import com.appisoft.iperkz.callback.OfflineProcessRequestCallback;
import com.appisoft.iperkz.callback.PastOrderRequestCallback;
import com.appisoft.iperkz.callback.PaymentOptionsRequestCallback;
import com.appisoft.iperkz.callback.RewardsRequestCallback;
import com.appisoft.iperkz.callback.SendOtpRequestCallback;
import com.appisoft.iperkz.callback.UpdateCustomerRequestCallback;
import com.appisoft.iperkz.callback.VerifyOtpRequestCallback;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.Credentials;
import com.appisoft.iperkz.entity.CustomerEntity;
import com.appisoft.iperkz.entity.CustomerOrderCreationRequest;
import com.appisoft.iperkz.entity.DrivingPoints;
import com.appisoft.iperkz.entity.MenuFilterCriteria;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.entity.OfflinePaymentDetails;
import com.appisoft.iperkz.entity.PastOrderItem;
import com.appisoft.iperkz.entity.PaymentDetails;
import com.appisoft.iperkz.entity.Reward;
import com.appisoft.iperkz.entity.SimpleResponse;
import com.appisoft.iperkz.entity.Store;
import com.appisoft.iperkz.entity.UserLocation;
import com.appisoft.iperkz.entity.uploader.ByteBufferUploadProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.appisoft.iperkz.callback.RewardsAllRequestCallback;
import org.chromium.net.CronetEngine;
import org.chromium.net.UploadDataProvider;
import org.chromium.net.UrlRequest;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
/*
    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }
*/

    public void login(int customerId, String phoneNumber, String pin, Context context, MutableLiveData<LoginResult> loginResult) {

        try {
            CronetEngine cronetEngine = Cronet.getCronetEngine(context);
            VerifyOtpRequestCallback verifyOtpRequestCallback = new VerifyOtpRequestCallback(context, loginResult );

            Executor executor = Executors.newSingleThreadExecutor();
            String url = Data.SERVER_URL + "/api/ios/verify";

            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    url, verifyOtpRequestCallback, executor);
            requestBuilder.addHeader("Content-Type","application/json");
            requestBuilder.setHttpMethod("POST");

            Credentials creds = new Credentials();
            creds.setPhoneNumber(phoneNumber);
            creds.setPin(pin);
            creds.setCustomerId(customerId);
            ObjectMapper mapper = new ObjectMapper();
            byte[] bytes = mapper.writeValueAsBytes(creds);

            UploadDataProvider provider = ByteBufferUploadProvider.create(bytes);
            requestBuilder.setUploadDataProvider(provider, executor);

            UrlRequest request = requestBuilder.build();
            request.start();
        } catch (Exception e) {

        }
    }
    public void requestOTP(String phoneNumber, Context context, MutableLiveData<SimpleResponse> sendOtpResult ) {

        try {
            CronetEngine cronetEngine = Cronet.getCronetEngine(context);
            SendOtpRequestCallback otpRequestCallback = new SendOtpRequestCallback(context, sendOtpResult );

            Executor executor = Executors.newSingleThreadExecutor();
            String url = Data.SERVER_URL + "/api/sendotp/"+phoneNumber;
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    url, otpRequestCallback, executor);

            UrlRequest request = requestBuilder.build();
            request.start();

        } catch (Exception e) {

        }
    }



    public void createCustomer(CustomerEntity customer, Context context, MutableLiveData<CustomerEntity> createCustomerResult ) {

        try {
            CronetEngine cronetEngine = Cronet.getCronetEngine(context);
            CreateCustomerRequestCallback createCustomerRequestCallback = new CreateCustomerRequestCallback(context, createCustomerResult );

            Executor executor = Executors.newSingleThreadExecutor();
            String url = Data.SERVER_URL + "/api/customer";
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    url, createCustomerRequestCallback, executor);


            requestBuilder.addHeader("Content-Type","application/json");
            requestBuilder.setHttpMethod("POST");


            ObjectMapper mapper = new ObjectMapper();
            byte[] bytes = mapper.writeValueAsBytes(customer);

            UploadDataProvider provider = ByteBufferUploadProvider.create(bytes);
            requestBuilder.setUploadDataProvider(provider, executor);


            UrlRequest request = requestBuilder.build();
            request.start();

        } catch (Exception e) {

        }
    }

    public void updateCustomer(CustomerEntity customer, Context context, MutableLiveData<CustomerEntity> updateCustomerResult ) {

        try {
            CronetEngine cronetEngine = Cronet.getCronetEngine(context);
            UpdateCustomerRequestCallback updateCustomerRequestCallback = new UpdateCustomerRequestCallback(context, updateCustomerResult );

            Executor executor = Executors.newSingleThreadExecutor();
            String url = Data.SERVER_URL + "/api/register";
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    url, updateCustomerRequestCallback, executor);


            requestBuilder.addHeader("Content-Type","application/json");
            requestBuilder.setHttpMethod("POST");


            ObjectMapper mapper = new ObjectMapper();
            byte[] bytes = mapper.writeValueAsBytes(customer);

            UploadDataProvider provider = ByteBufferUploadProvider.create(bytes);
            requestBuilder.setUploadDataProvider(provider, executor);

            UrlRequest request = requestBuilder.build();
            request.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCustomerStore(CustomerEntity customer, Context context, MutableLiveData<CustomerEntity> updateCustomerResult ) {

        try {
            CronetEngine cronetEngine = Cronet.getCronetEngine(context);
            UpdateCustomerRequestCallback updateCustomerRequestCallback = new UpdateCustomerRequestCallback(context, updateCustomerResult );

            Executor executor = Executors.newSingleThreadExecutor();
            String url = Data.SERVER_URL + "/api/customer/store";
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    url, updateCustomerRequestCallback, executor);

            requestBuilder.addHeader("Content-Type","application/json");
            requestBuilder.setHttpMethod("POST");

            ObjectMapper mapper = new ObjectMapper();
            byte[] bytes = mapper.writeValueAsBytes(customer);

            UploadDataProvider provider = ByteBufferUploadProvider.create(bytes);
            requestBuilder.setUploadDataProvider(provider, executor);

            UrlRequest request = requestBuilder.build();
            request.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestClientSecret(PaymentDetails paymentDetails, Context context, MutableLiveData<String> clientSecrectResult ) {

        try {
            CronetEngine cronetEngine = Cronet.getCronetEngine(context);
            GetPaymentIntentRequestCallback createCustomerRequestCallback =
                        new GetPaymentIntentRequestCallback(context, clientSecrectResult );

            Executor executor = Executors.newSingleThreadExecutor();
            String url = Data.SERVER_URL + "/api/getintent";
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    url, createCustomerRequestCallback, executor);


            requestBuilder.addHeader("Content-Type","application/json");
            requestBuilder.setHttpMethod("POST");


            ObjectMapper mapper = new ObjectMapper();
            byte[] bytes = mapper.writeValueAsBytes(paymentDetails);

            UploadDataProvider provider = ByteBufferUploadProvider.create(bytes);
            requestBuilder.setUploadDataProvider(provider, executor);


            UrlRequest request = requestBuilder.build();
            request.start();

        } catch (Exception e) {

        }
    }

    public void requestPaymentOptions(int customerId, Context context, MutableLiveData<Boolean> useExistingPaymentDetails ) {

        try {
            CronetEngine cronetEngine = Cronet.getCronetEngine(context);
            PaymentOptionsRequestCallback getPaymentIntentRequestCallback =
                    new PaymentOptionsRequestCallback(context, useExistingPaymentDetails );

            Executor executor = Executors.newSingleThreadExecutor();
            String url = Data.SERVER_URL + "/api/paymentoptions/"+customerId;
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    url, getPaymentIntentRequestCallback, executor);

            UrlRequest request = requestBuilder.build();
            request.start();

        } catch (Exception e) {

        }
    }

    public void processOfflinePayment(OfflinePaymentDetails offlinePaymentDetails,
                                      Context context,
                                      MutableLiveData<SimpleResponse> offlineProcessResult) {
        try {
            CronetEngine cronetEngine = Cronet.getCronetEngine(context);
            OfflineProcessRequestCallback createCustomerRequestCallback =
                    new OfflineProcessRequestCallback(context, offlineProcessResult );

            Executor executor = Executors.newSingleThreadExecutor();
            String url = Data.SERVER_URL + "/api/chargeoffseason";
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    url, createCustomerRequestCallback, executor);


            requestBuilder.addHeader("Content-Type","application/json");
            requestBuilder.setHttpMethod("POST");


            ObjectMapper mapper = new ObjectMapper();
            byte[] bytes = mapper.writeValueAsBytes(offlinePaymentDetails);

            UploadDataProvider provider = ByteBufferUploadProvider.create(bytes);
            requestBuilder.setUploadDataProvider(provider, executor);


            UrlRequest request = requestBuilder.build();
            request.start();

        } catch (Exception e) {

        }
    }

    public void requestPastRecords(int customerId, Context context, MutableLiveData<PastOrderItem[]> pastOrdersResult ) {
        CronetEngine cronetEngine = Cronet.getCronetEngine(context);
        PastOrderRequestCallback menuCallback = new PastOrderRequestCallback(context, pastOrdersResult);

        UrlRequest.Callback callback = menuCallback;

        Executor executor = Executors.newSingleThreadExecutor();
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                Data.SERVER_URL + "/api/order/" + customerId,
                callback, executor);

        UrlRequest request = requestBuilder.build();
        request.start();
    }


    public void retrieveRewards(CustomerEntity customerEntity, Context context, MutableLiveData<Reward> rewards ) {

        try {
            CronetEngine cronetEngine = Cronet.getCronetEngine(context);
            RewardsRequestCallback rewardsRequestCallback =
                    new RewardsRequestCallback(context, rewards);

            Executor executor = Executors.newSingleThreadExecutor();
            String url = Data.SERVER_URL + "/api/perkz/"+customerEntity.getStoreId()+"/"+customerEntity.getCustomerId();
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    url, rewardsRequestCallback, executor);

            UrlRequest request = requestBuilder.build();
            request.start();

        } catch (Exception e) {

        }
    }

    public void retrieveAllRewards(CustomerEntity customerEntity, Context context, MutableLiveData<Reward[]> rewards ) {

        try {
            CronetEngine cronetEngine = Cronet.getCronetEngine(context);
            RewardsAllRequestCallback rewardsRequestCallback =
                    new RewardsAllRequestCallback(context, rewards);

            Executor executor = Executors.newSingleThreadExecutor();
            String url = Data.SERVER_URL + "/api/allperkz/"+customerEntity.getCustomerId();
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    url, rewardsRequestCallback, executor);

            UrlRequest request = requestBuilder.build();
            request.start();

        } catch (Exception e) {

        }
    }

    public void getInStoreResult(UserLocation userLocation, Context context, MutableLiveData<SimpleResponse> inStoreResult ) {

        try {
            CronetEngine cronetEngine = Cronet.getCronetEngine(context);
            InStoreCheckCallback callbackObject = new InStoreCheckCallback(context, inStoreResult );

            Executor executor = Executors.newSingleThreadExecutor();
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    Data.SERVER_URL + "/api/ios/instore", callbackObject, executor);

            requestBuilder.addHeader("Content-Type","application/json");
            requestBuilder.setHttpMethod("POST");

            try{
                ObjectMapper mapper = new ObjectMapper();
                byte[] bytes = mapper.writeValueAsBytes(userLocation);

                UploadDataProvider provider = ByteBufferUploadProvider.create(bytes);
                requestBuilder.setUploadDataProvider(provider, executor);
                UrlRequest request = requestBuilder.build();
                request.start();
            }catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {

        }
    }

    public void getDrivingDistanceResult(Store store, UserLocation userLocation, Context context, MutableLiveData<String> drivingDistanceResult ) {

        DrivingPoints drivingPoints = new DrivingPoints();
        drivingPoints.setStoreLatitude(store.getLatitude());
        drivingPoints.setStoreLongitude(store.getLongitude());
        drivingPoints.setUserLatitude(userLocation.getLatitude());
        drivingPoints.setUserLongitude(userLocation.getLongitude());

        //call the service to get the Data
        CronetEngine cronetEngine = Cronet.getCronetEngine(context);
        DrivingDistanceCallback drivingDistanceCallback = new DrivingDistanceCallback(context, drivingDistanceResult) ;

        Executor executor = Executors.newSingleThreadExecutor();
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                Data.SERVER_URL + "/api/store/distance", drivingDistanceCallback, executor);

        requestBuilder.addHeader("Content-Type","application/json");
        requestBuilder.setHttpMethod("POST");
        try{
            ObjectMapper mapper = new ObjectMapper();
            byte[] bytes = mapper.writeValueAsBytes(drivingPoints);

            UploadDataProvider provider = ByteBufferUploadProvider.create(bytes);
            requestBuilder.setUploadDataProvider(provider, executor);
            UrlRequest request = requestBuilder.build();
            request.start();
        }catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void getDrivingDistanceResultMenu(Store store, UserLocation userLocation, Context context, MutableLiveData<String> drivingDistanceResult, DrivingDistanceCallbackMenu drivingDistanceCallbackMenu) {

        DrivingPoints drivingPoints = new DrivingPoints();
        drivingPoints.setStoreLatitude(store.getLatitude());
        drivingPoints.setStoreLongitude(store.getLongitude());
        drivingPoints.setUserLatitude(userLocation.getLatitude());
        drivingPoints.setUserLongitude(userLocation.getLongitude());

        //call the service to get the Data
        CronetEngine cronetEngine = Cronet.getCronetEngine(context);
        //DrivingDistanceCallback drivingDistanceCallback = new DrivingDistanceCallback(context, drivingDistanceResult) ;

        Executor executor = Executors.newSingleThreadExecutor();
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                Data.SERVER_URL + "/api/store/distance", drivingDistanceCallbackMenu, executor);

        requestBuilder.addHeader("Content-Type","application/json");
        requestBuilder.setHttpMethod("POST");
        try{
            ObjectMapper mapper = new ObjectMapper();
            byte[] bytes = mapper.writeValueAsBytes(drivingPoints);

            UploadDataProvider provider = ByteBufferUploadProvider.create(bytes);
            requestBuilder.setUploadDataProvider(provider, executor);
            UrlRequest request = requestBuilder.build();
            request.start();
        }catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
