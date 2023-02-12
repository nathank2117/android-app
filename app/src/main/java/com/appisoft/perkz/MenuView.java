package com.appisoft.perkz;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.appisoft.iperkz.activity.MenuItemViewFragment;
import com.appisoft.iperkz.activity.SpecialInstructionsDialog;
import com.appisoft.iperkz.activity.ViewCartActivity;
import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.callback.ImageLoadCallBack;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.FoodItem;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.util.Util;
import com.appisoft.perkz.entity.DailySpecial;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MenuView extends LinearLayout implements View.OnClickListener {
    DailySpecial dailySpecial = null;
    Context context ;
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    TextView isSelectedTextView;
    LinearLayout backgroundLayout ;
     public MenuView(Context context) {
        super(context);
        initControl(context);
    }

    public MenuView(Context context, DailySpecial dailySpecial) {
        super(context);
        this.context = context;
        this.dailySpecial = dailySpecial;
        if (this.dailySpecial.isPlaceHolder() == true) {
            initControl_nodata(context);
        } else {
            initControl(context);

            //backgroundLayout.setOnClickListener(this);
            final TextView menuItemNameVW = (TextView) findViewById(R.id.menuItemName);
            menuItemNameVW.setText(dailySpecial.getMenuItemName());

            final TextView menuItemDescVW = (TextView) findViewById(R.id.menuDesc);
            if(!dailySpecial.getMenuItemDesc().equals("")&&dailySpecial.getMenuItemDesc()!=null) {
                menuItemDescVW.setText(dailySpecial.getMenuItemDesc());
            }
            else{
                menuItemDescVW.setVisibility(View.GONE);
            }

          /*  final TextView menuTimingsVW = (TextView) findViewById(R.id.timings);
            menuTimingsVW.setText("From "+dailySpecial.getStartTime() + " to " + dailySpecial.getEndTime());*/

            final TextView salePriceVW = (TextView) findViewById(R.id.salePrice);
            salePriceVW.setText("$" + Util.getFormattedDollarAmt(dailySpecial.getSalePrice()));

   /*         final TextView timeToMake = (TextView) findViewById(R.id.timeToMake);
            if (dailySpecial.getTimeToMake() == 0 ) {
                timeToMake.setText("Available now");
            } else {
                timeToMake.setText(dailySpecial.getTimeToMake() + " mins to make");
            }*/

            if (loginRepository.getCustomerEntity().getStore().getStoreTypeId() == 4) {
             //   menuTimingsVW.setVisibility(View.GONE);
                //timeToMake.setVisibility(View.GONE);
                ImageView img = (ImageView) findViewById(R.id.imageView);
                //img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                img.setAdjustViewBounds(true);
                img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }

            isSelectedTextView = (TextView) findViewById(R.id.isSelected);
            //compare with ViewCart and set the field
           ArrayList<FoodItem> selectedItem = Data.getInstance().getSelectedMenuItems();
           dailySpecial.setSelected(false);
           for (FoodItem item : selectedItem) {
               if (item.getMenuId() == dailySpecial.getMenuId()) {
                   dailySpecial.setSelected(true);
                  // qtySelectedTextView.setText(String.valueOf(item.getQuantity()));
                   isSelectedTextView.setText(String.valueOf(item.getQuantity()));
               }
           }
           System.out.println("VVVV: Before calling MenuView oncreate : " + selectedItem.size() );
            if (dailySpecial.isSelected() == false) {
                isSelectedTextView.setVisibility(View.GONE);
                //qtySelectedTextView.setVisibility(View.GONE);
                backgroundLayout.setBackgroundColor(Color.parseColor("#00ffffff"));
            } else {
                isSelectedTextView.setVisibility(View.VISIBLE);
                backgroundLayout.setBackgroundColor(Color.LTGRAY);
               // qtySelectedTextView.setVisibility(View.VISIBLE);
            }

            TextView soldOutLableTxt = (TextView) findViewById(R.id.soldOutLable);
            if(dailySpecial.getQuantityApplicable()) {
                soldOutLableTxt.setVisibility(View.VISIBLE);
                if(dailySpecial.getAvailableQuantity()>0) {
                    backgroundLayout.setOnClickListener(this);
                    soldOutLableTxt.setText(" In Store: "+dailySpecial.getAvailableQuantity());
                }else{

                    soldOutLableTxt.setText("Sold Out");
                    backgroundLayout.setBackgroundColor(Color.LTGRAY);
                }
            }
            else{
                soldOutLableTxt.setVisibility(View.GONE);
                backgroundLayout.setOnClickListener(this);
            }



        }

        CronetEngine cronetEngine = Cronet.getCronetEngine(this.getContext());
        ImageView view = (ImageView)findViewById(R.id.imageView);

        UrlRequest.Callback callback = new ImageLoadCallBack(view, this.getContext());
        Executor executor = Executors.newSingleThreadExecutor();
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                dailySpecial.getImageUrl(), callback, executor);

        UrlRequest request = requestBuilder.build();
        request.start();

    }

    private void initControl(Context context )
    {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.menu_view, this);
        backgroundLayout = (LinearLayout) findViewById(R.id.backgroundLayout);
    }

    private void initControl_nodata(Context context )
    {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.menu_view_no_data, this);
    }


    @Override
    public void onClick(View v) {
        final TextView isSelectedTextView = (TextView) findViewById(R.id.isSelected);

       DailySpecial cloneSelectedDailySpl = new DailySpecial();
        try {
            cloneSelectedDailySpl = (DailySpecial) dailySpecial.clone();
        } catch (Exception e) {

        }

        DialogFragment newFragment = new MenuItemViewFragment(cloneSelectedDailySpl, this);
        newFragment.setCancelable(false);
        newFragment.show(((DisplayMessageActivity)context).getSupportFragmentManager(), "missiles");

    }
    public void refreshData(FoodItem foodItem) {
        ArrayList<FoodItem> selectedItem = Data.getInstance().getSelectedMenuItems();
        dailySpecial.setSelected(false);

        for (FoodItem item : selectedItem) {
            if (item.getMenuId() == foodItem.getMenuId()) {
                dailySpecial.setSelected(true);
                isSelectedTextView.setText(String.valueOf(item.getQuantity()));
            }
        }
        if (dailySpecial.isSelected() == false) {
            isSelectedTextView.setVisibility(View.GONE);
            backgroundLayout.setBackgroundColor(Color.WHITE);

        } else {
            isSelectedTextView.setVisibility(View.VISIBLE);
            setBackgroundColor(Color.LTGRAY);
            backgroundLayout.setBackgroundColor(Color.LTGRAY);
        }

    }

}
