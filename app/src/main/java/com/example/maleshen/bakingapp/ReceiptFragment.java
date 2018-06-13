package com.example.maleshen.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.maleshen.bakingapp.model.Ingredient;
import com.example.maleshen.bakingapp.model.Receipt;
import com.example.maleshen.bakingapp.model.Step;

import java.util.List;
import java.util.Objects;


public class ReceiptFragment extends Fragment {
    public static final String TAG = ReceiptFragment.class.getSimpleName();
    OnClickListener mCallback;
    ReceiptAdapter receiptAdapter;
    Receipt mrReceipt;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (ReceiptFragment.OnClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }

    public ReceiptFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_receipt, container, false);

        // Get a reference to the GridView in the fragment_master_list xml layout file
        TextView textView = rootView.findViewById(R.id.receipt_ingredients);
        if (savedInstanceState != null) {
            setMrReceipt((Receipt) savedInstanceState.getParcelable("receipt"));
        }

        textView.setText(mrReceipt.getIngredientsText(Objects.requireNonNull(getContext())));

        RecyclerView recyclerView = rootView.findViewById(R.id.steps);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Create the adapter
        // This adapter takes in the context and an ArrayList of ALL the image resources to display
        receiptAdapter = new ReceiptAdapter(getContext(), mrReceipt.getSteps());
        recyclerView.setAdapter(receiptAdapter);

        // Return the root view
        return rootView;
    }

    public interface OnClickListener {
        public void onClick(Step item);
    }

    public Receipt getMrReceipt() {
        return mrReceipt;
    }

    public void setMrReceipt(Receipt mrReceipt) {
        this.mrReceipt = mrReceipt;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("receipt", getMrReceipt());
    }
}
