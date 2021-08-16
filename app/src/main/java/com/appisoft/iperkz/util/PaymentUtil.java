package com.appisoft.iperkz.util;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.PaymentDetails;
import com.appisoft.iperkz.entity.Store;

public class PaymentUtil {
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    AlertDialog.Builder builder;
    private static PaymentUtil util;

    public static synchronized PaymentUtil getInstance() {
        if (util == null) {
            util = new PaymentUtil();
        }
        return util;
    }
    public PaymentDetails generatePaymentDetails(String paymentMode, boolean allowFutureUse) {
        PaymentDetails paymentDetails = new PaymentDetails();
        Store test = loginRepository.getCustomerEntity().getStore();
        if (loginRepository.getCustomerEntity().getStore().getChargeMode().equalsIgnoreCase("CUSTOMER") ){

            Double grandTotal = Data.getInstance().getGrandTotalAsDouble();
            Double transactionFee  = (grandTotal * loginRepository.getCustomerEntity().getStore().getChargeRate()/100 )
                    + loginRepository.getCustomerEntity().getStore().getTransactionFee();
            grandTotal = grandTotal + transactionFee;
            //nathan : perkz deduction
            if(loginRepository.getCustomerEntity().getPerkzRewards().size()>0 && loginRepository.getCustomerEntity().getPerkzRewards().get(0).getClaimedReward()!=null){
                grandTotal = grandTotal-(loginRepository.getCustomerEntity().getPerkzRewards().get(0).getClaimedReward()-loginRepository.getCustomerEntity().getPerkzRewards().get(0).getPreviousClaim());
            }

            if(loginRepository.getCustomerEntity().getDeliveryCharge()!=null && loginRepository.getCustomerEntity().getDeliveryCharge()>0){
                grandTotal = grandTotal + loginRepository.getCustomerEntity().getDeliveryCharge();
            }

            if(loginRepository.getCustomerEntity().getTipAmount()>0){
                grandTotal = grandTotal + loginRepository.getCustomerEntity().getTipAmount();
            }

            paymentDetails.setAmount(Util.convertDoubleToLong(grandTotal));
/*
            Double grandTotal = Data.getInstance().getTotalCostAsDouble();
            Double tax = new PaymentUtil().getTaxAmount(Data.getInstance().getTotalCostAsDouble());


            Double grandTotal = Data.getInstance().getTotalCostAsDouble();
            Double tax = new PaymentUtil().getTaxAmount(Data.getInstance().getTotalCostAsDouble());


            Double transactionFee = new PaymentUtil().getTransactionFee(grandTotal+ tax);
            grandTotal = grandTotal + transactionFee;
            transactionFee =  new PaymentUtil().getTransactionFee(grandTotal);
            paymentDetails.setTransactionFee(transactionFee);
            paymentDetails.setTax(tax);
            paymentDetails.setTotalSalePrice(Data.getInstance().getTotalCostAsDouble());
            */
        } else {
           //Double amt = Data.getInstance().getGrandTotalAsLong() + taxes
            Double grandTotal = Data.getInstance().getGrandTotalAsDouble();
            //nathan : perkz deduction
            if(loginRepository.getCustomerEntity().getPerkzRewards().size()>0 && loginRepository.getCustomerEntity().getPerkzRewards().get(0).getClaimedReward()!=null){
                grandTotal = grandTotal-(loginRepository.getCustomerEntity().getPerkzRewards().get(0).getClaimedReward()-loginRepository.getCustomerEntity().getPerkzRewards().get(0).getPreviousClaim());
            }

            if(loginRepository.getCustomerEntity().getDeliveryCharge()!=null && loginRepository.getCustomerEntity().getDeliveryCharge()>0){
                grandTotal = grandTotal + loginRepository.getCustomerEntity().getDeliveryCharge();
            }

            if(loginRepository.getCustomerEntity().getTipAmount()>0){
                grandTotal = grandTotal + loginRepository.getCustomerEntity().getTipAmount();
            }

            paymentDetails.setAmount(Util.convertDoubleToLong(grandTotal));
            //paymentDetails.setT
        }
        paymentDetails.setCurrency("usd");
        paymentDetails.setCustomerId(loginRepository.getCustomerEntity().getCustomerId());
        paymentDetails.setPaymentMode(paymentMode);
        paymentDetails.setAllowFutureUse(allowFutureUse);
        return paymentDetails;
    }

    public Double getTaxAmount(Double amount) {
        Double taxes = (amount * loginRepository.getCustomerEntity().getStore().getSalesTaxes()/100);
        return  taxes;
    }

    public Double getTransactionFee(Double amount) {
       Double transactionFee = (amount * loginRepository.getCustomerEntity().getStore().getChargeRate() / 100)
                + loginRepository.getCustomerEntity().getStore().getTransactionFee();
       return transactionFee;
    }

    public AlertDialog.Builder getDialogProgressBar(String title, Context context) {

        if (builder == null) {
            builder = new AlertDialog.Builder(context);

            builder.setTitle(title);

            final ProgressBar progressBar = new ProgressBar(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(lp);
            builder.setView(progressBar);
        }
        return builder;
    }

}
