package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Activity_login extends AppCompatActivity {

    EditText 이메일입력;
    EditText 비밀번호입력;
    String TAG = "로그인액티비티";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        이메일입력 = findViewById(R.id.입력란_이메일);
        비밀번호입력 = findViewById(R.id.입력란_비밀번호);
        PreferenceHelper 프리퍼런스헬퍼 = new PreferenceHelper(this);


        Date today = new Date();
        Log.i(TAG,"오늘날짜:"+String.valueOf(today));
        getweek(today);
        Log.i(TAG,"1주일배열:"+getweek(today));
        Calendar cal=Calendar.getInstance();
        cal.set(2022,0,0);//일, 월은 숫자를 1씩 빼야 된다
        Date specific = new Date(cal.getTimeInMillis());
        Log.i(TAG,"특정일자:"+String.valueOf(specific));
        getweek(specific);
        Log.i(TAG,"특정일자 속한 주 배열:"+getweek(specific));




        TextView 비밀번호찾기 = findViewById(R.id.비밀번호찾기);
        비밀번호찾기.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent 비밀번호찾기인텐트 = new Intent(Activity_login.this, Activity_password_reset.class);
                startActivity(비밀번호찾기인텐트);
            }
        });
        TextView 회원가입 = findViewById(R.id.회원가입);
        회원가입.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent 회원가입인텐트 = new Intent(Activity_login.this, Activity_sign_up.class);
                startActivity(회원가입인텐트);
            }
        });
        Button 로그인버튼 = findViewById(R.id.로그인버튼);
        String email = 이메일입력.getText().toString();
        로그인버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//온클릭메소드
                Log.i("회원가입 버튼 클릭", "클릭됨");
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                //연결여부, 연결된 인터넷 경로를 받아온다(wifi, lte등)
                String 상태 = String.valueOf(status);
                Log.i("인터넷상태", 상태);
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                    Log.i("조건문 진입", 상태);
                    // EditText값 예외처리
                    if (이메일입력.getText().toString().trim().length() > 0) {//입력여부 체크
                        if (비밀번호입력.getText().toString().trim().length() > 0) {
                            // Instantiate the RequestQueue.
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            //요청큐 생성
                            String url = "http://192.168.219.157/login.php";
                            //url 스트링값 생성
//                            String url = "http://192.168.0.193/login.php";

                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.i("응답", response);
                                            if (response.equals("로그인 실패")) {//형 변환 이슈
                                                Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                                            } else {
                                                프리퍼런스헬퍼.setLogin(true);
                                                프리퍼런스헬퍼.setUser_email(response);
                                                startActivityC(Activity_home.class);
                                                finish();
                                                Toast.makeText(getApplicationContext(), response + "님 반갑습니다", Toast.LENGTH_SHORT).show();
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
                                    params.put("user_email", 이메일입력.getText().toString().trim());
                                    Log.i("이메일입력값", 이메일입력.getText().toString());
                                    params.put("user_password", 비밀번호입력.getText().toString().trim());
                                    Log.i("비밀번호입력값", 비밀번호입력.getText().toString());
                                    return params;
                                }
                            };
                            // Add the request to the RequestQueue.
                            queue.add(stringRequest);
                        } else {
                            Toast.makeText(getApplicationContext(), "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
                        }
                    } else//입력여부 조건문
                    {
                        Toast.makeText(getApplicationContext(), "이메일 주소를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }//온클릭 메소드
        });


    }

    // 액티비티 전환 함수

    // 인텐트 액티비티 전환함수
    public void startActivityC(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 인텐트 화면전환 하는 함수
    // FLAG_ACTIVITY_CLEAR_TOP = 불러올 액티비티 위에 쌓인 액티비티 지운다.
    public void startActivityflag(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 문자열 인텐트 전달 함수
    public void startActivityString(Class c, String name, String sendString) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.putExtra(name, sendString);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 백스택 지우고 새로 만들어 전달
    public void startActivityNewTask(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }


    public ArrayList<String> getweek(Date date) {
        //매개 변수로 오늘 날짜 date를 받음-오늘을 넣으면 오늘이 속한 주의 날짜들을 얻어옴, 다른 날을입력하면 다른 주 날짜도 쉽게 구할 수 있게 만드는 것을 목표로 함
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        //어떤 형식으로 날짜를 받을지 정함.

        Calendar cal = Calendar.getInstance(Locale.KOREA);//캘린더 객체 생성
        cal.setTime(date);//매개변수로 받은 날짜를 캘린더의 시간으로 설정
        cal.setFirstDayOfWeek(Calendar.MONDAY);//캘린더상 1주의 첫날을 월요일로 지정-우리나라 표준-내가 쓴다
        ArrayList<String> 일주일날짜=new ArrayList<>();
        for(int i=0; i<7; i++){
            cal.add(Calendar.DAY_OF_MONTH, (2-cal.get(Calendar.DAY_OF_WEEK)+i));//해당 주차의 첫날 세팅
            String day = sdf.format(cal.getTime());
            일주일날짜.add(day);
        }
        return 일주일날짜;
    }
}

