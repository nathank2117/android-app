package com.appisoft.iperkz.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.appisoft.iperkz.entity.CustomerEntity;
import com.appisoft.iperkz.entity.FoodItem;
import com.appisoft.perkz.R;

import java.text.NumberFormat;

public class TipDialog extends DialogFragment {
    private PaymentActivity paymentActivity;
    private EditText otherAmtTxt;
    public TipDialog(PaymentActivity paymentActivity) {
        super();
        this.paymentActivity = paymentActivity;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.tips_dialog, null);

        //Button cancel = (Button) view.findViewById(R.id.cancel);
        //cancel.setOnClickListener(this::cancel);

        Button confirm = (Button) view.findViewById(R.id.confirm);
        confirm.setOnClickListener(this::confirm);

         otherAmtTxt =(EditText) view.findViewById(R.id.otherTipAmt);

         otherAmtTxt.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             private String current = "";
             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                 if(!s.toString().equals(current)){
                     otherAmtTxt.removeTextChangedListener(this);

                     String cleanString = s.toString().replaceAll("[$,.]", "");

                     double parsed = Double.parseDouble(cleanString);
                     String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                     current = formatted;
                     otherAmtTxt.setText(formatted);
                     otherAmtTxt.setSelection(formatted.length());
                     otherAmtTxt.addTextChangedListener(this);
                 }
             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         });



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

        String otherAmt = (String) otherAmtTxt.getText().toString().substring(1);
        paymentActivity.updateTotalWithTip(otherAmt);
        this.dismiss();
    }

    public void cancel(View view) {

        this.dismiss();
    }


}