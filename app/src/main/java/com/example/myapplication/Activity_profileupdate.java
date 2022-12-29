package com.example.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Activity_profileupdate extends AppCompatActivity {

    String TAG = "Activity_profileupdate";
    CircleImageView 프로필이미지;
    ImageView 카메라이미지;
    TextView 저장버튼;
    TextView 이름표시;
    TextView 자기소개표시;
    //    Dialog 이미지다이얼로그;
    PreferenceHelper 프리퍼런스헬퍼;
    String 유저메일;
    Bitmap 비트맵;
    String 인코드이미지스트링;
    Bitmap 비트맵이미지;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileupdate);
        프로필이미지 = findViewById(R.id.이미지뷰_프로필이미지);
        카메라이미지 = findViewById(R.id.이미지뷰_사진아이콘);
        저장버튼 = findViewById(R.id.저장하기_텍스트);
        이름표시 = findViewById(R.id.닉네임_입력);
        자기소개표시 = findViewById(R.id.자기소개_입력);

        프리퍼런스헬퍼 = new PreferenceHelper(getApplicationContext());
        유저메일 = 프리퍼런스헬퍼.getUser_email();
//        이미지다이얼로그 = new Dialog(this);
//        이미지다이얼로그.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        이미지다이얼로그.setContentView(R.layout.dialog_profileimage);


        getuserdata();
        //다이얼로그의 구성요소들이 동작할 코드작성

        카메라이미지.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });
        프로필이미지.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });


        저장버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                    //연결여부, 연결된 인터넷 경로를 받아온다(wifi, lte등)
                    String 상태 = String.valueOf(status);
                    Log.i("인터넷상태", 상태);
                    if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                        Log.i("조건문 진입", "진입");
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        Log.i("큐 생성", "큐 생성");
                        String url = "http://192.168.219.157/update_userprofile.php";
                        Log.i("url 생성", "유알엘생성");
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.i("응답내용", response);
                                        if (response.equals("File Uploaded Successfully")) {
                                            finish();
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
                                params.put("user_email", 유저메일);
                                params.put("user_name", 이름표시.getText().toString());
                                params.put("user_selfintro", 자기소개표시.getText().toString());
                                params.put("profile_image", 인코드이미지스트링);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Uri filePath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                비트맵 = BitmapFactory.decodeStream(inputStream);
                프로필이미지.setImageBitmap(비트맵);
                encodeBitmapImage(비트맵);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void getuserdata() {
        {
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
                                        String 프로필이미지스트링 = 아이템제이슨.getString("profile_image");
                                        Log.i("프로필이미지", 프로필이미지스트링);
                                        String 자기소개 = 아이템제이슨.getString("user_selfintro");
                                        Log.i("자기소개", 자기소개);
                                        //기록 아이템은 만들어 줌
                                        이름표시.setText(유저이름);
                                        if (자기소개.equals("null")) {
                                            자기소개표시.setText("");
                                        } else {
                                            자기소개표시.setText(자기소개);
                                        }


                                        Thread uThread = new Thread() {
                                            @Override
                                            public void run(){
                                                try{
                                                    //서버에 올려둔 이미지 URL

                                                    URL url2 = new URL("http://192.168.219.157/images/"+프로필이미지스트링);
                                                    HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
                                                    conn.setDoInput(true); //Server 통신에서 입력 가능한 상태로 만듦
                                                    conn.connect(); //연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)
                                                    InputStream is = conn.getInputStream(); //inputStream 값 가져오기
                                                    비트맵이미지 = BitmapFactory.decodeStream(is); // Bitmap으로 반환
                                                    encodeBitmapImage(비트맵이미지);
//                                                    프로필이미지.setImageBitmap(비트맵이미지);

                                                }catch (MalformedURLException e){
                                                    e.printStackTrace();
                                                }catch (IOException e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        uThread.start(); // 작업 Thread 실행


                                        try{
                                            //메인 Thread는 별도의 작업을 완료할 때까지 대기한다!
                                            //join() 호출하여 별도의 작업 Thread가 종료될 때까지 메인 Thread가 기다림
                                            //join() 메서드는 InterruptedException을 발생시킨다.
                                            uThread.join();
                                            //작업 Thread에서 이미지를 불러오는 작업을 완료한 뒤
                                            //UI 작업을 할 수 있는 메인 Thread에서 ImageView에 이미지 지정
                                            프로필이미지.setImageBitmap(비트맵이미지);
                                        }catch (InterruptedException e){
                                            e.printStackTrace();
                                        }
                                    }//제이슨 파싱하는 반복문
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
                        params.put("user_email", 유저메일);
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


    private void encodeBitmapImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytesOfImage = byteArrayOutputStream.toByteArray();
        인코드이미지스트링 = android.util.Base64.encodeToString(bytesOfImage, Base64.DEFAULT);
    }

}