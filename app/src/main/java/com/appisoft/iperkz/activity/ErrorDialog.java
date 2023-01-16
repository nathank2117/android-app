package com.appisoft.iperkz.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
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

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ErrorDialog extends DialogFragment {
    private String errorMessage;
    private String titleMessage;
    private String buttonText;

    private PaymentActivity paymentActivity;
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    public ErrorDialog(String message, PaymentActivity paymentActivity) {
        super();
        this.errorMessage = message;
        this.paymentActivity = paymentActivity;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.error_dialog, null);

        TextView errorMessageTxt =view.findViewById(R.id.errorMessage);
        errorMessageTxt.setText(""+errorMessage.toString());

     Button changeAddressBtn = (Button) view.findViewById(R.id.changeAddress);
        changeAddressBtn.setEnabled(true);
        changeAddressBtn.setOnClickListener(this::changeAddress);

        Button useTakeoutBtn = (Button) view.findViewById(R.id.useTakeout);
        useTakeoutBtn.setEnabled(true);
        useTakeoutBtn.setOnClickListener(this::useTakeout);

        if (loginRepository.getCustomerEntity().getStore().getStoreTypeId() == 4) {
            useTakeoutBtn.setVisibility(View.GONE);
        }


        // deliveryAddressName =(EditText) view.findViewById(R.id.deliveryAddressName);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // actual dialog input
                       // paymentActivity.radioButtonTakeOut.setChecked(true);
                        dialog.dismiss();
                    }
                });


        return builder.create();
    }


    public void changeAddress(View view) {

        paymentActivity.addressSpinner.setSelection(0);
        paymentActivity.addressSpinner.performClick();
        this.dismiss();
    }

    public void useTakeout(View view) {

        paymentActivity.radioButtonTakeOut.setChecked(true);
        this.dismiss();
    }

}