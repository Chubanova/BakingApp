package com.example.maleshen.bakingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.maleshen.bakingapp.model.Receipt;
import com.example.maleshen.bakingapp.model.Step;
import com.squareup.picasso.Picasso;

import static android.text.TextUtils.isEmpty;


public class ReceiptFragment extends Fragment {
    public static final String TAG = ReceiptFragment.class.getSimpleName();
    OnClickListener mCallback;
    ReceiptAdapter receiptAdapter;
    Receipt mrReceipt;
    ImageView imageView;

    ScrollView ingredientsSv;
    RecyclerView stepsRv;

    Parcelable recyclerViewState;

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
        ingredientsSv = rootView.findViewById(R.id.ingredients_scroll);
        stepsRv = rootView.findViewById(R.id.steps);
        imageView = rootView.findViewById(R.id.image_receipt);

        TextView textView = rootView.findViewById(R.id.receipt_ingredients);
        if (savedInstanceState != null) {
            setMrReceipt((Receipt) savedInstanceState.getParcelable(String.valueOf(R.string.RECEIPT)));
        }
        if (!isEmpty(mrReceipt.getImage())) {
            Uri builtUri = Uri.parse(mrReceipt.getImage()).buildUpon().build();
            Picasso.with(getContext()).load(builtUri).into(imageView);
        }
        textView.setText(mrReceipt.getIngredientsText(getContext()));

        stepsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        // Create the adapter
        // This adapter takes in the context and an ArrayList of ALL the image resources to display
        receiptAdapter = new ReceiptAdapter(getContext(), mrReceipt.getSteps());
        stepsRv.setAdapter(receiptAdapter);

        if (recyclerViewState != null) {
            stepsRv.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        }
        // Return the root view
        return rootView;
    }

    public interface OnClickListener {
        void onClick(Step item);
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
        outState.putParcelable(String.valueOf(R.string.RECEIPT), getMrReceipt());
    }

    @Override
    public void onPause() {
        super.onPause();
        recyclerViewState = stepsRv.getLayoutManager().onSaveInstanceState();
    }

    public Parcelable getRecyclerViewState() {
        return recyclerViewState;
    }

    public void setRecyclerViewState(Parcelable recyclerViewState) {
        this.recyclerViewState = recyclerViewState;
    }
}
