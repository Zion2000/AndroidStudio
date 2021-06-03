package com.example.myapplication.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {


    public MyDatabaseHelper(@Nullable Context context) {
        super(context, "StepMap.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE PoiInfo(_id integer primary key autoincrement," +
                "_PoiID VARCHAR  ," +
                "_Uid integer ," +
                "city VARCHAR ," +
                "name VARCHAR ," +
                "address VARCHAR ," +
                "latitude REAL ," +
                "longitude REAL ," +
                "stuImg VARCHAR ," +
                "details VARCHAR  )";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}