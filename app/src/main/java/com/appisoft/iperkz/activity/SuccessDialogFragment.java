package com.appisoft.iperkz.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.entity.FoodItem;
import com.appisoft.perkz.DisplayMessageActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appisoft.perkz.R;

import java.util.ArrayList;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     SuccessDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class SuccessDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";

    // TODO: Customize parameters
    public static SuccessDialogFragment newInstance(String[] messages) {
        final SuccessDialogFragment fragment = new SuccessDialogFragment();
        final Bundle args = new Bundle();
        args.putStringArray(ARG_ITEM_COUNT, messages);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_success_dialog_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ItemAdapter(getArguments().getStringArray(ARG_ITEM_COUNT)));
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            // TODO: Customize the item layout
            super(inflater.inflate(R.layout.fragment_success_dialog_list_dialog_item, parent, false));
            text = itemView.findViewById(R.id.text);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);
       handleUserExit();
    }

    private void handleUserExit () {
      if ( clearCart() == true) {
        Intent intent = new Intent(getActivity(), DisplayMessageActivity.class);
        getActivity().finish();
          getActivity().overridePendingTransition(0, 0);
        startActivity(intent);
          getActivity().overridePendingTransition(0, 0);
        }
      }

    private boolean clearCart() {
        Data.getInstance().setSelectedMenuItems(new ArrayList<FoodItem>());
        Data.getInstance().clearTotalCost();
        //Data.getInstance().setGrandTotal(0.0);
        return true;
    }
    private class ItemAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final int mItemCount;
        private final String[] mMessages;
        ItemAdapter(String[] messages) {
            mItemCount = messages.length;
            mMessages = messages;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(mMessages[position]);
        }

        @Override
        public int getItemCount() {
            return mItemCount;
        }


    }

}
