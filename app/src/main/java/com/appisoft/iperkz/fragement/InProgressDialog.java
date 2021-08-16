package com.appisoft.iperkz.fragement;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.perkz.R;


public class InProgressDialog extends DialogFragment {
    AnimationDrawable perkzAnimation;
    LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
       // getDialog ().getWindow().setBackgroundDrawable(Color.TRANSPARENT));

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.inprogress_dialog, null);

        //gif magic
        ImageView perkzRewardImage = (ImageView) view.findViewById(R.id.loadingImage);
        if(loginRepository.getCustomerEntity().getStore().getStoreTypeId()==1)
        perkzRewardImage.setBackgroundResource(R.drawable.loading_animation);
        else
            perkzRewardImage.setBackgroundResource(R.drawable.gen_loading_animation);



        perkzAnimation = (AnimationDrawable) perkzRewardImage.getBackground();

        builder.setView(view);
        return builder.create();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inprogress_dialog, container, false);
        // Set transparent background and no title
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = 500;
        params.height = 500;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

       // if (getDialog() != null && getDialog().getWindow() != null) {
      //      getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //}
    }

    @Override
    public void onStart() {
        super.onStart();
        perkzAnimation.start();
    }
}
