package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class LoginActivity extends AppCompatActivity {


    private EditText user_name;
    private EditText user_password;
    private ImageButton btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        /*
         * 账号 密码控件
         *
         * 登录按钮控件
         *
         * */
        user_name = findViewById(R.id.et_name);
        user_password = findViewById(R.id.et_password);
        btn_send = findViewById(R.id.btn_login);
        //按钮点击事件
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                passDate(); //数据传递

            }
        });





        /*
         *   打印日志
         * */
   /*     Log.v("MainActivity","verbose");
        Log.d("MainActivity","Debug");
        Log.i("MainActivity","Info");
        Log.w("MainActivity","Warning");
        Log.e("MainActivity","Error");*/

    }
    //数据传递
    private void passDate() {
        //创建意图对象  跳转页面
        Intent intent = new Intent(this,ShowloginActivity.class);
        //数据存入intent
        intent.putExtra("name", user_name.getText().toString().trim());
        intent.putExtra("password", user_password.getText().toString().trim());
        //开启意图
        startActivity(intent);
    }





   /* @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_one:
                myBtn_one.setText("already clidcked");
                break;
        }

    }*/

 /*   public void click(View v){
        myBtn_one.setText("already clidcked");
    }*/


}