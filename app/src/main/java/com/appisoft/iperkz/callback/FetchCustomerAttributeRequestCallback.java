package com.appisoft.iperkz.callback;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.appisoft.iperkz.activity.PaymentActivity;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.AddressDetail;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.util.Wrapper;
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

public class FetchCustomerAttributeRequestCallback extends UrlRequest.Callback {

    private static final String TAG = "FetchCustomAttsRequest";

    private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);
    public long start;
    private PaymentActivity mainActivity;


    public FetchCustomerAttributeRequestCallback(Context context) {

            this.mainActivity = (PaymentActivity)context;

    }

    @Override
    public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
        Log.i(TAG, "onRedirectRd method called.");
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
        Wrapper wrapper = new Wrapper();
        try {
            wrapper = mapper.readValue(returnedData, Wrapper.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //final List<LinkedHashMap<String, Object>> storeDetails = (List<LinkedHashMap<String, Object>>) wrapper.getItems();

        final List<LinkedHashMap<String, Object>> customerDetails =  (List<LinkedHashMap<String, Object>>) wrapper.getItems();
        int x = 2;
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                List<AddressDetail> addressDetailsList = new ArrayList<>();
                for (int i = 0; i < customerDetails.size(); i++) {

                    LinkedHashMap<String, Object> customerDetailsMap = (LinkedHashMap<String, Object>) customerDetails.get(i);

                    LinkedHashMap<String, Object> addressDetailsObjMap = (LinkedHashMap<String, Object>) customerDetailsMap.get("customerAttributes");

                    List<LinkedHashMap<String, Object>> addressDetailsListMap = (List<LinkedHashMap<String, Object>>) addressDetailsObjMap.get("addressDetails");


                    for(LinkedHashMap<String, Object> addressDetailsMap: addressDetailsListMap) {
                        AddressDetail addressDetail = new AddressDetail();
                        addressDetail.setId((int) addressDetailsMap.get("id"));
                        addressDetail.setTag((String) addressDetailsMap.get("tag"));
                        addressDetail.setFieldOne((String) addressDetailsMap.get("fieldOne"));
                        addressDetail.setFieldTwo((String) addressDetailsMap.get("fieldTwo"));
                        addressDetail.setCity((String) addressDetailsMap.get("city"));
                        addressDetail.setState((String) addressDetailsMap.get("state"));
                        addressDetail.setZip((String) addressDetailsMap.get("zip"));
                        addressDetail.setCountry((String) addressDetailsMap.get("country"));
                        addressDetail.setIsDefault((Boolean) addressDetailsMap.get("isDefault"));
                        addressDetail.setLongitude((Double) addressDetailsMap.get("longitude"));
                        addressDetail.setLatitude((Double) addressDetailsMap.get("latitude"));
                        addressDetail.setDistance((Double) addressDetailsMap.get("distance"));
                        addressDetailsList.add(addressDetail);
                    }
                    }

                    mainActivity.loadArrayAdapter(addressDetailsList);
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