package com.appisoft.iperkz.adapter;


import android.content.Context;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.recyclerview.widget.RecyclerView;

import com.appisoft.iperkz.entity.Store;
import com.appisoft.iperkz.view.StoreListViewItem;

import java.util.ArrayList;
import java.util.List;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.MyStoreViewHolder> implements Filterable {
    private Store[] mDataset;
    private Store[] masterList;

    // Provide a suitable constructor (depends on the kind of dataset)
    public StoreListAdapter(Store[] myDataset) {
        this.mDataset = myDataset;
        this.masterList = mDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyStoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        StoreListViewItem viewItem = new StoreListViewItem(parent.getContext());
        MyStoreViewHolder vh = new MyStoreViewHolder(viewItem, parent.getContext());
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyStoreViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.storeListViewItem.setValues(mDataset[position]);
        // holder.textView.setText(mDataset[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    private Store[] storeListFiltered;

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty() || charString.equalsIgnoreCase("All")) {
                    storeListFiltered = masterList;
                } else {
                    List<Store> filteredList = new ArrayList<>();
                    for (Store row : masterList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getStoreName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    storeListFiltered = new Store[filteredList.size()];
                    filteredList.toArray(storeListFiltered);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = storeListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDataset = (Store[]) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

    public class MyStoreViewHolder extends RecyclerView.ViewHolder {

        public StoreListViewItem storeListViewItem;

        public MyStoreViewHolder(StoreListViewItem viewItem, Context context) {
            super(viewItem);
            storeListViewItem = viewItem;
        }

    }
}