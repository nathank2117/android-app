package com.appisoft.perkz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.appisoft.iperkz.util.DateUtils;
import com.appisoft.perkz.entity.DailySpecial;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MyUrlRequestCallback extends UrlRequest.Callback {

    private static final String TAG = "MyUrlRequestCallback";

    private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);
    private ImageView imageView;
    public long start;
    private long stop;
    private Activity mainActivity;
    private ViewGroup view;
    private Context ctx;
    MyUrlRequestCallback(ViewGroup view, Context context) {
        this.view = view;
        this.mainActivity = (Activity) context;
        this.ctx = context;
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
       // Log.i(TAG, "onSucceeded method called." +bytesReceived);
        String returnedData = bytesReceived.toString();
        ObjectMapper mapper = new ObjectMapper();
        RequestStatus status = new RequestStatus();
        try {
            status = mapper.readValue(returnedData, RequestStatus.class);
         } catch (Exception e) {

        }
        final RequestStatus respStatus = status;

       // Data.list = (ArrayList<DailySpecial>) status.getPayload().get("DAILY_SPECIAL");

        final List<LinkedHashMap<String, Object>> mySpecials = (ArrayList<LinkedHashMap<String, Object>>) status.getPayload().get("DAILY_SPECIAL");
        Log.i(TAG, Data.list.size() +"");
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                Log.i(TAG, mySpecials.size() + "");

                for (int i=0; i<mySpecials.size(); i++) {
                   LinkedHashMap<String, Object> map =  ( LinkedHashMap<String, Object>)mySpecials.get(i);
                    //Button btn =;
    /*
                    String name =  (String) map.get("menuItemName");
                    Button btn =  new Button(ctx);
                    btn.setText(name);
                    view.addView(btn);
    */               DailySpecial spl = new DailySpecial();
                    if (respStatus.isResult() == false) {
                        spl.setPlaceHolder(true);
                        spl.setImageUrl((String)map.get("imageUrl"));
                        MenuView menuView = new MenuView(ctx,spl);
                        view.addView(menuView);
                        return;
                    }


                    spl.setMenuId((Integer) map.get("menuId"));
                    spl.setMenuItemName((String)map.get("menuItemName"));
                    spl.setMenuItemDesc((String)map.get("menuItemDesc"));
                    spl.setStartTime(DateUtils.getSQLDateString((String)map.get("availableStartTime")));
                    spl.setEndTime(DateUtils.getSQLDateString((String)map.get("availableEndTime")));
                    spl.setImageUrl((String)map.get("imageUrl"));
                    spl.setSalePrice((Double) map.get("salePrice"));
                    spl.setTimeToMake((Integer) map.get("timeToMake"));
                    spl.setHasAdditions((Integer)((map.get("hasAdditions")==null)? -1 : map.get("hasAdditions")));
                    spl.setAvailableQuantity((Integer) map.get("quantity"));
                    spl.setQuantityApplicable((Boolean) map.get("quantityApplicable"));
                    MenuView menuView = new MenuView(ctx,spl);
                    view.addView(menuView);

                }
            }
        });

/*
        Log.i(TAG, "****** Cronet Request Completed, status code is " + info.getHttpStatusCode()
                        + ", total received bytes is " + info.getReceivedByteCount());
        Log.i(TAG, "onSucceeded method called 2.");
*/
    }

    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.i(TAG, "onFailed method called.");
    }

}