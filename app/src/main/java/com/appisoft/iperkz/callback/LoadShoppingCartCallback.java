package com.appisoft.iperkz.callback;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.FoodItem;
import com.appisoft.iperkz.entity.ShoppingCart;
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

public class LoadShoppingCartCallback extends UrlRequest.Callback{
    private static final String TAG = "LoadCart";

    private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);
    private ImageView imageView;
    public long start;
    private long stop;
    private Context ctx;


    public LoadShoppingCartCallback(Context context) {
     //   this.mainActivity = (PaymentActivity)context;
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
        System.out.println("SHOPPING_CART : LOAD SUCCESS" );
        String returnedData = bytesReceived.toString();
        ObjectMapper mapper = new ObjectMapper();
        ShoppingCart shoppingCart = new ShoppingCart();
        try {
            shoppingCart = mapper.readValue(returnedData, ShoppingCart.class);
            for (FoodItem item: shoppingCart.getCartItems()) {
                System.out.println("SHOPPING_CART : " + item.getQuantity());
                System.out.println("SHOPPING_CART : " + item.getSalePrice());
            }
        } catch (Exception e) {

        }
        Data data = Data.getInstance(this.ctx);
        data.setSelectedMenuItems(shoppingCart.getCartItems());
        System.out.println("SHOPPING_CART : Save SUCCESS :" + shoppingCart.getCartItems().size());
    }

    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.i(TAG, "onFailed method called.");
    }
}
