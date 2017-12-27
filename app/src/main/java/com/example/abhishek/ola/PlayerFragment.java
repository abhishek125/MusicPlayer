package com.example.abhishek.ola;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

/**
 * Created by abhishek on 12/21/2017.
 */

public class PlayerFragment extends Fragment {

    private SimpleExoPlayer player;
    private StreamService streamService;
    private boolean isBound=false;
    private SimpleExoPlayerView playerView;
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle){
        View view=inflater.inflate(R.layout.player_fragment,viewGroup,false);
        return view;
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            streamService = ((StreamService.MediaBinder)service).getService();
            //playerView=streamService.getPlayerView();
            streamService.setPlayer(playerView);
            isBound = true;
            Log.i("playerviewvalue",(playerView==null)+"");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            streamService = null;
            isBound = false;
        }
    };
    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);
        playerView = (SimpleExoPlayerView) getView().findViewById(R.id.player);
        TextView textView=(TextView)getView().findViewById(R.id.songname);
        textView.setText(getArguments().getString("FILENAME"));
        Intent intent = getIntent();
        boolean isRunning=isMyServiceRunning("com.example.abhishek.ola.StreamService");
        Log.i("runningornot",isRunning+"");
        if(isRunning)
            getActivity().stopService(intent);
        getActivity().startService(intent);
        Log.i("runningornot",isMyServiceRunning("com.example.abhishek.ola.StreamService")+"");
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    private boolean isMyServiceRunning(String serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public Intent getIntent(){
        boolean isLocal=false;
        Intent i=new Intent(getActivity(), StreamService.class );
        String url=getArguments().getString("URL");
        String filename=getArguments().getString("FILENAME");
        if(url==null){
            url=getArguments().getString("PATH");
            isLocal=true;
        }

        i.putExtra("url",url);
        i.putExtra("filename",filename);
        i.putExtra("islocal",isLocal);
        return i;
    }


    @Override
    public void onStop() {
        super.onStop();
        if (isBound){
        getActivity().unbindService(mConnection);
        isBound = false;
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
