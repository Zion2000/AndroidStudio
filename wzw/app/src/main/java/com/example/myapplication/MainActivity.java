package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.widget.*;


import com.baidu.location.*;
import com.baidu.mapapi.SDKInitializer;

import com.baidu.mapapi.map.*;

import com.baidu.mapapi.model.LatLng;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView loacatoinInfo;
    LocationClient   mlocationClient;
    BaiduMap mBaiduMap =null;
    MapView mMapView;

    boolean isFirstlocation =true ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext()); //初始化

        setContentView(R.layout.activity_main);

        loacatoinInfo = findViewById(R.id.locationInfo);

        mlocationClient =  new LocationClient(getApplicationContext());
        mlocationClient.registerLocationListener(new MyLocationListener());

        mMapView =  findViewById(R.id.bmapView);
        mBaiduMap =  mMapView.getMap();

        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

        mBaiduMap.setMyLocationEnabled(true);





        List<String> permissionList = new ArrayList<String>();

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }else{
            requestLocation();
        }

    }

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
                    requestLocation();
                }else{
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void requestLocation(){
        initLocation();
        mlocationClient.start();
    }
    private  void initLocation(){
        LocationClientOption locationOption = new LocationClientOption();

//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("gcj02");

//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
//可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
//可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);


        locationOption.setWifiCacheTimeOut(5*60*100);
        locationOption.setIsNeedAddress(true);

        mlocationClient.setLocOption(locationOption);
    }
    private  class MyLocationListener extends BDAbstractLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            navigateTo(bdLocation);
            /*StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("维度:").append(bdLocation.getLatitude()).append("\n");
            currentPosition.append("经度:").append(bdLocation.getLongitude()).append("\n");
            currentPosition.append("国家:").append(bdLocation.getCountry()).append("\n");
            currentPosition.append("省:").append(bdLocation.getProvince()).append("\n");
            currentPosition.append("市:").append(bdLocation.getCity()).append("\n");
            currentPosition.append("区:").append(bdLocation.getDistrict()).append("\n");
            currentPosition.append("村镇:").append(bdLocation.getTown()).append("\n");
            currentPosition.append("街道:").append(bdLocation.getStreet()).append("\n");
            currentPosition.append("地址:").append(bdLocation.getAddrStr()).append("\n");
            currentPosition.append("定位方式: ");
            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation){
                currentPosition.append("GPS");
            }else if(bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                currentPosition.append("网络");
            }
            loacatoinInfo.setText(currentPosition);*/
        }
    }

    private  void   navigateTo(BDLocation  bdLocation){
        if(isFirstlocation){ LatLng ll = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(update);
        update = MapStatusUpdateFactory.zoomTo(16f);//当前图的缩放 16f
        mBaiduMap.animateMapStatus(update); // 重新展示
        isFirstlocation=false;
       }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.longitude(bdLocation.getLongitude());
        locationBuilder.latitude(bdLocation.getAltitude());
        MyLocationData locationData = locationBuilder.build();
        mBaiduMap.setMyLocationData(locationData);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        mlocationClient.stop();
    }

        @Override
        protected void onResume() {
            super.onResume();
            mMapView.onResume();
        }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
}
