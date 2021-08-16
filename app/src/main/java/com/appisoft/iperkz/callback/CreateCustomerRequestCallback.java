package com.appisoft.iperkz.callback;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.lifecycle.MutableLiveData;

import com.appisoft.iperkz.activity.RegistrationActivity;
import com.appisoft.iperkz.activity.RegistrationNewActivity;
import com.appisoft.iperkz.activity.ui.login.OtpActivity;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.CustomerEntity;
import com.appisoft.iperkz.entity.MenuFilterCriteria;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.entity.Reward;
import com.appisoft.iperkz.entity.SimpleResponse;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CreateCustomerRequestCallback extends UrlRequest.Callback {

    private static final String TAG = "CreateCustomerRequest";

    private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);
    public long start;
    private Activity mainActivity;
    private MutableLiveData<CustomerEntity> createCustomerResult;
    private String activityName = "";

    public CreateCustomerRequestCallback(Context context, MutableLiveData<CustomerEntity> createCustomerResult ) {
        activityName = context.getClass().getSimpleName() ;
        if (activityName.equalsIgnoreCase("RegistrationActivity") ){
            this.mainActivity = (RegistrationActivity)context;
        } else if (activityName.equalsIgnoreCase("RegistrationNewActivity")) {
            this.mainActivity = (RegistrationNewActivity)context;
        }

        this.createCustomerResult = createCustomerResult;
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
    //    Log.i(TAG, "onSucceeded method called." +bytesReceived);
        String returnedData = bytesReceived.toString();
        ObjectMapper mapper = new ObjectMapper();
        RequestStatus status = new RequestStatus();
        try {
            status = mapper.readValue(returnedData, RequestStatus.class);
         } catch (Exception e) {

        }
        final RequestStatus statusResponse = status;

        final LinkedHashMap<String, Object> map =  ( LinkedHashMap<String, Object>) status.getPayload().get("CUSTOMER_DETAILS");
        int x = 2;
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (statusResponse.isResult() == true) {
                    CustomerEntity customerEntity = new CustomerEntity();
                    customerEntity.setCustomerId((Integer)map.get("customerId"));
                    customerEntity.setEmail((String)map.get("email"));
                    customerEntity.setFirstName((String)map.get("firstName"));
                    customerEntity.setLastName((String)map.get("lastName"));
                    customerEntity.setMiddleName((String)map.get("middleName"));
                    customerEntity.setCompany((String)map.get("company"));
                    customerEntity.setStoreId((Integer)map.get("storeId"));
                    customerEntity.setPhoneNumber((String)map.get("phoneNumber"));
                    List<Reward> perkRewards = new ArrayList<>();
                    List<LinkedHashMap<String, Object>> perkzRewards = (List<LinkedHashMap<String, Object>>) map.get("perkzRewards");
                    for(LinkedHashMap<String, Object> rewardMap : perkzRewards){
                        Reward reward = new Reward();
                        reward.setPerkzType((String)rewardMap.get("perkzType"));
                        reward.setRewardType((String)rewardMap.get("rewardType"));
                        reward.setName((String)rewardMap.get("name"));
                        reward.setPerkzId((Integer) rewardMap.get("perkzId"));
                        reward.setDescription((String)rewardMap.get("description"));
                        reward.setEndDate((String)rewardMap.get("endDate"));
                        reward.setStoreId((Integer) rewardMap.get("storeId"));
                        reward.setReward((Double)rewardMap.get("reward"));
                        reward.setStartdate((String)rewardMap.get("startDate"));
                        reward.setClaimedReward((Double) map.get("claimedReward"));
                        perkRewards.add(reward);
                    }
                    customerEntity.setPerkzRewards(perkRewards);
                    createCustomerResult.setValue(customerEntity);
                } else {
                    createCustomerResult.setValue(null);
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