package com.appisoft.iperkz.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.entity.CustomerEntity;
import com.appisoft.perkz.R;

public class DineInAllowedDialog extends DialogFragment {

    CustomerEntity customerEntity;
    PaymentActivity viewCartActivity;
    LinearLayout dineInTableLO;
    EditText tableNumValue;
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());

    public DineInAllowedDialog(CustomerEntity customerEntity, PaymentActivity viewCartActivity){
        this.customerEntity = customerEntity;
        this.viewCartActivity = viewCartActivity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dinein_allowed_dialog, null);

        viewCartActivity.loadingProgressBar.setVisibility(View.GONE);

        Button allowb = (Button) view.findViewById(R.id.allowbtn);
        allowb.setOnClickListener(this::allow);

        Button can = (Button) view.findViewById(R.id.cancelbtn);
        can.setOnClickListener(this::cancel);

        builder.setView(view)

                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                      // actual dialog input

                    }
                });


        return builder.create();
    }


    public void allow(View v) {

        viewCartActivity.radioButtonTakeOut.setChecked(true);
        viewCartActivity.allow();
        this.dismiss();
    }

    public void cancel(View view) {

        viewCartActivity.radioButtonTakeOut.setChecked(true);
        this.dismiss();
    }



}
