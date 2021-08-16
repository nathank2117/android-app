package com.appisoft.iperkz.callback;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.lifecycle.MutableLiveData;

import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.MenuFilterCriteria;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.entity.SimpleResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

public class PaymentOptionsRequestCallback extends UrlRequest.Callback {

    private static final String TAG = "PaymentOptionsRequest";

    private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);
    private ImageView imageView;
    public long start;
    private long stop;
    private Activity mainActivity;
    private ViewGroup view;
    private Context ctx;

  //  private String activityName = "";
    private MutableLiveData<Boolean> useExistingPaymentDetails;
    public PaymentOptionsRequestCallback(Context context, MutableLiveData<Boolean> useExistingPaymentDetails ) {
       // activityName = context.getClass().getSimpleName() ;
        this.useExistingPaymentDetails = useExistingPaymentDetails;
        this.mainActivity = (Activity)context;


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
     //   Log.i(TAG, "onReadCompleted method called.");
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
        final String returnedData = bytesReceived.toString();


        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (returnedData != null && returnedData.equalsIgnoreCase("true")) {
                    useExistingPaymentDetails.setValue(true);
                } else {
                    useExistingPaymentDetails.setValue(false);
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