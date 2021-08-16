package com.appisoft.iperkz.callback;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.appisoft.iperkz.activity.MenuDetailsActivity;
import com.appisoft.iperkz.activity.RegistrationActivity;
import com.appisoft.iperkz.activity.RegistrationNewActivity;
import com.appisoft.iperkz.activity.StoreDetailsDialogatMenu;
import com.appisoft.iperkz.activity.ViewCartActivity;
import com.appisoft.iperkz.util.Wrapper;
import com.appisoft.perkz.DisplayMessageActivity;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

public class DrivingDistanceCallbackMenu extends UrlRequest.Callback {

    private static final String TAG = "DrivingDistanceCallback";

    private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);

    private Activity mainActivity;
    private String activityName;


    private MutableLiveData<String> drivingDistanceResult;
    public DrivingDistanceCallbackMenu(Context context, MutableLiveData<String> drivingDistanceResult ) {
        this.drivingDistanceResult = drivingDistanceResult;
        activityName = context.getClass().getSimpleName() ;
        if (activityName.equalsIgnoreCase("DisplayMessageActivity") ){
            this.mainActivity = (DisplayMessageActivity)context;
        } else if (activityName.equalsIgnoreCase("MenuDetailsActivity")) {
            this.mainActivity = (MenuDetailsActivity)context;
        } else if (activityName.equalsIgnoreCase("ViewCartActivity")) {
            this.mainActivity = (ViewCartActivity)context;
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
        Log.i(TAG, "onSucceeded method called." +bytesReceived);
        String returnedData = bytesReceived.toString();
        ObjectMapper mapper = new ObjectMapper();
        Wrapper wrapper = new Wrapper();
        //List<SimpleResponse> statuses = new ArrayList();
        Double distance = 0.0;

        try {
            wrapper = mapper.readValue(returnedData, Wrapper.class);

           /* List<SimpleResponse> statuses = mapper.convertValue(
                    wrapper.getItems(),
                    new TypeReference<List<SimpleResponse>>() { });*/

            distance = (Double) wrapper.getItems().get(0);
         } catch (Exception e) {
            e.printStackTrace();
        }

try {
    final String distanceResult = distance.toString();

    mainActivity.runOnUiThread(new Runnable() {
                                   public void run() {
                                       drivingDistanceResult.setValue(distanceResult);
                                   }
                               }

    );
}catch (Exception e){
    e.printStackTrace();
}

    }

    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.i(TAG, "onFailed method called.");
    }

}