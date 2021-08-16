package com.appisoft.perkz.binding;

import android.content.Context;

import com.appisoft.iperkz.data.Data;

public class MainActivityPresenter implements MainActivityContract.Presenter {
    private MainActivityContract.View view;
    private Context ctx;

    public MainActivityPresenter(MainActivityContract.View view, Context ctx) {
        this.view = view;
        this.ctx = ctx;
    }

    @Override
    public void onShowData(Data data) {
        view.showData(data);
    }
}
