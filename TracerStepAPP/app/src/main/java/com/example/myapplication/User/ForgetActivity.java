package com.example.myapplication.User;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.example.myapplication.R;
import com.example.myapplication.util.MyDatabaseHelper;

public class ForgetActivity extends AppCompatActivity {

    private Button btn_back;
    private Button btn_register;
    private EditText et_uname;
    private EditText et_info;
    private TextView tv_register;
    private MyDatabaseHelper databaseHelper;
    private SQLiteDatabase db;


    String psw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

         btn_back = findViewById(R.id.btn_back);
        btn_register = findViewById(R.id.btn_register);
        et_uname = findViewById(R.id.et_uname);
        et_info = findViewById(R.id.et_info);
        tv_register = findViewById(R.id.tv_register);

        databaseHelper = new MyDatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();
        db = databaseHelper.getReadableDatabase();
        String content="你好";//想返回的内容

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_uname.getText().toString().trim().length() == 0){
                    Toast.makeText(ForgetActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                } else if (et_info.getText().toString().trim().length() == 0){
                    Toast.makeText(ForgetActivity.this,"邮箱不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    db = databaseHelper.getReadableDatabase();

                    String selection="name=? and email=?";
                    String selectionArgs[] = {et_uname.getText().toString().trim(), et_info.getText().toString().trim()};

                    Cursor cursor2 = db.query("User", null, selection, selectionArgs, null, null, null);
                    if (cursor2.getCount() != 0) {
                        cursor2.moveToFirst();
                         psw = cursor2.getString(2);

                        AlertDialog alertDialog2 = new AlertDialog.Builder(ForgetActivity.this)
                                .setTitle("查看？")
                                .setMessage("您的密码："+psw)
                                .setIcon(R.mipmap.ic_launcher)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent();
                                        //name相当于一个key,content是返回的内容
                                        intent.putExtra("name",et_uname.getText().toString().trim());
                                        intent.putExtra("password",psw);
                                        //resultCode是返回码,用来确定是哪个页面传来的数据，这里设置返回码是2
                                        //这个页面传来数据,要用到下面这个方法setResult(int resultCode,Intent intent)
                                        setResult(2,intent);
                                        //结束当前页面
                                        finish();
                                    }
                                })
                                .create();
                        alertDialog2.show();
                    } else {
                        Toast.makeText(ForgetActivity.this, "没有这玩意", Toast.LENGTH_LONG).show();

                    }
                }



            }
        });



        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(ForgetActivity.this,LoginActivity.class);
                    startActivity(intent);
            }
        });
    }

    /*
    * 查找密码功能~ 未实现
    * */



}