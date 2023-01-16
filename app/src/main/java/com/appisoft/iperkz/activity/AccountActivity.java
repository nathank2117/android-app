package com.appisoft.iperkz.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.activity.ui.login.LoginViewModel;
import com.appisoft.iperkz.activity.ui.login.LoginViewModelFactory;
import com.appisoft.iperkz.adapter.BottomNavigationHandler;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.CustomerEntity;
import com.appisoft.iperkz.entity.PastOrderItem;
import com.appisoft.iperkz.entity.Reward;
import com.appisoft.iperkz.entity.Store;
import com.appisoft.iperkz.util.Util;
import com.appisoft.perkz.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView termsLink;
    private LoginViewModel loginViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavigationAccountMenuId);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationHandler(this));

        final TextView customerNameTextView = findViewById(R.id.customerNameEditText);
        final TextView emailEditText = findViewById(R.id.emailEditText);
        final TextView storeNameTextView = findViewById(R.id.storeName);
        final TextView addressTextView = findViewById(R.id.addressTextView);
        final TextView availableCredits = findViewById(R.id.availableRewards);
        final TextView availableCreditsGroceries = findViewById(R.id.availableRewardsGroceries);

        termsLink = findViewById(R.id.termsLink);
        termsLink.setClickable(true);
        termsLink.setOnClickListener(this);
        try {
            CustomerEntity customer = loginRepository.getCustomerEntity();
            Store store = customer.getStore();
            customerNameTextView.setText(customer.getFirstName() + " " + customer.getLastName());
            emailEditText.setText(customer.getEmail());
            storeNameTextView.setText(store.getStoreName());
            String address = "";
            address += store.getAddressOne();
            address += ", ";
            if (store.getAddressTwo() != null && store.getAddressTwo().trim().length() > 0) {
                address += store.getAddressTwo();
                address += ", ";
            }
            address += store.getCity();
            address += ", ";
            address += store.getState();
            address += "\n Phone #: +1 "+store.getPhone();
            addressTextView.setText(address);
            loginViewModel.retrieveAllRewards(loginRepository.getCustomerEntity(), this);
            loginViewModel.getPerkzList().observe(this, new Observer<Reward[]>() {
                @Override
                public void onChanged(@Nullable Reward[] rewards) {
                    availableCredits.setText("Available Credits (Restaurants): $ 0.00");
                    availableCreditsGroceries.setText("Available Credits (Groceries): $ 0.00");
                    if (rewards == null || rewards.length == 0 ) {
                        availableCredits.setText("Available Credits (Restaurants): $ 0.00");
                        availableCreditsGroceries.setText("Available Credits (Groceries): $ 0.00");
                    } else {
                        for (Reward reward : rewards) {
                            Double claimedReward = 0.0;
                            if (reward.getClaimedReward() != null ) {
                                claimedReward = reward.getClaimedReward();
                            }
                            if (reward.getPerkzType().equalsIgnoreCase( "APP_DOWNLOAD" )) {
                                availableCredits.setText("Available Credits (Restaurants): $ " + Util.getFormattedDollarAmt(reward.getReward() - claimedReward));
                                //   availableCredits.setText("Available Credits (Restaurants)");
                            } else if (reward.getPerkzType().equalsIgnoreCase( "FIRST_GROCERY_ORDER" )) {
                                availableCreditsGroceries.setText("Available Credits (Groceries): $ " + Util.getFormattedDollarAmt(reward.getReward() - claimedReward));
                                //   availableCreditsGroceries.setText("Available Credits (Groceries):");
                            }
                        }
                        //loginViewModel.retrieveRewards(customer, this);
                    }
                }
            });
            /*
            loginViewModel.getReward().observe(this, new Observer<Reward>() {
                @Override
                public void onChanged(Reward reward) {

                    if (reward == null) {
                        System.out.println("test");
                        return;
                    }

            });
*/

        } catch(Exception e) {

        }
            /*
             loginViewModel.getReward().observe(this, new Observer<Reward>() {
            @Override
            public void onChanged(Reward reward) {

                if (reward == null) {
                    availableCredits.setText("Available Credits: $ 0.00");
                    return;
                }

                if(reward.getReward()>0) {
                    availableCredits.setText("Available Credits: $ " + Util.getFormattedDollarAmt(reward.getReward()-reward.getClaimedReward()));
                }
                else{
                    availableCredits.setText("Available Credits: $ 0.00");
                }
            }
        });

             */
    }

    @Override
    public void onClick(View v) {

            termsLink.setClickable(false);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Terms & Conditions");
            WebView wv = new WebView(this);
            wv.loadUrl("http:\\perkz.s3.us-east-2.amazonaws.com/Terms+and+conditions+-+iPERKZ-1.0.htm");
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
                    termsLink.setClickable(true);
                    dialog.dismiss();
                }
            });
            alert.show();
        }

        public void inviteFriends(View view){
        String refCode ="Perkz 101XYZ";
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey, try out this app. You can order food for Takeout or Dine-in at your favorite restaurants using this App. They are offering $5 off on first order.\n https://play.google.com/store/apps/details?id=com.appisoft.perkz");
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }
}
