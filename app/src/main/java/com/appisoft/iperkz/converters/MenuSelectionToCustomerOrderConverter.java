package com.appisoft.iperkz.converters;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.CustomerOrderCreationRequest;
import com.appisoft.iperkz.entity.CustomerOrderMenuItemRequest;
import com.appisoft.iperkz.entity.FoodItem;
import com.appisoft.iperkz.entity.MenuItemAddition;
import com.appisoft.iperkz.entity.Reward;
import com.appisoft.iperkz.entity.SubItem;
import com.appisoft.iperkz.util.DateUtils;
import com.appisoft.iperkz.util.PaymentUtil;
import com.appisoft.iperkz.util.Util;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class MenuSelectionToCustomerOrderConverter {

    public CustomerOrderCreationRequest convert(String paymentMode, boolean allowFutureUse) {
        LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
        CustomerOrderCreationRequest request = new CustomerOrderCreationRequest();
        request.setPaymentMode(paymentMode);
        request.setAllowFutureUse(allowFutureUse);
        request.setStoreId(loginRepository.getCustomerEntity().getStoreId());
        request.setCustomerId(loginRepository.getCustomerEntity().getCustomerId());
       // request.setPerkzRewards(loginRepository.getCustomerEntity().getPerkzRewards());
        if (loginRepository.reward != null) {
            List<Reward> rewardsList = new ArrayList<>();
            rewardsList.add(loginRepository.reward);
            request.setPerkzRewards(rewardsList);
        } else {
            request.setPerkzRewards(null);
        }
        request.setIsBillable(loginRepository.getCustomerEntity().isBillable());
        request.setIsTakeOut(loginRepository.getCustomerEntity().isTakeOut());
        request.setTableNumber(loginRepository.getCustomerEntity().getTableNumber());
        request.setTipAmount(loginRepository.getCustomerEntity().getTipAmount());
        request.setDeliveryCharge(loginRepository.getCustomerEntity().getDeliveryCharge());
        request.setOrderCompletionMethod(loginRepository.getCustomerEntity().getOrderCompletionMethod());
        request.setAddress(loginRepository.getCustomerEntity().getDeliveryAddress());
        loginRepository.getCustomerEntity().setTableNumber("");
        loginRepository.getCustomerEntity().setTipAmount(0.0);
        loginRepository.getCustomerEntity().setDeliveryCharge(0.0);
        loginRepository.getCustomerEntity().setOrderCompletionMethod(-1);
        loginRepository.getCustomerEntity().setDeliveryAddress("");
        request.setSpecialInstructions("");
        if (paymentMode.equalsIgnoreCase("CASH")) {
            Double tax = new PaymentUtil().getTaxAmount(Data.getInstance().getTotalCostAsDouble());
            request.setTax(tax);
            request.setTransactionFee(0.0);
            request.setTotalSalePrice(Data.getInstance().getTotalCostAsDouble());
        } else {
            if (loginRepository.getCustomerEntity().getStore().getChargeMode().equalsIgnoreCase("CUSTOMER")) {

                Double grandTotal = Data.getInstance().getTotalCostAsDouble();
                Double tax = new PaymentUtil().getTaxAmount(Data.getInstance().getTotalCostAsDouble());

                Double transactionFee = new PaymentUtil().getTransactionFee(grandTotal + tax);
               // grandTotal = grandTotal + transactionFee;
                //transactionFee = new PaymentUtil().getTransactionFee(grandTotal);
                request.setTransactionFee(transactionFee);
                request.setTax(tax);
                request.setTotalSalePrice(Data.getInstance().getTotalCostAsDouble());
            } else {
                Double tax = new PaymentUtil().getTaxAmount(Data.getInstance().getTotalCostAsDouble());
                request.setTax(tax);
                Double totalAmt = Data.getInstance().getTotalCostAsDouble() + tax;
                request.setTransactionFee(new PaymentUtil().getTransactionFee(totalAmt));
                request.setTotalSalePrice(Data.getInstance().getTotalCostAsDouble());

            }
        }
        request.setOrderCreationTime(DateUtils.getCurrentDateAndTime());
        request.setDeliveryDate(loginRepository.getCustomerEntity().getDeliveryDate());
        loginRepository.getCustomerEntity().setDeliveryDate("");
        ArrayList<CustomerOrderMenuItemRequest> orderItems = new ArrayList<>();
        for (FoodItem item : Data.getInstance().getSelectedMenuItems()) {
            CustomerOrderMenuItemRequest orderItem = new CustomerOrderMenuItemRequest();
            orderItem.setMenuItemId(item.getMenuId());
            orderItem.setSpecialInstructions(item.getSpecialInstructions());
            orderItem.setSalePrice(item.getSalePrice());
            orderItem.setCount(item.getQuantity());
            orderItem.setDailySpecial(item.isDailySpecial());
            if(item.getHasAdditions()>0) {
                String additions = "";
                for (MenuItemAddition menuItemAddition : item.getAdditions()) {

                    if (menuItemAddition.getSubItems().size() >0){
                             for (SubItem selectedSubItem : menuItemAddition.getSubItems()) {
                                if (selectedSubItem.isSelected()) {
                                    additions += selectedSubItem.getName() + " | ";
                                }
                            }

                    }
                    else {
                        if (menuItemAddition.isSelected()) {
                            additions += menuItemAddition.getName() + " | ";
                        }
                    }
                }
                if(!additions.isEmpty()) {
                    orderItem.setAdditions(additions.substring(0, additions.length() - 2));
                }
            }
            orderItems.add(orderItem);
        }
        request.setOrderItems(orderItems);
       return request;
    }


}
