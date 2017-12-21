package com.example.abhishek.ola;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

/**
 * Created by abhishek on 12/21/2017.
 */

public class PlayerFragment extends Fragment {
    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle){
        View view=inflater.inflate(R.layout.player_fragment,viewGroup,false);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);
        playerView = (SimpleExoPlayerView) getView().findViewById(R.id.player);
        TextView textView=(TextView)getView().findViewById(R.id.songname);
        textView.setText(getArguments().getString("FILENAME"));
        if(getArguments().getString("PATH")!=null){
            Log.i("pathisthe",getArguments().getString("PATH"));
        }
        if(bundle!=null){
            playbackPosition=bundle.getLong("playbackPosition");
            currentWindow=bundle.getInt("currentWindow");
            playWhenReady=bundle.getBoolean("playWhenReady");
        }
        /*
        //start service
        Intent i=new Intent(getActivity(), StreamService.class );
        i.putExtra("url",getArguments().getString(PlayerFragment.URL));
        i.putExtra("filename",getArguments().getString(PlayerFragment.FILENAME));


        getActivity().startService(i);*/

    }
    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("playbackPosition", playbackPosition);
        outState.putInt("currentWindow", currentWindow);
        outState.putBoolean("playWhenReady", playWhenReady);
    }

    private void initializePlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(getActivity(),
                    new DefaultTrackSelector(), new DefaultLoadControl());
            playerView.setPlayer(player);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
        }
        MediaSource mediaSource;
        if(getArguments().getString("PATH")!=null)
             mediaSource = buildLocalMediaSource(Uri.fromFile(new File(getArguments().getString("PATH"))));
        else
             mediaSource = buildMediaSource(Uri.parse(getArguments().getString("URL")));
        player.prepare(mediaSource, true, false);
    }

    private MediaSource buildLocalMediaSource(Uri path) {

            return new ExtractorMediaSource(path,
                    new DefaultDataSourceFactory(getActivity(),"ua"),
                    new DefaultExtractorsFactory(), null, null);

    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        String userAgent = Util.getUserAgent(getActivity(), "ola");
        return new ExtractorMediaSource(uri, new DefaultHttpDataSourceFactory( userAgent,
                null ,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true),
                new DefaultExtractorsFactory(), null, null);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


}
