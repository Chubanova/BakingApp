package com.example.maleshen.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.maleshen.bakingapp.dummy.DummyContent.DummyItem;
import com.example.maleshen.bakingapp.utils.BakingUtils;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class BakingFragment extends Fragment {

    // Define a new interface OnImageClickListener that triggers a callback in the host activity
    View.OnClickListener mCallback;

    // OnImageClickListener interface, calls a method in the host activity named onImageSelected


    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (View.OnClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }


    // Mandatory empty constructor
    public BakingFragment() {
    }

    // Inflates the GridView of all AndroidMe images
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_baking, container, false);

        // Get a reference to the GridView in the fragment_master_list xml layout file
        GridView gridView = (GridView) rootView.findViewById(R.id.name_receipt);

        // Create the adapter
        // This adapter takes in the context and an ArrayList of ALL the image resources to display
        MyBakingRecyclerViewAdapter mAdapter = new MyBakingRecyclerViewAdapter(getContext(), BakingUtils.getSimpleReceiptStringsFromJson(getContext(), ));

        // Set the adapter on the GridView
        gridView.setAdapter(mAdapter);

        // Set a click listener on the gridView and trigger the callback onImageSelected when an item is clicked
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Trigger the callback method and pass in the position that was clicked
//                mCallback.onImageSelected(position);
            }
        });

        // Return the root view
        return rootView;
    }
}
