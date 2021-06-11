package com.example.myapplication.PoiSelect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.animation.AlphaAnimation;
import com.baidu.mapapi.animation.Animation;
import com.baidu.mapapi.animation.AnimationSet;
import com.baidu.mapapi.animation.RotateAnimation;
import com.baidu.mapapi.animation.ScaleAnimation;
import com.baidu.mapapi.animation.SingleScaleAnimation;
import com.baidu.mapapi.animation.Transformation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.myapplication.R;
import com.example.myapplication.pojo.PoiInfos;
import com.example.myapplication.util.MyDatabaseHelper;
import com.example.myapplication.util.PoiListAdapter;
import com.example.myapplication.util.PoiOverlay;

import java.util.ArrayList;
import java.util.List;

public class ShowUinfo extends AppCompatActivity implements BaiduMap.OnMapLoadedCallback {


    private Marker mMarkerA;
    private MyDatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Button mSearchBtn;
    private MapStatus mMapStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext()); //初始化
        setContentView(R.layout.activity_show_uinfo);
        getSupportActionBar().hide();//隐藏标题栏
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapLoadedCallback(this);

        addMarkers();
    }

    /**
     * 向地图添加Marker点
     */
    public void addMarkers() {
        // 添加Marker点
        databaseHelper = new MyDatabaseHelper(this);
        db = databaseHelper.getReadableDatabase();
        Cursor c = db.query("PoiInfo", null, null, null, null, null, null);

        while (c.moveToNext()){
            LatLng latLngA = new LatLng(c.getDouble(6), c.getDouble(7));
            MarkerOptions markerOptionsA = new MarkerOptions().position(latLngA).icon(BitmapDescriptorFactory.fromResource(R.drawable.markerblue));
            mMarkerA = (Marker) (mBaiduMap.addOverlay(markerOptionsA));

        }

        Toast.makeText(this," 查询完成",Toast.LENGTH_LONG).show();

        c.moveToFirst();
        LatLng ll = new LatLng(c.getDouble(6), c.getDouble(7));
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll);
        builder.zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));


    }


    @Override
    public void onMapLoaded() {
        // TODO Auto-generated method stub
        mMapStatus = new MapStatus.Builder().zoom(18.0f).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时必须调用mMapView. onResume ()
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时必须调用mMapView. onPause ()
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时必须调用mMapView.onDestroy()
        mMapView.onDestroy();
    }

    public void back2_btn(View view) {
        finish();
    }
}