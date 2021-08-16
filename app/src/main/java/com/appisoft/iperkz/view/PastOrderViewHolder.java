package com.appisoft.iperkz.view;


import androidx.recyclerview.widget.RecyclerView;

public class PastOrderViewHolder extends RecyclerView.ViewHolder {

    public PastOrderListViewItem pastOrderListViewItem;

    public PastOrderViewHolder(PastOrderListViewItem viewItem) {
        super(viewItem);
        pastOrderListViewItem = viewItem;
    }
}

