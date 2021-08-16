package com.appisoft.iperkz.adapter;


import android.content.Context;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.appisoft.iperkz.activity.ViewCartActivity;
import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.activity.ui.login.LoginViewModel;
import com.appisoft.iperkz.activity.ui.login.LoginViewModelFactory;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.FoodItem;
import com.appisoft.iperkz.view.OrderListViewItem;
import com.appisoft.iperkz.view.OrderViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderViewHolder> {
    private FoodItem[] mDataset;
    private FoodItem[] masterList;
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    private ViewCartActivity viewCartActivity;

    private LoginViewModel loginViewModel;
    // Provide a suitable constructor (depends on the kind of dataset)
    public OrderListAdapter(FoodItem[] myDataset, Context ctx) {
        this.mDataset = myDataset;
        this.masterList = mDataset;
        this.viewCartActivity = (ViewCartActivity) ctx;
        loginViewModel = ViewModelProviders.of(viewCartActivity, new LoginViewModelFactory())
                .get(LoginViewModel.class);
    }




        // Create new views (invoked by the layout manager)
        @Override
        public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            OrderListViewItem viewItem = new OrderListViewItem(parent.getContext());
            OrderViewHolder vh = new OrderViewHolder(viewItem);

           return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(OrderViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
           holder.orderListViewItem.setValues(mDataset[position]);

            holder.orderListViewItem.deleteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    FoodItem theRemovedItem = mDataset[position];
                    // remove your item from data base
               //     mDataset.remove(position);  // remove the item from list
                    FoodItem[] newList = new FoodItem[mDataset.length-1];
                    int k=0;
                    boolean removed = false;
                    for (int i=0; i<mDataset.length; i++) {
                       if (mDataset[i].getMenuId() != theRemovedItem.getMenuId()) {
                           newList[k++] = mDataset[i];
                       } else {
                           if ( removed == false &&  mDataset[i].getSpecialInstructions().equalsIgnoreCase(theRemovedItem.getSpecialInstructions())) {
                               Data.getInstance().removeItemFromAllSelections(i, theRemovedItem);
                               Data.getInstance().recalculateTotalCost();
                               removed = true;
                           } else {
                               newList[k++] = mDataset[i];
                           }
                       }
                    }
                   mDataset = newList;
                    Data.getInstance().recalculateTotalCost();
                    Data.getInstance().recalculateTotalCost();
                    notifyDataSetChanged();

                    loginViewModel.retrieveRewards(loginRepository.getCustomerEntity(), viewCartActivity);
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
        private FoodItem[] contactListFiltered;

        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty() || charString.equalsIgnoreCase("All") ) {
                        contactListFiltered = masterList;
                    } else {
                        List<FoodItem> filteredList = new ArrayList<>();
                        for (FoodItem row : masterList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getMealCategory().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }
                        contactListFiltered = new FoodItem[filteredList.size()];
                        filteredList.toArray(contactListFiltered);
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = contactListFiltered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mDataset = (FoodItem[]) filterResults.values;

                    // refresh the list with filtered data
                    notifyDataSetChanged();
                }
            };
        }


    }