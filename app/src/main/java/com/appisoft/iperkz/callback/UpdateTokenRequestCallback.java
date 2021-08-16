package com.appisoft.iperkz.callback;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.lifecycle.MutableLiveData;

import com.appisoft.iperkz.activity.ui.login.LoggedInUserView;
import com.appisoft.iperkz.activity.ui.login.LoginResult;
import com.appisoft.iperkz.activity.ui.login.ValidateOtp;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.MenuFilterCriteria;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.entity.SimpleResponse;
import com.appisoft.perkz.R;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

public class UpdateTokenRequestCallback extends UrlRequest.Callback {

    private static final String TAG = "UpdateTokenRequest";

    private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);
    private ImageView imageView;
    public long start;
    private long stop;
    private Activity mainActivity;
    private ViewGroup view;


    public UpdateTokenRequestCallback(Context context ) {
        this.mainActivity = (ValidateOtp)context;
     }
    public UpdateTokenRequestCallback() {
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
      //  Log.i(TAG, "onSucceeded method called." +bytesReceived);
        String returnedData = bytesReceived.toString();
        ObjectMapper mapper = new ObjectMapper();
        SimpleResponse status = new SimpleResponse();
        try {
            status = mapper.readValue(returnedData, SimpleResponse.class);
         } catch (Exception e) {

        }
    }


    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.i(TAG, "onFailed method called.");
    }

}