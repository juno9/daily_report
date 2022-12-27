package com.example.myapplication;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Activity_recordinput extends AppCompatActivity {
    EditText 제목입력;

    EditText 내용입력;
    EditText 집중도입력;
    TextView 시작날짜입력;
    TextView 시작시간입력;
    TextView 종료날짜입력;
    TextView 종료시간입력;
    Button 취소버튼;
    Button 저장버튼;
    PreferenceHelper 프리퍼런스헬퍼;
    DatePickerDialog 날짜선택다이얼로그;
    Dialog 시간선택다이얼로그;
    Date 선택된시작날짜시간date;
    Date 선택된종료날짜시간date;
    Date 오늘날짜시간;
    Date 시작날짜시간;
    Date 종료날짜시간;
    String url;
    SimpleDateFormat 넣을값데이트바꿀형식;

    //클래스 내에서 쓰일 데이터들의 타입을 지정하고 메모리를 확보.


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordinput);
        제목입력 = findViewById(R.id.입력란_제목);
        시작시간입력 = findViewById(R.id.기록입력란_시작시간);
        시작날짜입력 = findViewById(R.id.기록입력란_시작날짜);
        종료시간입력 = findViewById(R.id.기록입력란_종료시간);
//        종료날짜입력 = findViewById(R.id.기록입력란_종료날짜);
        내용입력 = findViewById(R.id.입력란_내용);
//        집중도입력 = findViewById(R.id.입력란_집중도);
        취소버튼 = findViewById(R.id.취소버튼);
        저장버튼 = findViewById(R.id.저장버튼);
        //레이아웃의 뷰와 클래스 파일 내의 요소들을 연결해 줌
        Log.i("", "");
        Intent intent = getIntent();
        String 받은시간스트링 = intent.getStringExtra("보낸시간");
        String 받은날짜스트링 = intent.getStringExtra("보낸날짜");
        SimpleDateFormat 받은날짜스트링데이트변환용 = new SimpleDateFormat("yyyy-MM-ddkk:mm:00");
        try {
            String 받은날짜시간스트링=받은날짜스트링+받은시간스트링;
            오늘날짜시간 = 받은날짜스트링데이트변환용.parse(받은날짜시간스트링);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        넣을값데이트바꿀형식 = new SimpleDateFormat("yyyy년 MM월 dd일 (EE)a KK시 mm분");
        프리퍼런스헬퍼 = new PreferenceHelper(this);
        //쉐어드프리퍼런스헬퍼 객체를 프리퍼런스헬퍼라는 객체에 할당함.
        String email = 프리퍼런스헬퍼.getUser_email();
        //스트링 변수를 선언하고 프리퍼런스헬퍼 객체에의 메소드를 실행함. 쉐어드 프리퍼런스 내에 저장되어 있는 유저 메일을 받아와 할당함.
        Log.i("풋레코드 액티비티", "프리퍼런스 내 이메일: " + email);


        //현재시간을 date객체로 만들어줌, 기록 액티비티에 진입하고 나면 최초로 설정되어 있는 시간은 오늘 날짜, 현재 시간으로 세팅
        //심플 데이터 포맷은 Date객체를 받아서 원하는 형태로 표시해 준다


        SimpleDateFormat 표시날짜형식 = new SimpleDateFormat("yyyy년 MM월 dd일 (EE)");//표시해 줄 날짜형식
        SimpleDateFormat 표시시간형식 = new SimpleDateFormat("a KK시 mm분");//표시해 줄 시간 형식


        String 날짜스트링 = 표시날짜형식.format(오늘날짜시간);//지금 시간을 기준으로 표시해줄 형식에 따라 년도, 월, 일을 만들어 줌.
        String 시간스트링 = 표시시간형식.format(오늘날짜시간);//지금 시간을 기준으로 표시해줄 형식에 따라 시, 분, 오전오후 표시해줌.
        시작날짜입력.setText(날짜스트링);
        시작시간입력.setText(시간스트링);

//        종료날짜입력.setText(날짜스트링);
        종료시간입력.setText(시간스트링);

        //저장할 날짜는 다 받은 다음에 설정해햐 한다.

        시작날짜입력.setOnClickListener(new View.OnClickListener() {//이 기록이 언제부터 시작하는지를 선택
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int 년 = cal.get(Calendar.YEAR);
                int 월 = cal.get(Calendar.MONTH);
                int 일 = cal.get(Calendar.DAY_OF_MONTH);

                날짜선택다이얼로그 = new DatePickerDialog(Activity_recordinput.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int 년도, int 월자, int 일자) {
                        월자 = (월자 + 1);
                        //여기서 받은 년도와 날짜를 반영하여 Date객체를 만들자
                        String 선택된날짜스트링값 = 년도 + "-" + 월자 + "-" + 일자;
                        SimpleDateFormat 선택된날짜date변환용 = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date 받은Date = 선택된날짜date변환용.parse(선택된날짜스트링값);
//                            Date 종료날짜=표시날짜형식.parse(종료날짜입력.getText().toString());



                            SimpleDateFormat date에서스트링변환용 = new SimpleDateFormat("yyyy년 MM월 dd일 (EE)");
                            String 받은date스트링변환 = date에서스트링변환용.format(받은Date);
//                            if(받은Date.after(종료날짜)){//만약 선택한 시작 날짜가 종료날짜보다 이후라면
                            시작날짜입력.setText(받은date스트링변환);
//                                종료날짜입력.setText(받은date스트링변환);
//                            }else{
//                                시작날짜입력.setText(받은date스트링변환);
//                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, 년, 월, 일);
                날짜선택다이얼로그.show();
            }
        });
        //터치하면 달력 데이트 피커

//        종료날짜입력.setOnClickListener(new View.OnClickListener() {
//            //여기는 종료날짜를 선택하는 곳이다. 종료날짜를 시작날짜보다 이전으로 설정하면 시작날짜도 따라 바뀌어야 하고
//            //종료날짜가 시작날짜보
//            @Override
//            public void onClick(View view) {
//                Calendar cal = Calendar.getInstance();
//                int 년 = cal.get(Calendar.YEAR);
//                int 월 = cal.get(Calendar.MONTH);
//                int 일 = cal.get(Calendar.DAY_OF_MONTH);
//
//                날짜선택다이얼로그 = new DatePickerDialog(Recordinput_Activity.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int 년도, int 월자, int 일자) {
//                        월자 = (월자 + 1);
//                        //여기서 받은 년도와 날짜를 반영하여 Date객체를 만들자
//                        String 선택된날짜스트링값 = 년도 + "-" + 월자 + "-" + 일자;
//                        SimpleDateFormat 선택된날짜date변환용 = new SimpleDateFormat("yyyy-MM-dd");
//                        try {
//                            Date 받은Date = 선택된날짜date변환용.parse(선택된날짜스트링값);
//                            Date 시작날짜=표시날짜형식.parse(시작날짜입력.getText().toString());
//                            SimpleDateFormat date에서스트링변환용 = new SimpleDateFormat("yyyy년 MM월 dd일 (EE)");
//                            String 받은date스트링변환 = date에서스트링변환용.format(받은Date);
//
////                            if(받은Date.after(시작날짜)){//선택한 종료날짜가 시작날짜보다 이후라면
////                                종료날짜입력.setText(받은date스트링변환);
////                            }else{
//                                시작날짜입력.setText(받은date스트링변환);
////                                종료날짜입력.setText(받은date스트링변환);
////                            }
//
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, 년, 월, 일);
//                날짜선택다이얼로그.show();
//            }
//        });
//        //터치하면 달력 데이트 피커

        시작시간입력.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int 시 = cal.get(Calendar.HOUR_OF_DAY);
                int 분 = cal.get(Calendar.MINUTE);


                시간선택다이얼로그 = new TimePickerDialog(Activity_recordinput.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int 시간, int 분표시) {

                        if (시간 > 12) {
                            시간 -= 12;
                            시작시간입력.setText("오후 0" + 시간 + "시 " + 분표시 + "분");

                            String 시작시간스트링=시작날짜입력.getText().toString()+시작시간입력.getText().toString();
                            String 종료시간스트링=시작날짜입력.getText().toString()+종료시간입력.getText().toString();
                            Log.i("시작시간",시작시간스트링);
                            Log.i("종료시간",종료시간스트링);
                            try {
                                시작날짜시간=넣을값데이트바꿀형식.parse(시작시간스트링);
                                종료날짜시간=넣을값데이트바꿀형식.parse(종료시간스트링);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (시작날짜시간.after(종료날짜시간)) {
                                if(시간<10) {
                                    시작시간입력.setText("오후 0" + 시간 + "시 " + 분표시 + "분");
                                    종료시간입력.setText("오후 0" + 시간 + "시 " + 분표시 + "분");
                                }else{
                                    시작시간입력.setText("오후 " + 시간 + "시 " + 분표시 + "분");
                                    종료시간입력.setText("오후 " + 시간 + "시 " + 분표시 + "분");
                                }
                            } else {
                                if(시간<10) {
                                    시작시간입력.setText("오후 0" + 시간 + "시 " + 분표시 + "분");
                                }else{
                                    시작시간입력.setText("오후 " + 시간 + "시 " + 분표시 + "분");
                                }
                            }

                        }
                        else {
                            시작시간입력.setText("오전 " + 시간 + "시 " + 분표시 + "분");
                            종료시간입력.setText("오전 " + 시간 + "시 " + 분표시 + "분");
                            String 시작시간스트링=시작날짜입력.getText().toString()+시작시간입력.getText().toString();
                            String 종료시간스트링=시작날짜입력.getText().toString()+종료시간입력.getText().toString();
                            Log.i("시작시간",시작시간스트링);
                            Log.i("종료시간",종료시간스트링);
                            try {
                                시작날짜시간=넣을값데이트바꿀형식.parse(시작시간스트링);
                                종료날짜시간=넣을값데이트바꿀형식.parse(종료시간스트링);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (시작날짜시간.after(종료날짜시간)) {
                                if(시간<10) {
                                    시작시간입력.setText("오후 0" + 시간 + "시 " + 분표시 + "분");
                                    종료시간입력.setText("오후 0" + 시간 + "시 " + 분표시 + "분");
                                }else{
                                    시작시간입력.setText("오후 " + 시간 + "시 " + 분표시 + "분");
                                    종료시간입력.setText("오후 " + 시간 + "시 " + 분표시 + "분");
                                }
                            } else {
                                if(시간<10) {
                                    시작시간입력.setText("오후 0" + 시간 + "시 " + 분표시 + "분");
                                }else{
                                    시작시간입력.setText("오후 " + 시간 + "시 " + 분표시 + "분");
                                }
                            }
                        }
                    }
                }, 시, 분, false);

                시간선택다이얼로그.show();
            }
        });//시작시간입력 온클릭리스너-시작시간을 선택하면 할 행동을 정의
        종료시간입력.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int 시 = cal.get(Calendar.HOUR_OF_DAY);
                int 분 = cal.get(Calendar.MINUTE);


                시간선택다이얼로그 = new TimePickerDialog(Activity_recordinput.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int 시간, int 분표시) {

                        if (시간 > 12) {
                            시간 -= 12;

                            종료시간입력.setText("오후 0" + 시간 + "시 " + 분표시 + "분");
                            String 시작시간스트링=시작날짜입력.getText().toString()+시작시간입력.getText().toString();
                            String 종료시간스트링=시작날짜입력.getText().toString()+종료시간입력.getText().toString();
                            Log.i("시작시간",시작시간스트링);
                            Log.i("종료시간",종료시간스트링);
                            try {
                                시작날짜시간=넣을값데이트바꿀형식.parse(시작시간스트링);
                                종료날짜시간=넣을값데이트바꿀형식.parse(종료시간스트링);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (종료날짜시간.after(시작날짜시간)) {//종료시간이 시작시간 이후면
                                if(시간<10) {
                                    종료시간입력.setText("오후 0" + 시간 + "시 " + 분표시 + "분");
                                }else{
                                    종료시간입력.setText("오후 " + 시간 + "시 " + 분표시 + "분");
                                }
                            } else {
                                if(시간<10) {
                                    시작시간입력.setText("오후 0" + 시간 + "시 " + 분표시 + "분");
                                    종료시간입력.setText("오후 0" + 시간 + "시 " + 분표시 + "분");
                                }else{
                                    시작시간입력.setText("오후 " + 시간 + "시 " + 분표시 + "분");
                                    종료시간입력.setText("오후 " + 시간 + "시 " + 분표시 + "분");
                                }
                            }

                        }
                        else {
                            시작시간입력.setText("오전 " + 시간 + "시 " + 분표시 + "분");
                            종료시간입력.setText("오전 " + 시간 + "시 " + 분표시 + "분");
                            String 시작시간스트링=시작날짜입력.getText().toString()+시작시간입력.getText().toString();
                            String 종료시간스트링=시작날짜입력.getText().toString()+종료시간입력.getText().toString();
                            Log.i("시작시간",시작시간스트링);
                            Log.i("종료시간",종료시간스트링);
                            try {
                                시작날짜시간=넣을값데이트바꿀형식.parse(시작시간스트링);
                                종료날짜시간=넣을값데이트바꿀형식.parse(종료시간스트링);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (종료날짜시간.after(시작날짜시간)) {//종료시간이 시작시간 이후면
                                if(시간<10) {
                                    종료시간입력.setText("오후 0" + 시간 + "시 " + 분표시 + "분");
                                }else{
                                    종료시간입력.setText("오후 " + 시간 + "시 " + 분표시 + "분");
                                }
                            } else {
                                if(시간<10) {
                                    시작시간입력.setText("오후 0" + 시간 + "시 " + 분표시 + "분");
                                    종료시간입력.setText("오후 0" + 시간 + "시 " + 분표시 + "분");
                                }else{
                                    시작시간입력.setText("오후 " + 시간 + "시 " + 분표시 + "분");
                                    종료시간입력.setText("오후 " + 시간 + "시 " + 분표시 + "분");
                                }
                            }
                        }
                    }
                }, 시, 분, false);

                시간선택다이얼로그.show();
            }
        });
        //터치하면 시간 타임피커


        저장버튼.setText("저장");
        저장버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("저장 버튼 클릭", "클릭됨");

                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                //연결여부, 연결된 인터넷 경로를 받아온다(wifi, lte등)
                String 상태 = String.valueOf(status);
                Log.i("인터넷상태", 상태);

                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    //요청큐 생성

                    url = "http://192.168.219.157/put_record.php";

                    String 시작날짜시간합치기 = 시작날짜입력.getText().toString() + 시작시간입력.getText().toString();
                    Log.i("시작날짜시간 스트링으로 일단 합치기", 시작날짜시간합치기);
                    String 종료날짜시간합치기 = 시작날짜입력.getText().toString() + 종료시간입력.getText().toString();
                    Log.i("종료날짜시간 스트링으로 일단 합치기", 종료날짜시간합치기);
                    //이제 스트링 형식으로 받은 값을 기반으로 Date객체를 만들어야 한다 그러기 전에 dateformat을 먼저 선정해야 한다.


                    try {
                        선택된시작날짜시간date = 넣을값데이트바꿀형식.parse(시작날짜시간합치기);
                        Log.i("시작날짜시간으로 만든 Date객체", 선택된시작날짜시간date.toString());
                        선택된종료날짜시간date = 넣을값데이트바꿀형식.parse(종료날짜시간합치기);
                        Log.i("종료날짜시간으로 만든 Date객체", 선택된종료날짜시간date.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat DB넣을날짜형식 = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat DB넣을시간형식 = new SimpleDateFormat("kk:mm:00");
                    String 보낼시작날짜 = DB넣을날짜형식.format(선택된시작날짜시간date);
                    String 보낼시작시간 = DB넣을시간형식.format(선택된시작날짜시간date);
                    String 보낼종료날짜 = DB넣을날짜형식.format(선택된종료날짜시간date);
                    String 보낼종료시간 = DB넣을시간형식.format(선택된종료날짜시간date);


                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.i("응답", response);
                                    if (response.equals("1")) {
                                        Toast.makeText(getApplicationContext(), "기록이 저장되었습니다", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else if (response.equals("2")) {
                                        Toast.makeText(getApplicationContext(), "기록을 저장하지 못했습니다", Toast.LENGTH_LONG).show();

                                    } else if (response.equals("3")) {
                                        Toast.makeText(getApplicationContext(), "수정이 완료되었습니다", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else if (response.equals("4")) {
                                        Toast.makeText(getApplicationContext(), "기록을 수정하는중에 오류가 발생하였습니다", Toast.LENGTH_LONG).show();

                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String 에러내용 = String.valueOf(error);


                        }
                    }) {
                        // 포스트 파라미터 넣기
                        @Override
                        protected Map getParams() {
                            Map params = new HashMap();

                            params.put("user_email", 프리퍼런스헬퍼.getUser_email());
                            Log.i("user_email", 프리퍼런스헬퍼.getUser_email());
                            params.put("start_date", 보낼시작날짜);//시작날짜-데이트피커에서 선택하고 선택된 날짜를 날짜 텍스트뷰에 설정하면 그 값을 스트링으로 받아와 넣어준다.
                            Log.i("start_date", 보낼시작날짜);
                            params.put("start_time", 보낼시작시간);
                            Log.i("start_time", 보낼시작시간);
                            params.put("end_date", 보낼종료날짜);//시작시간-타임피커에서 시간을 선택된 시간을 텍스트뷰에 설정하면 그 값을 스트링으로 받아와 넣어준다.
                            Log.i("end_date", 보낼종료날짜);
                            params.put("end_time", 보낼종료시간);
                            Log.i("end_time", 보낼종료시간);
                            params.put("title", 제목입력.getText().toString());
                            Log.i("보내는 title", 제목입력.getText().toString());
                            params.put("contents", 내용입력.getText().toString());
                            Log.i("보내는 contents", 제목입력.getText().toString());
                            params.put("focus", "2");


                            return params;
                        }
                    };
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);

                }//온클릭 메소드
            }
        });
        취소버튼.setText("취소");
        취소버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}