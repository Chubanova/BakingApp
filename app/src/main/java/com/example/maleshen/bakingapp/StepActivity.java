package com.example.maleshen.bakingapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.maleshen.bakingapp.IdlingResource.SimpleIdlingResource;
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
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.text.TextUtils.isEmpty;
import static com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;
import static com.google.android.exoplayer2.source.ExtractorMediaSource.DEFAULT_LOADING_CHECK_INTERVAL_BYTES;
import static com.google.android.exoplayer2.source.ExtractorMediaSource.MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA;

public class StepActivity extends AppCompatActivity implements EventListener {
    public static final String TAG = StepActivity.class.getSimpleName();
    private Step step;

    @BindView(R.id.next_instruction)
    Button mNextInstruction;
    @BindView(R.id.prev_instruction)
    Button mPrevInstruction;
    private Receipt mReceipt;
    private List<Ingredient> mIngriendt;
    private List<Step> mStep;
    private int countSteps;
    private int numberOfStep;
    @BindView(R.id.instruction)
    TextView mInstruction;
    @BindView(R.id.exoplayer)
    PlayerView mPlayerView;
    @BindView(R.id.image_step)
    ImageView imageView;
    @BindView(R.id.step_toolbar)
    Toolbar mToolbar;
    private SimpleExoPlayer mExoPlayer;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    private Dialog mFullScreenDialog;
    private boolean mExoPlayerFullscreen = false;
    private ImageView mFullScreenIcon;
    private FrameLayout mFullScreenButton;

    private static long currentPosition;
    private static boolean playWhenReady;
    private static Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        ButterKnife.bind(this);

        mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

        step = getIntent().getParcelableExtra(String.valueOf(R.string.ITEM));
        mReceipt = getIntent().getParcelableExtra(String.valueOf(R.string.RECEIPT));
        countSteps = mReceipt.getSteps().size();
        numberOfStep = step.getId();
        Log.d(TAG, step.getDescription() + "  " + step.getVideoURL());
        mInstruction.setText(step.getDescription());

        if (numberOfStep - 1 == countSteps) {
            mNextInstruction.setVisibility(View.INVISIBLE);
        }
        if (numberOfStep == 0) {
            mPrevInstruction.setVisibility(View.INVISIBLE);
        }
        initializeMediaSession();
        currentPosition = C.POSITION_UNSET;
        playWhenReady = true;
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getLong(String.valueOf(R.string.current_position));
            playWhenReady = savedInstanceState.getBoolean(String.valueOf(R.string.play_when_ready));
        }
        if (!isEmpty(step.getThumbnailURL())) {
            Uri builtUri = Uri.parse(step.getThumbnailURL()).buildUpon().build();
            Picasso.with(this).load(builtUri).into(imageView);
        }

        if (!isEmpty(step.getVideoURL())) {
            initializePlayer(Uri.parse(step.getVideoURL()));

            mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
            initFullscreenButton();
            initFullscreenDialog();
        } else if (isLandscapeOrientation(this)) {
            mInstruction.setVisibility(View.GONE);
        }

        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT) {
            openFullscreenDialog();
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mReceipt.getName());
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    //go back to "Recipe" screen
                    finish();
                }
            }
        );

        getIdlingResource();
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
            ExtractorMediaSource.Factory mFactory = new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(this, userAgent));
            MediaSource mediaSource = mFactory.createMediaSource(mediaUri, null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.seekTo(currentPosition);
            mExoPlayer.setPlayWhenReady(playWhenReady);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(String.valueOf(R.string.current_position), currentPosition);
        outState.putBoolean(String.valueOf(R.string.play_when_ready), playWhenReady);
    }

    @Override
    public void onPause() {
        super.onPause();
        destroyPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoUri != null)
            initializePlayer(videoUri);
    }

    private void destroyPlayer() {
        if (mExoPlayer != null) {
            currentPosition = mExoPlayer.getCurrentPosition();
            playWhenReady = mExoPlayer.getPlayWhenReady();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
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
    protected void onDestroy() {
        destroyPlayer();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        destroyPlayer();
        super.onStop();
    }
}
