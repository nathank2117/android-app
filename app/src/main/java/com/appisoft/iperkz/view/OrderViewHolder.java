package com.appisoft.iperkz.view;


import androidx.recyclerview.widget.RecyclerView;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    public OrderListViewItem orderListViewItem;

    public OrderViewHolder(OrderListViewItem viewItem) {
        super(viewItem);
        orderListViewItem = viewItem;
    }
}

