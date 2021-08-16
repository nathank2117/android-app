package com.appisoft.iperkz.callback;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.lifecycle.MutableLiveData;

import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.entity.Reward;
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
import java.util.LinkedHashMap;
import java.util.List;

public class RewardsRequestCallback extends UrlRequest.Callback {

    private static final String TAG = "PaymentOptionsRequest";

    private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);
    private ImageView imageView;
    public long start;
    private long stop;
    private Activity mainActivity;
    private ViewGroup view;
    private Context ctx;

    private MutableLiveData<Reward> reward;
    public RewardsRequestCallback(Context context, MutableLiveData<Reward> reward ) {
        this.reward = reward;
        this.mainActivity = (Activity)context;
    }

    @Override
    public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
        Log.i(TAG, "onRedirectReceived method called.");
        request.followRedirect();
    }

    @Override
    public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
        Log.i(TAG, "onResponseStarted method called.");
        request.read(ByteBuffer.allocateDirect(102400));
    }

    @Override
    public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) throws Exception {
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
        final String returnedData = bytesReceived.toString();
        ObjectMapper mapper = new ObjectMapper();
        Wrapper wrapper = new Wrapper();
        try {
            wrapper = mapper.readValue(returnedData, Wrapper.class);

        } catch (Exception e) {

        }

        final List<LinkedHashMap<String, Object>> rewards = (List<LinkedHashMap<String, Object>>) wrapper.getItems();
        mainActivity.runOnUiThread(new Runnable() {
                                       public void run() {
                                           if ((returnedData != null ) && (rewards.size()>0)) {
                                               for (LinkedHashMap<String, Object> map : rewards) {
                                                   Reward receivedReward = new Reward();
                                                   receivedReward.setStartdate((String) map.get("startDate"));
                                                   receivedReward.setEndDate((String) map.get("endDate"));
                                                   receivedReward.setPerkzId((Integer) map.get("perkzId"));
                                                   receivedReward.setName((String) map.get("name"));
                                                   receivedReward.setReward((Double) map.get("reward"));
                                                   receivedReward.setRewardType((String) map.get("rewardType"));
                                                   receivedReward.setPerkzType((String) map.get("perkzType"));
                                                   receivedReward.setStoreId((Integer) map.get("storeId"));
                                                   receivedReward.setClaimedReward((Double) map.get("claimedReward"));
                                                   reward.setValue(receivedReward);
                                               }
                                               } else{
                                                   reward.setValue(null);
                                               }
                                       }
                                   }

        );

    }


    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.i(TAG, "onFailed method called.");
    }

}