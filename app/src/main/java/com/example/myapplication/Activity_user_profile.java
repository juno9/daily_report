package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Activity_user_profile extends AppCompatActivity {
    ArrayList<Item_day> 날짜목록 = new ArrayList<>();
    ArrayList<Item_record> 기록리스트 = new ArrayList<>();
    ImageView 뒤로가기이미지뷰;
    TextView 상단이름표시텍스트뷰;
    TextView 프로필이름표시텍스트뷰;
    TextView 자기소개텍스트뷰;
    TextView 년월표시텍스트뷰;
    Button 메시지보내기버튼;
    Button 팔로우버튼;
    RecyclerView 주표시리사이클러뷰;
    RecyclerView 기록표시리사이클러뷰;
    Adapter_record 기록표시어댑터;
    Adapter_week 주표시어댑터;
    PreferenceHelper 프리퍼런스헬퍼;
    Item_user 유저;
    String 유저이메일;
    String 합친날짜스트링;
    Date date;
    SimpleDateFormat 날짜형식 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat 날짜비교용일자만 = new SimpleDateFormat("dd");
    SimpleDateFormat 월표시형식 = new SimpleDateFormat("yyyy년 MM월");
    String 프로필주인유저이메일;
    String 로그인한유저이메일;
    boolean following;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        뒤로가기이미지뷰 = findViewById(R.id.이미지뷰_뒤로가기);
        상단이름표시텍스트뷰 = findViewById(R.id.텍스트뷰_이름);
        프로필이름표시텍스트뷰 = findViewById(R.id.텍스트뷰_이름2);
        자기소개텍스트뷰 = findViewById(R.id.텍스트뷰_자기소개);
        년월표시텍스트뷰 = findViewById(R.id.텍스트뷰_년월표시);
        메시지보내기버튼 = findViewById(R.id.버튼_메시지보내기);
        팔로우버튼 = findViewById(R.id.버튼_팔로우);
        주표시리사이클러뷰 = findViewById(R.id.리사이클러뷰_1주일표시);
        기록표시리사이클러뷰 = findViewById(R.id.리사이클러뷰_기록표시);
        Intent intent = getIntent();
        프리퍼런스헬퍼 = new PreferenceHelper(getApplicationContext());
        date = new Date();
        프로필주인유저이메일 = intent.getStringExtra("user_email");
        로그인한유저이메일 = 프리퍼런스헬퍼.getUser_email();

        팔로우버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (팔로우버튼.getText().toString().equals("팔로우")) {//팔로우 안하고 있으면
                    follow();
                } else {//팔로우 하고 있으면
                    unfollow();
                }
            }
        });

        기록표시어댑터 = new Adapter_record(기록리스트);
        주표시어댑터 = new Adapter_week(날짜목록);

        주표시리사이클러뷰.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        기록표시리사이클러뷰.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        뒤로가기이미지뷰.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        주표시리사이클러뷰.setAdapter(주표시어댑터);
        주표시어댑터.setOnItemClickListener(new Adapter_week.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {//날짜아이템 하나 선택하면 이 날을 기준으로 Date객체를 만들고 전역변수인 date에 연결해주면 된다.
                날짜목록.get(pos).setChecked(true);
                for (int i = 0; i < 7; i++) {
                    if (i == pos) {
                        날짜목록.get(i).setChecked(true);
                        String 년도 = 날짜목록.get(i).getDayyear();
                        String 선택한월자 = 날짜목록.get(i).getDaymonth();
                        String 선택한일자 = 날짜목록.get(i).getDaynum();
                        합친날짜스트링 = 년도 + "-" + 선택한월자 + "-" + 선택한일자;
                        SimpleDateFormat 날짜형식 = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            date = 날짜형식.parse(합친날짜스트링);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        get_record(date);
                    } else {
                        날짜목록.get(i).setChecked(false);
                    }
                    주표시어댑터.notifyItemChanged(i);
                }

            }
        });
        기록표시리사이클러뷰.setAdapter(기록표시어댑터);
        유저이메일 = intent.getStringExtra("user_email");

        String 년도월표시 = 월표시형식.format(date);
        Log.i("년도월표시", 년도월표시);
        년월표시텍스트뷰.setText(년도월표시);
        Log.i("년도월표시", 년월표시텍스트뷰.getText().toString());
        get_userdata(유저이메일);
        get_record(date);
        getweek(date);
        get_followdata();

        합친날짜스트링 = 날짜형식.format(date);
        String 일자 = 날짜비교용일자만.format(date);//오늘날짜 일자만
        for (int i = 0; i < 7; i++) {
            if (날짜목록.get(i).getDaynum().equals(일자)) {
                날짜목록.get(i).setChecked(true);
            } else {
                날짜목록.get(i).setChecked(false);
            }
            주표시어댑터.notifyItemChanged(i);
        }

    }

    public void get_userdata(String email) {

        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        //연결여부, 연결된 인터넷 경로를 받아온다(wifi, lte등)
        String 상태 = String.valueOf(status);
        Log.i("인터넷상태", 상태);
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
            Log.i("조건문 진입", "진입");

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            Log.i("큐 생성", "큐 생성");
            String url = "http://192.168.219.157/get_userdata.php";
            Log.i("url 생성", "유알엘생성");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Log.i("응답", response);


                            try {
                                JSONObject 제이슨객체 = new JSONObject(response);//data:{"기록1,기록2,기록3"}
                                Log.i("제이슨객체", 제이슨객체.toString());
                                String data = 제이슨객체.getString("data");
                                Log.i("제이슨 객체 내 data", data);
                                JSONArray 제이슨어레이 = new JSONArray(data);
                                Log.i("제이슨어레이", 제이슨어레이.toString());
                                int 어레이길이 = 제이슨어레이.length();
                                Log.i("제이슨어레이 길이", String.valueOf(어레이길이));

                                for (int i = 0; i < 어레이길이; i++) {
                                    String 제이슨아이템 = 제이슨어레이.get(i).toString();//첫번째 기록 값을 스트링으로 받는다
                                    Log.i("제이슨어레이 아이템", 제이슨아이템);
                                    JSONObject 아이템제이슨 = new JSONObject(제이슨아이템);
                                    Log.i("제이슨 아이템" + (i + 1) + "번째: ", 제이슨아이템);
                                    String 유저메일 = 아이템제이슨.getString("user_email");
                                    Log.i("유저메일", 유저메일);
                                    String 유저이름 = 아이템제이슨.getString("user_name");
                                    Log.i("유저이름", 유저이름);
                                    String 프로필이미지 = 아이템제이슨.getString("profile_image");
                                    Log.i("프로필이미지", 프로필이미지);
                                    상단이름표시텍스트뷰.setText(유저이름);
                                    프로필이름표시텍스트뷰.setText(유저이름);


                                }
                            } catch (JSONException e) {

                                e.printStackTrace();
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
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
        }


    }

    public void getweek(Date date) {
        //매개 변수로 오늘 날짜 date를 받음-오늘을 넣으면 오늘이 속한 주의 날짜들을 얻어옴, 다른 날을입력하면 다른 주 날짜도 쉽게 구할 수 있게 만드는 것을 목표로 함
        SimpleDateFormat dayonly = new SimpleDateFormat("dd");
        //어떤 형식으로 날짜를 받을지 정함.
        Calendar cal = Calendar.getInstance(Locale.KOREA);//캘린더 객체 생성
        cal.setTime(date);//매개변수로 받은 날짜를 캘린더의 시간으로 설정
        Log.i("세팅된 시간", String.valueOf(date));
        cal.setFirstDayOfWeek(cal.MONDAY);
        ArrayList<Item_day> Day_item_array = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            cal.add(Calendar.DAY_OF_MONTH, (2 - cal.get(Calendar.DAY_OF_WEEK) + i));//해당 주차의 첫날 세팅
            String year = String.valueOf(cal.get(Calendar.YEAR));
            Log.i("year: ", year);
            int 월자보정 = cal.get(Calendar.MONTH) + 1;
            String month = String.valueOf(월자보정);
            Log.i("month: ", month);
            String day = dayonly.format(cal.getTime());
            String dayname = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.NARROW_FORMAT, Locale.KOREAN);//요일 표시 스트링
            Item_day dayitem = new Item_day(year, month, day, dayname, false);

            if (날짜목록.size() < 7)//서버에서 받아온 데이터보다 지금 기록리스트가 짧다면 같으면 그냥 돌고
            {
                날짜목록.add(dayitem);
                주표시어댑터.notifyItemChanged(i, dayitem);
            } else {
                날짜목록.set(i, dayitem);
                주표시어댑터.notifyItemChanged(i, dayitem);
            }


            Log.i("날짜목록에 더함", 날짜목록.get(i).getDaynum());
            주표시어댑터.notifyItemChanged(i);
        }
        Log.i("세팅된 시간기준 배열", String.valueOf(Day_item_array));
    }//입력받은 Date 객체를 기반으로 그 Date가 속한 주의 Day_Item을 ArrayList로 반환함.

    public void get_record(Date 받은date객체) {
        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        //연결여부, 연결된 인터넷 경로를 받아온다(wifi, lte등)
        String 상태 = String.valueOf(status);
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            //요청큐 생성
            String url = "http://192.168.219.157/get_record.php";
            //url 스트링값 생성
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(String response) {
                            Log.i("응답", response);
                            기록리스트.clear();
                            if (response.equals("기록없음")) {
                                기록표시어댑터.notifyItemChanged(0);
                            } else {
                                ArrayList<Item_record> 불러온기록어레이 = new ArrayList<>();
                                try {
                                    JSONObject 제이슨객체 = new JSONObject(response);//data:{"기록1,기록2,기록3"}
                                    Log.i("제이슨객체", 제이슨객체.toString());
                                    String data = 제이슨객체.getString("data");
                                    Log.i("제이슨 객체 내 data", data);
                                    JSONArray 제이슨어레이 = new JSONArray(data);
                                    Log.i("제이슨어레이", 제이슨어레이.toString());
                                    int 어레이길이 = 제이슨어레이.length();
                                    Log.i("제이슨어레이 길이", String.valueOf(어레이길이));

                                    for (int i = 0; i < 어레이길이; i++) {
                                        String 제이슨아이템 = 제이슨어레이.get(i).toString();//첫번째 기록 값을 스트링으로 받는다
                                        Log.i("제이슨어레이 아이템", 제이슨아이템);
                                        JSONObject 아이템제이슨 = new JSONObject(제이슨아이템);
                                        Log.i("제이슨 아이템" + (i + 1) + "번째: ", 제이슨아이템);
                                        String 유저메일 = 아이템제이슨.getString("user_email");
                                        Log.i("유저메일", 유저메일);
                                        String 시작날짜 = 아이템제이슨.getString("start_date");
                                        Log.i("시작날짜", 시작날짜);
                                        String 시작시간 = 아이템제이슨.getString("start_time");
                                        Log.i("시작시간", 시작시간);
                                        String 종료날짜 = 아이템제이슨.getString("end_date");
                                        Log.i("종료날짜", 종료날짜);
                                        String 종료시간 = 아이템제이슨.getString("end_time");
                                        Log.i("종료시간", 종료시간);
                                        String 제목 = 아이템제이슨.getString("title");
                                        Log.i("제목", 제목);
                                        String 내용 = 아이템제이슨.getString("contents");
                                        Log.i("내용", 내용);

                                        String 키값 = 아이템제이슨.getString("record_seq");
                                        Log.i("키값", 키값);
                                        Item_record 기록아이템 = new Item_record(유저메일, 시작날짜, 시작시간, 종료날짜, 종료시간, 제목, 내용, 키값);
                                        //기록 아이템은 만들어 줌


                                        if (기록리스트.size() < 어레이길이) {
                                            기록리스트.add(i, 기록아이템);
                                            Log.i("기록 들어갔나?" + i, 기록리스트.toString());
//                                            record_adapter.notifyItemInserted(i);


                                            //리스트에 넣어만 주고 쓰지를 않는다.

                                        } else {
                                            기록리스트.set(i, 기록아이템);
//                                            record_adapter.notifyItemChanged(i);
                                        }

                                    }//제이슨 파싱하는 반복문
                                    기록표시어댑터.setarraylist(기록리스트);
                                    기록표시어댑터.notifyDataSetChanged();
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                }

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
                    //여기서 기준이 되는 날짜를 받아야 한다.
                    SimpleDateFormat DB저장날짜형식 = new SimpleDateFormat("yyyy-MM-dd");//db에 저장된 날짜시간 형식
                    String 선택된날짜스트링 = DB저장날짜형식.format(받은date객체);
                    Log.i("db에 요청하는 날짜", 선택된날짜스트링);
                    Map params = new HashMap();
                    params.put("user_email", 유저이메일);
                    Log.i("기록요청할 유저의 이메일", 유저이메일);
                    params.put("start_date", 선택된날짜스트링);
                    return params;
                }
            };
            queue.add(stringRequest);

        }

    }

    public void get_followdata() {
        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        //연결여부, 연결된 인터넷 경로를 받아온다(wifi, lte등)
        String 상태 = String.valueOf(status);
        Log.i("인터넷상태", 상태);
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
            Log.i("조건문 진입", "진입");
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            Log.i("큐 생성", "큐 생성");
            String url = "http://192.168.219.157/get_followdata.php";
            Log.i("url 생성", "유알엘생성");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("get_followdata 응답", response);
                            Handler handler = new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        팔로우버튼.setText("팔로잉");
                                        Log.i("get_followdata", "팔로우버튼 텍스트 바꿈-팔로우");
                                    } else {
                                        팔로우버튼.setText("팔로우");
                                        Log.i("get_followdata", "팔로우버튼 텍스트 바꿈-팔로잉");
                                    }
                                }
                            };//핸들러는 스레드에서 받은 메시지에 따라 뷰에 이미지를 그려줌
                            if (response.equals("following")) {//내가지금
                                Message msg = handler.obtainMessage();//핸들러로 보낼 메시지 객체 생성
                                msg.what = 1;
                                Log.i("핸들러로 보내는 메시지", String.valueOf(msg.what));
                                handler.sendMessage(msg);
                            } else if (response.equals("not_following")) {
                                Message msg = handler.obtainMessage();//핸들러로 보낼 메시지 객체 생성
                                msg.what = 2;
                                Log.i("핸들러로 보내는 메시지", String.valueOf(msg.what));
                                handler.sendMessage(msg);
                            } else {
                                Log.i("get_followdata", "이상한값");
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
                    params.put("user_email", 로그인한유저이메일);
                    params.put("profile_user_email", 프로필주인유저이메일);
                    return params;
                }
            };
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        } else {
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
        }


    }

    public void follow() {//로그인한 유저가 프로필 유저를 팔로우하는 메소드
        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        //연결여부, 연결된 인터넷 경로를 받아온다(wifi, lte등)
        String 상태 = String.valueOf(status);
        Log.i("인터넷상태", 상태);
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
            Log.i("조건문 진입", "진입");
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            Log.i("큐 생성", "큐 생성");
            String url = "http://192.168.219.157/follow.php";
            Log.i("url 생성", "유알엘생성");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("응답 내용", response);
                            Handler handler = new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        팔로우버튼.setText("팔로잉");
                                        Log.i("팔로우버튼 텍스트 바꿈", "팔로잉");
                                    } else {
                                        팔로우버튼.setText("팔로우");
                                        Log.i("팔로우버튼 텍스트 바꿈", "팔로우");
                                    }
                                }
                            };//핸들러는 스레드에서 받은 메시지에 따라 뷰에 이미지를 그려줌
                            if (response.equals("following")) {
                                Message msg = handler.obtainMessage();//핸들러로 보낼 메시지 객체 생성
                                msg.what = 1;//메시지의 what을 1로 설정
                                Log.i("핸들러로 보내는 메시지", String.valueOf(msg.what));
                                handler.sendMessage(msg);
                            } else if (response.equals("not_following")) {
                                Message msg = handler.obtainMessage();//핸들러로 보낼 메시지 객체 생성
                                msg.what = 2;//메시지의 what을 1로 설정
                                Log.i("핸들러로 보내는 메시지", String.valueOf(msg.what));
                                handler.sendMessage(msg);
                            } else {
                                Log.i("follow", "이상한값");
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
                    params.put("user_email", 로그인한유저이메일);
                    params.put("profile_user_email", 프로필주인유저이메일);
                    return params;
                }
            };
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        } else {
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    public void unfollow() {

        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        //연결여부, 연결된 인터넷 경로를 받아온다(wifi, lte등)
        String 상태 = String.valueOf(status);
        Log.i("인터넷상태", 상태);
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
            Log.i("조건문 진입", "진입");
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            Log.i("큐 생성", "큐 생성");
            String url = "http://192.168.219.157/unfollow.php";
            Log.i("url 생성", "유알엘생성");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("응답 내용", response);

                            Handler handler = new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        팔로우버튼.setText("팔로우");
                                        Log.i("팔로우버튼 텍스트 바꿈", "팔로우");
                                    } else {
                                        팔로우버튼.setText("팔로잉");
                                        Log.i("팔로우버튼 텍스트 바꿈", "팔로잉");
                                    }
                                }
                            };//핸들러는 스레드에서 받은 메시지에 따라 뷰에 이미지를 그려줌
                            if (response.equals("not_following")) {
                                Message msg = handler.obtainMessage();//핸들러로 보낼 메시지 객체 생성
                                msg.what = 1;//메시지의 what을 1로 설정
                                handler.sendMessage(msg);
                            } else if (response.equals("following")) {
                                Message msg = handler.obtainMessage();//핸들러로 보낼 메시지 객체 생성
                                msg.what = 2;//메시지의 what을 1로 설정
                                handler.sendMessage(msg);
                            } else {
                                Log.i("unfollow", "이상한값");
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
                    params.put("user_email", 로그인한유저이메일);
                    params.put("profile_user_email", 프로필주인유저이메일);
                    return params;
                }
            };
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        } else {
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
        }


    }
}
