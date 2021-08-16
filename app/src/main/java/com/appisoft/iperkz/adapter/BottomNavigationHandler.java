package com.appisoft.iperkz.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appisoft.iperkz.activity.AccountActivity;
import com.appisoft.iperkz.activity.IperkzHomeActivity;
import com.appisoft.iperkz.activity.OrdersHistoryActivity;
import com.appisoft.iperkz.activity.RegistrationNewActivity;
import com.appisoft.iperkz.activity.ui.login.ValidateOtp;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.view.OrderListViewItem;
import com.appisoft.perkz.DisplayMessageActivity;
import com.appisoft.perkz.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationHandler extends  Object implements BottomNavigationView.OnNavigationItemSelectedListener{
    private Activity mainActivity;
    public BottomNavigationHandler(Context context) {
        this.mainActivity = (Activity) context;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
       // ViewGroup viewGrp = (ViewGroup) mainActivity.findViewById(R.id.horizontalScrollContainerView);
       /*
        mAdapter = new MenuListAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);
        */
        if (R.id.bottomNavigationHomeMenuId == menuItem.getItemId()) {

            if (mainActivity.getLocalClassName().equals(IperkzHomeActivity.class.getSimpleName())) {
                return true;
            }
            Intent intent = new Intent(mainActivity, IperkzHomeActivity.class);
            mainActivity.startActivity(intent);
            mainActivity.finish();

        } else if (R.id.bottomNavigationAccountMenuId == menuItem.getItemId()) {
            if (mainActivity.getLocalClassName().equals(AccountActivity.class.getSimpleName())) {
                return true;
            }
            Intent intent = new Intent(mainActivity, AccountActivity.class);
            mainActivity.startActivity(intent);
            mainActivity.finish();

        } else if (R.id.bottomNavigationOrderMenuId == menuItem.getItemId()) {
            if (mainActivity.getLocalClassName().equals(OrdersHistoryActivity.class.getSimpleName())) {
                return true;
            }
            Intent intent = new Intent(mainActivity, OrdersHistoryActivity.class);
            mainActivity.startActivity(intent);
            mainActivity.finish();
        }
         else if (R.id.bottomNavigationStoreMenuId == menuItem.getItemId()) {
            if (mainActivity.getLocalClassName().equals(RegistrationNewActivity.class.getSimpleName())) {
                return true;
            }
            Data.getInstance().getSelectedMenuItems().removeAll(Data.getInstance().getSelectedMenuItems());
            Data.getInstance().refreshTotalCost(0d);
            Intent intent = new Intent(mainActivity, RegistrationNewActivity.class);
            mainActivity.startActivity(intent);
            mainActivity.finish();
        }
        return true;
    }
}
