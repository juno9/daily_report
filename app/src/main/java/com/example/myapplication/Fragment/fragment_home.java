package com.example.myapplication.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Activity_recordinput;
import com.example.myapplication.Activity_recordupdate;
import com.example.myapplication.Adapter_record;
import com.example.myapplication.Adapter_week;
import com.example.myapplication.Item_day;
import com.example.myapplication.Item_record;
import com.example.myapplication.NetworkStatus;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class fragment_home extends Fragment {
    String ip="192.168.0.5";
    String TAG = "홈프래그먼트";
    ViewGroup 뷰;
    RecyclerView week_recycler;//주 날짜 표시 리사이클러뷰 선언
    Adapter_week adapter_week;
    TextView 오늘날짜표시텍스트;
    ArrayList<Item_day> 날짜목록 = new ArrayList<>();
    RecyclerView record_recycler;//기록 표시 리사이클러뷰 선언
    Adapter_record _adapterRecord;//빈거 하나 만들어 둠
    ArrayList<Item_record> 기록리스트 = new ArrayList<>();
    String user_email;//스트링으로 로그인 한 유저의 이메일 값 선언
    FloatingActionButton 기록추가버튼;
    ViewPager2 뷰페이저2;
    TextView 월표시텍스트뷰;
    DatePickerDialog 날짜선택다이얼로그;
    String 합친날짜스트링;
    SimpleDateFormat 날짜형식 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat 날짜비교용일자만 = new SimpleDateFormat("dd");
    SimpleDateFormat 월표시형식 = new SimpleDateFormat("yyyy년 MM월");

    int 임의값 = -1;
    Date date;//오늘 날짜 기준 Date객체 생성



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        뷰 = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        week_recycler = 뷰.findViewById(R.id.weekrecyclerview);//자바 파일의 객체와 레이아웃의 뷰를 연결
        record_recycler = 뷰.findViewById(R.id.recordrecyclerview);//자바 파일의 객체와 레이아웃의 뷰를 연결
        월표시텍스트뷰 = 뷰.findViewById(R.id.년월텍스트뷰);
        기록추가버튼 = 뷰.findViewById(R.id.기록추가버튼);
        오늘날짜표시텍스트 = 뷰.findViewById(R.id.오늘날짜표시);

        Bundle bundle = getArguments();
        if (bundle != null) {
            user_email = bundle.getString("user_email");
        }//액티비티에서 전달 받은 유저의 이메일 값.

        Calendar cal = Calendar.getInstance();
        int 년 = cal.get(Calendar.YEAR);
        int 월 = cal.get(Calendar.MONTH);
        int 일 = cal.get(Calendar.DAY_OF_MONTH);

        오늘날짜표시텍스트.setText(String.valueOf(일));
        오늘날짜표시텍스트.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = new Date();//오늘날짜기준
                SimpleDateFormat 월표시형식 = new SimpleDateFormat("yyyy년 MM월");
                SimpleDateFormat 일자표시형식 = new SimpleDateFormat("dd");
                SimpleDateFormat 날짜형식 = new SimpleDateFormat("yyyy-MM-dd");
                String 일자 = 일자표시형식.format(date);
                합친날짜스트링 = 날짜형식.format(date);
                String 년도월표시 = 월표시형식.format(date);
                월표시텍스트뷰.setText(년도월표시);

                getweek(date);
                get_record(date);
                for (int i = 0; i < 7; i++) {
                    if (날짜목록.get(i).getDaynum().equals(일자)) {
                        날짜목록.get(i).setChecked(true);

                    } else {
                        날짜목록.get(i).setChecked(false);
                    }
                    adapter_week.notifyItemChanged(i);
                }
            }
        });
        Date now = new Date();//오늘 날짜 생성팅
        Log.i("처음 넣어주는 날짜", String.valueOf(now));

        String 년도월표시 = 월표시형식.format(now);
        월표시텍스트뷰.setText(년도월표시);
        월표시텍스트뷰.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                날짜선택다이얼로그.show();
            }
        });


        week_recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        adapter_week = new Adapter_week(날짜목록);
        adapter_week.setOnItemClickListener(new Adapter_week.OnItemClickListener() {
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
                    adapter_week.notifyItemChanged(i);
                }

            }
        });
        getweek(now);
        week_recycler.setAdapter(adapter_week);
        날짜선택다이얼로그 = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int 년도, int 월자, int 일자) {//날짜를 선택하고 나면 어떻게 작동할 것인지
                //여기서 데이트 객체를 만들고 날짜목록 초기화 하고 겟데이트를 만들어 주면 된다.
                월자 = 월자 + 1;//1월을 픽하면 0을 받고 12월을 픽하면 11을 받으니 플러스 1씩 해서 월 숫자를 맞춘다
                String 월자스트링;//변수만 생성, 메모리는 아직 할당하지 않음
                String 일자스트링;//변수만 생성, 메모리는 아직 할당하지 않음
                if (월자 < 10) {
                    월자스트링 = "0" + 월자;
                } else {
                    월자스트링 = String.valueOf(월자);
                }
                if (일자 < 10) {
                    일자스트링 = "0" + 일자;
                } else {
                    일자스트링 = String.valueOf(일자);
                }

                //월자와 일자를 원하는 형식을 갖춘 스트링 값으로 만들어 줌

                String 픽한날짜 = 년도 + "" + 월자스트링 + "" + 일자스트링;
                //픽한 날짜를 스트링으로 만들어 줌-내가 선택한 날짜
                SimpleDateFormat 날짜형식 = new SimpleDateFormat("yyyyMMdd");
                //픽한 날짜를 스트링으로 만들어 준 값을 Date 객체로 만들어주기 위한 데이트포맷을 선언함
                try {
                    Date 날짜객체 = 날짜형식.parse(픽한날짜);
                    //픽한 날짜를 기준으로 데이트 객체를 만들어줌
                    SimpleDateFormat dayonly = new SimpleDateFormat("dd");
                    //픽한 날짜로 만든 데이트 객체에서 일자만 빼내기 위한 형식을 선언함
                    String 픽한날짜의일자 = dayonly.format(날짜객체);
                    Calendar cal = Calendar.getInstance(Locale.KOREA);
                    //캘린더 객체 생성
                    cal.setTime(날짜객체);
                    //캘린더상의 날짜를 내가 픽한 날짜로 만들어 줌
                    cal.setFirstDayOfWeek(cal.MONDAY);
                    //한 주의 첫 날은 월요일부터
//                    ArrayList<Day_Item> Day_item_array = new ArrayList<>();//데이아이템을


                    for (int i = 0; i < 7; i++) {//여기 반복문이 의미하는 바를 모르겠네 찍어보자
                        cal.add(Calendar.DAY_OF_MONTH, (2 - cal.get(Calendar.DAY_OF_WEEK) + i));//해당 주차의 첫날 세팅
                        Log.i("문제의반복문: ", i + "회차");
                        String year = String.valueOf(년도);
                        Log.i("year: ", year);
                        String month = String.valueOf(월자);
                        Log.i("month: ", month);
                        String day = dayonly.format(cal.getTime());

                        Log.i("dayonly: ", day);
                        String dayname = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.NARROW_FORMAT, Locale.KOREAN);//요일 표시 스트링
                        Log.i("year: ", dayname);
                        if (day.equals(픽한날짜의일자)) {//선택해서 받은 날짜랑 그냥 넣어주는 날짜랑 어떻게 판별하지
                            Item_day dayitem = new Item_day(year, month, day, dayname, true);
                            날짜목록.set(i, dayitem);
                            //선택한날짜에 맞는 데이트 객체를 만들어 줘야 한다.
                            //만들고 나면 전역변수에 선언되어 있는 date 변수에 할당해야 한다.
                            date=날짜객체;
                        } else {
                            Item_day dayitem = new Item_day(year, month, day, dayname, false);
                            날짜목록.set(i, dayitem);
                        }
                        get_record(날짜객체);
                        adapter_week.notifyItemChanged(i);

                    }


//                    Log.i("세팅된 시간기준 배열", String.valueOf(Day_item_array));

                    월표시텍스트뷰.setText(년도 + "년 " + 월자스트링 + "월");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }, 년, 월, 일);
        date = new Date();
        //로그인 하고 처음 기록을 불러올때는 오늘을 기준으로 불러온다.

        합친날짜스트링 = 날짜형식.format(date);
        String 일자 = 날짜비교용일자만.format(date);//오늘날짜 일자만
        for (int i = 0; i < 7; i++) {
            if (날짜목록.get(i).getDaynum().equals(일자)) {
                날짜목록.get(i).setChecked(true);
            } else {
                날짜목록.get(i).setChecked(false);
            }
            adapter_week.notifyItemChanged(i);
        }
        get_record(date);
        record_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        _adapterRecord = new Adapter_record(기록리스트);//여기 넣은 내용물을 확인 해봐야 한다.
        _adapterRecord.setOnItemClickListener(new Adapter_record.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Log.i("기록리사이클러뷰 온클릭리스너 작동, 포지션: ", String.valueOf(pos));
                String 시작시간 = 기록리스트.get(pos).get시작시간();
                String 시작날짜 = 기록리스트.get(pos).get시작날짜();
                String 종료시간 = 기록리스트.get(pos).get종료시간();
                String 종료날짜 = 기록리스트.get(pos).get종료날짜();
                String 내용 = 기록리스트.get(pos).get내용();
                String 제목 = 기록리스트.get(pos).get제목();
                String 키값 = 기록리스트.get(pos).get키값();
                Log.i("시작시간 ", 시작시간);
                Log.i("시작날짜 ", 시작날짜);
                Log.i("종료시간", 종료시간);
                Log.i("종료날짜", 종료날짜);
                Log.i("내용", 내용);
                Log.i("제목", 제목);
                //시작시간,시작날짜,종료시간,종료날짜, 내용, 제목,
                Intent intent = new Intent(getActivity(), Activity_recordupdate.class);
                intent.putExtra("시작시간", 시작시간);
                intent.putExtra("시작날짜", 시작날짜);
                intent.putExtra("종료시간", 종료시간);
                intent.putExtra("종료날짜", 종료날짜);
                intent.putExtra("내용", 내용);
                intent.putExtra("제목", 제목);
                intent.putExtra("키값", 키값);
                intent.putExtra("포지션값", pos);
                intent.putExtra("from", "보기");
                startActivity(intent);

            }
        });
        record_recycler.setAdapter(_adapterRecord);
        기록추가버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Activity_recordinput.class);
                SimpleDateFormat 시간만=new SimpleDateFormat("kk:mm:00");
                Date 현재시간용=new Date();
                String 시간=시간만.format(현재시간용);
                합친날짜스트링 = 날짜형식.format(date);
                intent.putExtra("보낸시간",시간);
                intent.putExtra("보낸날짜", 합친날짜스트링);
                startActivity(intent);

            }
        });
        return 뷰;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("생명주기", "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("생명주기", "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("생명주기", "onDetach");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("생명주기", "onViewCreated");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("생명주기", "onStop");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        Log.i("생명주기", "onViewStateRestored");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("생명주기", "onDestroyView");

    }

    @Override
    public void onResume() {
        super.onResume();

        get_record(date);
        Log.i("생명주기", "onResume");

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
                adapter_week.notifyItemChanged(i, dayitem);
            } else {
                날짜목록.set(i, dayitem);
                adapter_week.notifyItemChanged(i, dayitem);
            }


            Log.i("날짜목록에 더함", 날짜목록.get(i).getDaynum());
            adapter_week.notifyItemChanged(i);
        }
        Log.i("세팅된 시간기준 배열", String.valueOf(Day_item_array));
    }//입력받은 Date 객체를 기반으로 그 Date가 속한 주의 Day_Item을 ArrayList로 반환함.


    public void get_record(Date 받은date객체) {

        int status = NetworkStatus.getConnectivityStatus(getActivity());
        //연결여부, 연결된 인터넷 경로를 받아온다(wifi, lte등)
        String 상태 = String.valueOf(status);
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            //요청큐 생성
            String url = "http://"+ip+"/get_record.php";
            //url 스트링값 생성
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(String response) {
                            Log.i("응답", response);
                            기록리스트.clear();
                            _adapterRecord.notifyDataSetChanged();
                            if (response.equals("기록없음")) {
                                _adapterRecord.notifyItemChanged(0);
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
                                            Log.i("기록 들어갔나?"+i,기록리스트.toString());
//                                            record_adapter.notifyItemInserted(i);


                                            //리스트에 넣어만 주고 쓰지를 않는다.

                                        } else {
                                            기록리스트.set(i, 기록아이템);
//                                            record_adapter.notifyItemChanged(i);
                                        }

                                    }//제이슨 파싱하는 반복문
                                    _adapterRecord.setarraylist(기록리스트);
                                    _adapterRecord.notifyDataSetChanged();
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
                    params.put("user_email", user_email);
                    params.put("start_date", 선택된날짜스트링);
                    return params;
                }
            };
            queue.add(stringRequest);

        }

    }


}
