package com.appisoft.iperkz.callback;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.appisoft.iperkz.activity.PaymentActivity;
import com.appisoft.iperkz.entity.SimpleResponse;
import com.appisoft.iperkz.util.Wrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.List;

public class InStoreCheckCallback extends UrlRequest.Callback {

    private static final String TAG = "InStoreCheckCallBack";

    private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);

    private PaymentActivity mainActivity;



    private MutableLiveData<SimpleResponse> inStoreResult;
    public InStoreCheckCallback( Context context, MutableLiveData<SimpleResponse> inStoreResult ) {
        this.inStoreResult = inStoreResult;
        this.mainActivity = (PaymentActivity)context;


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
        Log.i(TAG, "onSucceeded method called." +bytesReceived);
        String returnedData = bytesReceived.toString();
        ObjectMapper mapper = new ObjectMapper();
        Wrapper wrapper = new Wrapper();
        //List<SimpleResponse> statuses = new ArrayList();
        SimpleResponse status = new SimpleResponse();

        try {
            wrapper = mapper.readValue(returnedData, Wrapper.class);

            List<SimpleResponse> statuses = mapper.convertValue(
                    wrapper.getItems(),
                    new TypeReference<List<SimpleResponse>>() { });

            status = (SimpleResponse) statuses.get(0);
         } catch (Exception e) {
            e.printStackTrace();
        }


       final SimpleResponse statusResponse = status;

        mainActivity.runOnUiThread(new Runnable() {
            public void run() {

                if (statusResponse.isResult() == true) {
                    inStoreResult.setValue(statusResponse);
                } else {
                    inStoreResult.setValue(statusResponse);
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