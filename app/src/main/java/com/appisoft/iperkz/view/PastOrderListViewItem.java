package com.appisoft.iperkz.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.appisoft.iperkz.activity.MenuDetailsActivity;
import com.appisoft.iperkz.activity.SearchableActivity;
import com.appisoft.iperkz.activity.SpecialInstructionsDialog;
import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.callback.ImageLoadCallBack;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.FoodItem;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.entity.PastOrderDetailItem;
import com.appisoft.iperkz.entity.PastOrderItem;
import com.appisoft.iperkz.util.Util;
import com.appisoft.perkz.R;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PastOrderListViewItem extends LinearLayout  implements View.OnClickListener {
    private static final String TAG = "PastOrderListViewItem";
    private TextView trackingView;
    PastOrderItem pastOrderItem = null;
    Context context ;
    private String url = "";
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());


    public PastOrderListViewItem(Context context) {
        super(context);
        this.context = context;
        initControl(context);
    }

    public void setValues(PastOrderItem pastOrderItem) {

        this.pastOrderItem = pastOrderItem;

        TextView orderNumTextView = (TextView)findViewById(R.id.orderNum);
        orderNumTextView.setText(pastOrderItem.getOrderId() +"");

        trackingView.setText("Track");
        url = pastOrderItem.getTableNumber();
        System.out.println(pastOrderItem.getTableNumber());
        TextView orderCreationDate = (TextView)findViewById(R.id.orderCreationDate);
        orderCreationDate.setText(pastOrderItem.getOrderTime());

        TextView totalAmtTextView = (TextView)findViewById(R.id.totalAmt);
        Double totalSalePrice = pastOrderItem.getTotalSalePrice()
                +pastOrderItem.getSalesTax()
                +pastOrderItem.getTipAmount()
                +pastOrderItem.getTransactionFee();

        if(pastOrderItem.getTakeOut()==2) {

            totalSalePrice = totalSalePrice + pastOrderItem.getDeliveryAmount();

        }

        totalAmtTextView.setText("$" + Util.getFormattedDollarAmt(totalSalePrice));

        TextView discountedTotalAmt = (TextView)findViewById(R.id.discountedTotalAmt);
        discountedTotalAmt.setVisibility(View.VISIBLE);
        Double discountedTotalAmtValue = pastOrderItem.getTotalSalePrice()
                +pastOrderItem.getSalesTax()
                +pastOrderItem.getTipAmount()
                +pastOrderItem.getTransactionFee()
                - pastOrderItem.getPerkzDeducted();

        if(pastOrderItem.getTakeOut()==2) {

            discountedTotalAmtValue = discountedTotalAmtValue + pastOrderItem.getDeliveryAmount();

        }

        TextView menuAmtTextView = (TextView)findViewById(R.id.menuAmt);
        menuAmtTextView.setText("$" + Util.getFormattedDollarAmt(pastOrderItem.getMenuAmount()));

        TextView salesTaxAmt = (TextView)findViewById(R.id.salesTaxAmt);
        salesTaxAmt.setText("$" + Util.getFormattedDollarAmt(pastOrderItem.getSalesTax()));
        TextView cardtransactionFee = (TextView) findViewById(R.id.ccTxnFeeAmt);
        TextView cardtransactionFeeLbl = (TextView) findViewById(R.id.cardtransactionFee);

        //when transaction fee is ZERO we are assuming PAYMENT MODE is STORE( not customer)

        if(!loginRepository.getCustomerEntity().getStore().getChargeMode().equalsIgnoreCase("STORE")){

            if (pastOrderItem.getTransactionFee() != null && pastOrderItem.getTransactionFee() > 0) {
                cardtransactionFee.setVisibility(View.VISIBLE);
                cardtransactionFeeLbl.setVisibility(View.VISIBLE);
                if (pastOrderItem.getTransactionFee() != null && pastOrderItem.getTransactionFee() > 0) {
                    cardtransactionFee.setText("$" + Util.getFormattedDollarAmt(pastOrderItem.getTransactionFee()));
                } else {
                    cardtransactionFee.setText("$0.00");
                }
            } else {
                cardtransactionFee.setVisibility(View.GONE);
                cardtransactionFeeLbl.setVisibility(View.GONE);
            }
        }
        else{
            if (pastOrderItem.getTransactionFee() != null && pastOrderItem.getTransactionFee() > 0) {
                totalSalePrice = totalSalePrice - pastOrderItem.getTransactionFee();
                discountedTotalAmtValue = discountedTotalAmtValue - pastOrderItem.getTransactionFee();
            }
            cardtransactionFee.setVisibility(View.GONE);
            cardtransactionFeeLbl.setVisibility(View.GONE);
        }

        totalAmtTextView.setText("$" + Util.getFormattedDollarAmt(totalSalePrice));

        if(pastOrderItem.getPerkzDeducted()!=null && pastOrderItem.getPerkzDeducted()>0) {
            discountedTotalAmt.setText("$" + Util.getFormattedDollarAmt(discountedTotalAmtValue));
            totalAmtTextView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            discountedTotalAmt.setVisibility(View.GONE);
        }

        TextView perkzDeductedAmt = (TextView)findViewById(R.id.perkzDeductedAmt);
        perkzDeductedAmt.setText("$" + Util.getFormattedDollarAmt(pastOrderItem.getPerkzDeducted()));

        TextView remainingPerkzAmt = (TextView)findViewById(R.id.remainingPerkzAmt);
        remainingPerkzAmt.setText("$" + Util.getFormattedDollarAmt(pastOrderItem.getPerkzRemaining()));

        TextView pastOrderTipAmt = (TextView)findViewById(R.id.pastOrderTip);
        pastOrderTipAmt.setText("$" + Util.getFormattedDollarAmt(pastOrderItem.getTipAmount()));

        //Delivery
        TextView takeOutTV = (TextView)findViewById(R.id.orderType);
        if(pastOrderItem.getTakeOut()==2) {
            LinearLayout deliveryAmtLT = (LinearLayout) findViewById(R.id.pastOrderDeliveryAmtLT);
            deliveryAmtLT.setVisibility(View.VISIBLE);

            LinearLayout orderDeliveryDateLT = (LinearLayout) findViewById(R.id.orderDeliveryDateLT);
            orderDeliveryDateLT.setVisibility(View.VISIBLE);

            LinearLayout orderdeliveryAddressLT = (LinearLayout) findViewById(R.id.orderdeliveryAddressLT);
            orderdeliveryAddressLT.setVisibility(View.VISIBLE);


            takeOutTV.setText("Delivery");

            TextView deliveryAmtTV = (TextView)findViewById(R.id.pastOrderDeliveryAmt);
            deliveryAmtTV.setText("$" + Util.getFormattedDollarAmt(pastOrderItem.getDeliveryAmount()));


            TextView requestedDeliveryDateTV = (TextView)findViewById(R.id.orderDeliveryDate);
            requestedDeliveryDateTV.setText(pastOrderItem.getRequestedDeliveryDate());

            TextView requestedDeliveryAddressTV = (TextView)findViewById(R.id.orderdeliveryAddress);
            requestedDeliveryAddressTV.setText(pastOrderItem.getDeliveryAddress().replaceAll(",", "\n"));

        }else if(pastOrderItem.getTakeOut()==1){
            takeOutTV.setText("Take Out");
        }
        else{
            takeOutTV.setText("Dine In");
        }

        ////


        TextView orderStatus = (TextView)findViewById(R.id.orderStatus);
        orderStatus.setText( pastOrderItem.getOrderStatus());
        if (pastOrderItem.getOrderStatus() != null && !pastOrderItem.getOrderStatus().equalsIgnoreCase("COMPLETED")) {
            orderStatus.setTextColor(Color.MAGENTA);
        } else {
            orderStatus.setTextColor(Color.parseColor("#3F51B5"));
        }

        TextView storeName = (TextView) findViewById(R.id.orderStoreName);
        storeName.setText(pastOrderItem.getStoreName() +"\n"+ pastOrderItem.getStoreAddress1());

        ViewGroup viewToShowOrderDetails  = (ViewGroup)findViewById(R.id.menuDetailsLayout);

        for (PastOrderDetailItem detailItem : pastOrderItem.getDetails()) {

            PastOrderDetailsViewItem viewItem = new PastOrderDetailsViewItem(context);
            viewItem.setValues(detailItem);

                viewToShowOrderDetails.addView(viewItem);

        }

    }

    private void initControl(Context context )
    {

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.list_past_order_item, this);
        trackingView = (TextView)findViewById(R.id.trackinglink);
        trackingView.setClickable(true);
        trackingView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        trackingView
                .setClickable(false);
        AlertDialog.Builder alert = new AlertDialog.Builder(this.context);
        alert.setTitle("Track your order");
        WebView wv = new WebView(this.context);
        wv.getSettings().setJavaScriptEnabled(true);

       // wv.loadUrl("http:\\perkz.s3.us-east-2.amazonaws.com/Terms+and+conditions+-+iPERKZ-1.0.htm");
        wv.loadUrl(url);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                trackingView.setClickable(true);
                dialog.dismiss();
            }
        });
        alert.show();

    }


}
