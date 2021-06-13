package com.example.myapplication.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.util.MyDatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_uname;
    private EditText et_email;
    private EditText et_cpwd;
    private EditText et_pwd;
    private MyDatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        /*
         * 1.点击保存按钮 获取内容
         * 2.判断合法性
         * 3.注册内容保存到数据库
         * 4.注册结束返回登陆界面
         * */
        et_uname = findViewById(R.id.et_uname);
        et_email = findViewById(R.id.et_email);
        et_cpwd = findViewById(R.id.et_cpwd);
        et_pwd = findViewById(R.id.et_pwd);


        databaseHelper = new MyDatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

    }

    public void registerOnClick(View view) {
        db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.query("User", null, "name=?", new String[]{et_uname.getText().toString()}, null, null, null);
        if (cursor.getCount() != 0) {
            Toast.makeText(this, "账号已经存在", Toast.LENGTH_LONG).show();
        }else if (!(et_cpwd.getText().toString().trim().equals( et_pwd.getText().toString().trim()))){
            Toast.makeText(this,"密码不一致",Toast.LENGTH_LONG).show();
        }else if(et_pwd.getText().toString().length()>=8 ||et_pwd.getText().toString().length()<3) {
            Toast.makeText(this,"密码长度不符",Toast.LENGTH_LONG).show();
        }else if(et_uname.getText().toString().length()>=8) {
            Toast.makeText(this,"账号长度不符",Toast.LENGTH_LONG).show();

        }else {
            ContentValues cv = new ContentValues();
            cv.put("name", et_uname.getText().toString().trim());
            cv.put("passwrod", et_cpwd.getText().toString().trim());
            cv.put("email", et_email.getText().toString().trim());
            db.insert("User", null, cv);

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}