package com.appisoft.iperkz.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.appisoft.iperkz.activity.MenuDetailsActivity;
import com.appisoft.iperkz.activity.MenuItemViewFragment;
import com.appisoft.iperkz.activity.SearchableActivity;
import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.adapter.MenuListAdapter;
import com.appisoft.iperkz.callback.ImageLoadCallBack;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.FoodItem;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.util.Util;
import com.appisoft.perkz.R;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MenuListViewItem extends LinearLayout  implements View.OnClickListener {
    MenuItem menuItem = null;
    Context context ;
    MenuListAdapter adapter ;
    RelativeLayout layout;
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    public MenuListViewItem(Context context) {
        super(context);
        this.context = context;
        initControl(context);
    }

    public void setValues(MenuItem menuItem, MenuListAdapter adapter) {
        this.adapter = adapter;
        this.menuItem = menuItem;

        layout.setOnClickListener(this::onClick);
        TextView menuItemNameVW = (TextView)findViewById(R.id.menuItemName);
        menuItemNameVW.setText(menuItem.getMenuItemName());

        TextView menuItemDescVW = (TextView)findViewById(R.id.menuDesc);
        menuItemDescVW.setVisibility(View.VISIBLE);
        if(!menuItem.getMenuItemDesc().equals("")&&menuItem.getMenuItemDesc()!=null) {
            menuItemDescVW.setText(menuItem.getMenuItemDesc());
        }
        else{
            menuItemDescVW.setVisibility(View.GONE);
        }

        TextView menuTimingsVW = (TextView)findViewById(R.id.timings);
        menuTimingsVW.setText(menuItem.getStartTime() + " to " + menuItem.getEndTime());

        TextView salePriceVW = (TextView)findViewById(R.id.salePrice);
        if(menuItem.getSalePrice()==0.0){
            salePriceVW.setText("");
        }
        else
        salePriceVW.setText("$" + Util.getFormattedDollarAmt(menuItem.getSalePrice()));


        TextView timeToMake = (TextView) findViewById(R.id.timeToMake);
        if (menuItem.getTimeToMake() == 0 ) {
            timeToMake.setText("Available now");
        } else {
            timeToMake.setText(menuItem.getTimeToMake() + " mins to make");
        }

        if (loginRepository.getCustomerEntity().getStore().getStoreTypeId() == 4) {
            menuTimingsVW.setVisibility(View.GONE);
            timeToMake.setVisibility(View.GONE);
            ImageView img = (ImageView) findViewById(R.id.imageView);
            img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }


        final TextView isSelectedTextView = (TextView) findViewById(R.id.isSelected);
       // final TextView qtySelectedTextView = (TextView) findViewById(R.id.qtySelected);

        //compare with ViewCart and set the field
        ArrayList<FoodItem> selectedItem = Data.getInstance().getSelectedMenuItems();
        menuItem.setSelected(false);
        for (FoodItem item : selectedItem) {
            if (item.getMenuId() == menuItem.getMenuId()) {
                menuItem.setSelected(true);
                isSelectedTextView.setText(String.valueOf(item.getQuantity()));
               // qtySelectedTextView.setText();
            }
        }

        if (menuItem.isSelected() == false) {
            isSelectedTextView.setVisibility(View.GONE);
           //setBackground();
            //setBackgroundColor(Color.GRAY);
           // qtySelectedTextView.setVisibility(View.GONE);
            layout.setBackgroundColor(Color.WHITE);
        } else {
            isSelectedTextView.setVisibility(View.VISIBLE);
            layout.setBackgroundColor(Color.LTGRAY);
            //setBackgroundColor(Color.WHITE);
           // qtySelectedTextView.setVisibility(View.VISIBLE);
        }

        ImageView shareImageView = (ImageView) findViewById(R.id.shareMenuItemImg);
        shareImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDish(v);
            }
        });

        ImageView likeDishImageView = (ImageView) findViewById(R.id.likeDishimage);
        likeDishImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                likeDish(v);
            }
        });
        if(menuItem.getImageUrl()!= null) {
            CronetEngine cronetEngine = Cronet.getCronetEngine(this.getContext());
            ImageView view = (ImageView) findViewById(R.id.imageView);
            //Button buyButton = (Button)findViewById(R.id.order);

            UrlRequest.Callback callback = new ImageLoadCallBack(view, this.getContext());
            Executor executor = Executors.newSingleThreadExecutor();
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    menuItem.getImageUrl(), callback, executor);

            UrlRequest request = requestBuilder.build();
            request.start();
        }

    }

    private void initControl(Context context )
    {

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       // setBackgroundColor(Color.GREEN);
        inflater.inflate(R.layout.list_menu_view_item, this);
        layout  = (RelativeLayout) findViewById(R.id.itemBackground);
    }

    @Override
    public void onClick(View v) {

        MenuItem cloneSelectedMenuItme = new MenuItem();
        try {
            cloneSelectedMenuItme = (MenuItem)menuItem.clone();
        } catch (Exception e) {

        }
        //Data.getInstance().getSelectedMenuItems().add(cloneSelectedMenuItme);
        //Data.getInstance().setTotalCost(cloneSelectedMenuItme.getSalePrice());
        //Log.println(Log.INFO, "test ", Data.getInstance().getSelectedMenuItems().size() + "");

        final TextView isSelectedTextView = (TextView) findViewById(R.id.isSelected);
        //menuItem.setSelected(true);
        //isSelectedTextView.setVisibility(View.VISIBLE);
        /*final TextView qtySelectedTextView        = (TextView) findViewById(R.id.qtySelected);
        qtySelectedTextView.setText(String.valueOf(menuItem.getQuantity()));
        qtySelectedTextView.setVisibility(View.VISIBLE);*/

     DialogFragment newFragment = new MenuItemViewFragment(cloneSelectedMenuItme, adapter);
        newFragment.setCancelable(false);
        try {
            newFragment.show(((MenuDetailsActivity) context).getSupportFragmentManager(), "missiles");
        } catch (Exception e) {
            //ignore
        }
        try {
            newFragment.show(((SearchableActivity) context).getSupportFragmentManager(), "missiles");
        } catch (Exception e) {
//ignore
        }
    }

    public void shareDish(View v) {
        String text_msg = "Try the " + menuItem.getMenuItemName() + " referred by " + loginRepository.getCustomerEntity().getFirstName() + " from " + loginRepository.getCustomerEntity().getStore().getStoreName() + " !! \n" +
                "When you Download and Order from iPerkz, you will receive $5 OFF as One time offer.\n" +
                "https://iperkz/referral_Dish";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text_msg);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        getContext().startActivity(shareIntent);

    }

    public void likeDish(View v) {
      Log.i("Like", "User Liked Dish "+menuItem.getMenuItemName());
    }
}
