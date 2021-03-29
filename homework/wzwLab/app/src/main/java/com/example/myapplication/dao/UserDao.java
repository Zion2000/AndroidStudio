package com.example.myapplication.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.test.DatabaseHelper;

public class UserDao {
    private DatabaseHelper dbhelper;
    private String tableName = "user";

    public UserDao(Context context){
        dbhelper = new DatabaseHelper(context);
    }

    public UserDao() {

    }

    public void login(String name,String password){

    /*
    * 根据账号或手机号查询账号
    * */
        SQLiteDatabase readdb = dbhelper.getReadableDatabase();
        Cursor c = readdb.query(tableName, null, "name=? and password=?", new String[]{name,password}, null, null, null);
        if(c != null && c.getCount() >0){
            if(c.moveToNext()) {
                User user = new User();
                user.setName(name);
                user.setPassword(password);
            }
        }


    }


}
