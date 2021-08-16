package com.appisoft.iperkz.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.appisoft.perkz.R;

public class DineInErrorDialog extends DialogFragment {
    private String errorMessage;
    private PaymentActivity paymentActivity;
    public DineInErrorDialog(String message, PaymentActivity paymentActivity) {
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
        View view = inflater.inflate(R.layout.dinein_error_dialog, null);


        Button useTakeoutBtn = (Button) view.findViewById(R.id.useTakeout);
        useTakeoutBtn.setEnabled(true);
        useTakeoutBtn.setOnClickListener(this::useTakeout);


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

    public void useTakeout(View view) {

        paymentActivity.radioButtonTakeOut.setChecked(true);
        this.dismiss();
    }

}