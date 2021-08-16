package com.appisoft.iperkz.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.entity.PastOrderDetailItem;
import com.appisoft.iperkz.entity.PastOrderItem;
import com.appisoft.iperkz.util.Util;
import com.appisoft.perkz.R;

public class PastOrderDetailsViewItem extends LinearLayout implements View.OnClickListener {
    PastOrderDetailItem pastOrderDetailsItem = null;
    Context context;
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());

    public PastOrderDetailsViewItem(Context context) {
        super(context);
        this.context = context;
        initControl(context);
    }

    public void setValues(PastOrderDetailItem PastOrderDetailItem) {

        this.pastOrderDetailsItem = PastOrderDetailItem;
        TextView pastOrderMenuItem = (TextView) findViewById(R.id.pastOrderMenuItem);
        pastOrderMenuItem.setText(PastOrderDetailItem.getMenuItemName());

        TextView pastOrderSalePrice = (TextView) findViewById(R.id.pastOrderSalePrice);
        pastOrderSalePrice.setText("$" + Util.getFormattedDollarAmt(PastOrderDetailItem.getSalePrice()));

        TextView pastOrderSplInstructions = (TextView) findViewById(R.id.pastOrderSplInstructions);
        TextView pastOrderSplLabel = (TextView) findViewById(R.id.SplLabel);
        if (PastOrderDetailItem.getSplInstructions() == null || PastOrderDetailItem.getSplInstructions().trim().length() == 0) {
            pastOrderSplInstructions.setVisibility(View.GONE);
            pastOrderSplLabel.setVisibility(View.GONE);
        } else {
            pastOrderSplInstructions.setVisibility(View.VISIBLE);
            pastOrderSplLabel.setVisibility(View.VISIBLE);
            pastOrderSplInstructions.setText(PastOrderDetailItem.getSplInstructions());
        }

        TextView selectedChoiceItemTxt = (TextView) findViewById(R.id.selectedChoiceItem);
        selectedChoiceItemTxt.setVisibility(View.VISIBLE);
        if ((PastOrderDetailItem.getAdditionsNames() != null)&&!PastOrderDetailItem.getAdditionsNames().isEmpty()){
            selectedChoiceItemTxt.setText("Additions: " + PastOrderDetailItem.getAdditionsNames());
        } else {
            selectedChoiceItemTxt.setVisibility(View.GONE);
        }

        TextView pastOrderQuantity = (TextView) findViewById(R.id.pastOrderQuantity);
        pastOrderQuantity.setText(String.valueOf(PastOrderDetailItem.getQuantity()));

        ImageView imageView = (ImageView) findViewById(R.id.shareDishImg);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDish(v);
            }
        });
    }

    private void initControl(Context context) {

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.past_order_detatils, this);
    }


    @Override
    public void onClick(View v) {


    }

    public void shareDish(View v) {
        String text_msg = "Try the " + pastOrderDetailsItem.getMenuItemName() + " referred by " + loginRepository.getCustomerEntity().getFirstName() + " from " + loginRepository.getCustomerEntity().getStore().getStoreName() + " !! \n" +
                "When you Download and Order from iPerkz, you will receive $5 OFF as One time offer.\n" +
                "https://iperkz/referral_Dish";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text_msg);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        getContext().startActivity(shareIntent);

    }


}
