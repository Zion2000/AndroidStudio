package com.example.myapplication.PoiSelect;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
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
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.util.LocationStr;
import com.example.myapplication.util.MyDatabaseHelper;
import com.example.myapplication.util.PoiListAdapter;

import java.util.List;


/**
 * ???????????????
 */

public class LocationType extends AppCompatActivity implements SensorEventListener {

    // ????????????
    private LocationClient mLocClient;
   // private MyLocationListener myListener = new MyLocationListener();
    // ????????????????????????
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    // ???????????????
    private MapView mMapView;// MapView ??????????????????
    private BaiduMap mBaiduMap;
    private TextView mdestination_loc_tv;
    private TextView mdepart_loc_tv;
    private LatLng mCurrentPt;
    private String mTouchType;
    // ?????????????????????????????????
    // private BitmapDescriptor mbitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
    // ???????????????????????????????????????????????????
    private GeoCoder mSearch = null;
    private GeoCoder mSearch_depart = null;

    // ??????????????????
    private boolean isFirstLoc = true;
    // ????????????????????????
    private MyLocationData myLocationData;

    // ????????????????????????
    private Button mBtnPre = null; // ???????????????
    private Button mBtnNext = null; // ???????????????
    private RouteLine mRouteLine = null;
    //private OverlayManager mRouteOverlay = null;
    private boolean useDefaultIcon = false;
    // ???????????????????????????MapView???MyRouteMapView???????????????touch????????????????????????
    // ???????????????touch???????????????????????????????????????MapView??????
    private BaiduMap mBaidumap = null;
    //?????????list??????
    private RelativeLayout mPoiInfo;
    private ListView mPoiList;

    private List<PoiInfo> poiList;
    private MarkerOptions ooA;

    // ?????????????????????
    double mLat1 = 0;
    double mLon1 = 0;
    String mstreet ="null" ;
    String mcity ="null";
    // ???????????????
    double mLat2 = 0;
    double mLon2 = 0;
    String Endstreet ="null";
    String Endcity ="null";
    String Endname ="null";// ??????????????????????????????;
    String Endaddress =null;//??????????????????40???
    String EndpoiID = null;
    String Endtag =null;
    double EndLatitude =0;
    double EndLongitude =0 ;

    Intent intent ;
    private MyDatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext()); //?????????
        setContentView(R.layout.activity_location_type);
        getSupportActionBar().hide();//???????????????
        // ?????????ui
        mPoiInfo = (RelativeLayout) findViewById(R.id.poi_info);
        mPoiList = (ListView) findViewById(R.id.poi_list);
        // ???????????????
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        // ???????????????????????????
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        // ??????????????????????????????????????????
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);

        // ???????????????
        initLocation();

        mdestination_loc_tv = findViewById(R.id.destination_loc_tv);
        mdepart_loc_tv = findViewById(R.id.depart_loc_tv);

        initListener();

        // ??????????????????????????????????????????
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new mOnGetGeoCoderResultListener());

        mSearch_depart = GeoCoder.newInstance();
        mSearch_depart.setOnGetGeoCodeResultListener(new mResultListener());

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // ??????poiInfor ??????
                showNearbyPoiView(false);
            }

            @Override
            public void onMapPoiClick(MapPoi poi) {

            }
        });

        //?????????~
        databaseHelper = new MyDatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();
    }

    /**
     * ???????????????
     */
    public  void initLocation(){
        // ??????????????????
        mBaiduMap.setMyLocationEnabled(true);
        // ???????????????
        mLocClient = new LocationClient(this);
        //??????????????????
     //   mLocClient.registerLocationListener(myListener);
        mLocClient.registerLocationListener(continuoueLocationListener);
        LocationClientOption locationOption = new LocationClientOption();


        //?????????????????????????????????????????????????????????????????????????????????
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //???????????????gcj02??????????????????????????????????????????????????????????????????????????????????????????bd09ll;
        locationOption.setCoorType("bd09ll");
        //???????????????0?????????????????????????????????????????????????????????????????????????????????1000ms???????????????
        locationOption.setScanSpan(0);
        //?????????????????????????????????????????????????????????
        locationOption.setIsNeedAddress(true);
        //???????????????????????????????????????
        locationOption.setIsNeedLocationDescribe(true);
        //?????????????????????????????????????????????
        locationOption.setNeedDeviceDirect(true);
        //???????????????false??????????????????gps???????????????1S1???????????????GPS??????
        locationOption.setLocationNotify(true);
        //???????????????true?????????SDK???????????????SERVICE?????????????????????????????????????????????stop?????????????????????????????????????????????
        locationOption.setIgnoreKillProcess(true);
        //???????????????false??????????????????????????????????????????????????????BDLocation.getLocationDescribe?????????????????????????????????????????????????????????
        locationOption.setIsNeedLocationDescribe(false);
        //???????????????false?????????????????????POI??????????????????BDLocation.getPoiList?????????
        locationOption.setIsNeedLocationPoiList(true);
        //???????????????false?????????????????????CRASH?????????????????????
        locationOption.SetIgnoreCacheException(false);
        //???????????????false?????????????????????Gps??????
        locationOption.setOpenGps(true);
        //???????????????false?????????????????????????????????????????????????????????????????????????????????????????????
        locationOption.setIsNeedAltitude(false);
        //??????????????????????????????????????????????????????????????????????????????SDK????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????SDK??????????????????????????????????????????????????????
        locationOption.setOpenAutoNotifyMode();
        //??????????????????????????????????????????????????????????????????????????????SDK???????????????????????????????????????????????????
        locationOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        locationOption.setAddrType("all");

        mLocClient.setLocOption(locationOption);
        mLocClient.start();
    }

    /*
    * ??????????????????????????????????????????????????????
    * */
    public void setting_btn(View view) {
        switch (view.getId()){
            case R.id.travel_btn:
                //System.out.println("test"+EndpoiID);
                if (EndpoiID == null){
                    Toast.makeText(LocationType.this, "??????????????????poi", Toast.LENGTH_LONG).show();
                    break;
                }else if (Endaddress == null){
                    Toast.makeText(LocationType.this, "??????????????????poi", Toast.LENGTH_LONG).show();
                    break;
                }
                AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                        .setTitle(Endname)
                        .setMessage("???????????????"+Endaddress+"???"+Endname+"\n????????????????????????Poi??????")
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("?????????", new DialogInterface.OnClickListener() {//??????"Yes"??????
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db = databaseHelper.getReadableDatabase();
                                ContentValues cv = new ContentValues();
                                cv.put("_PoiID",EndpoiID);
                                cv.put("_Uid",1);
                                cv.put("city",Endcity);
                                cv.put("name",Endname);
                                cv.put("address",Endaddress);
                                cv.put("latitude",EndLatitude);
                                cv.put("longitude",EndLongitude);
                                db.insert("PoiInfo",null,cv);
                                Toast.makeText(LocationType.this, "???????????????~", Toast.LENGTH_LONG).show();

                               // db.close();
                            }
                        })

                        .setNegativeButton("??????", new DialogInterface.OnClickListener() {//????????????
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               // Toast.makeText(LocationTypeDemo.this, "??????????????????", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create();
                alertDialog2.show();

                break;
            case R.id.Info_btn:
               intent = new Intent(this, InfoActivity.class);
                if (EndpoiID != null){
                    intent.putExtra("EndpoiID", EndpoiID);
                    intent.putExtra("Endaddress", Endaddress);
                    intent.putExtra("Endname", Endname);
                    intent.putExtra("Endtag",Endtag);
                }

                startActivity(intent);
                break;
            case R.id.User_btn:
                //??????????????????  ????????????
                intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            myLocationData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)// ????????????????????????????????????????????????
                    .direction(mCurrentDirection)// ?????????????????????????????????????????????????????????0-360
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
     * ??????????????????????????????   ??????????????????
     */
    private void initListener() {
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            /**
             * ????????????
             */
            @Override
            public void onMapClick(LatLng point) {
               /* mTouchType = "????????????";
                mCurrentPt = point;
                updateMapState(null);*/
            }

            /**
             * ??????????????????POI???
             */
            @Override
            public void onMapPoiClick(MapPoi poi) {
              /*  mTouchType = "??????POI???";
                mCurrentPt = poi.getPosition();
                updateMapState(null);*/
            }
        });
        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            /**
             * ????????????
             */
            @Override
            public void onMapLongClick(LatLng point) {
                mTouchType = "??????";
                mCurrentPt = point;
                updateMapState(null);
            }
        });
        mBaiduMap.setOnMapDoubleClickListener(new BaiduMap.OnMapDoubleClickListener() {
            /**
             * ????????????
             */
            @Override
            public void onMapDoubleClick(LatLng point) {
                mTouchType = "??????";
                mCurrentPt = point;
                updateMapState(null);
            }
        });
    }
    /**
     * ??????????????????????????????
     */
    StringBuffer getmdestination ;//???????????????????????????
    private void updateMapState(String str) {
        //System.out.println("test1");
        String state;
        if (mCurrentPt == null) {
            state = "???????????????????????????????????????????????????????????????";
            mdestination_loc_tv.setText(state);
        } else {
            /*getmdestination.append("\n???????????????");
            getmdestination.append(mCurrentPt.longitude);
            getmdestination.append("\n???????????????");
            getmdestination.append(mCurrentPt.latitude);*/
            ooA = new MarkerOptions().position(mCurrentPt).icon(BitmapDescriptorFactory.fromResource(R.drawable.markerblue));
            mBaiduMap.clear();
            mBaiduMap.addOverlay(ooA);
            searchProcess( mCurrentPt.longitude, mCurrentPt.latitude); //???????????? ??????????????????????????????-> ???????????? ->?????????????????????????????????

        }

    }
    /**
     * ????????????
     */
    public void searchProcess(double mEditLongitude,double mEditLatitude) {
       // System.out.println("test2");
        LatLng ptCenter = new LatLng((Float.valueOf((float) mEditLatitude)), (Float.valueOf((float) mEditLongitude)));
        //int radius = Integer.parseInt(mEditRadius);
       /* int version = 0;
        // ???Geo??????
        if (mNewVersionCB.isChecked()) {
            version = 1;
        }*/
        ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption()
                .location(ptCenter) // ?????????????????????????????????
                .newVersion(1) // ??????????????????????????? ?????????0????????????1??????
                .radius(200)//  POI????????????????????????????????????0-1000????????????1000??????1000????????????????????????1000
                ;
        // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        mSearch.reverseGeoCode(reverseGeoCodeOption);
    }
    /**
     * ?????????????????????????????????
     */
    class mOnGetGeoCoderResultListener implements OnGetGeoCoderResultListener{
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            //System.out.println("test3");

            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(LocationType.this, "???????????????????????????", Toast.LENGTH_LONG).show();
                return;
            }

            // ????????????poi??????
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
                        EndpoiID  = poiInfo.uid;
                        Endtag    = poiInfo.tag;
                        EndLatitude =poiInfo.getLocation().latitude;
                        EndLongitude =poiInfo.getLocation().longitude;
                        addPoiLoction(poiInfo.getLocation());
                    }
                });

            }else {
                Toast.makeText(LocationType.this, "????????????poi", Toast.LENGTH_LONG).show();
                showNearbyPoiView(false);
            }
           // Toast.makeText(LocationTypeDemo.this, result.getAddress() + " adcode: " + result.getAdcode(), Toast.LENGTH_SHORT).show();
            //get???????????????????????????
            /*
            * 	city
                ????????????
                java.lang.String	district
                ????????????
                java.lang.String	province
                ????????????
                java.lang.String	street
                ????????????
                java.lang.String	streetNumber
                 ????????????
            * */

            mLat2 = result.getLocation().latitude;
            mLon2 = result.getLocation().longitude;
            Endcity =result.getAddressDetail().city;
            Endstreet =result.getAddressDetail().street;
            getmdestination = new StringBuffer(256);
            getmdestination.append(mTouchType);
            getmdestination.append("\n???????????????");
            getmdestination.append(result.getLocation().latitude);
            getmdestination.append("\n???????????????");
            getmdestination.append(result.getLocation().longitude);
            getmdestination.append("\n???????????????") ;
            getmdestination.append(result.getAddressDetail().province);
            getmdestination.append(result.getAddressDetail().city);
            getmdestination.append(result.getAddressDetail().district);
            getmdestination.append(result.getAddressDetail().street);
            getmdestination.append(result.getAddressDetail().streetNumber);
            mdestination_loc_tv.setText(getmdestination);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param latLng ??????????????????
     */
    private void addPoiLoction(LatLng latLng) {
        mBaiduMap.clear();

        ooA = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.markerblue));
        mBaiduMap.addOverlay(ooA);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng);
        builder.zoom(18.0f);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        searchProcess( latLng.longitude, latLng.latitude); //???????????? ??????????????????????????????-> ???????????? ->?????????????????????????????????
    }

    /**
     * ??????poi?????? view
     */
    private void showNearbyPoiView(boolean whetherShow) {
        if (whetherShow) {
            mPoiInfo.setVisibility(View.VISIBLE);
        } else {
            mPoiInfo.setVisibility(View.GONE);
        }
    }

    /**
     * ????????????????????? ??????
     */
    public void setRoute(View v){
       /* mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        // ??????null?????????????????????
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, null));
        MapStatus.Builder builder1 = new MapStatus.Builder();
        builder1.overlook(0);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));*/
     switch (v.getId()){
         case R.id.TransitRoute :
             if ( /*(mLat1 == 0 && mLon1== 0)||*/ (mLat2 == 0 && mLon2 == 0)){
                 Toast.makeText(this,"????????????????????????!",Toast.LENGTH_LONG).show();
                 break;
             }else if ("null".equals(mstreet.trim()) ){
                 Toast.makeText(this,"???????????????!",Toast.LENGTH_LONG).show();
                 break;
             }
             startRoutePlanTransit();
             break;
         case R.id.DrivingRoute:
             if ( (mLat1 == 0 && mLon1== 0)/*|| (mLat2 == 0 && mLon2 == 0)*/){
                 Toast.makeText(this,"???????????????!",Toast.LENGTH_LONG).show();
                 break;
             }else if (Endname.equals("null") || Endcity.equals("null")){
                 Toast.makeText(this,"???????????????,???????????????????????? ??? ????????????????????????!",Toast.LENGTH_LONG).show();
                 break;
             }
            // startRoutePlanDriving();
             break;
         case R.id.WalkingRoute:
             if ( (mLat1 == 0 && mLon1== 0)|| (mLat2 == 0 && mLon2 == 0) ){
                 Toast.makeText(this,"??????????????????????????????????????????!",Toast.LENGTH_LONG).show();
                 break;
             }else if ("null".equals(Endaddress.trim()) || "null".equals(Endcity.trim())){
                 Toast.makeText(this,"???????????????,???????????????????????? ??? ????????????????????????!",Toast.LENGTH_LONG).show();
                 break;
             }
             startRoutePlanWalking();
             break;
     }
    }
    /**
     * ????????????????????????????????????
     */
    public void startRoutePlanTransit() {

        LatLng ptEnd = new LatLng(mLat2, mLon2);
        // ?????? route????????????
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
     * ????????????????????????????????????
     */
    public void startRoutePlanDriving() {
        LatLng ptStart= new LatLng(mLat1, mLon1);

        // ?????? route????????????
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
     * ????????????????????????????????????
     */
    public void startRoutePlanWalking() {
        LatLng ptStart= new LatLng(mLat1, mLon1);
        // ?????? route????????????
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
     * ???????????????????????????app???app????????????
     */
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("???????????????????????????app???app????????????????????????????????????");
        builder.setTitle("??????");
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OpenClientUtil.getLatestBaiduMapApp(LocationType.this);
            }
        });

        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
    /**
     * ??????SDK????????????   ????????????    BDAbstractLocationListener??????????????????~
     */
    StringBuilder currentPosition =null;//???????????????????????????
 /*   public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            currentPosition  = new StringBuilder(256);
            // MapView ???????????????????????????????????????
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            myLocationData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())// ????????????????????????????????????????????????
                    .direction(mCurrentDirection)// ?????????????????????????????????????????????????????????0-360
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

    /**
     * ????????????????????????
     *  ????????????
     */
    private BDAbstractLocationListener continuoueLocationListener = new BDAbstractLocationListener() {


            @Override
            public void onReceiveLocation(BDLocation location) {
                currentPosition  = new StringBuilder(256);
                mLat1 = location.getLatitude();
                mLon1 = location.getLongitude();
                if(location.getLocType() == BDLocation.TypeGpsLocation){
                    currentPosition.append("GPS");
                    //Toast.makeText(getApplicationContext(),"GPS",Toast.LENGTH_SHORT).show();

                }else if(location.getLocType() == BDLocation.TypeNetWorkLocation){
                    currentPosition.append("??????");
                    // Toast.makeText(getApplicationContext(),"??????",Toast.LENGTH_SHORT).show();
                }
                String locationStr = LocationStr.getLocationStr(location);
                if (!TextUtils.isEmpty(locationStr)) {
                    currentPosition.append(locationStr);
                }
                // MapView ???????????????????????????????????????
                if (location == null || mMapView == null) {
                    return;
                }

                myLocationData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())// ????????????????????????????????????????????????
                        .direction(mCurrentDirection)// ?????????????????????????????????????????????????????????0-360
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
     * ???????????????????????????????????????????????? ??????????????? ????????????????????????
     */
    private void getdata(LatLng ll) {
        // ?????????????????????????????????
        mSearch_depart.reverseGeoCode(new ReverseGeoCodeOption().location(ll).newVersion(1));
       // System.out.println("test2");
    }
    class mResultListener implements OnGetGeoCoderResultListener {
        // ???????????????????????????????????????
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null
                    || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // ?????????????????????
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

        // ????????????????????????????????????
        @Override
        public void onGetGeoCodeResult(GeoCodeResult result) {

        }
    };





    @Override
    protected void onResume() {
        super.onResume();
        // ???activity??????onResume???????????????mMapView. onResume ()
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // ???activity??????onPause???????????????mMapView. onPause ()
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ??????????????????
        mSearch.destroy();
        // ???????????????????????????
        mSensorManager.unregisterListener(this);
        // ?????????????????????
        mLocClient.stop();
        // ??????????????????
        mBaiduMap.setMyLocationEnabled(false);
        // ???activity??????onDestroy???????????????mMapView.onDestroy()
        mMapView.onDestroy();
        // ??????poi???id
        EndpoiID = null;
    }
}
