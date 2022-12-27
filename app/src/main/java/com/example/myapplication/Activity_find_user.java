package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Activity_find_user extends AppCompatActivity {
    private ArrayList<String> arraylist;
    EditText 검색어입력;
    ListView 검색결과리스트뷰;
    ArrayList<Item_user> 목록 = new ArrayList<Item_user>();
    Adapter_search 유저검색어댑터;
    ImageView 뒤로가기;
    ProgressBar 프로그레스바;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        검색어입력 = findViewById(R.id.입력란_검색어);
        검색결과리스트뷰 = findViewById(R.id.검색결과리스트뷰);
        뒤로가기 = findViewById(R.id.뒤로가기);
//        프로그레스바 = findViewById(R.id.프로그레스바_검색);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        뒤로가기.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        유저검색어댑터 = new Adapter_search(목록, this);
        검색결과리스트뷰.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(i + "번째 아이템 선택됨", String.valueOf(i));

                Intent intent = new Intent(getApplicationContext(), Activity_user_profile.class);
                startActivity(intent);
            }
        });

        검색결과리스트뷰.setAdapter(유저검색어댑터);
        검색어입력.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i("비포텍스트체인지드", "입력됨");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i("온텍스트체인지드", "입력됨");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i("애프러텍스트체인지드", "입력됨");
                if (검색어입력.getText().toString().equals("")) {
                    Log.i("검색어 입력된 값이 없다", "없다면");
                    목록.clear();
                    Log.i("목록 클리어 돌아감", 목록.toString());
                    유저검색어댑터.setarraylist(목록);
                    유저검색어댑터.notifyDataSetChanged();
                } else {
                    Log.i("입력된 값이 있다", "있다면");
                    목록.clear();
                    Log.i("목록 클리어 돌아감", 목록.toString());
                    유저검색어댑터.setarraylist(목록);
                    유저검색어댑터.notifyDataSetChanged();
                    Log.i("검색어입력", "입력됨");
                    int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                    //연결여부, 연결된 인터넷 경로를 받아온다(wifi, lte등)
                    String 상태 = String.valueOf(status);
                    Log.i("인터넷상태", 상태);
                    if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                        Log.i("조건문 진입", "진입");

//                    프로그레스바.setVisibility(View.VISIBLE);
//                    Log.i("프로그래스바 돌아가기시작", "시작");
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        Log.i("큐 생성", "큐 생성");
                        String url = "http://192.168.219.157/search_user.php";
                        Log.i("url 생성", "유알엘생성");
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String response) {
//                                    프로그레스바.setVisibility(View.INVISIBLE);
//                                    Log.i("프로그래스바 멈춤", "멈춤");
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

                                                Item_user 유저데이터 = new Item_user(프로필이미지, 유저이름, 유저메일);
                                                //기록 아이템은 만들어 줌

                                                목록.add(i, 유저데이터);


                                            }//제이슨 파싱하는 반복문
                                            유저검색어댑터.setarraylist(목록);
                                            유저검색어댑터.notifyDataSetChanged();
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
                                params.put("search_word", 검색어입력.getText().toString().trim());
                                Log.i("검색어 전송값 확인용", 검색어입력.getText().toString().trim());
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


        });


    }
//
//    public void search(String charText) {
//
//        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
//        목록.clear();
//
//        // 문자 입력이 없을때는 모든 데이터를 보여준다.
//        if (charText.length() == 0) {
//            목록.addAll(arraylist);
//        }
//        // 문자 입력을 할때..
//        else
//        {
//            // 리스트의 모든 데이터를 검색한다.
//            for(int i = 0;i < arraylist.size(); i++)
//            {
//                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
//                if (arraylist.get(i).toLowerCase().contains(charText))
//                {
//                    // 검색된 데이터를 리스트에 추가한다.
//                    목록.add(arraylist.get(i));
//                }
//            }
//        }
//        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
//        유저검색어댑터.notifyDataSetChanged();
//    }
}

