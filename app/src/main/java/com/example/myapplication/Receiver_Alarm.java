package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class Receiver_Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("[AlarmReceiver]","온 리시브");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("[AlarmReceiver]","sdk가 오레오 이상이라면");
            Intent in = new Intent(context, Service_restart.class);
            context.startForegroundService(in);
            Log.i("[AlarmReceiver]","받은 컨텍스트에서 Service_restart를 실행(포그라운드)");
        } else {
            Log.i("[AlarmReceiver]","");
            Intent in = new Intent(context, Service_chat.class);
            context.startService(in);
            Log.i("[AlarmReceiver]","컨텍스트에서  Service_restart 서비스를 실행");
        }
    }

}
