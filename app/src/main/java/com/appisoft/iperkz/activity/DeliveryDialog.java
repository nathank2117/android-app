package com.appisoft.iperkz.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;

import com.appisoft.iperkz.activity.PaymentActivity;
import com.appisoft.iperkz.callback.CreateCustomAttributeRequestCallback;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.AddressDetail;
import com.appisoft.iperkz.entity.CustomerAttributes;
import com.appisoft.iperkz.entity.StoreSummary;
import com.appisoft.iperkz.entity.UpdateCustomerAttributesRequest;
import com.appisoft.iperkz.entity.uploader.ByteBufferUploadProvider;
import com.appisoft.perkz.R;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.chromium.net.CronetEngine;
import org.chromium.net.UploadDataProvider;
import org.chromium.net.UrlRequest;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class DeliveryDialog extends DialogFragment {
    private PaymentActivity paymentActivity;
    private EditText deliveryAddressName;
    private int customerId;
    private AddressDetail addressDetail;
    private StoreSummary storeSummary;
    private List<AddressDetail> deliveryAddressDetailsList = new ArrayList<>();

    public DeliveryDialog(StoreSummary storeSummary, int customerId, AddressDetail addressDetail, List<AddressDetail> addressDetailsList, PaymentActivity paymentActivity) {
        super();
        this.paymentActivity = paymentActivity;
        this.customerId = customerId;
        this.addressDetail = addressDetail;
        this.storeSummary = storeSummary;

      for(AddressDetail addressDetail1: addressDetailsList) {
          this.deliveryAddressDetailsList.add(addressDetail1);
      }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.delivery_dialog, null);

        Button cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this::cancel);

        Button confirm = (Button) view.findViewById(R.id.confirm);
        confirm.setOnClickListener(this::confirm);

         deliveryAddressName =(EditText) view.findViewById(R.id.deliveryAddressName);





        builder.setView(view)
                // Add action buttons
                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // actual dialog input

                    }
                });

        return builder.create();
    }

    public void confirm(View view) {

        String aName = (String) deliveryAddressName.getText().toString();

        //List<AddressDetail> addressList = new ArrayList<AddressDetail>();
        addressDetail.setTag(aName);

        deliveryAddressDetailsList.add(addressDetail);
        CustomerAttributes customerAttributes = new CustomerAttributes();
        customerAttributes.setAddressDetails(deliveryAddressDetailsList);

        UpdateCustomerAttributesRequest updateCustomerAttributesRequest = new UpdateCustomerAttributesRequest();
        updateCustomerAttributesRequest.setCustomerId(customerId);
        updateCustomerAttributesRequest.setStoreSummary(storeSummary);
        updateCustomerAttributesRequest.setCustomerAttributes(customerAttributes);

        updateCustomAttributes(updateCustomerAttributesRequest, this.getContext(), new MutableLiveData<AddressDetail>());

        this.dismiss();
    }

    public void cancel(View view) {
        paymentActivity.addressSpinner.setSelection(0);
        paymentActivity.addressSpinner.performClick();
        this.dismiss();
    }

    public void updateCustomAttributes(UpdateCustomerAttributesRequest updateCustomerAttributesRequest, Context context, MutableLiveData<AddressDetail> createCustomAttributeResult ) {

        try {
            CronetEngine cronetEngine = Cronet.getCronetEngine(context);
            CreateCustomAttributeRequestCallback createCustomAttributeRequestCallback = new CreateCustomAttributeRequestCallback(context, createCustomAttributeResult);

            Executor executor = Executors.newSingleThreadExecutor();
            String url = Data.SERVER_URL + "/api/customer/attributes";
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    url, createCustomAttributeRequestCallback, executor);


            requestBuilder.addHeader("Content-Type", "application/json");
            requestBuilder.setHttpMethod("POST");

            ObjectMapper mapper = new ObjectMapper();
            byte[] bytes = mapper.writeValueAsBytes(updateCustomerAttributesRequest);

            UploadDataProvider provider = ByteBufferUploadProvider.create(bytes);
            requestBuilder.setUploadDataProvider(provider, executor);

            UrlRequest request = requestBuilder.build();
            request.start();

        } catch (Exception e) {

        }

    }
}