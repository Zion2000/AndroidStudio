package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.VersionInfo;
import com.example.myapplication.PoiSelect.LocationType;
import com.example.myapplication.PoiSelect.ShowUinfo;
import com.example.myapplication.User.ShowloginActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
 /*   BaiduMap mBaiduMap =null;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    boolean isFirstlocation =true ;*/
    /**
     * 广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            //System.out.println("广播监听类，监听 SDK key 验证以及网络异常广播");
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            TextView text = (TextView) findViewById(R.id.text_Info);
          text.setTextColor(Color.RED);
            if(action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                text.setText("key 验证出错! 错误码 :" + intent.getIntExtra
                       (SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE, 0)
                        +  " ; 请在 AndroidManifest.xml 文件中检查 key 设置");
                //Toast.makeText(getApplicationContext(),"key 验证出错! 错误码" , Toast.LENGTH_LONG).show();

            } else if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                text.setText("key 验证成功! 功能可以正常使用");
                text.setTextColor(Color.GREEN);
                //Toast.makeText(getApplicationContext(),"key 验证成功!" , Toast.LENGTH_LONG).show();

            } else if (action.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                text.setText("网络出错");
                //Toast.makeText(getApplicationContext(),"网络出错" , Toast.LENGTH_LONG).show();

            }
        }
    }


    private SDKReceiver mReceiver;//监听~
    private boolean isPermissionRequested;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text = (TextView) findViewById(R.id.text_Info);
        text.setTextColor(Color.GREEN);
        text.setText("欢迎使用TracerStepApp");
        setTitle(getTitle() + " v" + VersionInfo.getApiVersion());

        // 添加ListItem，设置事件响应
        ListView mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(new DemoListAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
                onListItemClick(index);
            }
        });

        // 申请动态权限
        requestPermission();


        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        //网络产生变化或者 intent的
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);


    }

/*
*    写一个只属于MainActivity的DemoInfo  POJO。
*     用数组储存赋值
*     在这增加你需要的功能~
* */
    private static final DemoInfo[] DEMOS = {
        new DemoInfo(R.drawable.user, R.string.user, R.string.desc_user, ShowloginActivity.class),
            new DemoInfo(R.drawable.route, R.string.demo_title_layerlist, R.string.demo_desc_layerlist, LocationType.class),
            new DemoInfo(R.drawable.map, R.string.demo_title_interest, R.string.demo_desc_layerlist, ShowUinfo.class)
    };

/*
 *  ListItem-事件响应
 * index--点击的列表跳转Activity
 * */
    void onListItemClick(int index) {
        Intent intent;
        intent = new Intent(MainActivity.this, DEMOS[index].demoClass);
        this.startActivity(intent);
    }
/*
*    ListView与 BaseAdapter
*   调用demo_item.xml格式，并设置demo_item中的各个组件
* */
    private class DemoListAdapter extends BaseAdapter {
        private DemoListAdapter() {
            super();
        }
        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = View.inflate(MainActivity.this, R.layout.demo_item, null);
            }
            ImageView imageView =(ImageView)convertView.findViewById(R.id.image);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView desc = (TextView) convertView.findViewById(R.id.desc);
            imageView.setBackgroundResource(DEMOS[index].image);
            title.setText(DEMOS[index].title);
            desc.setText(DEMOS[index].desc);
            return convertView;
        }

        @Override
        public int getCount() {
            return DEMOS.length;
        }

        @Override
        public Object getItem(int index) {
            return DEMOS[index];
        }

        @Override
        public long getItemId(int id) {
            return id;
        }
    }


    /**
     * Android6.0之后需要动态申请权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {
            isPermissionRequested = true;
            ArrayList<String> permissionsList = new ArrayList<>();
            String[] permissions = {
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
            };

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                    // 进入到这里代表没有权限.
                }
            }

            if (!permissionsList.isEmpty()) {
                String[] strings = new String[permissionsList.size()];
                requestPermissions(permissionsList.toArray(strings), 0);
            }
        }
    }
    /**
     * 没有权限就别用了
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for (int result : grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有的权限才能使用",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }

                }else{
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消监听 SDK 广播
        unregisterReceiver(mReceiver);
    }
/*
*       DemoInfo --- POJO   ！！！！！和util中的不同，
* */
    private static class DemoInfo {
        private final int image;
        private final int title;
        private final int desc;
        private final Class<? extends Activity> demoClass;

        private DemoInfo(int image,int title, int desc, Class<? extends Activity> demoClass) {
            this.image = image;
            this.title = title;
            this.desc = desc;
            this.demoClass = demoClass;
        }
    }


}
