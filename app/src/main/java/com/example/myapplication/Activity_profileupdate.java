package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

public class Activity_profileupdate extends AppCompatActivity {

    String TAG = "Activity_profileupdate";
    ImageView 프로필이미지;
    ImageView 카메라이미지;
    Dialog 이미지다이얼로그;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileupdate);
        프로필이미지 = findViewById(R.id.이미지뷰_프로필이미지);
        카메라이미지 = findViewById(R.id.이미지뷰_사진아이콘);
        이미지다이얼로그=new Dialog(this);
        이미지다이얼로그.requestWindowFeature(Window.FEATURE_NO_TITLE);
        이미지다이얼로그.setContentView(R.layout.dialog_profileimage);
        //다이얼로그의 구성요소들이 동작할 코드작성
        Button 카메라버튼 = 이미지다이얼로그.findViewById(R.id.버튼_사진찍기);
        카메라버튼.setText("사진 찍기");
        카메라버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");

                launcher.launch(intent);
            }
        });

        Button 갤러리버튼 = 이미지다이얼로그.findViewById(R.id.버튼_갤러리);
        갤러리버튼.setText("갤러리");
        갤러리버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                launcher.launch(intent);
            }
        });
        프로필이미지.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                이미지다이얼로그.show();
            }
        });
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Log.e(TAG, "result : " + result);
                        Intent intent = result.getData();
                        Log.e(TAG, "intent : " + intent);
                        Uri uri = intent.getData();
                        Log.e(TAG, "uri : " + uri);
                        Glide.with(Activity_profileupdate.this)
                                .load(uri)
                                .into(프로필이미지);
                    }
                }
            });
}