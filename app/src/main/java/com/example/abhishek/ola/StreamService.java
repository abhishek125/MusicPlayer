package com.example.abhishek.ola;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.net.Uri;

import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

/**
 * Created by abhishek on 12/21/2017.
 */

public class StreamService extends IntentService {

    private static int NOTIFY_ID=1337;
    private static int FOREGROUND_ID=1338;
    public StreamService(){
        super("stream_music");//name of the worker thread
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String filename=intent.getStringExtra("filename");
        String url=intent.getStringExtra("url");
        startForeground(FOREGROUND_ID,
                buildForegroundNotification(filename));
        startExoPlayer(filename,url);
    }

    private void startExoPlayer(String filename,String url) {

    }
    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    private Notification buildForegroundNotification(String filename) {
        Notification.Builder b=new Notification.Builder(this);
        b.setOngoing(true)
                .setContentTitle(getString(R.string.streaming))
                .setContentText(filename)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setTicker(getString(R.string.streaming));

        return(b.build());
    }
}
