package com.example.maleshen.bakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maleshen.bakingapp.model.Ingredient;
import com.example.maleshen.bakingapp.model.Receipt;
import com.example.maleshen.bakingapp.model.Step;

import java.util.List;

public class ReceiptActivity extends AppCompatActivity implements
        ReceiptFragment.OnClickListener {
    public static final String TAG = ReceiptActivity.class.getSimpleName();

    Receipt mReceipt;
    TextView mIngridientTV;
    RecyclerView mListStepsRV;
    List<Ingredient> ingredient;
    List<Step> steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        ReceiptFragment receiptFragment = new ReceiptFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();

        mReceipt = getIntent().getParcelableExtra(String.valueOf(R.string.RECEIPT));
        Log.d(TAG, mReceipt.getName());
        ingredient = mReceipt.getIngredients();
        steps = mReceipt.getSteps();
        Log.d(TAG, String.valueOf(ingredient.toString()));

        receiptFragment.setMrReceipt(mReceipt);

        fragmentManager.beginTransaction()
                .add(R.id.receipt_container, receiptFragment)
                .commit();
    }


    @Override
    public void onClick(Step item) {

        Bundle b = new Bundle();
        b.putParcelable(String.valueOf(R.string.ITEM), item);
        b.putParcelable(String.valueOf(R.string.ITEM), mReceipt);
        final Intent intent = new Intent(this, StepActivity.class);
        intent.putExtras(b);


        startActivity(intent);
    }
}
