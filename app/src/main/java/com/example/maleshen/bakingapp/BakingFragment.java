package com.example.maleshen.bakingapp;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;

import com.example.maleshen.bakingapp.model.Receipt;
import com.example.maleshen.bakingapp.utils.BakingUtils;
import com.example.maleshen.bakingapp.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class BakingFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Receipt>> {
    private static final String TAG = BakingFragment.class.getSimpleName();
    MyBakingAdapter myBakingAdapter;

    // Define a new interface OnImageClickListener that triggers a callback in the host activity
    OnClickListener mCallback;

    // OnImageClickListener interface, calls a method in the host activity named onImageSelected
    public interface OnClickListener {
        void onClick(Receipt receipt);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    // Mandatory empty constructor
    public BakingFragment() {
    }

    // Inflates the GridView of all AndroidMe images
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_baking_list, container, false);

        // Create the adapter
        // This adapter takes in the context and an ArrayList of ALL the image resources to display
        myBakingAdapter = new MyBakingAdapter(getContext());

        // Select layout manager according to display orientation
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(getContext(),3);
        }

        // Get a reference to the View in the fragment_master_list xml layout file
        RecyclerView recyclerView = rootView.findViewById(R.id.list);
        recyclerView.setLayoutManager(mLayoutManager);
        // Set the adapter on the View
        recyclerView.setAdapter(myBakingAdapter);

        getLoaderManager().restartLoader(0, new Bundle(), this);

        // Return the root view
        return rootView;
    }

    @NonNull
    @Override
    public Loader<List<Receipt>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<Receipt>>(getContext()) {

            List<Receipt> mReceiptData = null;

            @Override
            protected void onStartLoading() {

                if (mReceiptData != null && mReceiptData.size() > 0) {
                    deliverResult(mReceiptData);
                } else {
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
                            .getSimpleReceiptStringsFromJson(jsonMoviesResponse);
                } catch (Exception e) {
                    Log.e(String.valueOf(R.string.error), e.getMessage());
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
        myBakingAdapter.setReceiptsData(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Receipt>> loader) {

    }
}
