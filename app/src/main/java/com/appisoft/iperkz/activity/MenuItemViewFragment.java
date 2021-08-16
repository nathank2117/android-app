package com.appisoft.iperkz.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;


import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.DialogFragment;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.adapter.ChoiceExpandableListAdapter;
import com.appisoft.iperkz.adapter.MenuListAdapter;
import com.appisoft.iperkz.callback.ImageLoadCallBack;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.AppSettings;
import com.appisoft.iperkz.entity.Choice;
import com.appisoft.iperkz.entity.FoodItem;
import com.appisoft.iperkz.entity.MenuItemAddition;
import com.appisoft.iperkz.entity.Setting;
import com.appisoft.iperkz.entity.SubItem;
import com.appisoft.iperkz.util.Util;
import com.appisoft.perkz.MenuView;
import com.appisoft.perkz.R;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.appisoft.iperkz.activity.RegistrationNewActivity.LOCATIONPREFERENCES;

public class MenuItemViewFragment extends DialogFragment {
    private FoodItem foodItem;
    int quantity = 1;
    TextView qtyTxt;
    EditText splInstructionEditText;
    MenuListAdapter adapter;
    Button incBtn;
    Button decBtn;
    boolean isNewItem = true;
    Button add2Cart;
    MenuView menuView;
    HashMap<Integer, View> childCheckedState = new HashMap<>();
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    SharedPreferences sharedpreferences;
    public MenuItemViewFragment(FoodItem foodItem, MenuListAdapter adapter) {
        super();
        this.foodItem = foodItem;
        this.adapter = adapter;
    }

    public MenuItemViewFragment(FoodItem foodItem, MenuView menuView) {
        super();
        this.foodItem = foodItem;
        this.menuView = menuView;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        sharedpreferences = getContext().getSharedPreferences(LOCATIONPREFERENCES, Context.MODE_PRIVATE);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.activity_menu_item, null);
        splInstructionEditText = view.findViewById(R.id.splInstructions);

        if (loginRepository.getCustomerEntity().getStore().getStoreTypeId() == 4) {
            ScrollView scrollView = view.findViewById(R.id.expListScroll);
            scrollView.fullScroll(ScrollView.FOCUS_UP);
            ExpandableListView expandableListView = view.findViewById(R.id.expandableListView);
            expandableListView.setFocusable(false);

            splInstructionEditText.setVisibility(View.INVISIBLE);
            TextView spltv = view.findViewById(R.id.splLabel);
            spltv.setText("");
            spltv.setVisibility(View.GONE);
            TextView spldesctv = view.findViewById(R.id.spldesc);
            spldesctv.setVisibility(View.GONE);
            spldesctv.setText("");
            ImageView img = (ImageView) view.findViewById(R.id.imageView1);
            //android:adjustViewBounds="true"
              img.setAdjustViewBounds(true);
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        qtyTxt = view.findViewById(R.id.quntitytxt);

        incBtn = view.findViewById(R.id.plusBtn);
        incBtn.setOnClickListener(this::incQty);
        decBtn = view.findViewById(R.id.minusBtn);
        decBtn.setOnClickListener(this::decQty);
        add2Cart = view.findViewById(R.id.order1);
        add2Cart.setOnClickListener(this::addToCart);
        ImageView ignoreAdd2Cart = view.findViewById(R.id.imageView_custom_dialog_close);
        ignoreAdd2Cart.setOnClickListener(this::ignoreAddToCart);

        TextView txtitem = (TextView) view.findViewById(R.id.menuItemName1);
        txtitem.setText(foodItem.getMenuItemName());

        // for Title
        if(foodItem.getImageUrl()!=null) {
            CronetEngine cronetEngine = Cronet.getCronetEngine(getContext());

            ImageView imgView = (ImageView) view.findViewById(R.id.imageView1);
            UrlRequest.Callback callback = new ImageLoadCallBack(imgView, getContext());
            Executor executor = Executors.newSingleThreadExecutor();
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    foodItem.getImageUrl(), callback, executor);

            UrlRequest request = requestBuilder.build();
            request.start();
        }

        TextView txtSalePrice = (TextView) view.findViewById(R.id.salePrice1);
        txtSalePrice.setText("$" + Util.getFormattedDollarAmt(foodItem.getSalePrice()));

        if(foodItem.getSalePrice()==0.0){
            add2Cart.setEnabled(false);
        }

        TextView txtMenuDesc = (TextView) view.findViewById(R.id.menuDesc1);
        if(!foodItem.getMenuItemDesc().equals("")&&foodItem.getMenuItemDesc()!=null) {
            txtMenuDesc.setText(foodItem.getMenuItemDesc());
        }
        else{
            txtMenuDesc.setVisibility(View.GONE);
        }
        txtMenuDesc.setText(foodItem.getMenuItemDesc());

        TextView qtyTxt = view.findViewById(R.id.quntitytxt);

        ArrayList<FoodItem> selectedItem = Data.getInstance().getSelectedMenuItems();
        if (Data.getInstance().getSelectedMenuItems().contains(foodItem)) {
            for (FoodItem item : selectedItem) {
                if (item.getMenuId() == foodItem.getMenuId()) {
                    splInstructionEditText.setText(item.getSpecialInstructions());
                    qtyTxt.setText(String.valueOf(item.getQuantity()));
                    quantity = item.getQuantity();
                    if (item.getHasAdditions() > 0) {
                        txtSalePrice.setText("$" + Util.getFormattedDollarAmt(item.getSalePrice()));
                        /*//Double salePrice=foodItem.getSalePrice();
                        for(MenuItemAddition menuItemAddition: item.getAdditions())
                        if(menuItemAddition.isSelected()){
                            salePrice += menuItemAddition.getPrice();
                        }*/

                    }


                    break;
                }
            }
            isNewItem = false;
            add2Cart.setText("Update Cart");
        }
        ///.......................ExpandableListView
        //setContentView(R.layout.activity_main1);

        if (foodItem.getHasAdditions() > 0) {
            ExpandableListView expandableListView;
            ExpandableListAdapter expandableListAdapter;
            List<String> expandableListTitle;
            LinkedHashMap<String, List<Choice>> expandableListDetail = new LinkedHashMap<String, List<Choice>>();

                List<Choice> additions = new ArrayList<Choice>();
            for (MenuItemAddition menuItemAddition : foodItem.getAdditions()) {

               List<Choice> subItems = new ArrayList<Choice>();
                if(menuItemAddition.getSubItems().size() > 0) {
                    for (SubItem subItem : menuItemAddition.getSubItems()) {
                        subItem.setMenuId(menuItemAddition.getMenuId());
                        subItem.setSelectionType(menuItemAddition.getSelectionType());
                        subItems.add(subItem);
                    }
                    expandableListDetail.put(menuItemAddition.getName(), subItems);
                }
                else{
                    additions.add(menuItemAddition);
                }
            }
            if(additions.size()>0) {

                TextView choicesHeader = (TextView) view.findViewById(R.id.choicesHeader);

                if(expandableListDetail.size()>1) {
                    expandableListDetail.put("Other", additions);
                    choicesHeader.setVisibility(View.VISIBLE);
                }
                else {
                    choicesHeader.setVisibility(View.GONE);
                    expandableListDetail.put("Choices", additions);
                }
            }
            expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
            expandableListView.setVisibility(View.VISIBLE);
            expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
            expandableListAdapter = new ChoiceExpandableListAdapter(this.getContext(), expandableListTitle, expandableListDetail, childCheckedState);
            expandableListView.setAdapter(expandableListAdapter);
            expandableListView.expandGroup(0);
            expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                @Override
                public void onGroupExpand(int groupPosition) {
                   /* Toast.makeText(getContext(),
                            expandableListTitle.get(groupPosition) + " List Expanded.",
                            Toast.LENGTH_SHORT).show();*/
                }
            });

            expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                @Override
                public void onGroupCollapse(int groupPosition) {
                    /*Toast.makeText(getContext(),
                            expandableListTitle.get(groupPosition) + " List Collapsed.",
                            Toast.LENGTH_SHORT).show();*/

                }
            });

            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {

                    Log.i("group position + child ",groupPosition+" "+childPosition);

                    Double currentSalePrice = Double.parseDouble(txtSalePrice.getText().toString().substring(1));

                    /////////////////////////////////
                    if (childCheckedState.get(groupPosition) != null){
                    //childCheckedState.put(groupPosition, v);
                       View otherView = childCheckedState.get(groupPosition);
                        RadioButton otherRadioButton = (RadioButton) otherView.findViewById(R.id.radioChoice);
                        if( childPosition != (int) otherRadioButton.getTag()) {
                            otherRadioButton.setChecked(false);
                            otherRadioButton.setTextColor(Color.BLACK);
                            int otherChildPosition = (int) otherRadioButton.getTag();
                            expandableListDetail.get(
                                    expandableListTitle.get(groupPosition)).get(
                                    otherChildPosition).setSelected(false);

                            currentSalePrice = currentSalePrice - expandableListDetail.get(
                                    expandableListTitle.get(groupPosition)).get(
                                    otherChildPosition).getPrice();
                            foodItem.setSalePrice(currentSalePrice);

                            Log.i("foodItem.getSalePrice()", " " + foodItem.getSalePrice());
                            txtSalePrice.setText("$" + Util.getFormattedDollarAmt(foodItem.getSalePrice()));

                            TextView otherItemPriceTextView = (TextView) otherView
                                    .findViewById(R.id.extraItemPrice);
                            otherItemPriceTextView.setTextColor(Color.BLACK);
                        }
                        else{
                            childCheckedState.remove(groupPosition);
                        }

                }


                    /////////////////////////////////////////
                    CheckBox checkbox = (CheckBox) v.findViewById(R.id.checkboxChoice);
                    RadioButton radioButton = (RadioButton) v.findViewById(R.id.radioChoice);

                    TextView extraItemPriceTextView = (TextView) v
                            .findViewById(R.id.extraItemPrice);


                    if (!expandableListDetail.get(
                            expandableListTitle.get(groupPosition)).get(
                            childPosition).isSelected()) {
                        foodItem.setSalePrice(currentSalePrice + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition).getPrice());
                        txtSalePrice.setText("$" + Util.getFormattedDollarAmt(foodItem.getSalePrice()));
                        expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition).setSelected(true);
                        if(checkbox!=null) {
                            checkbox.setChecked(true);
                            //checkbox.setTextColor(Color.parseColor("#ba7004"));
                            checkbox.setTypeface(null, Typeface.BOLD);
                            //extraItemPriceTextView.setTypeface(null,Typeface.BOLD);
                        }
                        if(radioButton!=null) {
                            radioButton.setChecked(true);
                            radioButton.setTag(childPosition);
                            childCheckedState.put(groupPosition, v);
                            //radioButton.setTextColor(Color.parseColor("#ba7004"));
                            radioButton.setTypeface(null, Typeface.BOLD);
                        }

                       // extraItemPriceTextView.setTextColor(Color.parseColor("#ba7004"));
                        extraItemPriceTextView.setTypeface(null, Typeface.BOLD);
                    } else {
                        expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition).setSelected(false);
                        foodItem.setSalePrice(currentSalePrice - expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition).getPrice());
                        txtSalePrice.setText("$" + Util.getFormattedDollarAmt(foodItem.getSalePrice()));
                        if(checkbox!=null) {
                            checkbox.setChecked(false);
                            checkbox.setTextColor(Color.BLACK);
                        }
                        if(radioButton!=null) {
                            radioButton.setChecked(false);
                            radioButton.setTag(childPosition);
                            //childCheckedState.put(groupPosition, v);
                            radioButton.setTextColor(Color.BLACK);
                        }

                        extraItemPriceTextView.setTextColor(Color.BLACK);
                    }


                    if(foodItem.getSalePrice()>0.0){
                        add2Cart.setEnabled(true);
                    }else{
                        add2Cart.setEnabled(false);
                    }

                    return false;

                }



            });


        }


        String appsettingsJson = sharedpreferences.getString("APPSETTINGS", null);
        ObjectMapper mapper11 = new ObjectMapper();
        AppSettings appSettings = null;
        try {
            if (appsettingsJson != null)
                appSettings = mapper11.readValue(appsettingsJson, AppSettings.class);


        } catch (IOException e) {
            e.printStackTrace();
        }

        String wallpaper = "";
        for(Setting setting : appSettings.getSettings()){

            if(setting.getKey().equals("main-background")){
                wallpaper = setting.getValue();
            }
        }

        StrictMode.ThreadPolicy policy10 = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy10);
        try {
            URL url = new URL(wallpaper);
            Drawable backgroundImge = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeStream((InputStream) url.getContent()));
            ConstraintLayout constraintLayout = view.findViewById(R.id.menuitemlt);
            constraintLayout.setBackground(backgroundImge);


        }
        catch(IOException io){
            io.printStackTrace();
        }

        ///.......................
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        foodItem.setSpecialInstructions(splInstructionEditText.getText().toString());
                        foodItem.setQuantity(Integer.parseInt(qtyTxt.getText().toString()));
                    }
                });

        return builder.create();

    }

    public void incQty(View view) {
        quantity += 1;

        if (quantity > 0) {
            decBtn.setEnabled(true);
        }
        if (!isNewItem) {
            add2Cart.setText("Update Cart");
        }
        if (foodItem.getQuantityApplicable()) {
            if (quantity < foodItem.getAvailableQuantity())
                qtyTxt.setText(String.valueOf(quantity));
            else {
                quantity = foodItem.getAvailableQuantity();
                qtyTxt.setText(String.valueOf(quantity));
            }
        } else {
            qtyTxt.setText(String.valueOf(quantity));
        }
    }

    public void decQty(View view) {
        quantity -= 1;
        if (isNewItem) {
            if (quantity == 0) {
                quantity = 1;
            }
        } else {
            if (quantity == 0) {
                decBtn.setEnabled(false);
                add2Cart.setText("Remove from Cart");
            }

        }
        qtyTxt.setText(String.valueOf(quantity));
    }

    public void addToCart(View view) {

        foodItem.setSpecialInstructions(splInstructionEditText.getText().toString());
        foodItem.setQuantity(Integer.parseInt(qtyTxt.getText().toString()));
        foodItem.setDailySpecial(true);
        ArrayList<FoodItem> selectedItem = Data.getInstance().getSelectedMenuItems();
        if (isNewItem) {
            Data.getInstance().getSelectedMenuItems().add(foodItem);
        } else {
            if (Data.getInstance().getSelectedMenuItems().contains(foodItem)) {
                for (FoodItem item : selectedItem) {
                    if (item.getMenuId() == foodItem.getMenuId()) {
                        if (foodItem.getQuantity() == 0) { // remove from selections
                            selectedItem.remove(item);
                            Data.getInstance().setSelectedMenuItems(selectedItem);
                        } else {
                            item.setQuantity(foodItem.getQuantity());
                            item.setSpecialInstructions(foodItem.getSpecialInstructions());
                            //update choices
                            if (item.getHasAdditions() > 0) {
                                item.setSalePrice(foodItem.getSalePrice());
                                item.setAdditions(foodItem.getAdditions());
                            } //update choices
                        }
                        break;
                    }
                }
            }
        }

        //Data.getInstance().setTotalCost(foodItem.getSalePrice() * quantity);
        Data.getInstance().recalculateTotalCost();
        Log.println(Log.INFO, "test ", Data.getInstance().getSelectedMenuItems().size() + "");
        LinearLayout linearLayout = getActivity().findViewById(R.id.vcartfragment);
        if(!Data.getInstance().getTotalCost().equals("$ 0.00")) {
            linearLayout.setBackgroundColor(Color.parseColor("#3F51B5"));
        }
        else{
            linearLayout.setBackgroundColor(Color.GRAY);
        }
        this.dismiss();
    }

    public void ignoreAddToCart(View view) {

      if (isNewItem) {

            if (!Data.getInstance().getSelectedMenuItems().contains(foodItem)) {
                if(!foodItem.isSelected()) {
                    if (foodItem.getHasAdditions() > 0) {
                        for (MenuItemAddition addition : foodItem.getAdditions()) {
                            addition.setSelected(false);
                        }
                    }
                }
            }
        }

        this.dismiss();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        if (adapter instanceof MenuListAdapter) {
            // adapter.setmDataset(Data.getInstance().getSelectedMenuItems())
            adapter.notifyDataSetChanged();
        } else if (menuView instanceof MenuView) {
            menuView.refreshData(foodItem);
        }

        childCheckedState.clear();
    }
}
