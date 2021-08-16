package com.appisoft.iperkz.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.util.Util;
import com.appisoft.perkz.R;

import java.text.NumberFormat;
import java.util.List;

public class DirectTipDialog extends DialogFragment {
    private ViewCartActivity viewCartActivity;
    private EditText otherAmtTxt;
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());

    private TextView tip1Amt;
    private TextView tip2Amt;
    private TextView tip3Amt;
    private LinearLayout noTip;
    private LinearLayout Tip1Lt;
    private LinearLayout Tip2Lt;
    private LinearLayout Tip3Lt;
    private Double customerTip = 0.0;

    Button payButton;
    private Boolean tipFlag = true;

    public DirectTipDialog(ViewCartActivity viewCartActivity) {
        super();
        this.viewCartActivity = viewCartActivity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.direct_tips_dialog, null);


        noTip = (LinearLayout) view.findViewById(R.id.noTipLT);
        Tip1Lt = (LinearLayout) view.findViewById(R.id.Tip1Lt);
        Tip2Lt = (LinearLayout) view.findViewById(R.id.Tip2Lt);
        Tip3Lt = (LinearLayout) view.findViewById(R.id.Tip3Lt);
        final TextView tip1 = (TextView) view.findViewById(R.id.Tip1);
        final TextView tip2 = (TextView) view.findViewById(R.id.Tip2);
        final TextView tip3 = (TextView) view.findViewById(R.id.Tip3);
        tip1Amt = (TextView) view.findViewById(R.id.Tip1Amt);
        tip2Amt = (TextView) view.findViewById(R.id.Tip2Amt);
        tip3Amt = (TextView) view.findViewById(R.id.Tip3Amt);


        Tip1Lt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlight("Tip1Lt");
                updateTotalWithTip(((String) tip1Amt.getText()).substring(1));
            }
        });

        Tip2Lt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlight("Tip2Lt");
                updateTotalWithTip(((String) tip2Amt.getText()).substring(1));
            }
        });

        Tip3Lt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlight("Tip3Lt");
                updateTotalWithTip(((String) tip3Amt.getText()).substring(1));
            }
        });

        noTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlight("noTip");
                updateTotalWithTip("0.0");
            }
        });


        if (loginRepository.getCustomerEntity().getStore().getStoreAttributes().getShowTips() == 1) {


            if (loginRepository.getCustomerEntity().getStore().getStoreAttributes().getTips() != null) {

                List<Double> tips = loginRepository.getCustomerEntity().getStore().getStoreAttributes().getTips();
                tip1.setText(Util.convertDouble2String(tips.get(0)) + "%");
                tip2.setText(Util.convertDouble2String(tips.get(1)) + "%");
                tip3.setText(Util.convertDouble2String(tips.get(2)) + "%");
            }
        }

        ImageView cancel = (ImageView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this::cancel);

        payButton = (Button) view.findViewById(R.id.payTipTotal);
        payButton.setOnClickListener(this::confirm);

        otherAmtTxt = (EditText) view.findViewById(R.id.otherTipAmt);

        otherAmtTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                updateTotalWithTip("0.0");
            }

            private String current = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    otherAmtTxt.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[$,.]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));

                    current = formatted;
                    otherAmtTxt.setText(formatted);
                    updateTotalWithTip(formatted.substring(1));
                    otherAmtTxt.setSelection(formatted.length());
                    otherAmtTxt.addTextChangedListener(this);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //updateTotalWithTip(((String) s.toString()));
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
        showCardOptions();
        return builder.create();

    }

    public void confirm(View view) {

//        viewCartActivity.processOrder(view);
        this.dismiss();
    }

    private void prepareTips(Double grandTotal) {
        if (tipFlag) {
            if (loginRepository.getCustomerEntity().getStore().getStoreAttributes().getTips() != null) {

                List<Double> tips = loginRepository.getCustomerEntity().getStore().getStoreAttributes().getTips();
                tip1Amt.setText("$" + Util.getFormattedDollarAmt(tips.get(0) * grandTotal / 100));
                tip2Amt.setText("$" + Util.getFormattedDollarAmt(tips.get(1) * grandTotal / 100));
                tip3Amt.setText("$" + Util.getFormattedDollarAmt(tips.get(2) * grandTotal / 100));
            }
            tipFlag = false;
        }
    }

    public void updateTotalWithTip(String tipAmount) {
        customerTip = Double.valueOf(tipAmount);
        loginRepository.getCustomerEntity().setTipAmount(customerTip);
        showCardOptions();

    }

    public void showCardOptions() {

        payButton.setVisibility(View.VISIBLE);

        Double grandTotal = Data.getInstance().getGrandTotalAsDouble() ;

        Double transactionFee = ((grandTotal * loginRepository.getCustomerEntity().getStore().getChargeRate()) / 100)
                + loginRepository.getCustomerEntity().getStore().getTransactionFee();
        grandTotal = grandTotal + transactionFee;

        if (loginRepository.getCustomerEntity().getPerkzRewards().size() > 0 && loginRepository.getCustomerEntity().getPerkzRewards().get(0).getClaimedReward() != null) {
            grandTotal = grandTotal - (loginRepository.getCustomerEntity().getPerkzRewards().get(0).getClaimedReward() - loginRepository.getCustomerEntity().getPerkzRewards().get(0).getPreviousClaim());
        }

        if (loginRepository.getCustomerEntity().getStore().getChargeMode().equalsIgnoreCase("CUSTOMER")) {
            //
            prepareTips(grandTotal);
            //
            grandTotal= grandTotal+ customerTip;
            payButton.setText("Place Order : $ " + Util.getFormattedDollarAmt(grandTotal));
        } else {
            Double grandTotall = Data.getInstance().getGrandTotalAsDouble() + customerTip;
            if (loginRepository.getCustomerEntity().getPerkzRewards().size() > 0 && loginRepository.getCustomerEntity().getPerkzRewards().get(0).getClaimedReward() != null) {
                grandTotall = grandTotall - (loginRepository.getCustomerEntity().getPerkzRewards().get(0).getClaimedReward() - loginRepository.getCustomerEntity().getPerkzRewards().get(0).getPreviousClaim());

                prepareTips(grandTotall);
                payButton.setText("Place Order : $ " + Util.getFormattedDollarAmt(grandTotall));
            } else {
                Double gtotal = Double.valueOf(Data.getInstance().getGrandTotal().substring(1));
                prepareTips(gtotal);
                gtotal = gtotal + customerTip;
                payButton.setText("Place Order : $ " + Util.getFormattedDollarAmt(gtotal));
            }
        }
        if (grandTotal < .50) {
            if (loginRepository.getCustomerEntity().getStore().getChargeMode().equalsIgnoreCase("CUSTOMER")) {
                if ((grandTotal - transactionFee) > 0) {
                    payButton.setText("Place Order : $ 0.00");
                }
            }
            payButton.setEnabled(false);
            payButton.setBackgroundColor(Color.LTGRAY);
        }
    }

    public void cancel(View view) {

        viewCartActivity.loadingProgressBar.setVisibility(View.GONE);
        this.dismiss();

    }

    private void highlight(String tiplt) {

        switch (tiplt) {
            case "noTip":
                Tip1Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip1Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip2Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip2Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip3Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip3Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                noTip.setBackgroundResource(R.drawable.tip_box_border_highlight);
                noTip.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                break;
            case "Tip1Lt":
                Tip1Lt.setBackgroundResource(R.drawable.tip_box_border_highlight);
                Tip1Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip2Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip2Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip3Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip3Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                noTip.setBackgroundResource(R.drawable.tip_box_border);
                noTip.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                break;
            case "Tip2Lt":
                Tip1Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip1Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip2Lt.setBackgroundResource(R.drawable.tip_box_border_highlight);
                Tip2Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip3Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip3Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                noTip.setBackgroundResource(R.drawable.tip_box_border);
                noTip.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                break;
            case "Tip3Lt":
                Tip1Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip1Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip2Lt.setBackgroundResource(R.drawable.tip_box_border);
                Tip2Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                Tip3Lt.setBackgroundResource(R.drawable.tip_box_border_highlight);
                Tip3Lt.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                noTip.setBackgroundResource(R.drawable.tip_box_border);
                noTip.setPadding(dptopx(10), dptopx(10), dptopx(10), dptopx(10));
                break;


        }

    }

    private int dptopx(int padding_in_dp) {
    final float scale = getResources().getDisplayMetrics().density;
    int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
    return  padding_in_px;
}

}