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

public class PlayerFragment extends Fragment {

    private static final String PLAYERVIEWVALUE = "playerviewvalue";
    private static final String ISRUNNING = "runningornot";
    private SimpleExoPlayer player;
    private StreamService streamService;
    private boolean isBound=false;
    private SimpleExoPlayerView playerView;
    private  boolean isRunning;
    private Intent intent;
    @Override
    public void onStart() {
        super.onStart();
        AlarmReceiver.isActivityInBackground=false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle){
        return inflater.inflate(R.layout.player_fragment,viewGroup,false);
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            streamService = ((StreamService.MediaBinder)service).getService();
            //playerView=streamService.getPlayerView();
            streamService.setPlayer(playerView);
            /*if(isRunning)
            {

                if(!intent.getStringExtra("url").equals(streamService.getUrl()))
                {
                    getActivity().stopService(intent);
                    getActivity().startService(intent);
                }

            }
            else
                getActivity().startService(intent);*/
            isBound = true;
            Log.i(PLAYERVIEWVALUE,(playerView==null)+"");
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
        intent = getIntent();
        isRunning=isMyServiceRunning("com.example.abhishek.ola.StreamService");

        if(isRunning)
            getActivity().stopService(intent);
        getActivity().startService(intent);
        Log.i(ISRUNNING,isMyServiceRunning("com.example.abhishek.ola.StreamService")+"");
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    private boolean isMyServiceRunning(String serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if(manager==null)
            return false;
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
    public void onPause(){
        super.onPause();
        AlarmReceiver.isActivityInBackground=true;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
