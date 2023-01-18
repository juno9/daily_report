package com.example.myapplication;

public class Item_message {

    //종류는 세가지-알림, 상대방, 나, 시간
    //알림은 가운대, 상대방 메시지는 왼쪽에 나의 메시지는 오른쪽에
    //상대가 보낸 메시지에는 이름, 시간,내용,프로필사진 이 나와야 한다.
    //나의 메시지에는 시간,내용이 보여야 한다.
    //알림으로 메시지 시간 읽고 몇시에 보낸건지 이정표 역할을 해 줄 문구가 필요하다.
    //서비스를 적용하여 앱 구동중이 아닐 때 알림을 받을 수 있어야 하고 알림 끄기 기능도 있어야 한다.

    String 유형;//내가 보낸 메시지, 상대가 보낸 메시지, 알림
    String 시간;
    String 메시지내용;
    String 유저이름;
    String 유저프로필url;

    Item_message(String 받은유형, String 받은시간, String 받은메시지내용, String 받은유저이름, String 유저프로필url) {
        this.유형 = 받은유형;
        this.시간 = 받은시간;
        this.메시지내용 = 받은메시지내용;
        this.유저이름 = 받은유저이름;
        this.유저프로필url = 유저프로필url;
    }


}
