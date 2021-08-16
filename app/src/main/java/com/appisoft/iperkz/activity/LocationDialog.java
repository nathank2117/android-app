package com.appisoft.iperkz.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.callback.StoreIdListServiceRequestCallback;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.StoreFilterCriteria;
import com.appisoft.iperkz.entity.UserLocation;
import com.appisoft.iperkz.entity.uploader.ByteBufferUploadProvider;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LocationDialog extends DialogFragment {

    IperkzHomeActivity homeActivity;
    LinearLayout dineInTableLO;
    EditText tableNumValue;
    private ProgressBar loadingProgressBar;
    LocationManager locationManager;
    LocationListener locationListener;
    UserLocation userLocation;

    public static final String LOCATIONPREFERENCES = "LocPrefs" ;
    public static final String userPreviousLocation = "nameKey";
    SharedPreferences sharedpreferences;
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    private int storeTypeId;

    public LocationDialog(IperkzHomeActivity homeActivity, int storeTypeId){
        this.storeTypeId = storeTypeId;
        this.homeActivity = homeActivity;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.location_dialog, null);

        loadingProgressBar = view.findViewById(R.id.loading);
        sharedpreferences = this.getActivity().getSharedPreferences(LOCATIONPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();



        locationManager = (LocationManager) homeActivity.getSystemService(homeActivity.getApplicationContext().LOCATION_SERVICE);


        Button enableLocBtn = (Button) view.findViewById(R.id.gpsLocBtn);
        enableLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadingProgressBar.setVisibility(View.VISIBLE);
                if(ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(homeActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                else
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 100, homeActivity.locationListener);
            dismiss();
            }
        });

        //Google places auto complete
        String apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(this.getActivity().getApplicationContext(), apiKey);
        }

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this.getContext());


        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                this.getActivity().getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment1);
        autocompleteFragment.setHint("Enter Address");
        //autocompleteFragment.setText(Color.parseColor("#3F51B5"));

        EditText input = (EditText)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input);
        input.setText("Pick a Location");
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        input.setTextSize(18f);
        input.setGravity(Gravity.CENTER);
        input.setTextColor(Color.WHITE);
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

                TextView linearLayout = (TextView) homeActivity.findViewById(R.id.text23);
                linearLayout.setVisibility(View.GONE);

                TextView locAddressTxt = homeActivity.findViewById(R.id.locAddress);
                //EditText input = (EditText)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input);
                locAddressTxt.setText(place.getName());

                userLocation = new UserLocation();
                userLocation.setDistance(25);
                userLocation.setSpinnerPosition(4);
                userLocation.setLatitude(place.getLatLng().latitude);
                userLocation.setLongitude(place.getLatLng().longitude);
                userLocation.setPlace(place.getName());
                userLocation.setTypeId(storeTypeId);


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

                    searchStores();

            }


            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        builder.setView(view)

                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                      // actual dialog input

                    }
                });


        return builder.create();


    }

    private void searchStores(){
        if(storeTypeId!=-1) {
            loadingProgressBar.setVisibility(View.GONE);
            this.dismiss();
            Intent intent = new Intent(homeActivity, RegistrationNewActivity.class);
            startActivity(intent);
        }
        else{
            this.dismiss();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AutocompleteSupportFragment f = (AutocompleteSupportFragment) getFragmentManager()
                .findFragmentById(R.id.autocomplete_fragment1);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
    }

}
