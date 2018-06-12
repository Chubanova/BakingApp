package com.example.maleshen.bakingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.maleshen.bakingapp.model.Receipt;

public class MainActivity extends AppCompatActivity implements BakingFragment.OnClickListener {
    private MyBakingAdapter myBakingAdapter;
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        setTitle("BakingApp");

        BakingFragment bakingFragment = new BakingFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.baking_container, bakingFragment)
                .commit();

    }


    @Override
    public void onClick(Receipt receipt) {
        BakingService.startActionReceiptIngridient(this, receipt);

        Bundle b = new Bundle();
        b.putParcelable(String.valueOf(R.string.RECEIPT), receipt);
        final Intent intent = new Intent(this, ReceiptActivity.class);
        intent.putExtras(b);
        startActivity(intent);

    }
}
