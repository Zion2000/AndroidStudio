package com.example.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.PlanNode;
import com.example.myapplication.util.LocationStr;

import java.util.List;


/**
 * 展示定位图
 */

public class LocationTypeDemo extends AppCompatActivity implements SensorEventListener {

    // 定位相关
    private LocationClient mLocClient;
    private MyLocationListener myListener = new MyLocationListener();
    // 定位图层显示方式
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    // 初始化地图
    private MapView mMapView;// MapView 是地图主控件
    private BaiduMap mBaiduMap;
    private TextView mdestination_loc_tv;
    private TextView mdepart_loc_tv;
    private LatLng mCurrentPt;
    private String mTouchType;
    // 用于显示地图状态的面板
    // private BitmapDescriptor mbitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
    // 搜索模块，也可去掉地图模块独立使用
    private GeoCoder mSearch = null;
    // 是否首次定位
    private boolean isFirstLoc = true;
    // 是否开启定位图层
    private MyLocationData myLocationData;

    // 浏览路线节点相关
    private Button mBtnPre = null; // 上一个节点
    private Button mBtnNext = null; // 下一个节点
    private RouteLine mRouteLine = null;
    //private OverlayManager mRouteOverlay = null;
    private boolean useDefaultIcon = false;
    // 地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
    // 如果不处理touch事件，则无需继承，直接使用MapView即可

    private BaiduMap mBaidumap = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext()); //初始化
        setContentView(R.layout.activity_location_type);
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 获取传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        // 为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);

        // 定位初始化
        initLocation();

        mdestination_loc_tv = findViewById(R.id.destination_loc_tv);
        mdepart_loc_tv = findViewById(R.id.depart_loc_tv);

        initListener();

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new mOnGetGeoCoderResultListener());
    }

    /**
     * 定位初始化
     */
    public  void initLocation(){
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        mLocClient.registerLocationListener(continuoueLocationListener);
        LocationClientOption locationClientOption = new LocationClientOption();
        // 可选，设置定位模式，默认高精度 LocationMode.Hight_Accuracy：高精度；
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，设置返回经纬度坐标类型，默认GCJ02
        locationClientOption.setCoorType("bd09ll");
        // 如果设置为0，则代表单次定位，即仅定位一次，默认为0
        // 如果设置非0，需设置1000ms以上才有效
        locationClientOption.setScanSpan(1000);
        //可选，设置是否使用gps，默认false
        locationClientOption.setOpenGps(true);
        // 可选，是否需要地址信息，默认为不需要，即参数为false
        // 如果开发者需要获得当前点的地址信息，此处必须为tru
        locationClientOption.setIsNeedAddress(true);
        // 可选，默认false，设置是否需要POI结果，可以在BDLocation
        locationClientOption.setIsNeedLocationPoiList(true);
        // 设置定位参数
        mLocClient.setLocOption(locationClientOption);
        mLocClient.start();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            myLocationData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)// 设置定位数据的精度信息，单位：米
                    .direction(mCurrentDirection)// 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(mCurrentLat)
                    .longitude(mCurrentLon)
                    .build();
            mBaiduMap.setMyLocationData(myLocationData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    /**
     * 对地图事件的消息响应
     */
    private void initListener() {
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            /**
             * 单击地图
             */
            @Override
            public void onMapClick(LatLng point) {
                mTouchType = "单击地图";
                mCurrentPt = point;
                updateMapState(null);
            }

            /**
             * 单击地图中的POI点
             */
            @Override
            public void onMapPoiClick(MapPoi poi) {
                mTouchType = "单击POI点";
                mCurrentPt = poi.getPosition();
                updateMapState(null);
            }
        });
        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            /**
             * 长按地图
             */
            @Override
            public void onMapLongClick(LatLng point) {
                mTouchType = "长按";
                mCurrentPt = point;
                updateMapState(null);
            }
        });
        mBaiduMap.setOnMapDoubleClickListener(new BaiduMap.OnMapDoubleClickListener() {
            /**
             * 双击地图
             */
            @Override
            public void onMapDoubleClick(LatLng point) {
                mTouchType = "双击";
                mCurrentPt = point;
                updateMapState(null);
            }
        });
    }
    /**
     * 更新地图状态显示面板
     */

    StringBuffer getmdestination ;//自定义获取详情地址
    private void updateMapState(String str) {
        System.out.println("test1");
        String state;
        getmdestination = new StringBuffer(256);
        if (mCurrentPt == null) {
            state = "点击、长按、双击地图以获取经纬度和地图状态";
            mdestination_loc_tv.setText(state);
        } else {
            getmdestination.append(mTouchType);
            getmdestination.append("\n指定经度：");
            getmdestination.append(mCurrentPt.longitude);
            getmdestination.append("\n指定纬度：");
            getmdestination.append(mCurrentPt.latitude);

           // state = String.format(mTouchType +"\n"+ "指定经度： %f \n 指定纬度：%f \n "+ str, mCurrentPt.longitude, mCurrentPt.latitude);
            MarkerOptions ooA = new MarkerOptions().position(mCurrentPt).icon(BitmapDescriptorFactory.fromResource(R.drawable.markerblue));
            mBaiduMap.clear();
            mBaiduMap.addOverlay(ooA);
            searchProcess( mCurrentPt.longitude, mCurrentPt.latitude); //执行顺序 更新地图状态显示面板-> 发起搜索 ->逆地理编码查询回调结果

        }

    }

    /**
     * 逆地理编码查询回调结果
     */
    class mOnGetGeoCoderResultListener implements OnGetGeoCoderResultListener{
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            System.out.println("test2");

            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(LocationTypeDemo.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
                return;
            }
            //mBaiduMap.clear();
           // BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
            //添加poi的方法 但我们不需要~
             /*// 添加poi
            mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation()).icon(bitmapDescriptor));
            // 更新地图中心点
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));

            // 获取周边poi结果
         List<PoiInfo> poiList = result.getPoiList();
        if (null != poiList && poiList.size() > 0){
            PoiListAdapter poiListAdapter = new PoiListAdapter(this, poiList);
            mPoiList.setAdapter(poiListAdapter);
            showNearbyPoiView(true);
        }else {
            Toast.makeText(ReverseGeoCodeDemo.this, "周边没有poi", Toast.LENGTH_LONG).show();
            showNearbyPoiView(false);
        }*/
            Toast.makeText(LocationTypeDemo.this, result.getAddress() + " adcode: " + result.getAdcode(), Toast.LENGTH_SHORT).show();
            //bitmapDescriptor.recycle();

            //get自定义获取详情地址
            /*
            * 	city
                城市名称
                java.lang.String	district
                区县名称
                java.lang.String	province
                省份名称
                java.lang.String	street
                街道名称
                java.lang.String	streetNumber
                 门牌号码
            * */
            getmdestination.append("\n地理位置：") ;
            getmdestination.append(result.getAddressDetail().city);
            getmdestination.append(result.getAddressDetail().district);
            getmdestination.append(result.getAddressDetail().province);
            getmdestination.append(result.getAddressDetail().street);
            getmdestination.append(result.getAddressDetail().streetNumber);
            mdestination_loc_tv.setText(getmdestination);

        }
    }

    /**
     * 发起搜索
     */
    public void searchProcess(double mEditLongitude,double mEditLatitude) {
        System.out.println("test3");
        LatLng ptCenter = new LatLng((Float.valueOf((float) mEditLatitude)), (Float.valueOf((float) mEditLongitude)));
        //int radius = Integer.parseInt(mEditRadius);
       /* int version = 0;
        // 反Geo搜索
        if (mNewVersionCB.isChecked()) {
            version = 1;
        }*/
        ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption()
                .location(ptCenter) // 设置反地理编码位置坐标
                .newVersion(1) // 设置是否返回新数据 默认值0不返回，1返回
                // .radius(radius)  POI召回半径，允许设置区间为0-1000米，超过1000米按1000米召回。默认值为1000
                ;
        // 发起反地理编码请求，该方法必须在监听之后执行，否则会在某些场景出现拿不到回调结果的情况
        mSearch.reverseGeoCode(reverseGeoCodeOption);
    }


    /**
     * 设置普通模式
     */
    public void setNormalType(View v){
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        // 传入null，则为默认图标
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, null));
        MapStatus.Builder builder1 = new MapStatus.Builder();
        builder1.overlook(0);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
    }


    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // MapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            myLocationData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())// 设置定位数据的精度信息，单位：米
                    .direction(mCurrentDirection)// 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(myLocationData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }


    private BDAbstractLocationListener continuoueLocationListener = new BDAbstractLocationListener() {

        /**
         * 定位请求回调函数
         *  定位结果
         */
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {

                StringBuilder currentPosition = new StringBuilder();

                if(bdLocation.getLocType() == BDLocation.TypeGpsLocation){
                    currentPosition.append("GPS");
                //Toast.makeText(getApplicationContext(),"GPS",Toast.LENGTH_SHORT).show();

                }else if(bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                currentPosition.append("网络");
               // Toast.makeText(getApplicationContext(),"网络",Toast.LENGTH_SHORT).show();
                }
                String locationStr = LocationStr.getLocationStr(bdLocation);
                if (!TextUtils.isEmpty(locationStr)) {
                    currentPosition.append(locationStr);
                }
                mdepart_loc_tv.setText(currentPosition.toString());

            }
    };

    /**
     * 发起路线规划搜索示例
     */
   /* public void searchButtonProcess(View v) {
        // 重置浏览节点的路线数据
        mRouteLine = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        // 清除之前的覆盖物
        mBaidumap.clear();
        // 设置起终点信息 起点参数
        // 设置起终点信息，对于tranistsearch 来说，城市名无意义
        PlanNode startNode = PlanNode.withCityNameAndPlaceName(mEditStartCity.getText().toString().trim(),
                mStrartNodeView.getText().toString().trim());
        // 终点参数
        PlanNode endNode = PlanNode.withCityNameAndPlaceName(mEditEndCity.getText().toString().trim(),
                mEndNodeView.getText().toString().trim());
        mTransitRoutePlanOption.from(startNode) // 设置起点参数
                .city(mEditEndCity.getText().toString().trim()) // 设置换乘路线规划城市，起终点中的城市将会被忽略，这里写的是终点城市
                .to(endNode); // 设置终点
        // 发起换乘路线规划
        mSearch.transitSearch(mTransitRoutePlanOption);
    }*/


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
        // 释放检索对象
        mSearch.destroy();
        // 取消注册传感器监听
        mSensorManager.unregisterListener(this);
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        // 在activity执行onDestroy时必须调用mMapView.onDestroy()
        mMapView.onDestroy();
    }
}
