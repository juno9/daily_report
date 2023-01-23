package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    String 주소 = "192.168.0.5";
    int 포트 = 10008;
    PrintWriter 프린트라이터;
    String 받아온메시지;
    Handler 핸들러;
    String 보낼메시지;
    String 유저메일;
    String 받는유저메일;
    String UserID;
    PreferenceHelper 프리퍼런스헬퍼;
    ArrayList<Item_message> 메시지목록 = new ArrayList<>();
    Adapter_message 메시지어댑터;
    BufferedReader 버퍼리더;

    @Override
    protected void onPause() {
        super.onPause();
        try{
            소켓.close();
        }catch (Exception e){

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        채팅리스트뷰 = findViewById(R.id.리스트뷰_채팅내용);
        입력창 = findViewById(R.id.입력란_채팅내용);
        보내기버튼 = findViewById(R.id.버튼_전송);
        상대유저이름 = findViewById(R.id.텍스트뷰_상대유저이름);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        프리퍼런스헬퍼 = new PreferenceHelper(getApplicationContext());

        유저메일 = 프리퍼런스헬퍼.getUser_email();
        받는유저메일 = getIntent().getStringExtra("받는유저이메일");
        Log.i("보내는 유저", 유저메일);
        Log.i("받는 유저", 받는유저메일);

        메시지어댑터 = new Adapter_message(메시지목록, this);//어댑터 선언
        채팅리스트뷰.setAdapter(메시지어댑터);


        Thread 연결스레드=new Thread() {
            @Override
            public void run() {
                super.run();
                InetAddress 서버주소 = null;
                try

                {//액티비티 들어오면서 소켓을 하나만 만들어주고
                    서버주소 = InetAddress.getByName(주소);
                    소켓 = new Socket(서버주소, 포트);
                    프린트라이터 = new PrintWriter(소켓.getOutputStream());
                    버퍼리더 = new BufferedReader(new InputStreamReader(소켓.getInputStream()));
                    받아온메시지 = 버퍼리더.readLine();
                    프린트라이터.println(유저메일);
                    프린트라이터.flush();//지금 로그인 한 유저 이메일 주소를 서버에 보냄
                    프린트라이터.println(받는유저메일);
                    프린트라이터.flush();//메시지를 받을 상대 유저의 이메일주소를 서버에 보냄

                } catch(UnknownHostException e)

                {
                    e.printStackTrace();
                } catch(IOException e)

                {
                    e.printStackTrace();
                }
            }


        };
        연결스레드.start();




        gethistory(유저메일, 받는유저메일);//이전 메시지 기록들

        핸들러 = new Handler(){
            public void handleMessage(Message msg){
                Log.i("받은 핸들러 메시지",String.valueOf(msg.what));
                if(msg.what ==0){
                    메시지어댑터.notifyDataSetChanged();
                    Log.i("노티 여부","노티 함");
                }
            }
        };



        Thread 메시지보내기스레드=new Thread() {//새로운 스레드가 돌면서 메시지를 보내주고 받아온다.
            public void run() {
                try {

                    while (true) {
                        //데이터를 받아옴
                        Log.i("서버에서 받은 메시지", 받아온메시지);
                        if (받아온메시지 != null) {
                            Log.i("받아온 메시지", 받아온메시지);
                            Item_message 메시지 = new Item_message("sentMessage", "11:11:11", 받아온메시지, 받는유저메일, 유저메일);

                            Log.i("보낸유저메일",유저메일);
                            Log.i("받는유저메일",받는유저메일);

                            메시지목록.add(메시지);
                            메시지어댑터.setarraylist(메시지목록);
                            //여기서 핸들러에 보낼 메시지 객체를 만들고 보내주자.
                            핸들러.sendEmptyMessage(0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        메시지보내기스레드.start();

        보내기버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                보낼메시지 = 입력창.getText().toString();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Item_message 메시지 = new Item_message("sentMessage", "11:11:11", 보낼메시지, 유저메일, 받는유저메일);
                            메시지목록.add(메시지);
                            프린트라이터.println(보낼메시지);// 출력 스트림으로 흘려보내기 전, 프린트라이터에 텍스트를 담음
                            프린트라이터.flush();//프린트라이터에 담긴 메시지를 아웃풋스트림에 흘려보냄
                            입력창.setText("");//기존입력 내용을 ""으로 대체함
                            Log.i("스레드개수", String.valueOf(Thread.activeCount()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                메시지어댑터.notifyDataSetChanged();
            }
        });
    }//온크리에이트

    class msgUpdate implements Runnable {
        private String msg;

        public msgUpdate(String str) {
            this.msg = str;
        }

        @Override
        public void run() {

            Log.i("입력함", msg);
        }
    }

    public void gethistory(String 보내는유저, String 받는유저) {
        {
            int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
            //연결여부, 연결된 인터넷 경로를 받아온다(wifi, lte등)
            String 상태 = String.valueOf(status);
            Log.i("인터넷상태", 상태);
            if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                Log.i("조건문 진입", "진입");
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                Log.i("큐 생성", "큐 생성");
                String url = "http://" + 주소 + "/get_message.php";
                Log.i("url 생성", "유알엘생성");
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                Log.i("응답내용", response);
                                try {
                                    JSONObject 제이슨객체 = new JSONObject(response);//data:{"기록1,기록2,기록3"}
                                    Log.i("제이슨객체", 제이슨객체.toString());
                                    String data = 제이슨객체.getString("data");
                                    Log.i("제이슨 객체 내 data", data);
                                    JSONArray 제이슨어레이 = new JSONArray(data);
                                    Log.i("제이슨어레이", 제이슨어레이.toString());
                                    int 어레이길이 = 제이슨어레이.length();
                                    Log.i("제이슨어레이 길이", String.valueOf(어레이길이));

                                    for (int i = 0; i < 어레이길이; i++) {//반복문 돌면서 어레이 안의 채팅 내용들을 뺴내자
                                        String 제이슨아이템 = 제이슨어레이.get(i).toString();//첫번째 기록 값을 스트링으로 받는다
                                        Log.i("제이슨어레이 아이템", 제이슨아이템);
                                        JSONObject 아이템제이슨 = new JSONObject(제이슨아이템);
                                        Log.i("제이슨 아이템" + (i + 1) + "번째: ", 제이슨아이템);
                                        String 보낸유저메일 = 아이템제이슨.getString("sender_email");
                                        Log.i("보낸유저메일", 보낸유저메일);
                                        String 받은유저메일 = 아이템제이슨.getString("receiver_email");
                                        Log.i("받은유저메일", 받은유저메일);
                                        String 시간 = 아이템제이슨.getString("time");
                                        Log.i("시간", 시간);
                                        String 내용 = 아이템제이슨.getString("contents");
                                        Log.i("내용", 내용);
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


                                } catch (JSONException e) {

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
                Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }//이전 채팅 목록 받아오는 코드


}
