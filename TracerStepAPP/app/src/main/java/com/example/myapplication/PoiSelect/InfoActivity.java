package com.example.myapplication.PoiSelect;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.pojo.PoiInfos;
import com.example.myapplication.util.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {
    private MyDatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    List<PoiInfos> list;
    private PoiInfos poiInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        SelectItem();


    }

    private void SelectItem() {
        //进来的时候直接查询PoiInfo 表 然后adapt出数据！
        TextView tv_HelloWorld = findViewById(R.id.HelloWorld);
        databaseHelper = new MyDatabaseHelper(this);
        db = databaseHelper.getReadableDatabase();
        Cursor c = db.query("PoiInfo", null, null, null, null, null, null);
        if(c.getCount()==0){
            tv_HelloWorld.setText("你没有添加地址耶！手机真轻松");
        }else{
            list = new ArrayList();
            while (c.moveToNext()){
             //   tv_show.append("\n"+"id:"+cursor2.getString(0)+"\tName:"+cursor2.getString(1)+"\tage:"+cursor2.getString(2));

                poiInfo = new PoiInfos(
                        c.getInt(0),
                        c.getString(1),
                        c.getInt(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5),
                        c.getDouble(6),
                        c.getDouble(7),
                        c.getString(8),
                        c.getString(9)
                );
                //HashMap<String, Object> stringObjectHashMap = poiInfo.madeMap();
                list.add(poiInfo);

            }
            Toast.makeText(this," 查询完成",Toast.LENGTH_LONG).show();
            //System.out.println("test1-"+list.size());
            // 添加ListItem，设置事件响应
            ListView mListView = (ListView) findViewById(R.id.listView2);
            mListView.setAdapter(new InfoListAdapter());
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    showPopupMenu(parent,view,position);
                }
            });
            //System.out.println("text3");
        }

        c.close();
        // db.close();
    }

    Adapter adapter;
    int positions;
    PoiInfos item1;
    private void showPopupMenu(AdapterView<?> parent, View view, int position){
        PopupMenu popupMenu = new PopupMenu(InfoActivity.this, view);
        popupMenu.inflate(R.menu.menu_info);
        popupMenu.show();
         adapter = parent.getAdapter();
        positions=position;
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item1 = (PoiInfos) adapter.getItem(positions);  //获取点击的所有信息~

                switch ( item.getItemId()){
                    /*
                    * 单纯的查看详细信息
                    * */
                    case R.id.detail_item :
                        Intent  intent = new Intent(InfoActivity.this, ShowdetailInfo.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("_id", item1.get_id());
                        bundle.putString("_PoiID", item1.get_PoiID());
                        bundle.putDouble("Atitude", item1.getAtitude());
                        bundle.putDouble("Longitude",   item1.getLongitude());
                        bundle.putString("City",  item1.getCity());
                        bundle.putString("Address",  item1.getAddress());
                        bundle.putString("Name",  item1.getName());
                        bundle.putString("Details",  item1.getDetails());
                        bundle.putString("StuImg",  item1.getStuImg());
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                        break;
                    case R.id.delete_item :
                        AlertDialog alertDialog2 = new AlertDialog.Builder(InfoActivity.this)
                            .setTitle("确认")
                            .setMessage("您确定删除？")
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton("是的！", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    db = databaseHelper.getReadableDatabase();
                                    String id = String.valueOf(item1.get_id());
                                    db.delete("PoiInfo","_id=?",new String[]{id});
                                    Toast.makeText(InfoActivity.this, "删除成功~", Toast.LENGTH_LONG).show();
                                    db.close();
                                    SelectItem();
                                }
                            })

                            .setNegativeButton("算了", new DialogInterface.OnClickListener() {//添加取消
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Toast.makeText(LocationTypeDemo.this, "这是取消按钮", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create();
                        alertDialog2.show();
                        break;
                }



                return false;
            }
        });
    }




    /*
     *    ListView与 BaseAdapter
     *   调用demo_poiinfo_item.xml格式，demo_poiinfo_item
     * */
    private class InfoListAdapter extends BaseAdapter{
        public InfoListAdapter() {
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PoiInfos info = list.get(position);
            Util  util = new Util();

           // System.out.println("test2 "+info);
            if (null == convertView) {
                convertView = View.inflate(InfoActivity.this, R.layout.demo_poiinfo_item, null);
            }

            util.tv_id = (TextView) convertView.findViewById(R.id.tv_id);
            util.tv__Uid = (TextView) convertView.findViewById(R.id.tv__Uid);
            util.tv__PoiID = (TextView) convertView.findViewById(R.id.tv__PoiID);
            util.tv_city = (TextView) convertView.findViewById(R.id.tv_city);
            util.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            util.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            util.tv_atitude = (TextView) convertView.findViewById(R.id.tv_atitude);
            util.tv_longitude = (TextView) convertView.findViewById(R.id.tv_longitude);
            //util.delete_item_btn = (ImageButton) findViewById(R.id.delete_item_btn);

            util.tv_id.setText("id:"+String.valueOf(info.get_id()));
            util.tv__PoiID.setText(info.get_PoiID());
            util.tv__Uid.setText("用户账号:"+String.valueOf(info.get_Uid()));
            util.tv_city.setText(info.getCity());
            util.tv_address.setText(info.getAddress());
            util.tv_name.setText(info.getName());
            util.tv_atitude.setText(info.getAtitude().toString());
            util.tv_longitude.setText(info.getLongitude().toString());


            return convertView;
        }
    }
    /**
     * 内部类，用于辅助适配
     *
     * @author qiangzi
     *
     */
    class Util {

        TextView tv_id, tv__Uid, tv__PoiID,tv_city,tv_address,tv_name,tv_atitude,tv_longitude;
        ImageButton delete_item_btn;

    }



    public void back_btn(View view) {
        Intent  intent;
        switch (view.getId()){
            case R.id.btn_addinfo:
                  intent = new Intent(this, AddDetailInfo.class);
                startActivity(intent);
                break;
            case R.id.btn_back:
                  finish();
                break;
            case R.id.User_btn:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_ShowUinfo:
                intent = new Intent(this, ShowUinfo.class);
                startActivity(intent);
                break;
        }

    }



}