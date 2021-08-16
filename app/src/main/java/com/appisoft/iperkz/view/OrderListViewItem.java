package com.appisoft.iperkz.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appisoft.iperkz.activity.ViewCartActivity;
import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.callback.ImageLoadCallBack;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.FoodItem;
import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.entity.MenuItemAddition;
import com.appisoft.iperkz.entity.SubItem;
import com.appisoft.iperkz.util.Util;
import com.appisoft.perkz.R;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class OrderListViewItem extends LinearLayout {
    FoodItem foodItem = null;
    ViewCartActivity activity;
    public ImageButton deleteButton;
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    public OrderListViewItem(Context context) {
        super(context);
        initControl(context);
    }

    public void setValues(FoodItem menuItem) {

        this.foodItem = foodItem;

        TextView menuItemNameVW = (TextView) findViewById(R.id.menuItemName);
        menuItemNameVW.setText(menuItem.getMenuItemName());

        TextView menuItemDescVW = (TextView) findViewById(R.id.menuDesc);
        if(!menuItem.getMenuItemDesc().equals("")&&menuItem.getMenuItemDesc()!=null) {
            menuItemDescVW.setText(menuItem.getMenuItemDesc());
        }
        else{
            menuItemDescVW.setVisibility(View.GONE);
        }

        TextView choicesVW = (TextView) findViewById(R.id.selectedChoice);

        if (menuItem.getHasAdditions() > 0) {
            String choicesTxt = "Additions:\n";
            for (MenuItemAddition selectedChoice : menuItem.getAdditions()) {

                if(selectedChoice.getSubItems().size()>0) {
                    for (SubItem selectedSubItem : selectedChoice.getSubItems()) {
                        if (selectedSubItem.isSelected()) {
                            choicesTxt += selectedSubItem.getName();
                             if ( selectedSubItem.getPrice() > 0 ) {
                                 choicesTxt  += ": $" + selectedSubItem.getPrice();
                             }
                            choicesTxt +=  "\n";
                        }
                    }
                }
                else{
                    if (selectedChoice.isSelected()) {
                        choicesTxt += selectedChoice.getName();
                        if ( selectedChoice.getPrice() > 0 ) {
                            choicesTxt += ": $" + selectedChoice.getPrice();
                        }
                        choicesTxt +=  "\n";
                    }
                }

            }
            choicesVW.setVisibility(View.VISIBLE);
            if(!choicesTxt.equals("Additions:\n")) {
                choicesVW.setText(choicesTxt);
            }
            else{
                choicesVW.setVisibility(View.GONE);
            }
        }
        else{
            choicesVW.setVisibility(View.GONE);
        }

        TextView splInstructions = (TextView) findViewById(R.id.splInstructions);
        splInstructions.setVisibility(View.VISIBLE);
        if(!menuItem.getSpecialInstructions().equals("")&&menuItem.getSpecialInstructions()!=null)
            splInstructions.setText(menuItem.getSpecialInstructions());
        else
            splInstructions.setVisibility(View.GONE);

        TextView salePriceVW = (TextView) findViewById(R.id.salePrice);
        salePriceVW.setText("$" + Util.getFormattedDollarAmt(menuItem.getSalePrice()));
        TextView timetoMakeView = (TextView) findViewById(R.id.timeToMake);

        if (menuItem.getTimeToMake() == 0) {
            timetoMakeView.setText("Available now");
        } else {
            timetoMakeView.setText(menuItem.getTimeToMake() + " mins to make");
        }

        if (loginRepository.getCustomerEntity().getStore().getStoreTypeId() == 4) {

            TextView labelTV = (TextView) findViewById(R.id.itemTotalLbl);
            labelTV.setVisibility(VISIBLE);
            TextView totalPriceTV = (TextView) findViewById(R.id.itemsubTotalPrice);
            totalPriceTV.setVisibility(VISIBLE);
            totalPriceTV.setText("$" + Util.getFormattedDollarAmt(menuItem.getQuantity()*menuItem.getSalePrice()));
            timetoMakeView.setVisibility(View.GONE);
            ImageView img = (ImageView) findViewById(R.id.imageView);
            img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        }

        TextView quantity = (TextView) findViewById(R.id.quantity);
        quantity.setText(String.valueOf(menuItem.getQuantity()));

        CronetEngine cronetEngine = Cronet.getCronetEngine(this.getContext());
        ImageView view = (ImageView) findViewById(R.id.imageView);

        deleteButton = (ImageButton) findViewById(R.id.delete_menu_item);
        //deleteButton.setOnClickListener(activity);

        if(menuItem.getImageUrl()!=null) {
            UrlRequest.Callback callback = new ImageLoadCallBack(view, this.getContext());
            Executor executor = Executors.newSingleThreadExecutor();
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    menuItem.getImageUrl(), callback, executor);

            UrlRequest request = requestBuilder.build();
            request.start();
        }

    }

    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.list_order_view_item, this);
        this.activity = (ViewCartActivity) context;
    }


}
