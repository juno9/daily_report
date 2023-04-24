package com.example.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Activity_messege extends AppCompatActivity {

    ListView 채팅리스트뷰;
    EditText 입력창;
    Button 보내기버튼;
    TextView 상대유저이름;
    Socket 소켓;
    String ip = ipclass.ip;
    int port = ipclass.port;
    BufferedReader 버퍼리더;
    PrintWriter 프린트라이터;
    String 받아온메시지;
    Handler 핸들러;
    String 보낼메시지;
    String 유저메일;
    String 받는유저메일;
    String 보내는메시지;
    PreferenceHelper 프리퍼런스헬퍼;
    ArrayList<Item_message> 메시지목록 = new ArrayList<>();
    Adapter_message 메시지어댑터;
    Bitmap 프로필이미지;
    String 현재시간;
    String url2;
    ImageView 뒤로가기;
    Bundle bundle;
    Service_chat.Thread_receiver 받기쓰레드;
    ImageView 프로필이미지뷰;
//    내용받기쓰레드 내받스;

    @Override
    protected void onPause() {
        super.onPause();
        setContentView(R.layout.activity_message);
        Log.i("[Activity_messege]", "온 퍼즈");


    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("[Activity_messege]", "온 스탑");
        new Thread() {
            @Override
            public void run() {
                try {
                    프린트라이터.println("/quit");
                    프린트라이터.flush();
                    Service_chat.받기쓰레드.set위치("");
                    받기쓰레드 = null;
//                    Intent intent = new Intent(getApplicationContext(), Activity_home.class);
//                    intent.putExtra("user_email", 받는유저메일);
//                    startActivity(intent);

                } catch (Exception e) {

                }
            }
        }.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("[Activity_messege]", "온 디스트로이");


    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }


    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        bundle = savedInstanceState;
        Date now = new Date();
        SimpleDateFormat 시간변환 = new SimpleDateFormat("hh:mm:ss");
        현재시간 = 시간변환.format(now);
        입력창 = findViewById(R.id.입력란_채팅내용);
        보내기버튼 = findViewById(R.id.버튼_전송);
        상대유저이름 = findViewById(R.id.텍스트뷰_상대유저이름);
        뒤로가기 = findViewById(R.id.이미지뷰_뒤로가기2);
        프로필이미지뷰 = findViewById(R.id.이미지뷰_프로필이미지);
        Intent intent = getIntent();
        Log.i("[Activity_messege]", "");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Log.i("[Activity_messege]", "");
        프리퍼런스헬퍼 = new PreferenceHelper(getApplicationContext());
        Log.i("[Activity_messege]", "");
        유저메일 = 프리퍼런스헬퍼.getUser_email();//로그인되어 있는 유저의 이메일 보내는 유저 이메일

        Log.i("[Activity_messege]", "");

        받는유저메일 = intent.getStringExtra("받는유저메일");//메시지를 받는 유저의 이메일
        Log.i("[Activity_messege]", "");
        url2 = (String) intent.getStringExtra("이미지url스트링");
        Log.i("[Activity_messege]", "");
        상대유저이름.setText(받는유저메일);
        Log.i("[Activity_messege]", "");


        Log.i("보내는 유저", 유저메일);//이 채팅방에서 보내는 유저는 로그인 되어있는 유저이다.
        Log.i("받는 유저", 받는유저메일);
        채팅리스트뷰 = findViewById(R.id.리스트뷰_채팅내용);
        Log.i("[Activity_messege]", "");
        gethistory(유저메일, 받는유저메일);//이전 메시지 기록들
        Log.i("[Activity_messege]", "");
        메시지어댑터 = new Adapter_message(메시지목록, this, url2);//어댑터 선언
        Log.i("[Activity_messege]", "");
        채팅리스트뷰.setAdapter(메시지어댑터);
        Log.i("[Activity_messege]", "");


        핸들러 = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    메시지어댑터.notifyDataSetChanged();
                    Log.i("[Activity_messege]", "");
                    채팅리스트뷰.setSelection(메시지목록.size() - 1);
                    Log.i("[핸들러]메시지 전달받고 노티함", "노티함");
                }
            }


        };

        Service_chat.Thread_receiver 받기쓰레드 =  Service_chat.받기쓰레드;
//        if (받기쓰레드 == null) {
//            try {
//                InetAddress serverAddr = InetAddress.getByName(ip);
//                소켓 = new Socket(serverAddr, port);
//
//                InputStreamReader 인풋스트림 = new InputStreamReader(소켓.getInputStream());
//                OutputStreamWriter 아웃풋스트림라이터 = new OutputStreamWriter(소켓.getOutputStream());
//                프린트라이터 = new PrintWriter(아웃풋스트림라이터);
//                버퍼리더 = new BufferedReader(인풋스트림);
//
//                프린트라이터.println(유저메일);
//                프린트라이터.flush();//소켓 연결과 함께 유저의 이메일만 보내줌
//                Log.i("[Activity_login]자동로그인 참인 경우,서비스가 돌고 있는 경우", "서비스 시작하지 않음");
//                Thread_receiver 받기쓰레드2 = new Thread_receiver(this, 유저메일, 버퍼리더, 프린트라이터);
//                Service_chat.set받기쓰레드(받기쓰레드2);
//                받기쓰레드 = 받기쓰레드2;
//            } catch (Exception e) {
//
//            }
//            받기쓰레드.start();
//
//        }


        프린트라이터 = Service_chat.프린트라이터;
        Log.i("[Activity_messege]", "프린트라이터 설정함");
        버퍼리더 = Service_chat.버퍼리더;
        Log.i("[Activity_messege]", "버퍼리더 설정함");
        받기쓰레드.set핸들러(this.핸들러);
        Log.i("[Activity_messege]", "");
        받기쓰레드.대화중 = true;
        Log.i("[Activity_messege]", "");
        받기쓰레드.set대화상대(받는유저메일);
        Log.i("[Activity_messege]", "");
        받기쓰레드.set메시지목록(메시지목록);
        Log.i("[Activity_messege]", "");
        받기쓰레드.set위치(받는유저메일);
        Log.i("[Activity_messege]", "");


        new Thread() {
            @Override
            public void run() {
                프린트라이터.println(받는유저메일);
                Log.i("[채팅방 첫입장]보내는 유저 메일 전달함", "");
                프린트라이터.flush();
                Log.i("[채팅방 첫입장]메시지 전달받고 노티함", "");
            }
        }.start();


        뒤로가기.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        보내기버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[보내기]보내기 버튼 눌림", "버튼 눌림");
                보낼메시지 = 입력창.getText().toString();
                Log.i("[보내기]보낼 메시지 작성함: ", 보낼메시지);
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Item_message 보낼메시지아이템 = new Item_message("sentMessage", 현재시간, 보낼메시지, 유저메일, 받는유저메일);
                            Log.i("[보내기]보낼 메시지 아이템 객체 생성: ", "");
                            메시지목록.add(보낼메시지아이템);
                            Log.i("[보내기]보낼 메시지 아이템 객체 목록에 추가함: ", "");
                            Message msg2 = 핸들러.obtainMessage();
                            Log.i("[보내기]핸들러에 보낼 메시지 객체 생성함", "생성함");
                            msg2.what = 1;
                            Log.i("[보내기]핸들러에 보낼 메시지 객체 의 what값을 설정함", "설정함");
                            핸들러.sendMessage(msg2);
                            Log.i("[보내기]핸들러에 메시지객체를 전달함", "전달함");
                            //여기서 핸들러에 어떤 작업을 해주면 핸들러에서 리스트뷰에 노티를 해주자
                            프린트라이터.println(보낼메시지);// 출력 스트림으로 흘려보내기 전, 프린트라이터에 텍스트를 담음
                            Log.i("[보내기]프린트라이터에 메시지를 담음", 프린트라이터.toString());
                            프린트라이터.flush();//프린트라이터에 담긴 메시지를 아웃풋스트림에 흘려보냄
                            입력창.setText("");//기존입력 내용을 ""으로 대체함
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void gethistory(String 보내는유저, String 받는유저) {
        {
            int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
            //연결여부, 연결된 인터넷 경로를 받아온다(wifi, lte등)
            String 상태 = String.valueOf(status);

            if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                String url = "http://" + ip + "/get_message.php";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject 제이슨객체 = new JSONObject(response);//data:{"기록1,기록2,기록3"}

                                    String data = 제이슨객체.getString("data");

                                    JSONArray 제이슨어레이 = new JSONArray(data);

                                    int 어레이길이 = 제이슨어레이.length();


                                    for (int i = 0; i < 어레이길이; i++) {//반복문 돌면서 어레이 안의 채팅 내용들을 뺴내자
                                        String 제이슨아이템 = 제이슨어레이.get(i).toString();//첫번째 기록 값을 스트링으로 받는다

                                        JSONObject 아이템제이슨 = new JSONObject(제이슨아이템);

                                        String 보낸유저메일 = 아이템제이슨.getString("sender_email");

                                        String 받은유저메일 = 아이템제이슨.getString("receiver_email");

                                        String 시간 = 아이템제이슨.getString("time");

                                        String 내용 = 아이템제이슨.getString("contents");

                                        //기록 아이템은 만들어 줌

                                        if (보낸유저메일.equals(유저메일)) {
                                            Item_message 메시지 = new Item_message("sentMessage", 시간, 내용, 보낸유저메일, 받은유저메일);
                                            메시지목록.add(메시지);
                                        } else {
                                            Item_message 메시지 = new Item_message("receivedMessage", 시간, 내용, 보낸유저메일, 받은유저메일);
                                            메시지목록.add(메시지);
                                        }

                                    }
                                    메시지어댑터.setarraylist(메시지목록);
                                    메시지어댑터.notifyDataSetChanged();
                                    채팅리스트뷰.setSelection(메시지목록.size() - 1);

                                } catch (JSONException e) {

                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String 에러내용 = String.valueOf(error);
                        Log.i("[gethistory]온에러리스폰스", 에러내용);
                    }
                }) {
                    // 포스트 파라미터 넣기
                    @Override
                    protected Map getParams() {
                        Map params = new HashMap();
                        params.put("sender_email", 보내는유저);
                        Log.i("보내는유저", 보내는유저);
                        params.put("receiver_email", 받는유저);
                        Log.i("받는유저", 받는유저);
                        return params;
                    }
                };
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            } else {
                Toast.makeText(getApplicationContext(), "[gethistory]인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }//이전 채팅 목록 받아오는 코드

    public static boolean isServiceRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo rsi : am.getRunningServices(Integer.MAX_VALUE)) {
            if (Service_chat.class.getName().equals(rsi.service.getClassName())) //[서비스이름]에 본인 것을 넣는다.
                return true;
        }

        return false;
    }
}



