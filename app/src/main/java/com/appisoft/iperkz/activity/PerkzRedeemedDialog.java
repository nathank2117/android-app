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

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.perkz.R;

public class PerkzRedeemedDialog extends DialogFragment {
    private String errorMessage;
    private String titleMessage;
    private String buttonText;

    private PaymentActivity paymentActivity;
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    public PerkzRedeemedDialog(String title, String message, String buttonText, PaymentActivity paymentActivity) {
        super();
        this.titleMessage = title;
        this.errorMessage = message;
        this.buttonText = buttonText;

        this.paymentActivity = paymentActivity;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.perkz_redeemed_dialog, null);

        TextView errorHeadingTxt =view.findViewById(R.id.errorHeading);
        errorHeadingTxt.setText(titleMessage);

        TextView errorMessageTxt =view.findViewById(R.id.errorMessage);
        errorMessageTxt.setText(""+errorMessage.toString());


        Button closeButton = (Button) view.findViewById(R.id.changeAddress);

        closeButton.setText(buttonText);
        closeButton.setEnabled(true);
        closeButton.setOnClickListener(this::closeDialog);


        builder.setView(view)
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


    public void closeDialog(View view) {
      this.dismiss();
    }


}