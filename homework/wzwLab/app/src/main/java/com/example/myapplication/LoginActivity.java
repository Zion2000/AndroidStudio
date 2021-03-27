package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.*;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText user_name;
    private EditText user_password;
    private ImageButton btn_login;
    private TextView tv_register;
    private TextView tv_forget;

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
        btn_login = findViewById(R.id.btn_login);
        tv_register = findViewById(R.id.tv_register);
        tv_forget = findViewById(R.id.tv_forget);


        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        tv_forget.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = new Intent();
        switch (id) {

            case R.id.btn_login:
                passDate(); //数据传递
                break;
            case R.id.tv_register:
                intent = new Intent(this, RegisterActivity.class);
                break;
            case R.id.tv_forget:
                intent = new Intent(this, ForgetActivity.class);
                //开启意图 带返回值
                startActivityForResult(intent,1);

                break;
        }


    }

    //数据传递
    private void passDate() {
        //创建意图对象  跳转页面
        Intent intent = new Intent(this, ShowloginActivity.class);
        //数据存入intent
        intent.putExtra("name", user_name.getText().toString().trim());
        intent.putExtra("password", user_password.getText().toString().trim());
        //开启意图
        /*
                第一个参数:Intent对象
                第二个参数:请求的一个标识
                 */
        startActivityForResult(intent, 1);
    }

    /*
  通过startActivityForResult的方式接受返回数据的方法
  requestCode：请求的标志,给每个页面发出请求的标志不一样，这样以后通过这个标志接受不同的数据
  resultCode：这个参数是setResult(int resultCode,Intent data)方法传来的,这个方法用在传来数据的那个页面
   */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {//当请求码是1&&返回码是2进行下面操作
            System.out.println("OK");
            String content = data.getStringExtra("data");
            user_name.setText(content);
        }


 /*   public void click(View v){
        myBtn_one.setText("already clidcked");
    }*/


    }


}