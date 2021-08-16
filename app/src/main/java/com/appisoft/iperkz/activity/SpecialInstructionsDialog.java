package com.appisoft.iperkz.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.appisoft.iperkz.entity.FoodItem;
import com.appisoft.perkz.R;
import com.appisoft.perkz.entity.DailySpecial;

public class SpecialInstructionsDialog extends DialogFragment {
    private FoodItem foodItem;
    public SpecialInstructionsDialog(FoodItem foodItem) {
        super();
        this.foodItem = foodItem;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.special_instructions_dialog, null);
        final EditText splInstructionEditText = view.findViewById(R.id.instructionsText);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.spl_instructions_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        foodItem.setSpecialInstructions(splInstructionEditText.getText().toString());

                    }
                });
        /*
                .setNegativeButton(R.string.spl_instructions_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SpecialInstructionsDialog.this.getDialog().cancel();
                    }
                });
*/

        return builder.create();
    }
/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Set title for this dialog
        getDialog().setTitle("Almost Ready !);

        View v = inflater.inflate(R.layout.mydialog, container, false);
        // ...
        return v;
    }
    */

}
