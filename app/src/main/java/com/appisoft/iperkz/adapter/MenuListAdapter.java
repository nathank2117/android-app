package com.appisoft.iperkz.adapter;


import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.appisoft.iperkz.entity.MenuItem;
import com.appisoft.iperkz.view.MenuListViewItem;
import com.appisoft.iperkz.view.MyViewHolder;
import com.appisoft.perkz.R;
import com.appisoft.perkz.entity.DailySpecial;

import java.util.ArrayList;
import java.util.List;

public class MenuListAdapter extends RecyclerView.Adapter<MyViewHolder>   {
    private MenuItem[] mDataset;
    private MenuItem[] masterList;

   // private final PublishSubject<MenuItem> onClickSubject = PublishSubject.create();


    public void setmDataset(MenuItem[] mDataset) {
        this.mDataset = mDataset;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MenuListAdapter(MenuItem[] myDataset) {
        this.mDataset = myDataset;
        this.masterList = mDataset;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MenuListViewItem viewItem = new MenuListViewItem(parent.getContext());
        MyViewHolder vh = new MyViewHolder(viewItem);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
     //  MenuItem item = mDataset[position]
      //  holder.menuListViewItem.setBackgroundColor(Color.RED);
        holder.menuListViewItem.setValues(mDataset[position], this);

    }

   // public void onClickRow.on

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    private MenuItem[] contactListFiltered;

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty() || charString.equalsIgnoreCase("All")) {
                    contactListFiltered = masterList;
                } else {
                    List<MenuItem> filteredList = new ArrayList<>();
                    for (MenuItem row : masterList) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getMealCategory().toLowerCase().equalsIgnoreCase(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                        if (row.getMenuItemName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }

                    }
                    contactListFiltered = new MenuItem[filteredList.size()];
                    filteredList.toArray(contactListFiltered);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDataset = (MenuItem[]) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }
}