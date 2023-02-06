package com.example.myapplication;

import android.graphics.Bitmap;
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
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Activity_messege_backup extends AppCompatActivity {

    ListView 채팅리스트뷰;
    EditText 입력창;
    Button 보내기버튼;
    TextView 상대유저이름;
    Socket 소켓;
    String 주소 = ipclass.ip;
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
    String 프로필이미지;
    String 현재시간;
    URL url2;
    ImageView 뒤로가기;
    Bitmap 비트맵이미지;
    내용받기쓰레드 내받스;

    @Override
    protected void onPause() {
        super.onPause();
        setContentView(R.layout.activity_message);
        Log.i("생명주기", "온 퍼즈");

        new Thread() {
            @Override
            public void run() {
                try {
                    프린트라이터.println("/quit");
                    프린트라이터.flush();
                    내받스.interrupt();

                    Log.i("[온퍼스]", "받는 스레드 id" + 내받스.getId());
                    Log.i("[온퍼스]", "받는 스레드 현재상태" + 내받스.getState());
                    Log.i("[온퍼스]", "받는 스레드 이름" + 내받스.getName());
                    Log.i("[온퍼스]", "받는 스레드 살아있는지" + 내받스.isAlive());
                    Log.i("[온퍼스]", "받는 스레드 멈춤" + 내받스.isInterrupted());
                } catch (Exception e) {

                }
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Date now = new Date();
        SimpleDateFormat 시간변환 = new SimpleDateFormat("hh:mm:ss");
        현재시간 = 시간변환.format(now);
        입력창 = findViewById(R.id.입력란_채팅내용);
        보내기버튼 = findViewById(R.id.버튼_전송);
        상대유저이름 = findViewById(R.id.텍스트뷰_상대유저이름);
        뒤로가기 = findViewById(R.id.이미지뷰_뒤로가기2);
        상대유저이름.setText(받는유저메일);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        프리퍼런스헬퍼 = new PreferenceHelper(getApplicationContext());
        유저메일 = 프리퍼런스헬퍼.getUser_email();
        받는유저메일 = getIntent().getStringExtra("받는유저이메일");
        프로필이미지 = getIntent().getStringExtra("프로필이미지스트링");
        소켓 = Activity_login.소켓;
        프린트라이터 = Activity_login.프린트라이터;
        버퍼리더 = Activity_login.버퍼리더;
        Log.i("보내는 유저", 유저메일);
        Log.i("받는 유저", 받는유저메일);
        채팅리스트뷰 = findViewById(R.id.리스트뷰_채팅내용);
        gethistory(유저메일, 받는유저메일);//이전 메시지 기록들
        메시지어댑터 = new Adapter_message(메시지목록, this);//어댑터 선언
        채팅리스트뷰.setAdapter(메시지어댑터);


        핸들러 = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    메시지어댑터.notifyDataSetChanged();
                    채팅리스트뷰.setSelection(메시지목록.size() - 1);
                    Log.i("[핸들러]메시지 전달받고 노티함", "노티함");
                }
            }


        };


        내받스 = new 내용받기쓰레드();
        내받스.start();


        뒤로가기.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            프린트라이터.println("/quit");
                            프린트라이터.flush();
                        } catch (Exception e) {

                        }
                    }
                }.start();
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


    public void gethistory(String 보내는유저, String 받는유저) {
        {
            int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
            //연결여부, 연결된 인터넷 경로를 받아온다(wifi, lte등)
            String 상태 = String.valueOf(status);
            Log.i("[gethistory]인터넷상태", 상태);
            if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                Log.i("[gethistory]조건문 진입", "진입");
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                Log.i("[gethistory]큐 생성", "큐 생성");
                String url = "http://" + 주소 + "/get_message.php";
                Log.i("[gethistory]url 생성", "유알엘생성");
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                Log.i("응답내용", response);
                                try {
                                    JSONObject 제이슨객체 = new JSONObject(response);//data:{"기록1,기록2,기록3"}
                                    Log.i("[gethistory]제이슨객체", 제이슨객체.toString());
                                    String data = 제이슨객체.getString("data");
                                    Log.i("[gethistory]제이슨 객체 내 data", data);
                                    JSONArray 제이슨어레이 = new JSONArray(data);
                                    Log.i("[gethistory]제이슨어레이", 제이슨어레이.toString());
                                    int 어레이길이 = 제이슨어레이.length();
                                    Log.i("[gethistory]제이슨어레이 길이", String.valueOf(어레이길이));

                                    for (int i = 0; i < 어레이길이; i++) {//반복문 돌면서 어레이 안의 채팅 내용들을 뺴내자
                                        String 제이슨아이템 = 제이슨어레이.get(i).toString();//첫번째 기록 값을 스트링으로 받는다
                                        Log.i("[gethistory]제이슨어레이 아이템", 제이슨아이템);
                                        JSONObject 아이템제이슨 = new JSONObject(제이슨아이템);
                                        Log.i("[gethistory]제이슨 아이템" + (i + 1) + "번째: ", 제이슨아이템);
                                        String 보낸유저메일 = 아이템제이슨.getString("sender_email");
                                        Log.i("[gethistory]보낸유저메일", 보낸유저메일);
                                        String 받은유저메일 = 아이템제이슨.getString("receiver_email");
                                        Log.i("[gethistory]받은유저메일", 받은유저메일);
                                        String 시간 = 아이템제이슨.getString("time");
                                        Log.i("[gethistory]시간", 시간);
                                        String 내용 = 아이템제이슨.getString("contents");
                                        Log.i("[gethistory]내용", 내용);
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

    class 메시지업데이트 implements Runnable {
        private String 받아온메시지;

        public 메시지업데이트(String str) {
            this.받아온메시지 = str;
        }

        @Override
        public void run() {
            Item_message 메시지 = new Item_message("reveivedMessage", 현재시간, 받아온메시지, 받는유저메일, 유저메일);
            메시지목록.add(메시지);
//
            메시지어댑터.notifyDataSetChanged();
        }
    }


    class 내용받기쓰레드 extends Thread {

        @Override
        public void run() {
            super.run();
            int 스레드아이디 = (int) this.getId();
            String 스레드이름=this.getName();
            try {
                프린트라이터.println(받는유저메일);
                Log.i("[내용받기쓰레드" + 스레드아이디 + "]프린트라이터에 받는유저 메일 넣음", 프린트라이터.toString());
                Log.i("[내용받기쓰레드" + 스레드아이디 + "]쓰레드 고유값 확인", String.valueOf(this.getId()));
                프린트라이터.flush();
                Log.i("[내용받기쓰레드" + 스레드아이디 + "]받는유저 보냄", 받는유저메일);
                while (!Thread.currentThread().isInterrupted()) {//현재 스헤드가 인터럽티드 되지 않았다면
                    if (Thread.currentThread().isInterrupted()) {
                        Log.i("[내용받기쓰레드" + 스레드아이디 + "] 스레드의 이름", Thread.currentThread().getName());
                        break;

                    }else {
                        Log.i("[내용받기쓰레드" + 스레드아이디 + "] 서버로부터 값 받는 반복문 시작", 스레드이름);
                        Log.i("[내용받기쓰레드" + 스레드아이디 + "] 서버로부터 값 받는 반복문 시작", 스레드이름);

                        synchronized (버퍼리더) {
                            받아온메시지 = 버퍼리더.readLine();
                        }

                        Log.i("[내용받기쓰레드" + 스레드아이디 + "]서버로부터 받아온 메시지 내용", 받아온메시지);
                        Item_message 받아온메시지아이템 = new Item_message("receivedMessage", 현재시간, 받아온메시지, 받는유저메일, 유저메일);
                        메시지목록.add(받아온메시지아이템);
                        Log.i("[내용받기쓰레드" + 스레드아이디 + "]채팅 메시지아이템을  목록에 추가함", "추가함");
                        if (받아온메시지 != null) {
                            Message msg2 = 핸들러.obtainMessage();
                            Log.i("[내용받기쓰레드" + 스레드아이디 + "]핸들러에 보낼 메시지 객체 생성함", "생성함");
                            msg2.what = 1;
                            Log.i("[내용받기쓰레드" + 스레드아이디 + "]핸들러에 보낼 메시지 객체 의 what값을 설정함", "설정함");
                            핸들러.sendMessage(msg2);
                            Log.i("[내용받기쓰레드" + 스레드아이디 + "]핸들러에 메시지 객체를 전달함", "전달함");
                            //여기서 핸들러에 어떤 작업을 해주면 핸들러에서 리스트뷰에 노티를 해주자
                        }

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}



