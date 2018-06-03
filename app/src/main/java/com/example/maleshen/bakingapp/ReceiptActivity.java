package com.example.maleshen.bakingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.maleshen.bakingapp.model.Receipt;

public class ReceiptActivity extends AppCompatActivity {
    public static final String TAG = ReceiptActivity.class.getSimpleName();

    Receipt mReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);


        mReceipt = getIntent().getParcelableExtra("RECEIPT");
        Log.d(TAG, mReceipt.getName());
    }
}
