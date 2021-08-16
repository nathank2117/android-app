package com.appisoft.iperkz.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.appisoft.iperkz.entity.Reward;
import com.appisoft.perkz.R;

import java.util.List;

public class RewardFragment extends DialogFragment {
    private List<Reward> rewardList;
    AnimationDrawable perkzAnimation;
    public RewardFragment(List<Reward> rewardList) {
        super();
        this.rewardList = rewardList;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_reward, null);

        //gif magic
        ImageView perkzRewardImage = (ImageView) view.findViewById(R.id.RewardImage);
        perkzRewardImage.setBackgroundResource(R.drawable.perkz_reward);
        perkzAnimation = (AnimationDrawable) perkzRewardImage.getBackground();

        //

        Button closeBtn = (Button) view.findViewById(R.id.closebtn);
        closeBtn.setOnClickListener(this::closeReward);


        TextView textViewTitle  = (TextView) view.findViewById(R.id.rewardTextTitle);
        TextView textView  = (TextView) view.findViewById(R.id.rewardText);
        textViewTitle.setText("Enjoy $ 5 Reward for using the App");

        textView.setText("We added $5 into your account. When you place your first order " +
                "you can apply this amount towards your order only on Restaurants");


        builder.setView(view)
                // Add action buttons
                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        //foodItem.setSpecialInstructions(splInstructionEditText.getText().toString());
                            Log.i("Reward Fragment","Rewards");
                    }
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        perkzAnimation.start();
    }

    public void closeReward(View view) {
        this.dismiss();
    }

}
