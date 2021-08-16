package com.appisoft.iperkz.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.entity.CustomerEntity;
import com.appisoft.iperkz.entity.FoodItem;
import com.appisoft.perkz.R;

public class DineInDialog extends DialogFragment {

    CustomerEntity customerEntity;
    ViewCartActivity viewCartActivity;
    LinearLayout dineInTableLO;
    EditText tableNumValue;
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());

    public DineInDialog(CustomerEntity customerEntity, ViewCartActivity viewCartActivity){
        this.customerEntity = customerEntity;
        this.viewCartActivity = viewCartActivity;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dinein_dialog, null);

         dineInTableLO = view.findViewById(R.id.dineInTableLO);
         tableNumValue = (EditText) view.findViewById(R.id.tableNum);
        Button dineIn = (Button) view.findViewById(R.id.dinein);
        dineIn.setOnClickListener(this::showDineInTable);

        Button takeOut = (Button) view.findViewById(R.id.takeout);
        takeOut.setOnClickListener(this::setTakeOut);

        Button dineInOk = (Button) view.findViewById(R.id.dineInOK);
        dineInOk.setOnClickListener(this::setDineIn);

        builder.setView(view)

                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                      // actual dialog input

                    }
                });


        return builder.create();
    }

    public void showDineInTable(View view) {
        dineInTableLO.setVisibility(View.VISIBLE);
    }

    public void setDineIn(View view) {
        customerEntity.setTakeOut(false);
        customerEntity.setBillable(false);
        customerEntity.setTableNumber((String)tableNumValue.getText().toString());
        //call tip popup
        if(loginRepository.getCustomerEntity().isAllowCardFutureUse()) {
            tipPopup(view);
        }
        else {
//            viewCartActivity.processOrder(view);
        }
        this.dismiss();
}

    public void setTakeOut(View view) {
        customerEntity.setTakeOut(true);
        if(!customerEntity.isGPSEnabled()) {
            customerEntity.setBillable(true);
            customerEntity.setTableNumber("");
        }
        //call tip popup
        if(loginRepository.getCustomerEntity().isAllowCardFutureUse()) {
            tipPopup(view);
        }
        else {
   //         viewCartActivity.processOrder(view);
        }
        this.dismiss();
    }

    private void tipPopup(View view){

        DirectTipDialog newFragment = new DirectTipDialog(viewCartActivity);
        newFragment.setCancelable(false);
        newFragment.show(viewCartActivity.getSupportFragmentManager(), "tippopup");

    }

}
