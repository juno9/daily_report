package com.example.myapplication;


import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.List;

public class Service_chat extends Service {
    String ip = ipclass.ip;
    int port = ipclass.port;
    public static PrintWriter 프린트라이터;
    public static BufferedReader 버퍼리더;
    String 로그인한유저메일;
    static Thread_receiver 받기쓰레드;
    PreferenceHelper 프리퍼런스헬퍼;
    Handler 핸들러;

    boolean 작동스위치;

    public static Intent serviceIntent = null;

    public Service_chat() {
        Log.i("[Service_chat]", "생성자");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("[Service_chat]onStartCommand", "온 스타트커맨드");
        프리퍼런스헬퍼 = new PreferenceHelper(getApplicationContext());

        String 로그인한유저메일 = 프리퍼런스헬퍼.getUser_email();
        Log.i("[Service_chat]onStartCommand", "받은 유저 메일:" + 로그인한유저메일);
        new java.lang.Thread() {
            public void run() {
                try {
                    Thread[] threads = new Thread[Thread.activeCount()];
                    Thread.enumerate(threads);
                    for (Thread t : threads) {
                        Log.i("[Service_chat]onStartCommand", "Thread name: " + t.getName() + ", Thread ID: " + t.getId());
                    }
                    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
                    for (ActivityManager.RunningServiceInfo service : runningServices) {
                        Log.i("MyApp", "Running Service: " + service.service.getClassName());
                    }
                    InetAddress serverAddr = InetAddress.getByName(ip);
                    Socket 소켓 = new Socket(serverAddr, port);
                    Log.i("[Service_chat]onStartCommand", "소켓 생성");
                    InputStreamReader 인풋스트림 = new InputStreamReader(소켓.getInputStream());
                    Log.i("[Service_chat]onStartCommand", "인풋스트림리더 생성");
                    OutputStreamWriter 아웃풋스트림라이터 = new OutputStreamWriter(소켓.getOutputStream());
                    Log.i("[Service_chat]onStartCommand", "아웃풋스트림라이터 생성");
                    프린트라이터 = new PrintWriter(아웃풋스트림라이터);
                    Log.i("[Service_chat]onStartCommand", "프린트라이터 생성, 서비스 내의 변수에 할당");
                    버퍼리더 = new BufferedReader(인풋스트림);
                    Log.i("[Service_chat]onStartCommand", "버퍼리더 생성, 서비스 내의 변수에 할당");
                    프린트라이터.println(로그인한유저메일);
                    Log.i("[Service_chat]onStartCommand", "프린트라이터에 현재 로그인되어있는 유저의 이메일을 담음");
                    프린트라이터.flush();//소켓 연결과 함께 유저의 이메일만 보내줌
                    Log.i("[Service_chat]onStartCommand", "프린트라이터에 담긴 데이터 서버로 흘려보냄");

                    받기쓰레드 = new Thread_receiver(getApplication(), 로그인한유저메일, 버퍼리더, 프린트라이터);
                    set받기쓰레드(받기쓰레드);
                    Log.i("[Service_chat]onStartCommand", "받기 쓰레드 생성함 ID:" + 받기쓰레드.getId() + " name: " + 받기쓰레드.getName());
                    받기쓰레드.start();//접속하고 채팅 시작 하는데까지는 문제 없음

                    Log.i("[Service_chat]onStartCommand", "서버로부터 메시지 받아오는 스레드 시작");
                } catch (
                        IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();//받기 쓰레드 시작

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("[Service_chat]", "온디스트로이");
        try {
            프린트라이터.println("/destroy");
            프린트라이터.flush();
            Log.i("[Service_chat]", "서버로 /destroy 메시지 보냄");
        } catch (Exception e) {

        }
        serviceIntent = null;
        setAlarmTimer();
        Thread.currentThread().interrupt();
        if (받기쓰레드 != null) {
            받기쓰레드.interrupt();
            Log.i("[Service_chat]", "받기 쓰레드 인터럽트 함");
            받기쓰레드 = null;
            Log.i("[Service_chat]", "서비스 내의 받시 스레드 null할당");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("[Service_chat]", "온크리에이트");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("[Service_chat]", "온바인드");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("[Service_chat]", "온언바인드");
        return super.onUnbind(intent);
    }

    public void showToast(final Application application, final String msg) {
        Log.i("[Service_chat]", "쇼 토스트");
        Handler h = new Handler(application.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(application, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void setAlarmTimer() {
//        Log.i("[Service_chat]setAlarmTimer", "셋 알람타이머");
        final Calendar c = Calendar.getInstance();
//        Log.i("[Service_chat]setAlarmTimer", "캘린더생성");
        c.setTimeInMillis(System.currentTimeMillis());
        Log.i("[Service_chat]setAlarmTimer", "셋 캘린더 객체 시간 설정");
        c.add(Calendar.SECOND, 1);
        Log.i("[Service_chat]setAlarmTimer", "생성한 캘린더 객체의 시간을 1밀리초 추가함");
        Intent intent = new Intent(getApplicationContext(), Receiver_Alarm.class);
//        Log.i("[Service_chat]setAlarmTimer", "Receiver_Alarm 실행하는 인텐트 생성");
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
//        Log.i("[Service_chat]setAlarmTimer", "펜딩 인텐트 생성");
        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Log.i("[Service_chat]setAlarmTimer", "알람매니저 생성");
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
//        Log.i("[Service_chat]setAlarmTimer", "알람매니저의 시간,펜딩인텐트 설정");
    }

    public static void set받기쓰레드(Thread_receiver 받기쓰레드) {
        Service_chat.받기쓰레드 = 받기쓰레드;
    }


}
