package com.appisoft.iperkz.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.appisoft.iperkz.callback.DrivingDistanceCallback;
import com.appisoft.iperkz.callback.ImageLoadCallBack;
import com.appisoft.iperkz.callback.StoreDetailsRequestCallback;
import com.appisoft.iperkz.callback.StoreIdListServiceRequestCallback;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.DrivingPoints;
import com.appisoft.iperkz.entity.SimpleResponse;
import com.appisoft.iperkz.entity.Store;
import com.appisoft.iperkz.entity.StoreFilterCriteria;
import com.appisoft.iperkz.entity.Timing;
import com.appisoft.iperkz.entity.UserLocation;
import com.appisoft.iperkz.entity.uploader.ByteBufferUploadProvider;
import com.appisoft.perkz.DisplayMessageActivity;
import com.appisoft.perkz.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.chromium.net.CronetEngine;
import org.chromium.net.UploadDataProvider;
import org.chromium.net.UrlRequest;

import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.android.volley.VolleyLog.TAG;

public class StoreDetailsDialog extends DialogFragment {
    private RegistrationNewActivity registrationNewActivity;
    Store store;
    TableLayout tl;
    private TextView closingMsg;
    private Button gotoMenu;
    public StoreDetailsDialog(RegistrationNewActivity registrationNewActivity, Store store) {
        super();
        this.registrationNewActivity = registrationNewActivity;
        this.store = store;

    }
    @SuppressLint("ResourceAsColor")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.store_details_dialog, null);

        CronetEngine cronetEngine = Cronet.getCronetEngine(this.getContext());
        ImageView imgview = (ImageView) view.findViewById(R.id.imageView);
        UrlRequest.Callback callback = new ImageLoadCallBack(imgview, getContext());
        Executor executor = Executors.newSingleThreadExecutor();
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                store.getLogo(), callback, executor);

        UrlRequest request = requestBuilder.build();
        request.start();

             gotoMenu = (Button) view.findViewById(R.id.gotoMenu);

            gotoMenu.setOnClickListener(this::gotoMenu);

         tl = (TableLayout) view.findViewById(R.id.hours);

        TextView storeName = (TextView) view.findViewById(R.id.storeName);
        storeName.setText(store.getStoreName());

        TextView storeDesc = (TextView) view.findViewById(R.id.storeDesc);
        storeDesc.setText(store.getStoreAttributes().getDescription());

        TextView foodType = (TextView) view.findViewById(R.id.foodType);
        foodType.setText(store.getStoreAttributes().getStoreFoodType());

        TextView mealType = (TextView) view.findViewById(R.id.mealType);
        mealType.setText(store.getStoreAttributes().getStoreMealTypes());

        TextView distance = (TextView) view.findViewById(R.id.distance);


        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating);
        //ratingBar.setNumStars(store.getStoreAttributes().getRatings().intValue());
        ratingBar.setRating(store.getStoreAttributes().getRatings().floatValue());

        LayerDrawable layerDrawable2 = (LayerDrawable) ratingBar.getProgressDrawable();

        // No star
        DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable2.getDrawable(0)),
                ContextCompat.getColor(this.getContext(),
                        android.R.color.background_dark));

        // Partial star
        DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable2.getDrawable(1)),
                ContextCompat.getColor(this.getContext(),
                        // use background_dark instead of colorAccent
                        // R.color.colorAccent));
                        android.R.color.holo_blue_dark));

        // Custom star
        DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable2.getDrawable(2)),
                ContextCompat.getColor(this.getContext(),
                        R.color.places_autocomplete_progress_tint));


        TextView ratings = (TextView) view.findViewById(R.id.ratings);
        ratings.setText(store.getStoreAttributes().getRatings().toString());

        //
        TextView reviews = (TextView) view.findViewById(R.id.reviews);
        reviews.setText(" ("+store.getStoreAttributes().getReviews()+" reviews)");

        TextView addressTxt = (TextView) view.findViewById(R.id.address);
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
        addressTxt.setText(address);
        LinearLayout addressLT = (LinearLayout) view.findViewById(R.id.addressLT);
        addressLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView addressTxt = (TextView) view.findViewById(R.id.address);
                startActivity(viewOnMap(addressTxt.getText().toString()));
            }
        });

        ImageView locImg = (ImageView) view.findViewById(R.id.locImg);
        locImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView addressTxt = (TextView) view.findViewById(R.id.address);
                startActivity(viewOnMap(addressTxt.getText().toString()));
            }
        });


        TextView website = (TextView) view.findViewById(R.id.website);
        website.setText(store.getWebsite());
        LinearLayout websiteLT = (LinearLayout) view.findViewById(R.id.websiteLT);
        websiteLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(store.getWebsite().contains("http") ? store.getWebsite() : "http://"+store.getWebsite());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getContext().startActivity(intent);
            }
        });

        ImageView webImg = (ImageView) view.findViewById(R.id.webImg);
        webImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(store.getWebsite().contains("http") ? store.getWebsite() : "http://"+store.getWebsite());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getContext().startActivity(intent);
            }
        });

        TextView phone = (TextView) view.findViewById(R.id.phone);
        phone.setText(store.getPhone());

        ImageView phImg = (ImageView) view.findViewById(R.id.phImg);
        phImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone.performClick();
            }
        });


             closingMsg = (TextView) view.findViewById(R.id.closingMsg);

        TextView showToday = (TextView) view.findViewById(R.id.showToday);

        populateToday();

        if((store.getStoreTypeId()==4) && (store.getStoreStatus().equalsIgnoreCase("OPEN"))){
            closingMsg.setText("");
        }
        showToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showToday.getText().equals("Show All")) {
                    showToday.setText("Show Today");
                    populateTable();
                }
                else{
                    showToday.setText("Show All");
                    populateToday();
                }
            }
        });



        ImageView cancel = (ImageView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this::cancel);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // actual dialog input
                    }
                });

        getDrivingDistance(store, registrationNewActivity.userLocation);
        registrationNewActivity.getLoginViewModel().getDrivingDistanceResult( ).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String result) {
                distance.setText("Distance: "+result+" miles");
            }
        });

        return builder.create();
    }

    public void gotoMenu(View view) {
        callGetStoreDetailsService(this.store.getStoreCode());
        this.dismiss();
    }

    public void cancel(View view) {
        this.dismiss();
    }

    private void populateTable(){
        if(store.getStoreAttributes().getTimings().size()>0) {


           tl.removeAllViewsInLayout();
            for (Timing timing : store.getStoreAttributes().getTimings()) {

                    TableRow tr = new TableRow(getContext());
                    tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    /* Create a Button to be the row-content. */
                    TextView a = new TextView(getContext());
                    if (timing.getIsToday() == 1) {
                        a.setTypeface(null, Typeface.BOLD);
                        if (timing.getIsOpen() == 1) {
                            a.setText("Today - Open");
                            gotoMenu.setVisibility(View.VISIBLE);
                            closingMsg.setText(store.getStoreAttributes().getClosingMessage());
                            if(!store.isLoggedIn()) {
                                //closingMsg.setText("Currently Closed");
                                gotoMenu.setVisibility(View.GONE);
                            }

                        } else {
                            a.setText("Today - Closed");
                            closingMsg.setText("Closed");
                            gotoMenu.setVisibility(View.GONE);
                        }

                    } else {
                        a.setText(timing.getDay());
                    }
                    a.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    a.setMaxWidth(250);

                    TextView b = new TextView(getContext());
                    b.setText(timing.getStartTime() + " - " + timing.getEndTime());
                    if (timing.getIsToday() == 1) {
                        b.setTypeface(null, Typeface.BOLD);
                    }
                    b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    b.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                    tr.addView(a);
                    tr.addView(b);

                    tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }

        }

        if((store.getStoreTypeId()==4) && (store.getStoreStatus().equalsIgnoreCase("OPEN"))){
            closingMsg.setText("");
        }
    }

    private void populateToday(){

        if(store.getStoreAttributes().getTimings().size()>0) {

            tl.removeAllViewsInLayout();
            for (Timing timing : store.getStoreAttributes().getTimings()) {
                if (timing.getIsToday() == 1) {
                    TableRow tr = new TableRow(getContext());
                    tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    TextView a = new TextView(getContext());
                    if (timing.getIsToday() == 1) {
                        a.setTypeface(null, Typeface.BOLD);
                        if (timing.getIsOpen() == 1) {
                            a.setText("Today - Open");
                            gotoMenu.setVisibility(View.VISIBLE);
                            closingMsg.setText(store.getStoreAttributes().getClosingMessage());
                            if(!store.isLoggedIn()) {
                                //closingMsg.setText("Currently Closed");
                                gotoMenu.setVisibility(View.GONE);
                            }
                        } else {
                            a.setText("Today - Closed");
                            closingMsg.setText("Closed");
                            gotoMenu.setVisibility(View.GONE);
                        }


                    } else {
                        a.setText(timing.getDay());
                    }
                    a.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));


                    TextView b = new TextView(getContext());
                    b.setText(timing.getStartTime() + " - " + timing.getEndTime());
                    if (timing.getIsToday() == 1) {
                        b.setTypeface(null, Typeface.BOLD);
                    }
                    b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    b.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                    tr.addView(a);
                    tr.addView(b);

                    tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        }

        if((store.getStoreTypeId()==4) && (store.getStoreStatus().equalsIgnoreCase("OPEN"))){
            closingMsg.setText("");
        }
    }

    public static Intent viewOnMap(String address) {
        return new Intent(Intent.ACTION_VIEW,
                Uri.parse(String.format("geo:0,0?q=%s",
                        URLEncoder.encode(address))));
    }

    private void callGetStoreDetailsService(String storeCode) {
        try {
            CronetEngine cronetEngine = Cronet.getCronetEngine(getContext());
            StoreDetailsRequestCallback storeDetailsRequestCallback = new StoreDetailsRequestCallback(getContext());

            Executor executor = Executors.newSingleThreadExecutor();
            String url = Data.SERVER_URL + "/api/store/" + storeCode;
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    url, storeDetailsRequestCallback, executor);
            UrlRequest request = requestBuilder.build();
            request.start();

        } catch (Exception e) {

        }
    }

    private void getDrivingDistance(Store store, UserLocation userLocation) {
        registrationNewActivity.getLoginViewModel().getDrivingDistanceResult(store, userLocation, getContext());
    }

}