package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Activity_messege extends AppCompatActivity {

    ListView 채팅리스트뷰;
    EditText 입력창;
    Button 보내기버튼;
    TextView 상대유저이름;
    Socket 소켓;
    String 주소 = "192.168.219.157";
    int 포트 = 10010;
    PrintWriter 프린트라이터;
    String 읽은값;
    Handler 핸들러;
    String 보낼메시지;
    String UserID;
    PreferenceHelper 프리퍼런스헬퍼;


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
        String 유저메일 = 프리퍼런스헬퍼.getUser_email();
        핸들러 = new Handler();
        new Thread() {
            public void run() {
                try {
                    InetAddress 서버주소 = InetAddress.getByName(주소);
                    소켓 = new Socket(서버주소, 포트);
                    Log.i("소켓만들어줌", "연결됨");
                    프린트라이터 = new PrintWriter(소켓.getOutputStream());
                    BufferedReader input = new BufferedReader(new InputStreamReader(소켓.getInputStream()));
                    프린트라이터.println(유저메일);
                    프린트라이터.flush();
                    while (true) {
                        읽은값 = input.readLine();
                        System.out.println("TTTTTTTT" + 읽은값);
                        if (읽은값 != null) {
                            핸들러.post(new msgUpdate(읽은값));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        보내기버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                보낼메시지 = 입력창.getText().toString();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            프린트라이터.println(보낼메시지);// 출력 스트림으로 흘려보내기 전, 프린트라이터에 텍스트를 담음
                            프린트라이터.flush();//프린트라이터에 담긴 메시지를 아웃풋스트림에 흘려보냄
                            입력창.setText("");//기존입력 내용을 ""으로 대체함
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
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
}
