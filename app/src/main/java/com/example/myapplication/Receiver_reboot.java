package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class Receiver_reboot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("[RebootReceiver]","온 리시브");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("[RebootReceiver]","sdk버전 오레오 이상");
            Intent in = new Intent(context, Service_restart.class);
            context.startForegroundService(in);
            Log.i("[RebootReceiver]","service_restart 서비스 시작(포그라운드)");
        } else {
            Intent in = new Intent(context, Service_chat.class);
            context.startService(in);
        }
    }

}