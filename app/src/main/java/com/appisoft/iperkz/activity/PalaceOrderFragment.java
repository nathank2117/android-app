package com.appisoft.iperkz.activity;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appisoft.iperkz.data.Data;
import com.appisoft.perkz.R;
import com.appisoft.perkz.databinding.FragmentViewCartBannerBinding;
import com.appisoft.perkz.databinding.PalaceOrderFragmentBinding;

public class PalaceOrderFragment extends Fragment {

    private PalaceOrderViewModel mViewModel;

    public static PalaceOrderFragment newInstance() {
        return new PalaceOrderFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        PalaceOrderFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.palace_order_fragment, container, false);
        View view = binding.getRoot();
        binding.setMyData(Data.getInstance());
      // return inflater.inflate(R.layout.palace_order_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PalaceOrderViewModel.class);
        // TODO: Use the ViewModel
    }


}
