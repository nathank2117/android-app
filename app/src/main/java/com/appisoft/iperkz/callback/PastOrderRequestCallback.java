package com.appisoft.iperkz.callback;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.appisoft.iperkz.activity.MenuDetailsActivity;
import com.appisoft.iperkz.activity.SearchableActivity;
import com.appisoft.iperkz.activity.ui.login.LoginResult;
import com.appisoft.iperkz.adapter.MenuListAdapter;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.MenuFilterCriteria;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.entity.PastOrderDetailItem;
import com.appisoft.iperkz.entity.PastOrderItem;
import com.appisoft.iperkz.util.DateUtils;
import com.appisoft.iperkz.util.Util;
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

public class PastOrderRequestCallback extends UrlRequest.Callback {

    private static final String TAG = "PastOrderRequest";

    private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);
    private ImageView imageView;
    public long start;
    private long stop;
    private Activity mainActivity;
    private ViewGroup view;
    private Context ctx;
    private MutableLiveData<PastOrderItem[]>  pastOrdersResult;

    public PastOrderRequestCallback( Context context, MutableLiveData<PastOrderItem[]> pastOrdersResult) {
        this.mainActivity = (Activity) context;
        this.pastOrdersResult = pastOrdersResult;
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
        RequestStatus status = new RequestStatus();
        try {
            status = mapper.readValue(returnedData, RequestStatus.class);
         } catch (Exception e) {

        }
       // Data.list = (ArrayList<DailySpecial>) status.getPayload().get("DAILY_SPECIAL");

        final List<LinkedHashMap<String, Object>> mySpecials = (ArrayList<LinkedHashMap<String, Object>>) status.getPayload().get("CUSTOMER_PAST_ORDERS");
        Log.i(TAG, com.appisoft.perkz.Data.list.size() +"");
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                Log.i(TAG, mySpecials.size() + "");


                ArrayList<PastOrderItem> filteredDataSetArray = new ArrayList<>();
                for (int i=0; i<mySpecials.size(); i++) {
                   LinkedHashMap<String, Object> map =  ( LinkedHashMap<String, Object>)mySpecials.get(i);

                    PastOrderItem item = new PastOrderItem();
                    item.setOrderId((Integer)map.get("customerOrderId"));
                    item.setOrderTime(DateUtils.getSQLDateAsString((String)map.get("orderCreationTime")));
                    item.setOrderStatus((String)map.get("orderStatus"));
                    item.setStoreName((String)map.get("storeName"));
                    item.setStoreAddress1((String)map.get("storeAddress1"));
                    item.setTotalSalePrice((Double) map.get("totalSalePrice"));
                    item.setPerkzDeducted((Double) map.get("discount"));
                    item.setSalesTax((Double) map.get("tax"));

                    item.setTipAmount((Double) map.get("tipAmount"));
                    item.setTransactionFee((Double) map.get("transactionFee"));
                    item.setTakeOut((Integer)map.get("takeOut"));
                    item.setDeliveryAddress((String)map.get("address"));
                    item.setDeliveryAmount((Double) map.get("deliveryAmount"));
                    item.setRequestedDeliveryDate(DateUtils.getSQLDateAsString((String)map.get("requestedDeliveryDate")));

                    List<LinkedHashMap<String, Object>> menuItems = (ArrayList<LinkedHashMap<String, Object>>)  map.get("menuList");
                    for (int j=0; j<menuItems.size(); j++) {
                        LinkedHashMap<String, Object> menuItemsMap =  ( LinkedHashMap<String, Object>)menuItems.get(j);
                        String menuItemName = (String)menuItemsMap.get("menuItemName");
                        String splInstructions = (String)menuItemsMap.get("specialInstructions");
                        Double salePrice = (Double)menuItemsMap.get("salePrice");
                        Integer quantity = (Integer)menuItemsMap.get("count");
                        PastOrderDetailItem detailItem = new PastOrderDetailItem();
                        detailItem.setMenuItemName(menuItemName);
                        detailItem.setSplInstructions(splInstructions);
                        detailItem.setSalePrice(salePrice);
                        detailItem.setQuantity(quantity);
                        detailItem.setAdditionsNames((String)menuItemsMap.get("additionsNames"));
                        item.getDetails().add(detailItem);
                    }
                    filteredDataSetArray.add(item);

                }
                PastOrderItem[] myDataset = new PastOrderItem[filteredDataSetArray.size()];
                filteredDataSetArray.toArray(myDataset);

                pastOrdersResult.setValue(myDataset);
            }
        });

/*
        Log.i(TAG, "****** Cronet Request Completed, status code is " + info.getHttpStatusCode()
                        + ", total received bytes is " + info.getReceivedByteCount());
        Log.i(TAG, "onSucceeded method called 2.");
*/
    }
/*
    private void storeInCache(String mealType, PastOrderItem[] items) {
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
*/

    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.i(TAG, "onFailed method called.");
    }


}