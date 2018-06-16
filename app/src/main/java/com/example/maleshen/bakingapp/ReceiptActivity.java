package com.example.maleshen.bakingapp;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ReceiptActivity extends AppCompatActivity implements
        ReceiptFragment.OnClickListener, EventListener {
    public static final String TAG = ReceiptActivity.class.getSimpleName();

    private Receipt mReceipt;
    private List<Ingredient> ingredient;
    private List<Step> steps;

    private boolean mTwoPane;

    private Step mStep;

    private TextView mInstruction;
    private PlayerView mPlayerView;
    private FrameLayout mMainMediaFrame;
    private SimpleExoPlayer mExoPlayer;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    private Dialog mFullScreenDialog;
    private boolean mExoPlayerFullscreen = false;
    private ImageView mFullScreenIcon;
    private FrameLayout mFullScreenButton;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private static long currentPosition;
    private static boolean playWhenReady;

    public ReceiptActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        ButterKnife.bind(this);

        ReceiptFragment receiptFragment = new ReceiptFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();

//         On change device orientation recreate fragment
        if (fragmentManager.getFragments().size() > 0) {
            for (Fragment fragment : fragmentManager.getFragments()) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
        }

        mReceipt = getIntent().getParcelableExtra(String.valueOf(R.string.RECEIPT));
        Log.d(TAG, mReceipt.getName());
        ingredient = mReceipt.getIngredients();
        steps = mReceipt.getSteps();
        Log.d(TAG, String.valueOf(ingredient.toString()));
        currentPosition = C.POSITION_UNSET;
        playWhenReady = true;

        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mReceipt.getName());
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (findViewById(R.id.scroll) != null) {
            mTwoPane = true;

            mPlayerView = findViewById(R.id.exoplayer);
            mMainMediaFrame = findViewById(R.id.main_media_frame);
            mStep = steps.get(0);
            if (savedInstanceState != null && savedInstanceState.getParcelable(String.valueOf(R.string.STEP)) != null) {
                mStep = savedInstanceState.getParcelable(String.valueOf(R.string.STEP));
            }
            if (savedInstanceState != null) {
                currentPosition = savedInstanceState.getLong(String.valueOf(R.string.current_position));
                playWhenReady = savedInstanceState.getBoolean(String.valueOf(R.string.play_when_ready));
            }
            mInstruction = findViewById(R.id.instruction);
            setInstruction(mStep);

            initFullscreenDialog();
            initFullscreenButton();
        } else {
            mTwoPane = false;
        }

        receiptFragment.setMrReceipt(mReceipt);
//
        fragmentManager.beginTransaction()
                .add(R.id.receipt_container, receiptFragment)
                .commit();
    }

    private void setInstruction(Step step) {
        Log.d(TAG, step.getDescription() + "  " + step.getVideoURL());
        mInstruction.setText(step.getDescription());

        destroyPlayer();
        if (step.getVideoURL() != null && !step.getVideoURL().isEmpty()) {
            initializeMediaSession();

            mPlayerView.setVisibility(View.VISIBLE);
            mMainMediaFrame.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams lp = mMainMediaFrame.getLayoutParams();
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mMainMediaFrame.setLayoutParams(lp);

            initializePlayer(Uri.parse(step.getVideoURL()));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mExoPlayer != null) {
            savePlayerState(mExoPlayer.getCurrentPosition(), mExoPlayer.getPlayWhenReady());
        }
        outState.putLong(String.valueOf(R.string.current_position), currentPosition);
        outState.putBoolean(String.valueOf(R.string.play_when_ready), playWhenReady);
        outState.putParcelable(String.valueOf(R.string.STEP), mStep);
    }


    @Override
    public void onClick(Step item) {
        if (mTwoPane) {
            mStep = item;
            setInstruction(mStep);
        } else {
            Bundle b = new Bundle();
            b.putParcelable(String.valueOf(R.string.ITEM), item);
            b.putParcelable(String.valueOf(R.string.RECEIPT), mReceipt);
            final Intent intent = new Intent(this, StepActivity.class);
            intent.putExtras(b);

            startActivity(intent);
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
            String userAgent = Util.getUserAgent(this, String.valueOf(R.string.app_name));
            ExtractorMediaSource.Factory mFactory = new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(this, userAgent));
            MediaSource mediaSource = mFactory.createMediaSource(mediaUri, null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.seekTo(currentPosition);
            mExoPlayer.setPlayWhenReady(playWhenReady);
            mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
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
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(ReceiptActivity.this, R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }

    private void closeFullscreenDialog() {

        ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);
        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(mPlayerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(ReceiptActivity.this, R.drawable.ic_fullscreen_expand));
    }

    private void initFullscreenButton() {

        PlayerControlView controlView = mPlayerView.findViewById(R.id.exo_controller);
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

    @Override
    protected void onDestroy() {
        destroyPlayer();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        destroyPlayer();
        super.onStop();
    }

    private void destroyPlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
        if (mPlayerView != null) {
            mPlayerView.setVisibility(View.INVISIBLE);
            mMainMediaFrame.setVisibility(View.INVISIBLE);
            ViewGroup.LayoutParams lp = mMainMediaFrame.getLayoutParams();
            lp.height = 1;
            mMainMediaFrame.setLayoutParams(lp);
        }
    }

    private void savePlayerState(long _currentPosition, boolean _playWhenReady) {
        currentPosition = _currentPosition;
        playWhenReady = _playWhenReady;
    }
}
