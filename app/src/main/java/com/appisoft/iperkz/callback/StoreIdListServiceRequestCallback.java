package com.appisoft.iperkz.callback;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.appisoft.iperkz.activity.RegistrationNewActivity;
import com.appisoft.iperkz.adapter.StoreListAdapter;
import com.appisoft.iperkz.entity.MealType;
import com.appisoft.iperkz.entity.Store;
import com.appisoft.iperkz.entity.StoreAttributes;
import com.appisoft.iperkz.entity.StoreFilterCriteria;
import com.appisoft.iperkz.entity.Timing;
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

public class StoreIdListServiceRequestCallback extends UrlRequest.Callback {

    private static final String TAG = "StoreIdListSvcReqClback";

    private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);
    private ImageView imageView;
    public long start;
    private long stop;
    private ViewGroup view;
    private Context ctx;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private StoreFilterCriteria criteria = null;
    private RegistrationNewActivity mainActivity;

    public StoreIdListServiceRequestCallback(RecyclerView _recyclerView, Context context) {
        this.recyclerView = _recyclerView;
        this.mainActivity = (RegistrationNewActivity) context;
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
    //    Log.i(TAG, "onSucceeded method called." + bytesReceived);
        String returnedData = bytesReceived.toString();
        ObjectMapper mapper = new ObjectMapper();
        Wrapper wrapper = new Wrapper();
        try {
            wrapper = mapper.readValue(returnedData, Wrapper.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        final List<LinkedHashMap<String, Object>> storeDetails = (List<LinkedHashMap<String, Object>>) wrapper.getItems();

        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                Log.i(TAG, storeDetails.size() + "");


                ArrayList<Store> filteredDataSetArray = new ArrayList<>();
                Store item = new Store();
                for (int i = 0; i < storeDetails.size(); i++) {
                    LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) storeDetails.get(i);
                    item = new Store();
                    item.setStoreId((Integer) map.get("storeId"));
                    item.setStoreCode((String) map.get("storeCode"));
                    item.setStoreName((String) map.get("storeName"));
                    item.setStoreDesc((String) map.get("storeDesc"));
                    item.setAddressOne((String) map.get("addressOne"));
                    item.setAddressTwo((String) map.get("addressTwo"));
                    item.setSalesTaxes((double) map.get("salesTaxes"));
                    item.setCity((String) map.get("city"));
                    item.setState((String) map.get("state"));
                    item.setCountry((String) map.get("country"));
                    item.setChargeMode((String) map.get("chargeMode"));
                    item.setTransactionFee((Double) map.get("transactionFee"));
                    item.setChargeRate((Double) map.get("chargeRate"));
                    item.setLogo((String) map.get("logo"));
                    item.setLoggedIn((Boolean) map.get("loggedIn"));
                    item.setFirstImageUrl((String) map.get("firstImageUrl"));
                    item.setSecondImageUrl((String) map.get("secondImageUrl"));
                    item.setThirdImageUrl((String) map.get("thirdImageUrl"));
                    item.setPhone((String) map.get("phone"));
                    item.setWebsite((String) map.get("website"));
                    item.setLatitude((Double) map.get("latitude"));
                    item.setLongitude((Double) map.get("longitude"));
                    item.setStoreTypeId((Integer) map.get("storeTypeId"));
                    item.setStoreStatus(((String) map.get("storeStatus")));
                    if(map.get("cardOnly")!=null)
                    item.setCardOnly((Boolean) map.get("cardOnly"));


                    LinkedHashMap<String, Object> storeAttributesListMap = (LinkedHashMap<String, Object>) map.get("storeAttributes");

                    if(storeAttributesListMap!=null) {
                        StoreAttributes storeAttributes = new StoreAttributes();
                        storeAttributes.setShowTips((Integer) storeAttributesListMap.get("showTips"));
                        storeAttributes.setTips((List<Double>) storeAttributesListMap.get("tips"));
                        storeAttributes.setDescription((String)storeAttributesListMap.get("description"));
                        storeAttributes.setStoreFoodType((String)storeAttributesListMap.get("storeFoodType"));
                        storeAttributes.setStoreMealTypes((String)storeAttributesListMap.get("storeMealTypes"));
                        storeAttributes.setDistance((Double)storeAttributesListMap.get("distance"));
                        storeAttributes.setRatings((Double)storeAttributesListMap.get("ratings"));
                        storeAttributes.setReviews(Math.round((Double)storeAttributesListMap.get("reviews")));
                        storeAttributes.setClosingMessage((String)storeAttributesListMap.get("closingMessage"));
                        storeAttributes.setPaymentOptions((List<String>)storeAttributesListMap.get("paymentOptions"));
                        storeAttributes.setServiceOptions((List<String>)storeAttributesListMap.get("serviceOptions"));
                        if(storeAttributesListMap.get("cardOnly")!=null)
                        storeAttributes.setCardOnly((Boolean)storeAttributesListMap.get("cardOnly"));

                        if(storeAttributesListMap.get("dineInAllowed")!=null)
                        storeAttributes.setDineInAllowed((boolean) storeAttributesListMap.get("dineInAllowed"));

                        if(storeAttributesListMap.get("allowFutureOrders")!=null)
                        storeAttributes.setAllowFutureOrders((Integer) storeAttributesListMap.get("allowFutureOrders"));

                        List<Timing> timings = new ArrayList<>();
                        //List<Timing> timingsList = (List<Timing>) storeAttributesListMap.get("timings");
                        List<LinkedHashMap<String, Object>> timingsList = (List<LinkedHashMap<String, Object>>) storeAttributesListMap.get("timings");

                        for(int t=0; t <timingsList.size(); t++) {
                            LinkedHashMap<String, Object> timingObject = timingsList.get(t);

                        Timing timing = new Timing();
                        timing.setDay((String)timingObject.get("day"));
                        timing.setStartTime((String)timingObject.get("startTime"));
                        timing.setEndTime((String)timingObject.get("endTime"));
                        timing.setIsOpen((Integer) timingObject.get("isOpen"));
                        timing.setIsToday((Integer) timingObject.get("isToday"));
                        timing.setEndTimeObject((String)timingObject.get("endTimeObject"));
                        timings.add(timing);
                        }
                        storeAttributes.setTimings(timings);
                        item.setStoreAttributes(storeAttributes);

                    }

                            //MealTypes
                    ArrayList<MealType> mealTypeArrayList = new ArrayList<>();
                    List<LinkedHashMap<String, Object>> mealTypesMapList = (List<LinkedHashMap<String, Object>>) map.get("mealTypes");
                    for(int m=0; m <mealTypesMapList.size(); m++){
                        LinkedHashMap<String, Object> mealTypesMap = (LinkedHashMap<String, Object>) mealTypesMapList.get(m);
                        MealType mealType = new MealType();
                        mealType.setMealTypeImage((String) mealTypesMap.get("mealTypeImage"));
                        mealType.setMealTypeName((String) mealTypesMap.get("mealTypeName"));
                        mealType.setMealTypeIcon((String) mealTypesMap.get("mealTypeIcon"));
                        mealTypeArrayList.add(mealType);
                    }

                    item.setMealTypes(mealTypeArrayList);

                    //breakfast
                    ArrayList breakfastCategories = (ArrayList) map.get("breakfastCategories");
                    String[] breakfastItems = new String[breakfastCategories.size()];
                    for (int j = 0; j < breakfastCategories.size(); j++) {
                        String val = (String) breakfastCategories.get(j);
                        breakfastItems[j] = val;
                    }
                    item.setBreakfastCategories(breakfastItems);
                    //Lunch
                    ArrayList lunchCategories = (ArrayList) map.get("lunchCategories");
                    String[] lunchItems = new String[lunchCategories.size()];
                    for (int j = 0; j < lunchCategories.size(); j++) {
                        String val = (String) lunchCategories.get(j);
                        lunchItems[j] = val;
                    }
                    item.setLunchCategories(lunchItems);

                    //Lunch
                    ArrayList allDayCategories = (ArrayList) map.get("allDayCategories");
                    String[] allDayItems = new String[allDayCategories.size()];
                    for (int j = 0; j < allDayCategories.size(); j++) {
                        String val = (String) allDayCategories.get(j);
                        allDayItems[j] = val;
                    }
                    item.setAllDayCategories(allDayItems);
                    if (criteria != null) {
                        String typedCriteria = criteria.getStoreName();
                        if (typedCriteria != null && typedCriteria.length() > 0) {
                            if (item.getStoreName().toLowerCase().contains(typedCriteria.toLowerCase()) || item.getStoreDesc().toLowerCase().contains(typedCriteria.toLowerCase())) {
                                filteredDataSetArray.add(item);
                            } else {
                                if (typedCriteria.toLowerCase().equals("all") && !item.getStoreName().toLowerCase().contains("all")) {
                                    filteredDataSetArray.add(item);
                                }
                            }

                        }
                    }

                }

                Store[] myDataset = new Store[filteredDataSetArray.size()];
                filteredDataSetArray.toArray(myDataset);

                mAdapter = new StoreListAdapter(myDataset);
                recyclerView.setAdapter(mAdapter);
                mainActivity.setAdapter((StoreListAdapter)mAdapter);
            }
        });

/*
        Log.i(TAG, "****** Cronet Request Completed, status code is " + info.getHttpStatusCode()
                        + ", total received bytes is " + info.getReceivedByteCount());
        Log.i(TAG, "onSucceeded method called 2.");
*/
    }

    public void setFilterCriteria(StoreFilterCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.i(TAG, "onFailed method called.");
    }

}