package com.example.myapplication;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Thread_receiver extends Thread {

    String 로그인한유저;
    BufferedReader 버퍼리더;
    PrintWriter 프린트라이터;
    Handler 핸들러;
    boolean 대화중 = false;
    String 대화상대;
    ArrayList<Item_message> 메시지목록;
    Date now = new Date();
    SimpleDateFormat 시간변환 = new SimpleDateFormat("hh:mm:ss");
    String 현재시간 = 시간변환.format(now);
    String 위치;

    Thread_receiver(String 유저, BufferedReader 받은버퍼리더, PrintWriter 받은프린트라이터) {
        this.로그인한유저 = 유저;
        this.버퍼리더 = 받은버퍼리더;
        this.프린트라이터 = 받은프린트라이터;
    }


    @Override
    public void run() {
        try {
            while (true) {
                Log.i("[내용받기쓰레드]반복문 작동여부 확인용", "도는 중");
                String 메시지보낸유저 = 버퍼리더.readLine();
                Log.i("[내용받기쓰레드]메시지를 보낸 유저의 이메일", 메시지보낸유저);
                String 받은텍스트 = 버퍼리더.readLine();
                Log.i("전달받은 텍스트", 받은텍스트);
                if (대화중 && 위치.equals(메시지보낸유저)) {
                    Message msg2 = 핸들러.obtainMessage();
                    msg2.what = 1;
                    Item_message 메시지아이템 = new Item_message("receivedMessage", 현재시간, 받은텍스트, 대화상대, 로그인한유저);
                    메시지목록.add(메시지아이템);
                    Log.i("[내용받기쓰레드]핸들러에 보낼 메시지 객체 의 what값을 설정함", "설정함");
                    핸들러.sendMessage(msg2);
                } else {
                    //알림
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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


}
