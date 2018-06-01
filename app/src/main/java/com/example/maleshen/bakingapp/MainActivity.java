package com.example.maleshen.bakingapp;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.maleshen.bakingapp.model.Receipt;
import com.example.maleshen.bakingapp.utils.BakingUtils;
import com.example.maleshen.bakingapp.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<List<Receipt>> {

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.id.recycler_view);
//        https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json

    }

    @NonNull
    @Override
    public Loader<List<Receipt>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<Receipt>>(this) {

            List<Receipt> mReceiptData = null;

            @Override
            protected void onStartLoading() {
                if (mReceiptData != null) {
                    deliverResult(mReceiptData);
                } else {
//                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public List<Receipt> loadInBackground() {

                List<Receipt> receipts = new ArrayList<>();


                    URL moviesRequestUrl = NetworkUtils.buildUrl();

                    try {
                        String jsonMoviesResponse = NetworkUtils
                                .getResponseFromHttpUrl(moviesRequestUrl);

                        receipts = BakingUtils
                                .getSimpleReceiptStringsFromJson(getApplicationContext(), jsonMoviesResponse);
                    } catch (Exception e) {
                        Log.e("Error fetching movies data", e.getMessage());
                    }

                return receipts;

            }

            public void deliverResult(List<Receipt> data) {
                mReceiptData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Receipt>> loader, List<Receipt> data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Receipt>> loader) {

    }
}
