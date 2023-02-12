package com.appisoft.iperkz.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.activity.ui.login.LoginViewModel;
import com.appisoft.iperkz.activity.ui.login.LoginViewModelFactory;
import com.appisoft.iperkz.adapter.BottomNavigationHandler;
import com.appisoft.iperkz.adapter.PastOrderListAdapter;
import com.appisoft.iperkz.entity.PastOrderItem;
import com.appisoft.perkz.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class OrdersHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private LoginViewModel loginViewModel;

    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    TextView noRecordsMessgTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_history);

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        recyclerView
                = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        noRecordsMessgTextView = findViewById(R.id.noRecordsMessg);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavigationOrderMenuId);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationHandler(this));
        loadingProgressBar.setVisibility(View.VISIBLE);
        loginViewModel.requestPastRecords(loginRepository.getCustomerEntity().getCustomerId(),this);
        loginViewModel.getPastOrdersResult().observe(this, new Observer<PastOrderItem[]>() {
            @Override
            public void onChanged(@Nullable PastOrderItem[] pastOrders) {
                loadingProgressBar.setVisibility(View.GONE);
                if (pastOrders == null) {
                    showFailure();
                } else {
                    updateUiWithPastOrder(pastOrders);
                }
                setResult(Activity.RESULT_OK);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginViewModel.requestPastRecords(loginRepository.getCustomerEntity().getCustomerId(),this);
    }


    private void showFailure() {

    }

    private void updateUiWithPastOrder( PastOrderItem[] args) {
        noRecordsMessgTextView.setVisibility(View.GONE);
        if ( args.length == 0 ) {
            noRecordsMessgTextView.setVisibility(View.VISIBLE);
        }
        mAdapter = new PastOrderListAdapter(args);
        recyclerView.setAdapter(mAdapter);
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(RecyclerView.Adapter mAdapter) {
        this.mAdapter = mAdapter;
    }


}
