package com.example.myapplication;


import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Thread_receiver extends Thread {
    Context context;
    String sourceurl=ipclass.url;
    String 로그인한유저;
    String ip = ipclass.ip;
    int port = ipclass.port;
    static BufferedReader 버퍼리더;
    static PrintWriter 프린트라이터;
    Handler 핸들러;
    boolean 대화중 = false;
    String 대화상대;
    ArrayList<Item_message> 메시지목록;
    Date now = new Date();
    SimpleDateFormat 시간변환 = new SimpleDateFormat("hh:mm:ss");
    String 현재시간 = 시간변환.format(now);
    String 위치;
    String 메시지보낸유저;
    PreferenceHelper 프리퍼런스헬퍼;
    // Channel에 대한 id 생성
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    // Channel을 생성 및 전달해 줄 수 있는 Manager 생성
    private NotificationManager mNotificationManager;

    // Notification에 대한 ID 생성
    private static final int NOTIFICATION_ID = 0;
    String 프로필이미지스트링;
    URL url2;

    Thread_receiver(Context 컨텍스트, String 유저, BufferedReader 받은버퍼리더, PrintWriter 받은프린트라이터) {
        this.context = 컨텍스트;
        this.로그인한유저 = 유저;
        this.버퍼리더 = 받은버퍼리더;
        this.프린트라이터 = 받은프린트라이터;
        this.프리퍼런스헬퍼 = new PreferenceHelper(컨텍스트);
        Log.i("[내용받기 쓰레드]프린트라이터 스트링", 프린트라이터.toString());
        Log.i("[내용받기 쓰레드]버퍼리더 스트링", 버퍼리더.toString());
        this.위치 = "";
        createNotificationChannel();
    }



    @Override
    public void run() {
        try {
            while (true) {
                Log.i("[내용받기쓰레드]반복문 작동여부 확인용 ", "도는 중, 쓰레드 Name: "+currentThread().getName()+" 쓰레드 ID: "+currentThread().getId());
                메시지보낸유저 = 버퍼리더.readLine();
                if(메시지보낸유저==null){
                    break;

                }
                Log.i("[Thread_receiver] 서버로부터 받은 첫 메시지:",메시지보낸유저);
                프리퍼런스헬퍼.set_sender_User_email(메시지보낸유저);//이 메시지를 보낸 유저는 받아올 수 있음
                프리퍼런스헬퍼.set_receiver_User_email(로그인한유저);//이 메시지를 받는 유저도 알 수 있음
                String 받은텍스트 = 버퍼리더.readLine();
                Log.i("전달받은 텍스트", 받은텍스트);
                Log.i("위치", 위치);
                if(받은텍스트.equals("/destroy")){
                    this.interrupt();
                }else {
                    if (대화중 && 위치.equals(메시지보낸유저)) {
                        Message msg2 = 핸들러.obtainMessage();
                        msg2.what = 1;
                        Item_message 메시지아이템 = new Item_message("receivedMessage", 현재시간, 받은텍스트, 대화상대, 로그인한유저);
                        메시지목록.add(메시지아이템);
                        Log.i("[내용받기쓰레드]핸들러에 보낼 메시지 객체 의 what값을 설정함", "설정함");
                        핸들러.sendMessage(msg2);
                    }
                    else {
                        sendNotification(메시지보낸유저, 받은텍스트);

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    public void sendNotification(String messageBody) {
//        Log.i("[RestartService]", "센드 노티피케이션");
//        get_userImage(프리퍼런스헬퍼.get_sender_User_email());
//        Intent intent = new Intent(context, Activity_messege.class);
//        intent.putExtra("받는유저메일", 프리퍼런스헬퍼.get_sender_User_email());//이 메시지 보낸 사람은 이 에뮬레이터에 로그인한 유저가 보내는 메시지를 받을 사람과 동일인물
//        intent.putExtra("이미지url스트링", 프리퍼런스헬퍼.get_imageurl());
//        Log.i("[RestartService]", "이미지url스트링:" + 프리퍼런스헬퍼.get_imageurl());
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
//
//        String channelId = "fcm_default_channel";//getString(R.string.default_notification_channel_id);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(context, channelId)
//                        .setSmallIcon(R.mipmap.ic_launcher)//drawable.splash)
//                        .setContentTitle("Daily Report")
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setPriority(Notification.FLAG_GROUP_SUMMARY)
//                        .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }


    public void createNotificationChannel() {
        //notification manager 생성
        mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // 기기(device)의 SDK 버전 확인 ( SDK 26 버전 이상인지 - VERSION_CODES.O = 26)
        if (android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.O) {
            //Channel 정의 생성자( construct 이용 )
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID
                    , "Test Notification", mNotificationManager.IMPORTANCE_HIGH);
            //Channel에 대한 기본 설정
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            // Manager을 이용하여 Channel 생성
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

    }


    // Notification을 보내는 메소드
    public void sendNotification(String sender, String messege) {
        get_userImage(프리퍼런스헬퍼.get_sender_User_email());
        Intent intent = new Intent(context, Activity_login.class);
        Intent intent2 = new Intent(context, Activity_user_profile.class);
        Intent intent3 = new Intent(context, Activity_messege.class);
        intent2.putExtra("user_email", 메시지보낸유저);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context.getApplicationContext());

        intent2.putExtra("받는유저메일", 프리퍼런스헬퍼.get_sender_User_email());//이 메시지 보낸 사람은 이 에뮬레이터에 로그인한 유저가 보내는 메시지를 받을 사람과 동일인물
        intent3.putExtra("이미지url스트링", 프리퍼런스헬퍼.get_imageurl());

        stackBuilder.addNextIntent(intent);
        stackBuilder.addNextIntent(intent2);
        stackBuilder.addNextIntent(intent3);


//        Log.i("[내용받기쓰레드]", "이미지url스트링:" + 프리퍼런스헬퍼.get_imageurl());
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        // Builder 생성
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setContentTitle("Daily Report")
                .setContentText(sender + ": " + messege)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.graycircle);
        // Manager를 통해 notification 디바이스로 전달
        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }


    public void set대화중(boolean 대화중) {
        this.대화중 = 대화중;
    }

    public void set메시지목록(ArrayList<Item_message> 메시지목록) {
        this.메시지목록 = 메시지목록;
    }

    public ArrayList<Item_message> get메시지목록() {
        return 메시지목록;
    }


    public void set로그인한유저(String 로그인한유저) {
        this.로그인한유저 = 로그인한유저;
    }

    public String get로그인한유저() {
        return 로그인한유저;
    }

    public void set위치(String 위치) {
        this.위치 = 위치;
    }

    public String get위치() {
        return 위치;
    }

    public void set대화상대(String 받는유저) {
        this.대화상대 = 받는유저;
    }

    public String get받는유저() {
        return 대화상대;
    }

    public void set핸들러(Handler 핸들러) {
        this.핸들러 = 핸들러;
    }

    public Handler get핸들러() {
        return 핸들러;
    }

    public void set버퍼리더(BufferedReader 버퍼리더) {
        this.버퍼리더 = 버퍼리더;
    }

    public void set프린트라이터(PrintWriter 프린트라이터) {
        this.프린트라이터 = 프린트라이터;
    }

    public BufferedReader get버퍼리더() {
        return 버퍼리더;
    }

    public PrintWriter get프린트라이터() {
        return 프린트라이터;
    }

    public void get_userImage(String email) {

        int status = NetworkStatus.getConnectivityStatus(context);
        //연결여부, 연결된 인터넷 경로를 받아온다(wifi, lte등)
        String 상태 = String.valueOf(status);
//        Log.i("인터넷상태", 상태);
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
//            Log.i("조건문 진입", "진입");

            RequestQueue queue = Volley.newRequestQueue(context);
//            Log.i("큐 생성", "큐 생성");

            String url = sourceurl + "get_userdata.php";
//            Log.i("url 생성", "유알엘생성");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
//                            Log.i("응답", response);
                            try {
                                JSONObject 제이슨객체 = new JSONObject(response);//data:{"기록1,기록2,기록3"}

                                String data = 제이슨객체.getString("data");

                                JSONArray 제이슨어레이 = new JSONArray(data);

                                int 어레이길이 = 제이슨어레이.length();


                                for (int i = 0; i < 어레이길이; i++) {
                                    String 제이슨아이템 = 제이슨어레이.get(i).toString();//첫번째 기록 값을 스트링으로 받는다

                                    JSONObject 아이템제이슨 = new JSONObject(제이슨아이템);

                                    프로필이미지스트링 = 아이템제이슨.getString("profile_image");
//                                    Log.i("프로필이미지", 프로필이미지스트링);
                                    url2 = new URL("http://" + ip + "/images/" + 프로필이미지스트링);
//                                    Log.i("프로필이미지url", url2.toString());
                                    프리퍼런스헬퍼.set_imageurl(url2.toString());

                                }
                            } catch (JSONException e) {

                                e.printStackTrace();
                            } catch (MalformedURLException e) {
                                throw new RuntimeException(e);
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String 에러내용 = String.valueOf(error);
                    Log.i("온에러리스폰스", 에러내용);
                }
            }) {
                // 포스트 파라미터 넣기
                @Override
                protected Map getParams() {
                    Map params = new HashMap();
                    params.put("user_email", email);
                    return params;
                }
            };
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        } else {
            Toast.makeText(context, "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
        }


    }

}
