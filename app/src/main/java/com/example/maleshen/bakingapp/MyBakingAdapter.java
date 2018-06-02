package com.example.maleshen.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maleshen.bakingapp.model.Receipt;
import com.example.maleshen.bakingapp.utils.BakingUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MyBakingAdapter  extends RecyclerView.Adapter<MyBakingAdapter.ViewHolder> {

    private Context localContext;

    private List<Receipt> mReceiptData;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTextView.setText( mReceiptData.get(position).getName());

    }

    MyBakingAdapter(Context ct) {
        this.localContext = ct;
    }


    @NonNull
    @Override
    public MyBakingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_baking, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public int getItemCount() {
        return null == mReceiptData ? 0 : mReceiptData.size();
    }

    public void setReceiptsData(List<Receipt> receiptData) {
        if (receiptData != null) {
            mReceiptData = receiptData;
        } else {
            mReceiptData = new ArrayList<>();
        }
        notifyDataSetChanged();
    }
}
