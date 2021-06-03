package com.example.myapplication.PoiSelect;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.myapplication.R;
import com.example.myapplication.util.MyDatabaseHelper;


public class ShowdetailInfo extends AppCompatActivity {

    private ImageView iv_img;
    private TextView tv_describe;
    private TextView et_city;
    private TextView et_street;
    private TextView et_latitude;
    private TextView et_longtitude;
    private TextView et_name;
    private MyDatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private String city;
    private String address;
    private String details;
    private String stuImg;
    private String name;
    private int _id;
    private String poiID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdetail_info);
        innit();//初始化~
        //获取意图
        Intent intent = getIntent();
        //根据Key 取出对应的value值

        Bundle bundle = intent.getBundleExtra("bundle");
        //bundle.getInt("");
        double atitude = bundle.getDouble("Atitude");
        double longitude = bundle.getDouble("Atitude");
        city = bundle.getString("City");
        address = bundle.getString("Address");
        details = bundle.getString("Details");
        stuImg = bundle.getString("StuImg");
        poiID = bundle.getString("_PoiID");
        name = bundle.getString("Name");
        _id = bundle.getInt("_id");
        System.out.println("poiID-"+poiID);
        System.out.println("_id-"+_id);
        et_city.setText(city);
        et_street.setText(address);
        et_name.setText(name);
        et_latitude.setText(String.valueOf(atitude));
        et_longtitude.setText(String.valueOf(longitude));
        tv_describe.setText(details);

        try {
            System.out.println("stuImg:"+ stuImg);
            Uri myUri = Uri.parse(stuImg);
      /*      System.out.println("myUri:"+myUri);
            ContentResolver resolver = getContentResolver();
          //  Bitmap bm = MediaStore.Images.Media.getBitmap(resolver, myUri);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), myUri);

        */
            //显得到bitmap图片
            iv_img.setImageURI(myUri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        databaseHelper = new MyDatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();


    }

    private void innit() {
        iv_img = findViewById(R.id.iv_img);
        tv_describe = findViewById(R.id.tv_describe);
        et_city = findViewById(R.id.et_city);
        et_street = findViewById(R.id.et_street);
        et_latitude = findViewById(R.id.et_latitude);
        et_longtitude = findViewById(R.id.et_longtitude);
        et_name = findViewById(R.id.et_name);

    }

    public void back_btns(View view) {
        switch (view.getId()){
            case R.id.update_btns:
                db = databaseHelper.getReadableDatabase();
                String sql;
                sql = "update PoiInfo set city='" +et_city.getText()+ "',name='"+et_name.getText()+"',address='"+et_street.getText()+"',details='"+tv_describe.getText()+"' where _id='"+ _id +"'";

                db.execSQL(sql);

                break;

            case R.id.btn_backs:
                finish();
                break;

        }

    }
}