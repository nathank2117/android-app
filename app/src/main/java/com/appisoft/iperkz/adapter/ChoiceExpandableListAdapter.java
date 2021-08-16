package com.appisoft.iperkz.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.drm.DrmStore;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.CompoundButtonCompat;

import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.Choice;
import com.appisoft.iperkz.entity.FoodItem;
import com.appisoft.iperkz.entity.MenuItemAddition;
import com.appisoft.iperkz.entity.SubItem;
import com.appisoft.iperkz.util.Util;
import com.appisoft.perkz.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.generateViewId;

public class ChoiceExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<Choice>> expandableListDetail;
    HashMap<Integer, View> childCheckedState = new HashMap<>();

    public ChoiceExpandableListAdapter(Context context, List<String> expandableListTitle,
                                       HashMap<String, List<Choice>> expandableListDetail, HashMap<Integer, View> childCheckedState) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.childCheckedState = childCheckedState;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Choice menuItemAddition = (Choice) getChild(listPosition, expandedListPosition);

        View childView = null;

        if(menuItemAddition.getSelectionType()!=null && menuItemAddition.getSelectionType().equalsIgnoreCase("RADIO")){

            childView = getRadioChildView(listPosition, expandedListPosition, isLastChild, convertView, parent);
        }
        else{
            childView = getCheckboxChildView(listPosition, expandedListPosition, isLastChild, convertView, parent);
        }
            return childView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        //listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    public View getRadioChildView(int listPosition, final int expandedListPosition,
                              boolean isLastChild, View convertView, ViewGroup parent) {
        final Choice menuItemAddition = (Choice) getChild(listPosition, expandedListPosition);

        //if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_radioitem, null);
        //}


        if(menuItemAddition.getSelectionType()!=null && menuItemAddition.getSelectionType().equalsIgnoreCase("Radio")){

            RadioButton radioButton = (RadioButton) convertView.findViewById(R.id.radioChoice);
            if(radioButton!=null) {
                radioButton.setText(menuItemAddition.getName());
                radioButton.setTypeface(radioButton.getTypeface(), Typeface.BOLD);

                TextView extraItemPriceTextView = (TextView) convertView
                        .findViewById(R.id.extraItemPrice);

                if (menuItemAddition.getPrice() != 0.0) {

                    extraItemPriceTextView.setText("$" + Util.getFormattedDollarAmt(menuItemAddition.getPrice()));
                } else {
                    extraItemPriceTextView.setText(" ");
                }

                if (radioButton != null) {
                    radioButton.setVisibility(View.VISIBLE);
                    if (menuItemAddition.isDefaultSelection()) {
                        radioButton.setChecked(true);
                        radioButton.setTag(expandedListPosition);

                    }
                }

                ArrayList<FoodItem> selectedItem = Data.getInstance().getSelectedMenuItems();
                for (FoodItem item : selectedItem) {
                    if (item.getHasAdditions() > 0) {
                        for (MenuItemAddition selectedChoice : item.getAdditions()) {

                            if (selectedChoice.getSubItems().size() > 0) {
                                for (SubItem subItem : selectedChoice.getSubItems()) {
                                    if (menuItemAddition.getAdditionsId().equals(subItem.getAdditionsId()) &&(menuItemAddition.getMenuId().equals(subItem.getMenuId())) && (selectedChoice.getAdditionsId().equals(subItem.getParentId()))) {
                                        if (subItem.isSelected()) {
                                            radioButton.setChecked(true);
                                            menuItemAddition.setSelected(true);
                                            radioButton.setTypeface(null,Typeface.BOLD);
                                            extraItemPriceTextView.setTypeface(null,Typeface.BOLD);
                                            //radioButton.setTextColor(Color.parseColor("#ba7004"));
                                            //extraItemPriceTextView.setTextColor(Color.parseColor("#ba7004"));
                                            radioButton.setTag(expandedListPosition);
                                            childCheckedState.put(listPosition, convertView);
                                        }
                                        break;
                                    }
                                }
                                //
                                if(menuItemAddition.isSelected()){
                                    radioButton.setChecked(true);
                                    radioButton.setTypeface(null,Typeface.BOLD);
                                    extraItemPriceTextView.setTypeface(null,Typeface.BOLD);
                                    //radioButton.setTextColor(Color.parseColor("#ba7004"));
                                    //extraItemPriceTextView.setTextColor(Color.parseColor("#ba7004"));
                                    radioButton.setTag(expandedListPosition);
                                    childCheckedState.put(listPosition, convertView);
                                }
                            }

                        }

                    }
                }

                if(selectedItem.size()==0){
                    if (radioButton != null){
                        if(menuItemAddition.isSelected()){
                            radioButton.setChecked(true);
                            //radioButton.setTextColor(Color.parseColor("#ba7004"));
                            //extraItemPriceTextView.setTextColor(Color.parseColor("#ba7004"));
                            radioButton.setTypeface(null,Typeface.BOLD);
                            extraItemPriceTextView.setTypeface(null,Typeface.BOLD);
                            radioButton.setTag(expandedListPosition);
                            childCheckedState.put(listPosition, convertView);
                        }
                    }
                }

                if (radioButton != null)
                    radioButton.setClickable(false);

            }

        }

        if (menuItemAddition.isDefaultSelection()) {
            childCheckedState.put(listPosition, convertView);

        }
        return convertView;
    }


    public View getCheckboxChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Choice menuItemAddition = (Choice) getChild(listPosition, expandedListPosition);

      //  if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_checkboxitem, null);
       // }

             if(menuItemAddition.getSelectionType()!=null && menuItemAddition.getSelectionType().equalsIgnoreCase("Checkbox")){
                CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.checkboxChoice);
                if(checkbox!=null) {
                    checkbox.setText(menuItemAddition.getName());
                    checkbox.setTypeface(checkbox.getTypeface(), Typeface.BOLD);
                    checkbox.setVisibility(View.VISIBLE);

                    TextView extraItemPriceTextView = (TextView) convertView
                            .findViewById(R.id.extraItemPrice);

                    if (menuItemAddition.getPrice() != 0.0) {

                        extraItemPriceTextView.setText("$" + Util.getFormattedDollarAmt(menuItemAddition.getPrice()));
                    } else {
                        extraItemPriceTextView.setText(" ");
                    }


                    ArrayList<FoodItem> selectedItem = Data.getInstance().getSelectedMenuItems();
                    for (FoodItem item : selectedItem) {
                        if (item.getHasAdditions() > 0) {
                            for (MenuItemAddition selectedChoice : item.getAdditions()) {

                                if (selectedChoice.getSubItems().size() > 0) {
                                    for (SubItem subItem : selectedChoice.getSubItems()) {
                                        if (menuItemAddition.getAdditionsId().equals(subItem.getAdditionsId()) &&(menuItemAddition.getMenuId().equals(subItem.getMenuId())) && (selectedChoice.getAdditionsId().equals(subItem.getParentId()))) {
                                            if (subItem.isSelected()) {
                                                checkbox.setChecked(true);
                                                checkbox.setTypeface(null,Typeface.BOLD);
                                                extraItemPriceTextView.setTypeface(null,Typeface.BOLD);
                                                //checkbox.setTextColor(Color.parseColor("#ba7004"));
                                                //extraItemPriceTextView.setTextColor(Color.parseColor("#ba7004"));
                                                menuItemAddition.setSelected(true);
                                            }
                                            break;
                                        }
                                    }
                                    //
                                    if(menuItemAddition.isSelected()){
                                        checkbox.setChecked(true);
                                        checkbox.setTypeface(null,Typeface.BOLD);
                                        extraItemPriceTextView.setTypeface(null,Typeface.BOLD);
                                        //checkbox.setTextColor(Color.parseColor("#ba7004"));
                                        //extraItemPriceTextView.setTextColor(Color.parseColor("#ba7004"));
                                    }

                                }

                            }

                        }
                    }

                    if(selectedItem.size()==0){
                        if (checkbox != null){
                            if(menuItemAddition.isSelected()){
                                checkbox.setChecked(true);
                                checkbox.setTypeface(null,Typeface.BOLD);
                                extraItemPriceTextView.setTypeface(null,Typeface.BOLD);
                                //checkbox.setTextColor(Color.parseColor("#ba7004"));
                                //extraItemPriceTextView.setTextColor(Color.parseColor("#ba7004"));
                            }
                        }
                    }

                    if (checkbox != null)
                        checkbox.setClickable(false);
                }
            }
             else {
                 CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkboxChoice);
                 if(checkBox!=null) {
                     checkBox.setText(menuItemAddition.getName());
                     checkBox.setTypeface(checkBox.getTypeface(), Typeface.BOLD);
                     checkBox.setVisibility(View.VISIBLE);

                     TextView extraItemPriceTextView = (TextView) convertView
                             .findViewById(R.id.extraItemPrice);

                     if (menuItemAddition.getPrice() != 0.0) {

                         extraItemPriceTextView.setText("$" + Util.getFormattedDollarAmt(menuItemAddition.getPrice()));
                     } else {
                         extraItemPriceTextView.setText(" ");
                     }

                     ArrayList<FoodItem> selectedItem = Data.getInstance().getSelectedMenuItems();
                     for (FoodItem item : selectedItem) {
                         if (item.getHasAdditions() > 0) {
                             for (MenuItemAddition selectedChoice : item.getAdditions()) {
                                 if (menuItemAddition.getAdditionsId().equals(selectedChoice.getAdditionsId()) && (menuItemAddition.getMenuId().equals(selectedChoice.getMenuId()))) {
                                     if (selectedChoice.isSelected()) {
                                         checkBox.setChecked(true);
                                         checkBox.setTypeface(null,Typeface.BOLD);
                                         extraItemPriceTextView.setTypeface(null,Typeface.BOLD);
                                         //checkBox.setTextColor(Color.parseColor("#ba7004"));
                                         //extraItemPriceTextView.setTextColor(Color.parseColor("#ba7004"));
                                         menuItemAddition.setSelected(true);
                                     }
                                     break;
                                 }

                             }

                             //
                             if(menuItemAddition.isSelected()){
                                 checkBox.setChecked(true);
                                 checkBox.setTypeface(null,Typeface.BOLD);
                                 extraItemPriceTextView.setTypeface(null,Typeface.BOLD);
                                 //checkBox.setTextColor(Color.parseColor("#ba7004"));
                                 //extraItemPriceTextView.setTextColor(Color.parseColor("#ba7004"));
                             }

                         }
                     }

                     if(selectedItem.size()==0){
                         if (checkBox != null){
                             if(menuItemAddition.isSelected()){
                                 checkBox.setChecked(true);
                                 checkBox.setTypeface(null,Typeface.BOLD);
                                 extraItemPriceTextView.setTypeface(null,Typeface.BOLD);
                                 //checkBox.setTextColor(Color.parseColor("#ba7004"));
                                 //extraItemPriceTextView.setTextColor(Color.parseColor("#ba7004"));
                             }
                         }
                     }

                     if (checkBox != null)
                         checkBox.setClickable(false);
                 }
             }

        return convertView;
    }

}
