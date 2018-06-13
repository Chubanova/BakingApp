package com.example.maleshen.bakingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.maleshen.bakingapp.IdlingResource.SimpleIdlingResource;
import com.example.maleshen.bakingapp.model.Receipt;

public class MainActivity extends AppCompatActivity implements BakingFragment.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BakingFragment bakingFragment = new BakingFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();

        // On change device orientation recreate fragment
        if (fragmentManager.getFragments().size() > 0) {
            for (Fragment fragment : fragmentManager.getFragments()) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
        }

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

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
