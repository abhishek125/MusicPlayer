package com.example.abhishek.ola;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

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

public class StreamService extends Service{
    public SimpleExoPlayer player;
    private long playbackPosition=0L;
    private int currentWindow=0;
    private boolean playWhenReady = true;
    private boolean isLocal=false;
    private static int NOTIFY_ID=1337;
    private static int FOREGROUND_ID=1338;
    private String filename,url;
    private SimpleExoPlayerView playerView;
    public SimpleExoPlayerView getPlayerView() {
        return playerView;
    }

    public void setPlayer(SimpleExoPlayerView simpleExoPlayerView){
        simpleExoPlayerView.setPlayer(player);
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        filename=intent.getStringExtra("filename");
        url=intent.getStringExtra("url");//local path or internet url
        isLocal=intent.getBooleanExtra("islocal",false);
        startForeground(FOREGROUND_ID,
                buildForegroundNotification(filename,PendingIntent.getActivity(
                        this,
                        0,
                        new Intent(this,MainActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT
                )));
        initializePlayer();
        Log.i("playerviewval","ended");
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return mediaBinder;
    }
    private IBinder mediaBinder=new MediaBinder();



    class MediaBinder extends Binder
    {
        StreamService getService()
        {

            return StreamService.this;
        }
    }

    private Notification buildForegroundNotification(String filename,PendingIntent pendingIntent) {
        PendingIntent pendingIntentClose = PendingIntent.getBroadcast(this, 12345, new Intent().setAction("CLOSE"), PendingIntent.FLAG_UPDATE_CURRENT);
        //NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.close, "close",pendingIntentClose).build();
        Notification.Builder b=new Notification.Builder(this);
        b.setOngoing(true)
                .setContentTitle(getString(R.string.streaming))
                .setContentText(filename)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.close,"",pendingIntentClose)
                .setTicker(getString(R.string.streaming));

        return(b.build());
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        player.release();
        Log.i("ended","service ended");
    }
    public void initializePlayer()
    {
        //playerView=new SimpleExoPlayerView(this);
        Log.i("inside","initialized player");

        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this,
                    new DefaultTrackSelector(), new DefaultLoadControl());
        }
        //playerView.setPlayer(player);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
    //    player.addListener(this);
        MediaSource mediaSource;
        if (isLocal)
            mediaSource = buildLocalMediaSource(Uri.fromFile(new File(url)), this);
        else
            mediaSource = buildMediaSource(Uri.parse(url), this);
        player.prepare(mediaSource, true, false);
    }

    private MediaSource buildLocalMediaSource(Uri path, Context context) {
        Log.i("info","localmediacalled"+path);
        return new ExtractorMediaSource(path,
                new DefaultDataSourceFactory(context,"ua"),
                new DefaultExtractorsFactory(), null, null);

    }


    private MediaSource buildMediaSource(Uri uri,Context context) {
        String userAgent = Util.getUserAgent(context, "ola");
        return new ExtractorMediaSource(uri, new DefaultHttpDataSourceFactory( userAgent,
                null ,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true),
                new DefaultExtractorsFactory(), null, null);
    }



}
