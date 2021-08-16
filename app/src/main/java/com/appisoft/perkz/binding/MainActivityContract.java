package com.appisoft.perkz.binding;

import com.appisoft.iperkz.data.Data;

public interface MainActivityContract {
    public interface Presenter {
        void onShowData(Data data);
    }

    public interface View {
        void showData(Data data);
    }
}
