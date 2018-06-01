package com.example.maleshen.bakingapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.maleshen.bakingapp.model.Receipt;
import com.example.maleshen.bakingapp.utils.BakingUtils;

import java.util.ArrayList;
import java.util.List;


public class MyBakingRecyclerViewAdapter extends BaseAdapter {




    private Context localContext;

    private List<Receipt> mReceiptData;

    MyBakingRecyclerViewAdapter(Context ct) {
        this.localContext = ct;
    }

    @Override
    public int getCount() {
        return null == mReceiptData ? 0 : mReceiptData.size();
    }

    @Override
    public Object getItem(int i) {
        return mReceiptData != null && mReceiptData.size() > i ? mReceiptData.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(localContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(1, 8, 1, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        Receipt receipt = mReceiptData.get(i);
        if (receipt.getImage() != null) {
            Picasso.with(imageView.getContext())
                    .load(receipt.getImage())
                    .into(imageView);
        } else if (receipt.getPosterByte() != null) {
            imageView.setImageBitmap(BakingUtils.getImage(receipt.getImage()));
        }
        return imageView;
    }

    public void setMoviesData(List<Receipt> receiptData) {
        if (receiptData != null) {
            mReceiptData = receiptData;
        } else {
            mReceiptData = new ArrayList<>();
        }
        notifyDataSetChanged();
    }
}
