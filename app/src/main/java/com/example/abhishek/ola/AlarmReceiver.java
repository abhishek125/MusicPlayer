package com.example.abhishek.ola;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("CLOSE"))
            context.stopService(new Intent(context, StreamService.class));
    }
}
