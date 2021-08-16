package com.appisoft.iperkz.callback;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.appisoft.iperkz.activity.IperkzHomeActivity;
import com.appisoft.iperkz.activity.RegistrationActivity;
import com.appisoft.iperkz.activity.RegistrationNewActivity;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.Item;
import com.appisoft.iperkz.entity.MealType;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.entity.Store;
import com.appisoft.iperkz.entity.StoreAttributes;
import com.appisoft.iperkz.entity.StoreTypes;
import com.appisoft.iperkz.util.Wrapper;
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

public class StoreTypesRequestCallback extends UrlRequest.Callback {

    private static final String TAG = "StoreTypesRequest";

    private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);

    public long start;
    private long stop;
    private IperkzHomeActivity mainActivity;
    private String activityName;
    public StoreTypesRequestCallback(Context context) {
        activityName = context.getClass().getSimpleName() ;
            this.mainActivity = (IperkzHomeActivity)context;
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
     //   Log.i(TAG, "onSucceeded method called." +bytesReceived);
        String returnedData = bytesReceived.toString();
        ObjectMapper mapper = new ObjectMapper();
        Wrapper wrapper = new Wrapper();
        try {
            wrapper = mapper.readValue(returnedData, Wrapper.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        final List<LinkedHashMap<String, Object>> storeTypes = (List<LinkedHashMap<String, Object>>) wrapper.getItems();


        mainActivity.runOnUiThread(new Runnable() {
            public void run() {

                    StoreTypes storeTypes1 = new StoreTypes();
                    List<Item> itemList = new ArrayList<>();
                    for (int i = 0; i < storeTypes.size(); i++) {
                        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) storeTypes.get(i);
                        Item item = new Item();
                        item.setStoreTypeId((Integer) map.get("storeTypeId"));
                        item.setStoreTypeName((String) map.get("storeTypeName"));
                        item.setStoreTypeDescription((String) map.get("storeTypeDescription"));
                        item.setImageURL((String) map.get("imageURL"));
                        itemList.add(item);
                    }
                    storeTypes1.setItems(itemList);
                    mainActivity.setStoreTypes(storeTypes1);
                }
        });
    }


    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.i(TAG, "onFailed method called.");
    }


}