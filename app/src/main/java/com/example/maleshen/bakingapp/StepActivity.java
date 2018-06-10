package com.example.maleshen.bakingapp;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maleshen.bakingapp.model.Receipt;
import com.example.maleshen.bakingapp.model.Step;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

public class StepActivity extends AppCompatActivity {
    public static final String TAG = StepActivity.class.getSimpleName();
    private Step step;
    private TextView mInstruction;
    private Button mNextInstruction;
    private Button mPrevInstruction;
    Receipt mReceipt;
    private int countSteps;
    private int numberOfStep;


    //    private SimpleExoPlayerView simpleExoPlayerView;
//    private SimpleExoPlayer simpleExoPlayer;
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        step = getIntent().getParcelableExtra("ITEM");
        mReceipt = getIntent().getParcelableExtra("RECEIPT");
        countSteps = mReceipt.getSteps().size();
        numberOfStep = step.getId();
        Log.d(TAG, step.getDescription());
        mInstruction = findViewById(R.id.instruction);
        mInstruction.setText(step.getDescription());

        mNextInstruction = findViewById(R.id.next_instruction);
        mPrevInstruction = findViewById(R.id.prev_instruction);
        if(numberOfStep==countSteps){
            mNextInstruction.setVisibility(View.INVISIBLE);
        }
        if(numberOfStep==0){
            mPrevInstruction.setVisibility(View.INVISIBLE);
        }



//        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.stepView);

    }



    private void changeStep(Step item) {
        Toast.makeText(this, "Position clicked = " + item.getShortDescription(), Toast.LENGTH_SHORT).show();
        Bundle b = new Bundle();
        b.putParcelable("ITEM", item);
        b.putParcelable("RECEIPT", mReceipt);
        final Intent intent = new Intent(this, StepActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void prevInstruction(View view) {
        if (step.getId()>0 ) {
            changeStep(mReceipt.getSteps().get(step.getId()-1));
        }
    }

    public void nextInstruction(View view) {
        if (step.getId()+1 < countSteps) {
            changeStep(mReceipt.getSteps().get(step.getId()+1));
        }
    }
}
