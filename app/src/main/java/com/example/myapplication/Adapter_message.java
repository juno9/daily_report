package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class Adapter_message extends BaseAdapter {
    private Context 컨텍스트;
    private ArrayList<Item_message> 아이템목록;//알림, 상대방 메시지,나의 메시지를 담아 둘 어레이리스트
    private LayoutInflater 인플레이터;
    private Adapter_search.ViewHolder 뷰홀더;

    Adapter_message(ArrayList<Item_message> 받은아이템목록,Context context){
        this.아이템목록=  받은아이템목록;
        this.컨텍스트=context;
        this.인플레이터=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        Log.i("getcount",String.valueOf(아이템목록.size()));
        return 아이템목록.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
