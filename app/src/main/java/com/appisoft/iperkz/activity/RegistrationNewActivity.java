package com.appisoft.iperkz.activity;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.activity.ui.login.LoginViewModel;
import com.appisoft.iperkz.activity.ui.login.LoginViewModelFactory;
import com.appisoft.iperkz.adapter.StoreListAdapter;
import com.appisoft.iperkz.callback.StoreIdListServiceRequestCallback;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.AppSettings;
import com.appisoft.iperkz.entity.CustomerEntity;
import com.appisoft.iperkz.entity.Setting;
import com.appisoft.iperkz.entity.Store;
import com.appisoft.iperkz.entity.StoreFilterCriteria;
import com.appisoft.iperkz.entity.StoreTypes;
import com.appisoft.iperkz.entity.UserLocation;
import com.appisoft.iperkz.entity.uploader.ByteBufferUploadProvider;
import com.appisoft.perkz.DisplayMessageActivity;
import com.appisoft.perkz.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.chromium.net.CronetEngine;
import org.chromium.net.UploadDataProvider;
import org.chromium.net.UrlRequest;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.GatheringByteChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegistrationNewActivity extends AppCompatActivity {// implements SearchView.OnQueryTextListener {
    private RecyclerView recyclerView;
    private ProgressBar loadingProgressBar;
    private RecyclerView.LayoutManager layoutManager;
    StoreListAdapter storeListAdapter;
    SearchView editsearch;
    Boolean alreadyExecuted = false;
    CustomerEntity customerItem = new CustomerEntity();
    Store store = new Store();
    LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    String storeTypeName = "Stores";

    public LoginViewModel getLoginViewModel() {
        return loginViewModel;
    }

    private LoginViewModel loginViewModel;

    LocationManager locationManager;
    LocationListener locationListener;
    UserLocation userLocation;
    Spinner spinner;
    public static final String LOCATIONPREFERENCES = "LocPrefs" ;
    public static final String userPreviousLocation = "nameKey";
    SharedPreferences sharedpreferences;
    private EditText input;
    String wallpaper;
    private Bitmap bitMap;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            else
                if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, locationListener);
                }
                else{

                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.welcomemessage);
                    TextView textView1 = (TextView) findViewById(R.id.text1);
                    textView1.setVisibility(View.GONE);
                    TextView textView2 = (TextView) findViewById(R.id.text2);
                    textView2.setVisibility(View.GONE);
                    TextView textView23 = (TextView) findViewById(R.id.text23);
                    textView23.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView23.setText("Phone Location services turned off! Please enable or enter address to continue!");
                    textView23.setTextColor(Color.RED);
                    linearLayout.setGravity(Gravity.CENTER);
                    linearLayout.setVisibility(View.VISIBLE);
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_new);





        /*try {
            URL url = new URL(wallpaper);
            Drawable backgroundImge = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeStream((InputStream) url.getContent()));
            ConstraintLayout linearLayout = findViewById(R.id.storelistlt);
            linearLayout.setBackground(backgroundImge);

        }
        catch(IOException io){

        }*/

        loadingProgressBar = findViewById(R.id.loading);
        loadingProgressBar.setVisibility(View.GONE);
        sharedpreferences = getSharedPreferences(LOCATIONPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        ViewGroup viewGrp = (ViewGroup) findViewById(R.id.horizontalScrollContainerView);


        String appsettingsJson = sharedpreferences.getString("APPSETTINGS", null);
        ObjectMapper mapper1 = new ObjectMapper();
        AppSettings appSettings = null;
        try {
            if (appsettingsJson != null)
                appSettings = mapper1.readValue(appsettingsJson, AppSettings.class);


        } catch (IOException e) {
            e.printStackTrace();
        }


        for(Setting setting : appSettings.getSettings()){

            if(setting.getKey().equals("main-background")){
                wallpaper = setting.getValue();
            }
        }


        Intent myIntent = getIntent();
        storeTypeName = myIntent.getStringExtra("storeTypeName");

        Log.e("wallpaper===========", "wall "+wallpaper);
        StrictMode.ThreadPolicy policy0 = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy0);
        try {
            URL url = new URL(wallpaper);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(true);
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) connection.getContent());
            Drawable backgroundImge = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.storelistlt);
            constraintLayout.setBackground(backgroundImge);


        }
        catch(Exception io){
            io.printStackTrace();
        }

        replaceStoreTypes(loginRepository.getStoreTypes(), viewGrp, storeTypeName);

        ImageButton enableLocBtn = (ImageButton) findViewById(R.id.locImg);
        enableLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                if(ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions((Activity)v.getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                else
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, locationListener);
            }
        });

        spinner = (Spinner) findViewById(R.id.distances);
        spinner.setSelection(5);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here


                if(userLocation!=null) {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    userLocation.setSpinnerPosition(position);
                    userLocation.setDistance(Integer.parseInt(((String) spinner.getSelectedItem().toString()).substring(0, 2).trim()));

                    //save preferences
                    ObjectMapper mapper = new ObjectMapper();
                    String userLocationJson = "";
                    try {
                        userLocationJson = mapper.writeValueAsString(userLocation);
                    } catch (Exception e) {

                    }
                    editor.putString("USERLOCATION", userLocationJson);
                    editor.commit();
                    //save preferences
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.welcomemessage);
                    linearLayout.setVisibility(View.GONE);
                    doMySearch("All", userLocation);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                Log.i("TAG", "YYYYYYYYYYYYYYYYYYYYYYYY: "+spinner.getSelectedItem().toString());
            }

        });

        //Google places auto complete
        String apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);


        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setHint("Enter Address");
        //autocompleteFragment.setText(Color.parseColor("#3F51B5"));

        input = (EditText)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input);
        input.setText("Enter Address");
        input.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        input.setTextSize(18f);
        input.setGravity(Gravity.RIGHT);
        //input.setTextColor(Color.parseColor("#3F51B5"));
        autocompleteFragment.getView().findViewById(R.id.places_autocomplete_clear_button).setPadding(0,0,0,55555);;
        autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button).setVisibility(View.GONE);
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {

                loadingProgressBar.setVisibility(View.VISIBLE);

                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.welcomemessage);
                linearLayout.setVisibility(View.GONE);

                Log.i("TAG", "Place: " + place.getName() + ", " + place.getLatLng() + ", " + ((String) spinner.getSelectedItem().toString()).substring(0, 2).trim());
                if (userLocation == null) {
                    userLocation = new UserLocation();
                    userLocation.setDistance(Integer.parseInt(((String) spinner.getSelectedItem().toString()).substring(0, 2).trim()));
                    userLocation.setLatitude(place.getLatLng().latitude);
                    userLocation.setLongitude(place.getLatLng().longitude);
                    userLocation.setPlace(place.getName());
                /*if(loginRepository.getCustomerEntity()!=null) {
                    loginRepository.getCustomerEntity().setUserLocation(userLocation);
                }*/

                    //save preferences
                    ObjectMapper mapper = new ObjectMapper();
                    String userLocationJson = "";
                    try {
                        userLocationJson = mapper.writeValueAsString(userLocation);
                    } catch (Exception e) {

                    }
                    editor.putString("USERLOCATION", userLocationJson);
                    editor.commit();
                    //save preferences

                    doMySearch("All", userLocation);
                }
                else{
                    userLocation.setDistance(Integer.parseInt(((String) spinner.getSelectedItem().toString()).substring(0, 2).trim()));
                    userLocation.setLatitude(place.getLatLng().latitude);
                    userLocation.setLongitude(place.getLatLng().longitude);
                    userLocation.setPlace(place.getName());
                    doMySearch("All", userLocation);
                }
            }



            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });


        locationManager = (LocationManager) this.getSystemService(getApplicationContext().LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationManager.removeUpdates(this);
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                //update current location

                //EditText input = (EditText)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input);
                input.setText("Current Location");
                //input.setGravity(Gravity.RIGHT);
                //input.setTextColor(Color.parseColor("#3F51B5"));
                //
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.welcomemessage);
                linearLayout.setVisibility(View.GONE);

                try{
                    if(userLocation==null) {
                        userLocation = new UserLocation();
                        userLocation.setLongitude(location.getLongitude());
                        userLocation.setLatitude(location.getLatitude());
                        userLocation.setDistance(Integer.parseInt(((String) spinner.getSelectedItem().toString()).substring(0, 2).trim()));

                        List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (listAddresses != null && listAddresses.size() > 0) {
                            Log.i("place Info", listAddresses.get(0).getAddressLine(0).toString());
                            userLocation.setPlace(listAddresses.get(0).getAddressLine(0).toString().substring(0, listAddresses.get(0).getAddressLine(0).toString().indexOf(',')));
                        }
                   /* if(loginRepository.getCustomerEntity()!=null) {
                        loginRepository.getCustomerEntity().setUserLocation(userLocation);
                    }*/

                        //save preferences
                        ObjectMapper mapper = new ObjectMapper();
                        String userLocationJson = "";
                        try {
                            userLocationJson = mapper.writeValueAsString(userLocation);
                        } catch (Exception e) {

                        }
                        editor.putString("USERLOCATION", userLocationJson);
                        editor.commit();
                        //save preferences
                        doMySearch("All", userLocation);
                    }
                    else{
                        userLocation.setLongitude(location.getLongitude());
                        userLocation.setLatitude(location.getLatitude());
                        userLocation.setDistance(Integer.parseInt(((String) spinner.getSelectedItem().toString()).substring(0, 2).trim()));

                        List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (listAddresses != null && listAddresses.size() > 0) {
                            Log.i("place Info", listAddresses.get(0).getAddressLine(0).toString());
                            userLocation.setPlace(listAddresses.get(0).getAddressLine(0).toString().substring(0, listAddresses.get(0).getAddressLine(0).toString().indexOf(',')));
                        }
                        doMySearch("All", userLocation);
                    }
                }
                catch(IOException e){
                    e.printStackTrace();
                }

                Log.i("PPK Location", location.toString());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);

        //grid layout
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        SharedPreferences prefs = this.getSharedPreferences("CUSTOMER_DETAILS", Context.MODE_PRIVATE);
        boolean isRegisteredUser = prefs.getBoolean("REGISTERED_USER", false );
        boolean isCustomerCreated = prefs.getBoolean("CUSTOMER_CREATED", false );
        if (isCustomerCreated == true ) {
            String customerJson = prefs.getString("CUSTOMER", null);
            ObjectMapper mapper = new ObjectMapper();
            try {
                CustomerEntity entity = mapper.readValue(customerJson, CustomerEntity.class);
              /*  if(userLocation!=null) {
                    entity.setUserLocation(userLocation);
                }*/
                loginRepository.setCustomerEntity(entity);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            if ( !(loginRepository.getCustomerEntity() != null &&
                    loginRepository.getCustomerEntity().getCustomerId() > 0 )) {
                createCustomer();
            }
        }

        loginViewModel.getCreateCustomerResult().observe(this, new Observer<CustomerEntity>() {
            @Override
            public void onChanged(@Nullable CustomerEntity customer) {
                if (customer == null) {
                    return;
                }

                if (customer.getCustomerId() <= 0) {
                    createCustomerFailed();
                }
                if (customer.getCustomerId() > 0) {
                    updateUiWithSuccess(customer);
                }

                setResult(Activity.RESULT_OK);

            }
        });
        EditText searchEditText = (EditText) findViewById(R.id.searchEditText);
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(storeListAdapter!=null)
                storeListAdapter.getFilter().filter(s.toString());
            }
        };
        searchEditText.addTextChangedListener(afterTextChangedListener);

        fetchStores();







    }
/*
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }------

    @Override
    public boolean onQueryTextChange(String query) {
        storeListAdapter.getFilter().filter(query.toString());
        return false;
    }
    */

    private void doMySearch(String query, UserLocation loc) {

        //call the service to get the Data
        CronetEngine cronetEngine = Cronet.getCronetEngine(this);
        StoreIdListServiceRequestCallback callbackOject = new StoreIdListServiceRequestCallback(recyclerView, this);
        StoreFilterCriteria criteria = new StoreFilterCriteria();
        criteria.setStoreName(query);
        callbackOject.setFilterCriteria(criteria);
        UrlRequest.Callback callback = callbackOject;

        Executor executor = Executors.newSingleThreadExecutor();
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                Data.SERVER_URL + "/api/ios/storesNearMe", callback, executor);

        requestBuilder.addHeader("Content-Type","application/json");
        requestBuilder.setHttpMethod("POST");
       try{
           ObjectMapper mapper = new ObjectMapper();
           byte[] bytes = mapper.writeValueAsBytes(loc);

           UploadDataProvider provider = ByteBufferUploadProvider.create(bytes);
           requestBuilder.setUploadDataProvider(provider, executor);
           UrlRequest request = requestBuilder.build();
           request.start();
    }catch (JsonProcessingException e) {
        e.printStackTrace();
    }
    }


    private void createCustomerFailed() {
        Toast.makeText(getApplicationContext(), "Could not create customer", Toast.LENGTH_SHORT).show();
    }

    private void updateUiWithSuccess(CustomerEntity customer) {
        customer.setStore(this.store);
        loginRepository.setCustomerEntity(customer);

        SharedPreferences.Editor editor = getSharedPreferences("CUSTOMER_DETAILS", MODE_PRIVATE).edit();
        editor.putBoolean("REGISTERED_USER", false);
        editor.putBoolean("CUSTOMER_CREATED", true);

        ObjectMapper mapper = new ObjectMapper();
        String customerJson = "";
        try {
            customerJson = mapper.writeValueAsString(customer);
        } catch (Exception e) {

        }
        editor.putString("CUSTOMER", customerJson);
        editor.commit();

        if(customer.getPerkzRewards()!=null && customer.getPerkzRewards().size()>0) {
            DialogFragment rewardFragment = new RewardFragment(customer.getPerkzRewards());
            rewardFragment.setCancelable(false);
            try {
                rewardFragment.show(this.getSupportFragmentManager(), "rewards");
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

        //fetchStores
        fetchStores();
    }

    public void showStoreDetails(Store store) {
        this.store = store;
        ProgressBar loadingStoreProgressBar = findViewById(R.id.loadingstore);
        loadingStoreProgressBar.setVisibility(View.GONE);

        //Nathan : At this stage CustomerId 'can' also be null
        //if (loginRepository.getCustomerEntity() != null &&  loginRepository.getCustomerEntity().getCustomerId() > 0 ) {
            //Customer already exists
            loginRepository.getCustomerEntity().setStoreId(store.getStoreId());
            loginRepository.getCustomerEntity().setStore(this.store);
            loginViewModel.updateCustomerStore(loginRepository.getCustomerEntity(), this);

       if (loginRepository.getCustomerEntity().getStore().getStoreTypeId() == 4) {
            Intent intent = new Intent(this, DisplayMessageActivity.class);
            intent.putExtra("wallPaper", wallpaper);
           loadingProgressBar.setVisibility(View.GONE);
            startActivity(intent);

        }
       else{
            Intent intent = new Intent(this, MenuDetailsActivity.class);
            String mealtypeId_bf = loginRepository.getCustomerEntity().getStore().getMealTypes().get(0).getMealTypeId().toString();
            intent.putExtra(Data.BREAKFAST, mealtypeId_bf);
           loadingProgressBar.setVisibility(View.GONE);
            startActivity(intent);
        }


        //}
         /*else {
            CustomerEntity customerEntity = populateCustomerDetails();
            loginViewModel.creatCustomer(customerEntity, this);
        }*/
    }


    public void setAdapter(StoreListAdapter storeListAdapter) {
        this.storeListAdapter=storeListAdapter;
        if(storeListAdapter.getItemCount()==0) {
            Log.i("No Stores found!", " " + storeListAdapter.getItemCount());
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.welcomemessage);
            TextView textView1 = (TextView) findViewById(R.id.text1);
            textView1.setVisibility(View.GONE);
            TextView textView2 = (TextView) findViewById(R.id.text2);
            textView2.setVisibility(View.GONE);
            TextView textView23 = (TextView) findViewById(R.id.text23);
            textView23.setVisibility(View.VISIBLE);
            textView23.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            if(storeTypeName==null){
                textView23.setText("No Stores found near you!");
            }
            else
                textView23.setText("No "+storeTypeName+" found near you!");

            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setVisibility(View.VISIBLE);
        }
        else
            {
            TextView textView23 = (TextView) findViewById(R.id.text23);
            textView23.setVisibility(View.GONE);
        }

       /* StrictMode.ThreadPolicy policy0 = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy0);
        try {
            URL url = new URL(wallpaper);
            Drawable backgroundImge = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeStream((InputStream) url.getContent()));
            ConstraintLayout linearLayout = findViewById(R.id.storelistlt);
            linearLayout.setBackground(backgroundImge);


        }
        catch(IOException io){

        }*/

        loadingProgressBar.setVisibility(View.GONE);
    }

    private void createCustomer(){
        CustomerEntity customerEntity = populateCustomerDetails();
        loginViewModel.creatCustomer(customerEntity, this);

    }
    private CustomerEntity populateCustomerDetails() {

        String udId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        CustomerEntity customerItem = new CustomerEntity();
        customerItem.setFirstName("Guest");
        customerItem.setLastName("LN");
        customerItem.setEmail("guest@iperkz.com");
        customerItem.setPhoneNumber("");
        customerItem.setStoreId(-1);
        customerItem.setUdidString(udId);
        customerItem.setCompany("android");
        return customerItem;
    }

    private void fetchStores(){
        loadingProgressBar.setVisibility(View.VISIBLE);
        String userlocationJson = sharedpreferences.getString("USERLOCATION", null);
        ObjectMapper mapper1 = new ObjectMapper();
        UserLocation userLocationPref =null;
        try {
            if(userlocationJson!=null)
                userLocationPref = mapper1.readValue(userlocationJson, UserLocation.class);


        } catch (IOException e) {
            e.printStackTrace();
        }

        if(loginRepository.getCustomerEntity()!=null && userLocationPref!=null) {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.welcomemessage);
            linearLayout.setVisibility(View.GONE);
            input.setText(userLocationPref.getPlace());
            userLocation = userLocationPref;
            spinner.setSelection(userLocationPref.getSpinnerPosition());
            doMySearch("All", userLocationPref);
        }

    }


    public void onClick(View v) {
        TextView view = (TextView) v;
        loadingProgressBar.setVisibility(View.VISIBLE);


        StoreTypes storeTypes = (StoreTypes) loginRepository.getStoreTypes();
        ViewGroup viewGrp = (ViewGroup) findViewById(R.id.horizontalScrollContainerView);
        String clickedStoreType = view.getText().toString();
        int storeTypeId = replaceStoreTypes(storeTypes, viewGrp,clickedStoreType);
        view.setTextColor(Color.parseColor("#3F51B5"));
        view.setTypeface(view.getTypeface(), Typeface.BOLD);

        storeTypeName=clickedStoreType;
        getStoresByType(storeTypeId);


    }

    private int replaceStoreTypes(StoreTypes storeTypes, ViewGroup viewGrp, String selectedItem) {
        viewGrp.removeAllViews();

        int storeTypeId =1;

        if (selectedItem == null) {
            /////
            String userlocationJson = sharedpreferences.getString("USERLOCATION", null);
            ObjectMapper mapper1 = new ObjectMapper();
            UserLocation userLocationPref =null;
            try {
                if(userlocationJson!=null)
                    userLocationPref = mapper1.readValue(userlocationJson, UserLocation.class);


            } catch (IOException e) {
                e.printStackTrace();
            }

            if(userLocationPref!=null) {
                userLocation = userLocationPref;
                storeTypeId = userLocation.getTypeId();
            }

            if (storeTypes.getItems().size()>0) {
                selectedItem = storeTypes.getItems().get(0).getStoreTypeName();
            }

            for (int p=0; p<storeTypes.getItems().size(); p++) {
                if(storeTypeId==storeTypes.getItems().get(p).getStoreTypeId()){
                    selectedItem = storeTypes.getItems().get(p).getStoreTypeName();
                    break;
                }
            }

        }


        for (int i=0,j=0; i<storeTypes.getItems().size(); i++) {
            ViewGroup grp = (ViewGroup) getLayoutInflater().inflate(R.layout.textview_storetype, viewGrp);
            TextView textView = (TextView) grp.getChildAt(j);
            if (selectedItem != null && selectedItem.equalsIgnoreCase(storeTypes.getItems().get(i).getStoreTypeName()) ) {
                textView.setTextColor(Color.parseColor("#3F51B5"));
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                storeTypeId=storeTypes.getItems().get(i).getStoreTypeId();
            }
            j=j+2;
            textView.setText(storeTypes.getItems().get(i).getStoreTypeName());
            if (i < storeTypes.getItems().size() -1) { //NOT last item
                getLayoutInflater().inflate(R.layout.spacer_submenu, viewGrp);
            }
        }

        return storeTypeId;
    }

    private void getStoresByType(int storeTypeId){
        loadingProgressBar.setVisibility(View.VISIBLE);
        userLocation.setTypeId(storeTypeId);
        doMySearch("All", userLocation);

    }

}
