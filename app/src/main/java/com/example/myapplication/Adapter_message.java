package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_message extends BaseAdapter {
    private Context 컨텍스트;
    private ArrayList<Item_message> 아이템목록;//알림, 상대방 메시지,나의 메시지를 담아 둘 어레이리스트
    private LayoutInflater 인플레이터;
    private Adapter_message.ViewHolder 뷰홀더;

    Adapter_message(ArrayList<Item_message> 받은메시지목록, Context context) {
        this.아이템목록 = 받은메시지목록;
        this.컨텍스트 = context;
        this.인플레이터 = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        Log.i("getcount", String.valueOf(아이템목록.size()));
        return 아이템목록.size();
    }

    @Override
    public Object getItem(int position) {
        Log.i("getItem","");
        return 아이템목록.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String 유형 = 아이템목록.get(position).get유형();
        View view = null;
        if (유형.equals("sentMessage")) {
            view = 인플레이터.inflate(R.layout.item_sent_messege, null);
            TextView 보낸메시지내용=(TextView) view.findViewById(R.id.textview_contents);
            String 채팅목록내용=아이템목록.get(position).get메시지내용();
            보낸메시지내용.setText(채팅목록내용);
            Log.i("[어댑터메시지] 보낸메시지내용",채팅목록내용);
        } else if (유형.equals("receivedMessage")) {
            view = 인플레이터.inflate(R.layout.item_received_message, null);
            TextView 받은메시지내용=(TextView) view.findViewById(R.id.textview_contents);
            String 채팅목록내용=아이템목록.get(position).get메시지내용();
            받은메시지내용.setText(채팅목록내용);
            Log.i("[어댑터메시지] 받은메시지내용",채팅목록내용);
        }// 유형에 따라 어떤 레이아웃을 인플레이트 할 것인지나누고


        return view;
    }

    class ViewHolder{
        public TextView 이름;
        public TextView 이메일;
        public ImageView 프로필이미지;
    }

    public void setarraylist(ArrayList<Item_message> 메시지리스트) {
        this.아이템목록 = 메시지리스트;
    }
}
