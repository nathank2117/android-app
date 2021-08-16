package com.appisoft.iperkz.adapter;


import android.view.ViewGroup;
import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;

import com.appisoft.iperkz.entity.PastOrderItem;
import com.appisoft.iperkz.view.PastOrderListViewItem;
import com.appisoft.iperkz.view.PastOrderViewHolder;

import java.util.ArrayList;
import java.util.List;

public class PastOrderListAdapter extends RecyclerView.Adapter<PastOrderViewHolder> {
    private PastOrderItem[] mDataset;
    private PastOrderItem[] masterList;
    // Provide a suitable constructor (depends on the kind of dataset)
    public PastOrderListAdapter(PastOrderItem[] myDataset) {
        this.mDataset = myDataset;
        this.masterList = mDataset;
    }




        // Create new views (invoked by the layout manager)
        @Override
        public PastOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            PastOrderListViewItem viewItem = new PastOrderListViewItem(parent.getContext());
            PastOrderViewHolder vh = new PastOrderViewHolder(viewItem);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(PastOrderViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
           holder.pastOrderListViewItem.setValues(mDataset[position]);
            // holder.textView.setText(mDataset[position]);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
        private PastOrderItem[] contactListFiltered;

        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty() || charString.equalsIgnoreCase("All") ) {
                        contactListFiltered = masterList;
                    } else {
                        List<PastOrderItem> filteredList = new ArrayList<>();
                        for (PastOrderItem row : masterList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            /*
                            if (row.getMealCategory().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }

                             */
                        }
                        contactListFiltered = new PastOrderItem[filteredList.size()];
                        filteredList.toArray(contactListFiltered);
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = contactListFiltered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mDataset = (PastOrderItem[]) filterResults.values;

                    // refresh the list with filtered data
                    notifyDataSetChanged();
                }
            };
        }


    }