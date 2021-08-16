package com.appisoft.iperkz.callback;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.appisoft.iperkz.activity.PaymentActivity;
import com.appisoft.iperkz.activity.RegistrationActivity;
import com.appisoft.iperkz.activity.RegistrationNewActivity;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.CustomerEntity;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.perkz.RequestStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.LinkedHashMap;

public class UpdateCustomerRequestCallback extends UrlRequest.Callback {

    private static final String TAG = "UpdateCustomerRequest";

    private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);
    public long start;
    private Activity mainActivity;
    private MutableLiveData<CustomerEntity> updateCustomerResult;
    private String activityName = "";

    public UpdateCustomerRequestCallback(Context context, MutableLiveData<CustomerEntity> updateCustomerResult) {
        this.updateCustomerResult = updateCustomerResult;
        activityName = context.getClass().getSimpleName() ;
        if (activityName.equalsIgnoreCase("PaymentActivity") ){
            this.mainActivity = (PaymentActivity)context;
        } else if (activityName.equalsIgnoreCase("RegistrationNewActivity")) {
            this.mainActivity = (RegistrationNewActivity)context;
        }
    }

    @Override
    public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
        Log.i(TAG, "onRedirectReceived method called.");
        // You should call the request.followRedirect() method to continue
        // processing the request.
        request.followRedirect();
    }

    @Override
    public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
        Log.i(TAG, "onResponseStarted method called.");
        // You should call the request.read() method before the request can be
        // further processed. The following instruction provides a ByteBuffer object
        // with a capacity of 102400 bytes to the read() method.
        request.read(ByteBuffer.allocateDirect(102400));
    }

    @Override
    public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) throws Exception {
        Log.i(TAG, "onReadCompleted method called.");
        // You should keep reading the request until there's no more data.
        byteBuffer.flip();
        try {
            receiveChannel.write(byteBuffer);
        } catch (IOException e) {
            Log.i(TAG, "IOException during ByteBuffer read. Details: ", e);
        }
        byteBuffer.clear();
        request.read(byteBuffer);
    }


    @Override
    public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
       // Log.i(TAG, "onSucceeded method called." + bytesReceived);
        String returnedData = bytesReceived.toString();
        ObjectMapper mapper = new ObjectMapper();
        RequestStatus status = new RequestStatus();
        try {
            status = mapper.readValue(returnedData, RequestStatus.class);
        } catch (Exception e) {

        }
        final RequestStatus statusResponse = status;

        final LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) status.getPayload().get("CUSTOMER_DETAILS");
        int x = 2;
        mainActivity.runOnUiThread(new Runnable() {
                                       public void run() {
                                           if (statusResponse.isResult() == true) {
                                               CustomerEntity customerEntity = new CustomerEntity();
                                               customerEntity.setCustomerId((Integer) map.get("customerId"));
                                               customerEntity.setEmail((String) map.get("email"));
                                               customerEntity.setFirstName((String) map.get("firstName"));
                                               customerEntity.setLastName((String) map.get("lastName"));
                                               customerEntity.setMiddleName((String) map.get("middleName"));
                                               customerEntity.setCompany((String) map.get("company"));
                                               customerEntity.setStoreId((Integer) map.get("storeId"));
                                               customerEntity.setPhoneNumber((String) map.get("phoneNumber"));
                                               updateCustomerResult.setValue(customerEntity);
                                           } else {
                                               updateCustomerResult.setValue(null);
                                           }
                                       }
                                   }

        );
    }

    private void storeInCache(String mealType, MenuItem[] items) {
        if (mealType != null) {
            if (mealType.equalsIgnoreCase(Data.BREAKFAST)) {
                Data.getInstance().setBreakfastList(items);
            }
            if (mealType.equalsIgnoreCase(Data.LUNCH)) {
                Data.getInstance().setLunchList(items);
            }
            if (mealType.equalsIgnoreCase(Data.ALL_DAY)) {
                Data.getInstance().setAllDayList(items);
            }
        }
    }

    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.i(TAG, "onFailed method called.");
    }
}