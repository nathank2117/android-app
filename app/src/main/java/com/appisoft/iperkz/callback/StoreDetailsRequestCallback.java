package com.appisoft.iperkz.callback;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.appisoft.iperkz.activity.RegistrationActivity;
import com.appisoft.iperkz.activity.RegistrationNewActivity;
import com.appisoft.iperkz.activity.ViewCartActivity;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.DeliveryByStore;
import com.appisoft.iperkz.entity.DeliveryOptions;
import com.appisoft.iperkz.entity.DeliveryPricing;
import com.appisoft.iperkz.entity.DeliveryRules;
import com.appisoft.iperkz.entity.MealType;
import com.appisoft.iperkz.entity.MenuFilterCriteria;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.entity.SimpleResponse;
import com.appisoft.iperkz.entity.Store;
import com.appisoft.iperkz.entity.StoreAttributes;
import com.appisoft.iperkz.entity.Timing;
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

public class StoreDetailsRequestCallback extends UrlRequest.Callback {

    private static final String TAG = "StoreDetailsRequest";

    private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);

    public long start;
    private long stop;
    private Activity mainActivity;
    private String activityName;
    public StoreDetailsRequestCallback(Context context) {
        activityName = context.getClass().getSimpleName() ;
        if (activityName.equalsIgnoreCase("RegistrationActivity") ){
            this.mainActivity = (RegistrationActivity)context;
        } else if (activityName.equalsIgnoreCase("RegistrationNewActivity")) {
            this.mainActivity = (RegistrationNewActivity)context;
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
     //   Log.i(TAG, "onSucceeded method called." +bytesReceived);
        String returnedData = bytesReceived.toString();
        ObjectMapper mapper = new ObjectMapper();
        RequestStatus status = new RequestStatus();
        try {
            status = mapper.readValue(returnedData, RequestStatus.class);
         } catch (Exception e) {

        }
        final RequestStatus statusResponse =status;
        final List<LinkedHashMap<String, Object>> storeDetails = (ArrayList<LinkedHashMap<String, Object>>) status.getPayload().get("STORE_DETAILS");

        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (statusResponse.isResult() == true) {
                    Store item = new Store();
                    for (int i = 0; i < storeDetails.size(); i++) {
                        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) storeDetails.get(i);
                        item = new Store();
                        item.setStoreId((Integer) map.get("storeId"));
                        item.setStoreName((String) map.get("storeName"));
                        item.setStoreDesc((String) map.get("storeDesc"));
                        item.setAddressOne((String) map.get("addressOne"));
                        item.setAddressTwo((String) map.get("addressTwo"));
                        item.setSalesTaxes((double)map.get("salesTaxes"));
                        item.setCity((String) map.get("city"));
                        item.setState((String) map.get("state"));
                        item.setCountry((String) map.get("country"));
                        item.setLogo((String) map.get("logo"));
                        item.setWebsite((String) map.get("website"));
                        item.setChargeMode((String) map.get("chargeMode"));
                        item.setTransactionFee((Double) map.get("transactionFee"));
                        item.setChargeRate((Double) map.get("chargeRate"));
                        item.setFirstImageUrl((String) map.get("firstImageUrl"));
                        item.setSecondImageUrl((String) map.get("secondImageUrl"));
                        item.setThirdImageUrl((String) map.get("thirdImageUrl"));
                        item.setPhone((String) map.get("phone"));
                        item.setStoreTypeId((Integer) map.get("storeTypeId"));
                        item.setStoreStatus(((String) map.get("storeStatus")));
                        item.setLatitude((Double) map.get("latitude"));
                        item.setLongitude((Double) map.get("longitude"));
                        if(map.get("cardOnly")!=null)
                        item.setCardOnly((Boolean) map.get("cardOnly"));


                            LinkedHashMap<String, Object> storeAttributesListMap = (LinkedHashMap<String, Object>) map.get("storeAttributes");
                        if(storeAttributesListMap!=null) {
                            StoreAttributes storeAttributes = new StoreAttributes();
                            storeAttributes.setShowTips((Integer) storeAttributesListMap.get("showTips"));
                            storeAttributes.setTips((List<Double>) storeAttributesListMap.get("tips"));
                            storeAttributes.setPaymentOptions((List<String>)storeAttributesListMap.get("paymentOptions"));
                            storeAttributes.setServiceOptions((List<String>)storeAttributesListMap.get("serviceOptions"));

                            storeAttributes.setDescription((String)storeAttributesListMap.get("description"));
                            storeAttributes.setStoreFoodType((String)storeAttributesListMap.get("storeFoodType"));
                            storeAttributes.setStoreMealTypes((String)storeAttributesListMap.get("storeMealTypes"));
                            storeAttributes.setDistance((Double)storeAttributesListMap.get("distance"));
                            storeAttributes.setRatings((Double)storeAttributesListMap.get("ratings"));
                            storeAttributes.setReviews(Math.round((Double)storeAttributesListMap.get("reviews")));
                            storeAttributes.setClosingMessage((String)storeAttributesListMap.get("closingMessage"));


                            if(storeAttributesListMap.get("dineInAllowed")!=null)
                            storeAttributes.setDineInAllowed((boolean) storeAttributesListMap.get("dineInAllowed"));

                            if(storeAttributesListMap.get("cardOnly")!=null)
                                storeAttributes.setCardOnly((Boolean)storeAttributesListMap.get("cardOnly"));

                            if(storeAttributesListMap.get("allowFutureOrders")!=null)
                            storeAttributes.setAllowFutureOrders((Integer) storeAttributesListMap.get("allowFutureOrders"));

                            //delivery options
                            LinkedHashMap<String, Object> deliveryOptionsMap = (LinkedHashMap<String, Object>) storeAttributesListMap.get("deliveryOptions");
                            if(deliveryOptionsMap!=null) {
                                DeliveryOptions deliveryOptions = new DeliveryOptions();
                                deliveryOptions.setDeliveryBy((String) deliveryOptionsMap.get("deliveryBy"));
                                deliveryOptions.setDeliveryEnabled((Boolean) deliveryOptionsMap.get("deliveryEnabled"));

                                LinkedHashMap<String, Object> deliveryOptionsStoreMap = (LinkedHashMap<String, Object>) deliveryOptionsMap.get("store");
                                DeliveryByStore deliveryByStore = new DeliveryByStore();

                                List<LinkedHashMap<String, Object>> pricingMapList = (List<LinkedHashMap<String, Object>>) deliveryOptionsStoreMap.get("pricing");

                                List<DeliveryPricing> deliveryPricings = new ArrayList<>();
                                for (int s = 0; s < pricingMapList.size(); s++) {
                                    LinkedHashMap<String, Object> deliveryPricingMap = (LinkedHashMap<String, Object>) pricingMapList.get(s);
                                    DeliveryPricing deliveryPricing = new DeliveryPricing();
                                    deliveryPricing.setMaxDistance((Double) deliveryPricingMap.get("maxDistance"));
                                    deliveryPricing.setMinDistance((Double) deliveryPricingMap.get("minDistance"));

                                    List<DeliveryRules> deliveryRules = new ArrayList<>();
                                    List<LinkedHashMap<String, Object>> deliveryRulesMapList = (List<LinkedHashMap<String, Object>>) deliveryPricingMap.get("rules");
                                    for (int u = 0; u < deliveryRulesMapList.size(); u++) {
                                        LinkedHashMap<String, Object> deliveryRulesMap = (LinkedHashMap<String, Object>) deliveryRulesMapList.get(u);
                                        DeliveryRules deliveryRuleMap = new DeliveryRules();
                                        deliveryRuleMap.setMaxAmt((Double) deliveryRulesMap.get("maxAmt"));
                                        deliveryRuleMap.setMinAmt((Double) deliveryRulesMap.get("minAmt"));
                                        deliveryRuleMap.setDeliveryCharge((Double) deliveryRulesMap.get("deliveryCharge"));
                                        deliveryRules.add(deliveryRuleMap);
                                    }
                                    deliveryPricing.setDeliveryRules(deliveryRules);
                                    deliveryPricings.add(deliveryPricing);
                                }

                                deliveryByStore.setPricing(deliveryPricings);
                                deliveryOptions.setDeliveryByStore(deliveryByStore);
                                storeAttributes.setDeliveryOptions(deliveryOptions);
                            }

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
                            mealType.setMealTypeId((int) mealTypesMap.get("mealTypeId"));
                            mealType.setMealTypeImage((String) mealTypesMap.get("mealTypeImage"));
                            mealType.setMealTypeName((String) mealTypesMap.get("mealTypeName"));
                            mealType.setMealTypeIcon((String) mealTypesMap.get("mealTypeIcon"));
                            mealTypeArrayList.add(mealType);
                        }

                        item.setMealTypes(mealTypeArrayList);

                        //breakfast
                        ArrayList breakfastCategories = (ArrayList) map.get("breakfastCategories");
                        String[] breakfastItems = new String[breakfastCategories.size()];
                        for (int j=0; j<breakfastCategories.size(); j++) {
                           String val =  (String) breakfastCategories.get(j);
                            breakfastItems[j] = val;
                        }
                        item.setBreakfastCategories(breakfastItems);
                        //Lunch
                        ArrayList lunchCategories = (ArrayList) map.get("lunchCategories");
                        String[] lunchItems = new String[lunchCategories.size()];
                        for (int j=0; j<lunchCategories.size(); j++) {
                            String val =  (String) lunchCategories.get(j);
                            lunchItems[j] = val;
                        }
                        item.setLunchCategories(lunchItems);

                        //dinner
                        ArrayList dinnerCategories = (ArrayList) map.get("dinnerCategories");
                        String[] dinnerItems = new String[dinnerCategories.size()];
                        for (int j=0; j<dinnerCategories.size(); j++) {
                            String val =  (String) dinnerCategories.get(j);
                            dinnerItems[j] = val;
                        }
                        item.setDinnerCategories(dinnerItems);

                        //Lunch
                        ArrayList allDayCategories = (ArrayList) map.get("allDayCategories");
                        String[] allDayItems = new String[allDayCategories.size()];
                        for (int j=0; j<allDayCategories.size(); j++) {
                            String val =  (String) allDayCategories.get(j);
                            allDayItems[j] = val;
                        }
                        item.setAllDayCategories(allDayItems);
                    }
                    if (activityName.equalsIgnoreCase("RegistrationActivity") ) {
                        ((RegistrationActivity) mainActivity).showStoreDetails(item);
                    }
                    else{
                        ((RegistrationNewActivity) mainActivity).showStoreDetails(item);
                    }

                }else {
                    if (activityName.equalsIgnoreCase("RegistrationActivity") ) {
                        ((RegistrationActivity) mainActivity).showNoMatchingStoreDetails();
                    }
                }

            }
        });
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