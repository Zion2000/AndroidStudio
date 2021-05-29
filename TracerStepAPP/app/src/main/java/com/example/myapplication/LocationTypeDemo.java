package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.baidu.mapapi.map.OverlayOptions;
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
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.example.myapplication.util.LocationStr;
import com.example.myapplication.util.PoiListAdapter;

import java.util.List;


/**
 * 展示定位图
 */

public class LocationTypeDemo extends AppCompatActivity implements SensorEventListener {

    // 定位相关
    private LocationClient mLocClient;
   // private MyLocationListener myListener = new MyLocationListener();
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
    // 点击模块，也可去掉地图模块独立使用
    private GeoCoder mSearch = null;
    private GeoCoder mSearch_depart = null;
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
    //弹出的list组件
    private RelativeLayout mPoiInfo;
    private ListView mPoiList;

    private List<PoiInfo> poiList;
    private MarkerOptions ooA;

    // 当前位置的坐标
    double mLat1 = 0;
    double mLon1 = 0;
    String mstreet ="null" ;
    String mcity ="null";
    // 目的地坐标
    double mLat2 = 0;
    double mLon2 = 0;
    String Endstreet ="null";
    String Endcity ="null";
    String Endname ="null";// 珠海市香洲区第十小学;
    String Endaddress ="null";//香洲区富柠街40号
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext()); //初始化
        setContentView(R.layout.activity_location_type);
        getSupportActionBar().hide();//隐藏标题栏
        // 初始化ui
        mPoiInfo = (RelativeLayout) findViewById(R.id.poi_info);
        mPoiList = (ListView) findViewById(R.id.poi_list);
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);
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

        // 初始化点击模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new mOnGetGeoCoderResultListener());

        mSearch_depart = GeoCoder.newInstance();
        mSearch_depart.setOnGetGeoCodeResultListener(new mResultListener());

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // 隐藏poiInfor 控件
                showNearbyPoiView(false);
            }

            @Override
            public void onMapPoiClick(MapPoi poi) {

            }
        });

    }

    /**
     * 定位初始化
     */
    public  void initLocation(){
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        //注册监听函数
     //   mLocClient.registerLocationListener(myListener);
        mLocClient.registerLocationListener(continuoueLocationListener);
        LocationClientOption locationOption = new LocationClientOption();


        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(0);
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(true);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(false);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        locationOption.setAddrType("all");

        mLocClient.setLocOption(locationOption);
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
     * 对地图事件的消息响应   取消单击事件
     */
    private void initListener() {
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            /**
             * 单击地图
             */
            @Override
            public void onMapClick(LatLng point) {
               /* mTouchType = "单击地图";
                mCurrentPt = point;
                updateMapState(null);*/
            }

            /**
             * 单击地图中的POI点
             */
            @Override
            public void onMapPoiClick(MapPoi poi) {
              /*  mTouchType = "单击POI点";
                mCurrentPt = poi.getPosition();
                updateMapState(null);*/
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
        //System.out.println("test1");
        String state;
        if (mCurrentPt == null) {
            state = "点击、长按、双击地图以获取经纬度和地图状态";
            mdestination_loc_tv.setText(state);
        } else {

            /*getmdestination.append("\n指定经度：");
            getmdestination.append(mCurrentPt.longitude);
            getmdestination.append("\n指定纬度：");
            getmdestination.append(mCurrentPt.latitude);*/

            ooA = new MarkerOptions().position(mCurrentPt).icon(BitmapDescriptorFactory.fromResource(R.drawable.markerblue));
            mBaiduMap.clear();
            mBaiduMap.addOverlay(ooA);
            searchProcess( mCurrentPt.longitude, mCurrentPt.latitude); //执行顺序 更新地图状态显示面板-> 发起搜索 ->逆地理编码查询回调结果

        }

    }

    /**
     * 发起搜索
     */
    public void searchProcess(double mEditLongitude,double mEditLatitude) {
       // System.out.println("test2");
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
                .radius(200)//  POI召回半径，允许设置区间为0-1000米，超过1000米按1000米召回。默认值为1000
                ;
        // 发起反地理编码请求，该方法必须在监听之后执行，否则会在某些场景出现拿不到回调结果的情况
        mSearch.reverseGeoCode(reverseGeoCodeOption);
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
            //System.out.println("test3");

            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(LocationTypeDemo.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
                return;
            }

            // 获取周边poi结果
            poiList = result.getPoiList();
            if (null != poiList && poiList.size() > 0){

                PoiListAdapter poiListAdapter = new PoiListAdapter(getApplicationContext(), poiList);
                mPoiList.setAdapter(poiListAdapter);
            //    System.out.println("poiList "+ poiList);
                showNearbyPoiView(true);
                mPoiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       // System.out.println("testwzw!~~#!@#!@#");
                        PoiInfo poiInfo = poiList.get(position);
                        if (poiInfo.getLocation() == null) {
                            return;
                        }
                        System.out.println(poiInfo);
                        Endname = poiInfo.name;
                        Endaddress =poiInfo.address;
                        addPoiLoction(poiInfo.getLocation());
                    }
                });

            }else {
                Toast.makeText(LocationTypeDemo.this, "周边没有poi", Toast.LENGTH_LONG).show();
                showNearbyPoiView(false);
            }
            Toast.makeText(LocationTypeDemo.this, result.getAddress() + " adcode: " + result.getAdcode(), Toast.LENGTH_SHORT).show();
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

            mLat2 = result.getLocation().latitude;
            mLon2 = result.getLocation().longitude;
            Endstreet =result.getAddressDetail().city;
            Endcity =result.getAddressDetail().street;
            getmdestination = new StringBuffer(256);
            getmdestination.append(mTouchType);
            getmdestination.append("\n指定经度：");
            getmdestination.append(result.getLocation().latitude);
            getmdestination.append("\n指定纬度：");
            getmdestination.append(result.getLocation().longitude);
            getmdestination.append("\n地理位置：") ;
            getmdestination.append(result.getAddressDetail().province);
            getmdestination.append(result.getAddressDetail().city);
            getmdestination.append(result.getAddressDetail().district);
            getmdestination.append(result.getAddressDetail().street);
            getmdestination.append(result.getAddressDetail().streetNumber);
            mdestination_loc_tv.setText(getmdestination);
        }
    }

    /**
     * 更新到子节点的位置
     *
     * @param latLng 子节点经纬度
     */
    private void addPoiLoction(LatLng latLng) {
        mBaiduMap.clear();

        ooA = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.markerblue));
        mBaiduMap.addOverlay(ooA);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng);
        builder.zoom(18.0f);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        searchProcess( latLng.longitude, latLng.latitude); //执行顺序 更新地图状态显示面板-> 发起搜索 ->逆地理编码查询回调结果
    }

    /**
     * 展示poi信息 view
     */
    private void showNearbyPoiView(boolean whetherShow) {
        if (whetherShow) {
            mPoiInfo.setVisibility(View.VISIBLE);
        } else {
            mPoiInfo.setVisibility(View.GONE);
        }
    }

    /**
     * 调入百度导航！ 无敌
     */
    public void setRoute(View v){
       /* mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        // 传入null，则为默认图标
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, null));
        MapStatus.Builder builder1 = new MapStatus.Builder();
        builder1.overlook(0);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));*/
     switch (v.getId()){
         case R.id.TransitRoute :
             if ( /*(mLat1 == 0 && mLon1== 0)||*/ (mLat2 == 0 && mLon2 == 0)){
                 Toast.makeText(this,"终点或起点不确定!",Toast.LENGTH_LONG).show();
                 break;
             }else if ("null".equals(mstreet.trim()) ){
                 Toast.makeText(this,"起点不确定!",Toast.LENGTH_LONG).show();
                 break;
             }
             startRoutePlanTransit();
             break;
         case R.id.DrivingRoute:
             if ( (mLat1 == 0 && mLon1== 0)/*|| (mLat2 == 0 && mLon2 == 0)*/){
                 Toast.makeText(this,"起点不确定!",Toast.LENGTH_LONG).show();
                 break;
             }else if (Endname.equals("null") || Endcity.equals("null")){
                 Toast.makeText(this,"终点不确定,请长按确认目的地 或 先选择附近展示噢!",Toast.LENGTH_LONG).show();
                 break;
             }
            // startRoutePlanDriving();
             break;
         case R.id.WalkingRoute:
             if ( (mLat1 == 0 && mLon1== 0)|| (mLat2 == 0 && mLon2 == 0) ){
                 Toast.makeText(this,"终点不确定，请长按确认目的地!",Toast.LENGTH_LONG).show();
                 break;
             }else if ("null".equals(Endaddress.trim()) || "null".equals(Endcity.trim())){
                 Toast.makeText(this,"终点不确定,请长按确认目的地 或 先选择附近展示噢!",Toast.LENGTH_LONG).show();
                 break;
             }
             startRoutePlanWalking();
             break;
     }
    }
    /**
     * 启动百度地图公交路线规划
     */
    public void startRoutePlanTransit() {

        LatLng ptEnd = new LatLng(mLat2, mLon2);
        // 构建 route搜索参数
        RouteParaOption routeParaOption = new RouteParaOption()
                .startName(mstreet)
                .endPoint(ptEnd)
                .busStrategyType(RouteParaOption.EBusStrategyType.bus_recommend_way);

        try {
            BaiduMapRoutePlan.openBaiduMapTransitRoute(routeParaOption, this);
        } catch (Exception e) {
            e.printStackTrace();
            showDialog();
        }
    }

    /**
     * 启动百度地图驾车路线规划
     */
    public void startRoutePlanDriving() {
        LatLng ptStart= new LatLng(mLat1, mLon1);

        // 构建 route搜索参数
        RouteParaOption routeParaOption = new RouteParaOption()
                .startPoint(ptStart)
                .endName(Endname)
                .cityName(Endcity);

        try {
            BaiduMapRoutePlan.openBaiduMapDrivingRoute(routeParaOption, this);
        } catch (Exception e) {
            e.printStackTrace();
            showDialog();
        }
    }
    /**
     * 启动百度地图步行路线规划
     */
    public void startRoutePlanWalking() {
        LatLng ptStart= new LatLng(mLat1, mLon1);
        // 构建 route搜索参数
        RouteParaOption routeParaOption = new RouteParaOption()
                .startPoint(ptStart)
                .endName(Endaddress)
                .cityName(Endcity);
        try {
            BaiduMapRoutePlan.openBaiduMapWalkingRoute(routeParaOption, this);
        } catch (Exception e) {
            e.printStackTrace();
            showDialog();
        }
    }

    /**
     * 提示未安装百度地图app或app版本过低
     */
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OpenClientUtil.getLatestBaiduMapApp(LocationTypeDemo.this);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
    /**
     * 定位SDK监听函数   不能异步    BDAbstractLocationListener可以异步定位~
     */
    StringBuilder currentPosition =null;//自定义获取详情地址
 /*   public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            currentPosition  = new StringBuilder(256);
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
                getdata(ll);
            }
            System.out.println("test4 ");


        }
    }*/


    private BDAbstractLocationListener continuoueLocationListener = new BDAbstractLocationListener() {

        /**
         * 定位请求回调函数
         *  定位结果
         */
            @Override
            public void onReceiveLocation(BDLocation location) {

                currentPosition  = new StringBuilder(256);
                mLat1 = location.getLatitude();
                mLon1 = location.getLongitude();
                if(location.getLocType() == BDLocation.TypeGpsLocation){
                    currentPosition.append("GPS");
                    //Toast.makeText(getApplicationContext(),"GPS",Toast.LENGTH_SHORT).show();

                }else if(location.getLocType() == BDLocation.TypeNetWorkLocation){
                    currentPosition.append("网络");
                    // Toast.makeText(getApplicationContext(),"网络",Toast.LENGTH_SHORT).show();
                }
                String locationStr = LocationStr.getLocationStr(location);
                if (!TextUtils.isEmpty(locationStr)) {
                    currentPosition.append(locationStr);
                }
                // MapView 销毁后不在处理新接收的位置
                if (location == null || mMapView == null) {
                    return;
                }

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
                    getdata(ll);
                }

            }
    };

    /**
     * 定位后获取当前经纬度，根据经纬度 采用反编码 获取当前位置信息
     */
    private void getdata(LatLng ll) {
        // 设置地理编码检索监听者
        mSearch_depart.reverseGeoCode(new ReverseGeoCodeOption().location(ll).newVersion(1));
       // System.out.println("test2");
    }
    class mResultListener implements OnGetGeoCoderResultListener {
        // 反地理编码查询结果回调函数
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null
                    || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // 没有检测到结果
                return;
            }
           // System.out.println("test1");
            mstreet = result.getAddressDetail().street;
            mcity   = result.getAddressDetail().city;
            currentPosition.append("\n");
            currentPosition.append(result.getAddressDetail().province);
            currentPosition.append(result.getAddressDetail().city);
            currentPosition.append(result.getAddressDetail().district);

            currentPosition.append(result.getAddressDetail().street);
            currentPosition.append(result.getAddressDetail().streetNumber);
            mdepart_loc_tv.setText(currentPosition);

        }

        // 地理编码查询结果回调函数
        @Override
        public void onGetGeoCodeResult(GeoCodeResult result) {

        }
    };





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
