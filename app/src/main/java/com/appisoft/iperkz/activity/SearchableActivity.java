package com.appisoft.iperkz.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.SearchView;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.callback.MenuListServiceRequestCallback;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.MenuFilterCriteria;
//import com.appisoft.iperkz.fragement.InProgressDialog;
import com.appisoft.perkz.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SearchableActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
   // InProgressDialog builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);

       // builder = new InProgressDialog();
        //builder.setCancelable(false);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
            getSupportActionBar().setTitle("Search results for '" + query + "'");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();


        searchView.setSearchableInfo(
               searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);

         */
        // Associate searchable configuration with the SearchView
        return true;
    }

    public void showCartDetails(View view) {
        Intent intent = new Intent(this, ViewCartActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        // String message = editText.getText().toString();
        // intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private void doMySearch(String query){
         //call the service to get the Data
        CronetEngine cronetEngine = Cronet.getCronetEngine(this);

        //builder.show(getSupportFragmentManager(), "test");

        MenuListServiceRequestCallback callbackOject = new MenuListServiceRequestCallback(recyclerView, this);
        MenuFilterCriteria criteria = new MenuFilterCriteria();
        criteria.setMenuName(query);
        callbackOject.setFilterCriteria(criteria);

        UrlRequest.Callback callback = callbackOject;

        Executor executor = Executors.newSingleThreadExecutor();
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                Data.SERVER_URL + "/api/menu/" + loginRepository.getCustomerEntity().getStoreId(), callback, executor);

        UrlRequest request = requestBuilder.build();
        request.start();
    }


}
