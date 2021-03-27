package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ShowloginActivity extends AppCompatActivity {

    private TextView textView;
    private RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showlogin);

        //获取意图
        Intent intent = getIntent();
        //根据Key 取出对应的value值
        String name = intent.getStringExtra("name");
        String password = intent.getStringExtra("password");
        //把用户名和密码展示出来
        TextView tv_name = findViewById(R.id.et_name);
        TextView tv_password = findViewById(R.id.et_password);

        if(tv_name.equals("") || tv_password.equals("")){
            tv_name.setText(name);
            tv_password.setText(password);
        }



        /*
         * 男女的按钮控件
         * */
        textView = findViewById(R.id.tv);
        radioGroup = findViewById(R.id.rdg);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rbtn) {
                    textView.setText("you choiced female");
                } else {
                    textView.setText("you choiced male");
                }

            }
        });


    }
}