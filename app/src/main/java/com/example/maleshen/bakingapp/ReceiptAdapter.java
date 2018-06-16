package com.example.maleshen.bakingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maleshen.bakingapp.model.Receipt;
import com.example.maleshen.bakingapp.model.Step;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder> {
    private static final String TAG = ReceiptAdapter.class.getSimpleName();


    private Context localContext;
    private static ReceiptFragment.OnClickListener listener;


    private List<Step> steps;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public ViewHolder(TextView view) {
            super(view);
            mTextView = view;

        }

        public void bind(final Step item, final ReceiptFragment.OnClickListener listener) {
            mTextView.setText(item.getShortDescription());
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(item);

                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptAdapter.ViewHolder holder, int position) {
        holder.bind(steps.get(position), listener);
    }



    public ReceiptAdapter(Context localContext, List<Step>steps ) {
        this.localContext = localContext;
        this.steps = steps;
        this.listener = (ReceiptFragment.OnClickListener) localContext;
    }

    @NonNull
    @Override
    public ReceiptAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_step, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public int getItemCount() {
        return steps.size();
    }

    public void setReceiptsData(Receipt receiptData) {
        if (receiptData != null) {
            steps = receiptData.getSteps();
        } else {
            steps = new ArrayList<>();
        }
        notifyDataSetChanged();
    }
}
