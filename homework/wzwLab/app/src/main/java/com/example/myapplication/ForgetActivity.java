package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class ForgetActivity extends AppCompatActivity {

    private Button btn_back;
    private Button btn_register;
    private EditText et_uname;
    private EditText et_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

         btn_back = findViewById(R.id.btn_back);
        btn_register = findViewById(R.id.btn_back);
        et_uname = findViewById(R.id.et_uname);
        et_info = findViewById(R.id.et_info);

        String content="你好";//想返回的内容


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent intent = new Intent(ForgetActivity.this,LoginActivity.class);
                Intent intent = new Intent();
                //name相当于一个key,content是返回的内容
                intent.putExtra("data",content);
                //resultCode是返回码,用来确定是哪个页面传来的数据，这里设置返回码是2
                //这个页面传来数据,要用到下面这个方法setResult(int resultCode,Intent intent)
                setResult(2,intent);
                //结束当前页面
                finish();

            }
        });



        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_uname.getText().toString().trim().length() == 0){
                    Toast.makeText(ForgetActivity.this,"User Name is empty",Toast.LENGTH_SHORT).show();
                    return;//中断方法
                }
                if (et_info.getText().toString().trim().length() == 0){
                    Toast.makeText(ForgetActivity.this,"Phone or email is empty",Toast.LENGTH_SHORT).show();
                    return;//中断方法
                }
            }
        });
    }

    /*
    * 查找密码功能~ 未实现
    * */



}