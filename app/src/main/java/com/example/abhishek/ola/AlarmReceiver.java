package com.example.abhishek.ola;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AlarmReceiver extends BroadcastReceiver {

    static boolean isActivityInBackground;
    @Override
    public void onReceive(Context context, Intent intent) {
       /*if(!isActivityInBackground){
            ((MainActivity)context).switchFragment();
            Log.i(TAG,"yes");
        }*/

        if(intent.getAction()!=null && intent.getAction().equals("CLOSE"))
            context.stopService(new Intent(context, StreamService.class));
    }

}
