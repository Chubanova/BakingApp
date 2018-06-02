package com.example.maleshen.bakingapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements BakingFragment.OnClickListener {
    private MyBakingAdapter myBakingAdapter;
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        recyclerView = findViewById(R.id.recycler_view);
//        myBakingAdapter = new MyBakingAdapter(this);
//        recyclerView.setAdapter(myBakingAdapter);
//        https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json
        Log.d(TAG, "onCreate");

        setTitle("BakingApp");

        BakingFragment bakingFragment = new BakingFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.baking_container, bakingFragment)
                .commit();

    }

    @Override
    public void onClick(View position) {
        Toast.makeText(this, "Position clicked = " + position, Toast.LENGTH_SHORT).show();

    }
}
