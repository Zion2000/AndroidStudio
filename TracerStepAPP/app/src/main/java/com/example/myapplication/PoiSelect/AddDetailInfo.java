package com.example.myapplication.PoiSelect;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.util.MyDatabaseHelper;

public class AddDetailInfo extends AppCompatActivity {


    private final String IMAGE_TYPE = "image/*";
    Uri bitmapUri = null;

    private ImageView iv;
    private Button bt_album;
    private EditText et_des;
    private Button search_name;
    private String uid=null;
    private String city;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private Uri originalUri;
    private MyDatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);

        bt_album = (Button) findViewById(R.id.btn_pop_album);
        iv = (ImageView) findViewById(R.id.iv);
        et_des = findViewById(R.id.et_des);
        search_name =(Button) findViewById(R.id.search_name);
        databaseHelper = new MyDatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();


        search_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddDetailInfo.this,PoiCitySearch.class);


                startActivityForResult(intent, 1);
            }
        });
        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(IMAGE_TYPE);
                startActivityForResult(intent, 0);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK ) {

                Uri imageUri=data.getData();


                try {
                    Bitmap bm = null;
                    ContentResolver resolver = getContentResolver();
                    //获得图片的uri
                    originalUri = data.getData();
                    bitmapUri = originalUri;
                    System.out.println("URi:"+ originalUri);
                    System.out.println("URi:"+ originalUri.getClass());
                    // isSelectPic = true;
                    bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                    //显得到bitmap图片
                    iv.setImageBitmap(bm);

                } catch (Exception e) {
                    Log.e("TAG-->Error", e.toString());
                }

                //放到imageview中


        }


        if (requestCode == 1 && resultCode == 2) {//当请求码是1&&返回码是2进行下面操作
            System.out.println("OK");
            uid = data.getStringExtra("uid");
            city = data.getStringExtra("city");
            name = data.getStringExtra("name");
            address = data.getStringExtra("address");
            latitude = data.getDoubleExtra("latitude", 0);
            longitude = data.getDoubleExtra("longitude", 0);
            search_name.setText(name);
          //  System.out.println(uid+"\n"+city+"\n"+name+"\n"+address+"\n"+latitude+"\n"+longitude+"\n");

        }

    }

    public void addinfo(View view) {
        AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                .setTitle("添加？")
                .setMessage("您确定添加吗？")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("是的！", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                if (uid !=null) {
                    db = databaseHelper.getReadableDatabase();
                    String details = et_des.getText().toString();
                    ContentValues cv = new ContentValues();
                   // cv.put("_id", null);
                    cv.put("_PoiID", uid);
                    cv.put("_Uid", 1);
                    cv.put("city", city);
                    cv.put("name", name);
                    cv.put("address", address);
                    cv.put("latitude", latitude);
                    cv.put("longitude", longitude);
                    cv.put("stuImg", originalUri.toString());
                    cv.put("details", details);
                    db.insert("PoiInfo", null, cv);
                    db.close();
                    Intent intent = new Intent(AddDetailInfo.this, InfoActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(AddDetailInfo.this,"没选择地点 不能添加噢",Toast.LENGTH_LONG).show();
                }
                    }
                })

                .setNegativeButton("NO NO NO", new DialogInterface.OnClickListener() {//添加取消
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Toast.makeText(LocationTypeDemo.this, "这是取消按钮", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
        alertDialog2.show();

    }

    public void back_btnb(View view) {
        finish();
    }
}