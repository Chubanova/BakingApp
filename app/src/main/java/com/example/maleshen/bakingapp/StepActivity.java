package com.example.maleshen.bakingapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maleshen.bakingapp.model.Ingredient;
import com.example.maleshen.bakingapp.model.Receipt;
import com.example.maleshen.bakingapp.model.Step;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

import static com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;

public class StepActivity extends AppCompatActivity implements EventListener {
    public static final String TAG = StepActivity.class.getSimpleName();
    private Step step;
    private Button mNextInstruction;
    private Button mPrevInstruction;
    private Receipt mReceipt;
    private List<Ingredient> mIngriendt;
    private List<Step> mStep;
    private int countSteps;
    private int numberOfStep;

    private TextView mInstruction;
    private PlayerView mPlayerView;
    private SimpleExoPlayer mExoPlayer;
    MediaSource videoSource;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private boolean mTwoPanel;

    private Dialog mFullScreenDialog;
    private boolean mExoPlayerFullscreen = false;
    private ImageView mFullScreenIcon;
    private FrameLayout mFullScreenButton;

    private static long currentPosition;
    private static Uri videoUri;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);


        mPlayerView = (PlayerView) findViewById(R.id.exoplayer);
        mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

        step = getIntent().getParcelableExtra(String.valueOf(R.string.ITEM));
        mReceipt = getIntent().getParcelableExtra(String.valueOf(R.string.RECEIPT));
        countSteps = mReceipt.getSteps().size();
        numberOfStep = step.getId();
        Log.d(TAG, step.getDescription() + "  " + step.getVideoURL());
        mInstruction = findViewById(R.id.instruction);
        mInstruction.setText(step.getDescription());

        mNextInstruction = findViewById(R.id.next_instruction);
        mPrevInstruction = findViewById(R.id.prev_instruction);
        if (numberOfStep - 1 == countSteps) {
            mNextInstruction.setVisibility(View.INVISIBLE);
        }
        if (numberOfStep == 0) {
            mPrevInstruction.setVisibility(View.INVISIBLE);
        }
        initializeMediaSession();
        currentPosition = C.POSITION_UNSET;
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getLong("current_position");
        }

        if (!step.getVideoURL().isEmpty()) {
            initializePlayer(Uri.parse(step.getVideoURL()));

            mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
            initFullscreenButton();
            initFullscreenDialog();
        } else if (isLandscapeOrientation(this)) {
            mInstruction.setVisibility(View.GONE);

        }


    }


    private void changeStep(Step item) {
        Toast.makeText(this, "Position clicked = " + item.getShortDescription(), Toast.LENGTH_SHORT).show();
        Bundle b = new Bundle();
        b.putParcelable(String.valueOf(R.string.ITEM), item);
        b.putParcelable(String.valueOf(R.string.RECEIPT), mReceipt);
        final Intent intent = new Intent(this, StepActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void prevInstruction(View view) {
        if (step.getId() > 0) {
            changeStep(mReceipt.getSteps().get(step.getId() - 1));
        }
    }

    public void nextInstruction(View view) {
        if (step.getId() + 1 < countSteps) {
            changeStep(mReceipt.getSteps().get(step.getId() + 1));
        }
    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);
            mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this, "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.seekTo(currentPosition);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
            mExoPlayer.setVideoScalingMode(VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        }
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(this, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    private void initFullscreenDialog() {

        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {

        ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);
        mFullScreenDialog.addContentView(mPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(StepActivity.this, R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
//        mPlayerView.setRotation(90);
        mFullScreenDialog.show();
    }

    public boolean isLandscapeOrientation(Context context) {
        return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    private void closeFullscreenDialog() {

        ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);
        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(mPlayerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(StepActivity.this, R.drawable.ic_fullscreen_expand));
    }

    private void initFullscreenButton() {

        PlaybackControlView controlView = mPlayerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExoPlayerFullscreen)
                    openFullscreenDialog();
                else
                    closeFullscreenDialog();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        if (mExoPlayer != null)
        outState.putLong("current_position", currentPosition);
    }

    //    private boolean destroyVideo = true;
//    @Override
//    protected void onResume(){
//        super.onResume();
//        mPlayerView = (PlayerView) findViewById(R.id.stepView);
//
//
//        ExoPlayerVideoHandler.getInstance()
//                .prepareExoPlayerForUri(this,
//                        Uri.parse(step.getVideoURL()), mPlayerView);
//        ExoPlayerVideoHandler.getInstance().goToForeground();
//
//        findViewById(R.id.exo_fullscreen_button).setOnClickListener(
//                new View.OnClickListener(){
//                    @Override
//                    public void onClick(View v){
//                        destroyVideo = false;
//                        finish();
//                    }
//                });
//    }
//
//    @Override
//    public void onBackPressed(){
//        destroyVideo = false;
//        super.onBackPressed();
//    }
//
    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            currentPosition = mExoPlayer.getCurrentPosition();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoUri != null)
            initializePlayer(videoUri);
    }
//}
//
//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//        if(destroyVideo){
//            ExoPlayerVideoHandler.getInstance().releaseVideoPlayer();
//        }
//    }


    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
}
