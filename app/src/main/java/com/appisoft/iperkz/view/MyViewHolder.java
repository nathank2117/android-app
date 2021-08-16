package com.appisoft.iperkz.view;


import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class
MyViewHolder extends RecyclerView.ViewHolder {

    public MenuListViewItem menuListViewItem;

    public MyViewHolder(MenuListViewItem viewItem) {
        super(viewItem);
        menuListViewItem = viewItem;
    }
}

