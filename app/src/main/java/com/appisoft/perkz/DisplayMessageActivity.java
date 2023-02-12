package com.appisoft.perkz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import android.app.ActionBar;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.appisoft.iperkz.activity.MenuDetailsActivity;
import com.appisoft.iperkz.activity.StoreDetailsDialogatMenu;
import com.appisoft.iperkz.activity.ViewCartActivity;
import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.adapter.BottomNavigationHandler;
import com.appisoft.iperkz.callback.ImageButtonLoadCallBack;
import com.appisoft.iperkz.callback.ImageLoadCallBack;
import com.appisoft.iperkz.entity.ShoppingCart;
import com.appisoft.iperkz.firebase.FirebaseService;
import com.appisoft.perkz.binding.MainActivityContract;
import com.appisoft.perkz.binding.MainActivityPresenter;
import com.appisoft.iperkz.engine.Cronet;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.appisoft.iperkz.data.Data;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.stripe.android.PaymentConfiguration;

public class DisplayMessageActivity extends AppCompatActivity {
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    private Menu mOptionsMenu;
    public static String MEAL_TYPE = Data.BREAKFAST;
    private String mealtypeId_bf ="";
    private String mealtypeId_lun ="";
    private String mealtypeId_all ="";
    private String wallpaper;
    private ProgressBar loadingProgressBar2;
    private ProgressBar loadingProgressBar1;
    private ProgressBar loadingProgressBar3;
    SharedPreferences sharedpreferences;
    public static final String LOCATIONPREFERENCES = "LocPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        loadingProgressBar1 = findViewById(R.id.loading1);
        loadingProgressBar2 = findViewById(R.id.loading2);
        loadingProgressBar3 = findViewById(R.id.loading3);

        createNotificationChannel();
      //  initializeStripePayment();
      //  ActionBar actionBar = getActionBar();
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationHandler(this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView storeTitle = (TextView) findViewById(R.id.toolbar_title);
        try {
            String title = loginRepository.getCustomerEntity().getStore().getStoreName() + "  ";
            storeTitle.setText(title);
        } catch (Exception e){

        }

         if(!Data.getInstance().getTotalCost().equals("$ 0.00")) {
        LinearLayout linearLayout = findViewById(R.id.vcartfragment);
        linearLayout.setBackgroundColor(Color.parseColor("#3F51B5"));
        }


        if (loginRepository.getCustomerEntity().getStore().getStoreTypeId() == 4) {


            Intent myIntent = getIntent();
            wallpaper = myIntent.getStringExtra("wallPaper");

            Bitmap myImage = getBitmapFromURL(wallpaper);
            Drawable dr = new BitmapDrawable(myImage);
            ConstraintLayout linearLayout = (ConstraintLayout) findViewById(R.id.displaymessagelt);
            linearLayout.setBackground(dr);

        }

       ImageView infoView = (ImageView)findViewById(R.id.infoView);
        DisplayMessageActivity menuDetailsActivity = this;
        infoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new StoreDetailsDialogatMenu(loginRepository.getCustomerEntity().getStore());
                newFragment.setCancelable(false);
                newFragment.show((menuDetailsActivity).getSupportFragmentManager(), "missiles");
            }
        });
  /*
        final androidx.appcompat.app.ActionBar supportActionBar = getSupportActionBar();
        try {
            String title = Data.PROJECT_NAME + " - " +
                    loginRepository.getCustomerEntity().getStore().getStoreName();
            supportActionBar.setTitle(title);
        } catch (Exception e){

        }*/

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        String topic = Data.ENVIRONMENT
                        + FirebaseService.TOPIC_ALL
                        + loginRepository.getCustomerEntity().getStoreId();
        FirebaseMessaging.getInstance().subscribeToTopic(topic);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);


        //mealtypes

        mealtypeId_bf = loginRepository.getCustomerEntity().getStore().getMealTypes().get(0).getMealTypeId().toString();
        mealtypeId_lun = loginRepository.getCustomerEntity().getStore().getMealTypes().get(1).getMealTypeId().toString();
        mealtypeId_all = loginRepository.getCustomerEntity().getStore().getMealTypes().get(2).getMealTypeId().toString();

        if (loginRepository.getCustomerEntity().getStore().getStoreTypeId() == 4) {
            TextView tv1 = findViewById(R.id.textView1);
            tv1.setVisibility(View.VISIBLE);
            tv1.setText(""+loginRepository.getCustomerEntity().getStore().getMealTypes().get(0).getMealTypeName());
            TextView tv2 = findViewById(R.id.textView2);
            tv2.setVisibility(View.VISIBLE);
            tv2.setText(""+loginRepository.getCustomerEntity().getStore().getMealTypes().get(1).getMealTypeName());
            TextView tv3 = findViewById(R.id.textView3);
            tv3.setVisibility(View.VISIBLE);
            tv3.setText(""+loginRepository.getCustomerEntity().getStore().getMealTypes().get(2).getMealTypeName());
        }

        // Capture the layout's TextView and set the string as its text
        ViewGroup view = findViewById(R.id.testId);
        // CronetProviderInstaller.installProvider(this);
        // CronetEngine.Builder myBuilder = new CronetEngine.Builder(this);
        CronetEngine cronetEngine = Cronet.getCronetEngine(this);

        UrlRequest.Callback callback = new MyUrlRequestCallback(view, this);
        Executor executor = Executors.newSingleThreadExecutor();
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                Data.SERVER_URL + "/api/dailyspecial/" + loginRepository.getCustomerEntity().getStoreId() , callback, executor);

        UrlRequest request = requestBuilder.build();
        request.start();

        //Dynamic images bf,lunch & allday
        ImageButton imageBtnBF = (ImageButton) findViewById(R.id.imageButtonBreakfast);
        UrlRequest.Callback callbackBFImg = new ImageButtonLoadCallBack(imageBtnBF, this);
        Executor executorBF = Executors.newSingleThreadExecutor();
        UrlRequest.Builder requestBuilderBF = cronetEngine.newUrlRequestBuilder(
                loginRepository.getCustomerEntity().getStore().getMealTypes().get(0).getMealTypeImage(), callbackBFImg, executorBF);
        UrlRequest requestBF = requestBuilderBF.build();
        requestBF.start();

        ImageButton imageBtnLunch = (ImageButton) findViewById(R.id.imageButtonLunch);

        UrlRequest.Callback callbackBFLun = new ImageButtonLoadCallBack(imageBtnLunch, this);
        Executor executorLun = Executors.newSingleThreadExecutor();
        UrlRequest.Builder requestBuilderLun = cronetEngine.newUrlRequestBuilder(
                loginRepository.getCustomerEntity().getStore().getMealTypes().get(1).getMealTypeImage(), callbackBFLun, executorLun);
        UrlRequest requestLun = requestBuilderLun.build();
        requestLun.start();


        ImageButton imageBtnAllDay = (ImageButton) findViewById(R.id.imageButtonAllDay);
        UrlRequest.Callback callbackAllDayImg = new ImageButtonLoadCallBack(imageBtnAllDay, this);
        Executor executorAllDay = Executors.newSingleThreadExecutor();
        UrlRequest.Builder requestBuilderAllDay = cronetEngine.newUrlRequestBuilder(
                loginRepository.getCustomerEntity().getStore().getMealTypes().get(2).getMealTypeImage(), callbackAllDayImg, executorAllDay);
        UrlRequest requestAllDay = requestBuilderAllDay.build();
        requestAllDay.start();

        if (loginRepository.getCustomerEntity().getStore().getStoreTypeId() != 4) {
            Intent intent2 = new Intent(this, MenuDetailsActivity.class);

            intent2.putExtra(MEAL_TYPE, mealtypeId_bf);
            startActivity(intent);

        }
        sharedpreferences = getSharedPreferences(LOCATIONPREFERENCES, Context.MODE_PRIVATE);
        //getShoppingCart();
        Data.getInstance().recalculateTotalCostWithoutSaving();
        LinearLayout linearLayout = findViewById(R.id.vcartfragment);
        if(!Data.getInstance().getTotalCost().equals("$ 0.00")) {
            linearLayout.setBackgroundColor(Color.parseColor("#3F51B5"));
        }
        else{
            linearLayout.setBackgroundColor(Color.GRAY);
        }

       // Data.getInstance().recalculateTotalCostWithoutSaving();

    }

    public void getShoppingCart() {
        String shoppingCartString = sharedpreferences.getString("SHOPPING_CART", null);
        Data data = Data.getInstance(this.getApplicationContext());
        ObjectMapper mapper = new ObjectMapper();
        ShoppingCart shoppingCart = null;

        try {
            if (shoppingCartString != null) {
                System.out.println("VVVV:****: loading Shopping cart JSON : " + shoppingCartString );
                shoppingCart = mapper.readValue(shoppingCartString, ShoppingCart.class);
                if (shoppingCart != null ) {
                    System.out.println("VVVV:****: Shopping cart is not null");
                    data.setSelectedMenuItems(shoppingCart.getCartItems());
                    System.out.println("VVVV:**** Loaded records: " + shoppingCart.getCartItems().size());

                } else {
                    System.out.println("VVVV:****Shopping cart IS null");
                }

            } else {
                System.out.println("VVVV: JSON IS null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    @Override
    public void onStart() {
        super.onStart();
        System.out.println("XYZ::: 1" );
        Data.getInstance().recalculateTotalCostWithoutSaving();
        LinearLayout linearLayout = findViewById(R.id.vcartfragment);
        System.out.println("XYZ::: 2 :: " + Data.getInstance().getTotalCost());
        if(!Data.getInstance().getTotalCost().equals("$ 0.00")) {
            linearLayout.setBackgroundColor(Color.parseColor("#3F51B5"));
        }
        else{
            linearLayout.setBackgroundColor(Color.GRAY);
        }

    }
    */



    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Other channel"; // getString(R.string.channel_name);
            String description = "My Description "; //getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("TEST", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager =
                        (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                SearchView searchView =
                        (SearchView) mOptionsMenu.findItem(R.id.search).getActionView();
                searchView.setSearchableInfo(
                        searchManager.getSearchableInfo(getComponentName()));
                searchView.setIconifiedByDefault(false);
                searchView.setSubmitButtonEnabled(true);
        // Associate searchable configuration with the SearchView
        return true;
    }*/


    public void displayContent(View view) {
        /*
       View viewX =  LayoutInflater.from(this).inflate(R.layout.menu_view, (ViewGroup)findViewById(R.id.bottomCard), true);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);

        addContentView(viewX, llp);
        */
    }

    public void showCartDetails(View view) {
        if(!Data.getInstance().getTotalCost().equals("$ 0.00")) {
            LinearLayout linearLayout = findViewById(R.id.vcartfragment);
            linearLayout.setBackgroundColor(Color.parseColor("#3F51B5"));
            Intent intent = new Intent(this, ViewCartActivity.class);
            startActivity(intent);
        }else {
            return;
        }
    }

    public void showBreakfastDetails(View view) {
        loadingProgressBar1.setVisibility(View.VISIBLE);
       Intent intent = new Intent(this, MenuDetailsActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        // String message = editText.getText().toString();
        intent.putExtra(MEAL_TYPE, mealtypeId_bf);
        intent.putExtra("wallPaper", wallpaper);
        startActivity(intent);
    }
    public void showLunchDetails(View view) {
        loadingProgressBar2.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, MenuDetailsActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        // String message = editText.getText().toString();
        intent.putExtra(MEAL_TYPE, mealtypeId_lun);
        intent.putExtra("wallPaper", wallpaper);
        startActivity(intent);
    }
    public void showAllDayDetails(View view) {
        loadingProgressBar3.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, MenuDetailsActivity.class);
        intent.putExtra(MEAL_TYPE, mealtypeId_all);
        intent.putExtra("wallPaper", wallpaper);
        startActivity(intent);
    }

    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}
