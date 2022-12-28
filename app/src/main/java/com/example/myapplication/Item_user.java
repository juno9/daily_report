package com.example.myapplication;

public class Item_user {
    String 프로필이미지;
    String 이름;
    String 이메일;
    String 자기소개;

    public Item_user(String 이미지url, String 이름, String 이메일, String 자기소개){
        this.프로필이미지=이미지url;
        this.이름=이름;
        this.이메일=이메일;
        this.자기소개=자기소개;
    }


    public String get이름() {
        return 이름;
    }

    public String get이메일() {
        return 이메일;
    }

    public String get프로필이미지() {
        return 프로필이미지;
    }

    public void set이름(String 이름) {
        this.이름 = 이름;
    }

    public void set이메일(String 이메일) {
        this.이메일 = 이메일;
    }

    public void set프로필이미지(String 프로필이미지) {
        this.프로필이미지 = 프로필이미지;
    }
}


