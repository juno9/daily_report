package com.example.myapplication;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class Service_restart extends Service {

    public Service_restart() {
        Log.i("[Service_restart]", "생성자");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("[Service_restart]", "온크리에이트");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("[Service_restart]", "온디스트로이");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("[Service_restart]", "온스타트커맨드");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "default");
        Log.i("[Service_restart]", "노티피케이션 빌더 생성");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Log.i("[Service_restart]", "빌더의 아이콘 설정");
        builder.setContentTitle(null);
        Log.i("[Service_restart]", "빌더의 타이틀 null로 설정");
        builder.setContentText(null);
        Log.i("[Service_restart]", "빌더의 텍스트 null로 설정");
        Intent notificationIntent = new Intent(getApplicationContext(), Activity_messege.class);
        Log.i("[Service_restart]", "ACTIVITY_messege 를 시작하는 인텐트 생성");
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        Log.i("[Service_restart]", "먼저 만든 인텐트를 담는 펜딩인텐트 생성");
        builder.setContentIntent(pendingIntent);
        Log.i("[Service_restart]", "빌더의 인텐트를 펜딩 인텐트로 설정");
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Log.i("[Service_restart]", "노티피케이션 매니저 생성");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("[Service_restart]", "sdk버전이 오레오 이상이라면");
            manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_NONE));
            Log.i("[Service_restart]", "노티피케이션 채널 생성");
        }

        Notification notification = builder.build();
        Log.i("[Service_restart]", " 노티피케이션 빌더의 build 매소드를 통해 노티피케이션 객체 생성");
        startForeground(9, notification);
        Log.i("[Service_restart]", "노티피케이션 객체를 매개변수로 전달하여 스타트 포그라운드 실행");
        /////////////////////////////////////////////////////////////////////
        Intent in = new Intent(getApplicationContext(), Service_chat.class);
        Log.i("[Service_restart]", "Service_chat 서비스를 시작하는 인텐트 생성");
        startService(in);
        Log.i("[Service_restart]", "서비스 시작");
        stopForeground(true);
        Log.i("[Service_restart]", "스탑포그라운드");
        stopSelf();
        Log.i("[Service_restart]", "스탑 셀프");

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

}

