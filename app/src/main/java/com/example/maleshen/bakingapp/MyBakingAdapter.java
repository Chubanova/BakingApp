package com.example.maleshen.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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


public class MyBakingAdapter extends RecyclerView.Adapter<MyBakingAdapter.ViewHolder> {
    private static final String TAG = MyBakingAdapter.class.getSimpleName();


    private Context localContext;
    private static BakingFragment.OnClickListener listener;



    private List<Receipt> mReceiptData;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public Receipt mReceipt;

        public ViewHolder( TextView view) {
            super(view);
            mTextView = view;
        }

        public void bind(final Receipt item, final BakingFragment.OnClickListener listener) {
            mTextView.setText(item.getName());
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(item);
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mReceiptData.get(position),listener);
    }


    public MyBakingAdapter(Context localContext) {
        this.localContext = localContext;
        this.listener = (BakingFragment.OnClickListener) localContext;
    }

    @NonNull
    @Override
    public MyBakingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_baking, parent, false);
        ViewHolder vh = new ViewHolder( v);
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
