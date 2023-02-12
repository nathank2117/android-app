package com.appisoft.iperkz.callback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.appisoft.iperkz.activity.MenuDetailsActivity;
import com.appisoft.iperkz.activity.SearchableActivity;
import com.appisoft.iperkz.adapter.MenuListAdapter;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.MenuFilterCriteria;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.entity.MenuItemAddition;
import com.appisoft.iperkz.entity.SubItem;
//import com.appisoft.iperkz.fragement.InProgressDialog;
import com.appisoft.iperkz.util.DateUtils;
import com.appisoft.iperkz.util.Util;
import com.appisoft.perkz.MenuView;
import com.appisoft.perkz.RequestStatus;
import com.appisoft.perkz.entity.DailySpecial;
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

public class MenuListServiceRequestCallback extends UrlRequest.Callback {

    private static final String TAG = "MyUrlRequestCallback";

    private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);
    private ImageView imageView;
    public long start;
    private long stop;
    private Activity mainActivity;
    private ViewGroup view;
    private Context ctx;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private MenuFilterCriteria criteria = null;
    private String activityName = "";
   // private InProgressDialog progressDialog;
   // public MenuListServiceRequestCallback(RecyclerView _recyclerView, InProgressDialog progressDialog, Context context) {
    public MenuListServiceRequestCallback(RecyclerView _recyclerView, Context context) {

            this.recyclerView = _recyclerView;
       // this.progressDialog = progressDialog;
        activityName = context.getClass().getSimpleName() ;
        if (activityName.equalsIgnoreCase("MenuDetailsActivity") ){
            this.mainActivity = (MenuDetailsActivity) context;
        } else if (activityName.equalsIgnoreCase("SearchableActivity")) {
            this.mainActivity = (SearchableActivity) context;
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
   //     Log.i(TAG, "onSucceeded method called." +bytesReceived);
        String returnedData = bytesReceived.toString();
        ObjectMapper mapper = new ObjectMapper();
        RequestStatus status = new RequestStatus();
        try {
            status = mapper.readValue(returnedData, RequestStatus.class);
         } catch (Exception e) {

        }
       // Data.list = (ArrayList<DailySpecial>) status.getPayload().get("DAILY_SPECIAL");

        final List<LinkedHashMap<String, Object>> mySpecials = (ArrayList<LinkedHashMap<String, Object>>) status.getPayload().get("MENU_DETAILS");
        Log.i(TAG, com.appisoft.perkz.Data.list.size() +"");
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                Log.i(TAG, mySpecials.size() + "");


                ArrayList<MenuItem> filteredDataSetArray = new ArrayList<>();
                for (int i=0; i<mySpecials.size(); i++) {
                   LinkedHashMap<String, Object> map =  ( LinkedHashMap<String, Object>)mySpecials.get(i);

                    MenuItem item = new MenuItem();
                    item.setMenuId((Integer) map.get("menuId"));
                    item.setMenuItemName((String)map.get("menuItemName"));
                    item.setMenuItemDesc((String)map.get("menuItemDescription"));
                    item.setStartTime(DateUtils.getSQLDateString((String)map.get("availableStartTime")));
                    item.setEndTime(DateUtils.getSQLDateString((String)map.get("availableEndTime")));
                    item.setImageUrl((String)map.get("imageUrl"));
                    item.setSalePrice((Double) map.get("salePrice"));
                    item.setMealType((String)map.get("mealType"));
                    item.setMealCategory((String)map.get("mealCategory"));
                    item.setSpecialInstructions((String)map.get("specialInstructions"));
                    item.setTimeToMake((Integer) map.get("timeToMake"));
                    item.setAvailableQuantity((Integer) (map.get("quantity")==null?-1:map.get("quantity")));
                    item.setHasAdditions((Integer)map.get("hasAdditions"));
                    if(item.getHasAdditions()>0) {
                       List<LinkedHashMap<String, Object>> additions = (List<LinkedHashMap<String, Object>>) map.get("additions");
                        List<MenuItemAddition> menuItemAdditions = new ArrayList<>();
                        for (int j = 0; j < additions.size(); j++) {
                            LinkedHashMap<String, Object> addtionMap = (LinkedHashMap<String, Object>) additions.get(j);
                            MenuItemAddition menuItemAddition = new MenuItemAddition();
                            menuItemAddition.setName((String) addtionMap.get("name"));
                            menuItemAddition.setDescription((String) addtionMap.get("description"));
                            menuItemAddition.setPrice((Double) addtionMap.get("price"));
                            menuItemAddition.setAdditionsId((Integer) addtionMap.get("additionsId"));
                            menuItemAddition.setMenuId((Integer) addtionMap.get("menuId"));
                            menuItemAddition.setParentId((Integer) addtionMap.get("parentId"));
                            menuItemAddition.setSelectionType((String) addtionMap.get("selectionType"));
                            menuItemAddition.setChildCount((Integer) addtionMap.get("childCount"));

                            //private List<SubItem> subItems = null;
                            List<LinkedHashMap<String, Object>> subItemsList = (List<LinkedHashMap<String, Object>>) addtionMap.get("subItems");
                            if(subItemsList!=null) {
                                List<SubItem> subItems = new ArrayList<>();
                                for (int k = 0; k < subItemsList.size(); k++) {
                                    LinkedHashMap<String, Object> subItemMap = (LinkedHashMap<String, Object>) subItemsList.get(k);
                                    SubItem subItem = new SubItem();

                                    subItem.setName((String) subItemMap.get("name"));
                                    subItem.setDescription((String) subItemMap.get("description"));
                                    subItem.setPrice((Double) subItemMap.get("price"));
                                    subItem.setAdditionsId((Integer) subItemMap.get("additionsId"));
                                    subItem.setMenuId((Integer) subItemMap.get("menuId"));
                                    subItem.setParentId((Integer) subItemMap.get("parentId"));
                                    subItem.setSelectionType((String) subItemMap.get("selectionType"));
                                    subItem.setChildCount((Integer) subItemMap.get("childCount"));
                                    subItem.setDefaultSelection((Boolean) subItemMap.get("isSelected"));
                                    //subItem.setSubItems((Object) subItemMap.get("subItems"));
                                    subItems.add(subItem);
                                }
                                menuItemAddition.setSubItems(subItems);
                            }
                            menuItemAdditions.add(menuItemAddition);
                        }
                        item.setAdditions(menuItemAdditions);
                    }

                    if (criteria != null ){
                        String typedCriteria = criteria.getMenuName();
                        if (typedCriteria != null && typedCriteria.length() > 0 ) {
                            if (item.getMenuItemName().toLowerCase().contains(typedCriteria.toLowerCase())  ) {
                                Util.insertNoDuplicates(item, filteredDataSetArray);
                               // filteredDataSetArray.add(item);
                                continue;
                            }
                            if (item.getMenuItemDesc().toLowerCase().equalsIgnoreCase(typedCriteria.toLowerCase() )) {
                                Util.insertNoDuplicates(item, filteredDataSetArray);
                                //filteredDataSetArray.add(item);
                                continue;
                            }
                            if (item.getMealType().toLowerCase().equalsIgnoreCase(typedCriteria.toLowerCase() )) {
                                Util.insertNoDuplicates(item, filteredDataSetArray);
                                // filteredDataSetArray.add(item);
                                continue;
                            }
                            if (item.getMealCategory().toLowerCase().equalsIgnoreCase(typedCriteria.toLowerCase() ) ) {
                                Util.insertNoDuplicates(item, filteredDataSetArray);
                                // filteredDataSetArray.add(item);
                                continue;
                            }
                        } else if (item.getMealType().equalsIgnoreCase(criteria.getMealType())) {
                            filteredDataSetArray.add(item);
                        } /*else {
                            filteredDataSetArray.add(item);
                        } */

                    } else {
                        filteredDataSetArray.add(item);
                    }


                    // myDataset[i] = spl;

                }
                MenuItem[] myDataset = new MenuItem[filteredDataSetArray.size()];
                filteredDataSetArray.toArray(myDataset);

                storeInCache(criteria.getMealType(), myDataset);
                mAdapter = new MenuListAdapter(myDataset);
                recyclerView.setAdapter(mAdapter);
               /*
                if (progressDialog != null ) {
                    progressDialog.dismiss();
                }

                */
                if (activityName.equalsIgnoreCase("MenuDetailsActivity") ){
                    ((MenuDetailsActivity) mainActivity).setAdapter(mAdapter);
                } else if (activityName.equalsIgnoreCase("SearchableActivity")) {
                    //((SearchableActivity) mainActivity).setAdapter(mAdapter);
                }

            }
        });

/*
        Log.i(TAG, "****** Cronet Request Completed, status code is " + info.getHttpStatusCode()
                        + ", total received bytes is " + info.getReceivedByteCount());
        Log.i(TAG, "onSucceeded method called 2.");
*/
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

    public void setFilterCriteria(MenuFilterCriteria criteria) {
        this.criteria = criteria;
    }

}