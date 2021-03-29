package com.example.myapplication.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

public class test extends AppCompatActivity implements View.OnClickListener {

    private EditText et_name;
    private EditText et_psw;
    private TextView tv_show;
    private Button btn_add;
    private Button btn_delete;
    private Button btn_update;
    private Button btn_query;
    private String name;
    private String psw;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        databaseHelper = new DatabaseHelper(this);
    init(); // c初始化控件


    }

    private  void init(){
        et_name = findViewById(R.id.et_name);
        et_psw = findViewById(R.id.et_psw);
        tv_show = findViewById(R.id.tv_show);
        btn_add = findViewById(R.id.btn_add);
        btn_delete = findViewById(R.id.btn_delete);
        btn_update = findViewById(R.id.btn_update);
        btn_query = findViewById(R.id.btn_query);

        btn_add.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_query.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
     switch (v.getId()){

         case R.id.btn_add:
             name = et_name.getText().toString().trim();
             psw = et_psw.getText().toString().trim();
             db = databaseHelper.getReadableDatabase();
             contentValues = new ContentValues();
             contentValues.put("name",name);
             contentValues.put("password",psw);
             db.insert("user",null, contentValues);
             Toast.makeText(this," btn_add ok",Toast.LENGTH_LONG).show();
             db.close();
             break;

         case R.id.btn_query:
              db = databaseHelper.getReadableDatabase();
             Cursor cursor = db.query("user", null, null, null, null, null, null);
             if(cursor.getCount()==0){
                 tv_show.setText("");
                 Toast.makeText(this,"无数据",Toast.LENGTH_LONG).show();
             }else{
                 cursor.moveToFirst();
                 tv_show.setText("Name:"+cursor.getString(0)+"Psw:"+cursor.getString(1));
                 while (cursor.moveToNext()){
                     tv_show.append("\n"+"Name:"+cursor.getString(0)+"Psw:"+cursor.getString(1));
                 }
             }
             Toast.makeText(this," btn_query ok",Toast.LENGTH_LONG).show();
             cursor.close();
             db.close();

             break;
         case R.id.btn_delete:
             db = databaseHelper.getReadableDatabase();
             db.delete("user",null,null);
             Toast.makeText(this," delete ok",Toast.LENGTH_LONG).show();

             db.close();
             break;

         case R.id.btn_update:
             db = databaseHelper.getReadableDatabase();
             contentValues= new ContentValues();
             contentValues.put("password",et_psw.getText().toString().trim());
             db.update("user",contentValues,"name=?",new String[]{et_name.getText().toString().trim()});
             db.close();
             break;


     }
    }
}