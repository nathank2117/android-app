package com.appisoft.iperkz.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appisoft.iperkz.data.Data;
import com.appisoft.perkz.R;
import com.appisoft.perkz.databinding.FragmentViewCartBannerBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewCartBannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewCartBannerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ViewCartBannerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewCartBannerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewCartBannerFragment newInstance(String param1, String param2) {
        ViewCartBannerFragment fragment = new ViewCartBannerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        FragmentViewCartBannerBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_cart_banner, container, false);
        //MainActivityPresenter mainActivityPresenter = new MainActivityPresenter(this, getApplicationContext());
        View view = binding.getRoot();
        binding.setMyData(Data.getInstance());
         return view;
    }

    public void showMenuDetails(View view) {
        Intent intent = new Intent(getActivity(), MenuDetailsActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        // String message = editText.getText().toString();
        // intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
