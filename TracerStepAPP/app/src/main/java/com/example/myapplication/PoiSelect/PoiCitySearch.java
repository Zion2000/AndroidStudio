package com.example.myapplication.PoiSelect;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.example.myapplication.R;
import com.example.myapplication.util.KeybordUtil;
import com.example.myapplication.util.PoiListAdapter;
import com.example.myapplication.util.PoiOverlay;

import java.util.List;


/**
 * 演示城市检索poi以及父子节点的展示
 */
public class PoiCitySearch extends AppCompatActivity implements OnGetPoiSearchResultListener,
        OnGetSuggestionResultListener, AdapterView.OnItemClickListener, PoiListAdapter.OnGetChildrenLocationListener {

    private PoiSearch mPoiSearch = null;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    // 搜索关键字输入窗口
    private EditText mEditCity = null;
    private Button goToNextPage = null;
    private Button finish_btn = null;

    private AutoCompleteTextView mKeyWordsView = null;
    private RelativeLayout mPoiDetailView;
    private ListView mPoiList;

    // 分页
    private int mLoadIndex = 0;
    private List<PoiInfo> mAllPoi;
    private BitmapDescriptor mBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
    //poi INFO
    private String uid;
    private String address;
    private String city;
    private String name;
    private double latitude;
    private double longitude;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poicitysearch);
        getSupportActionBar().hide();//隐藏标题栏
        // 创建map
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        // 创建poi检索实例，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mEditCity = (EditText) findViewById(R.id.city);
        mKeyWordsView = (AutoCompleteTextView) findViewById(R.id.searchkey);

        // 展示父子节点控件
        mPoiDetailView = (RelativeLayout) findViewById(R.id.poi_detail);
        mPoiList = (ListView) findViewById(R.id.poi_list);
        goToNextPage = (Button) findViewById(R.id.goToNextPage);
        finish_btn = (Button) findViewById(R.id.finish_btn);
        mPoiList.setOnItemClickListener(this);
        // 地图点击事件
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                showPoiDetailView(false);
            }

            @Override
            public void onMapPoiClick(MapPoi poi) {

            }
        });
    }

    /**
     * 响应城市内搜索按钮点击事件
     * 检索Button
     */
    public void searchButtonProcess(View v) {
        //  按搜索按钮时隐藏软件盘，为了在结果回调时计算 PoiDetailView 控件的高度，把地图中poi展示到合理范围内
        KeybordUtil.closeKeybord(this);
        // 获取检索城市
        String cityStr = mEditCity.getText().toString();
        // 获取检索关键字
        String keyWordStr = mKeyWordsView.getText().toString();
        // 发起请求
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(cityStr)
                .keyword(keyWordStr)
                .pageNum(mLoadIndex) // 分页编号
                .cityLimit(true) // 区域数据召回限制为true时，仅召回city对应区域内数据
                .scope(2));   // 检索结果详细程度：取值为1 或空，则返回基本信息；取值为2，返回检索POI详细信息



    }

    /**
     * 下一页
     */
    public void goToNextPage(View v) {
        mLoadIndex++;
        searchButtonProcess(null);
    }

    /**
     * 获取城市poi检索结果
     * 地图上标的marker啥的都是这个方法
     * @param result poi查询结果
     */
    @Override
    public void onGetPoiResult(final PoiResult result) {
        System.out.println("test result:"+result);
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            mLoadIndex = 0;
            mBaiduMap.clear();
            showPoiDetailView(false);
            Toast.makeText(PoiCitySearch.this, "未找到结果", Toast.LENGTH_LONG).show();
            return;
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            showPoiDetailView(true);
            mBaiduMap.clear();

            // 监听 View 绘制完成后获取view的高度
            mPoiDetailView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int padding = 50;
                    // 添加poi
                    PoiOverlay overlay = new PoiCitySearch.MyPoiOverlay(mBaiduMap);
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(result);
                    overlay.addToMap();//添加marker
                    // 获取 view 的高度
                    int PaddingBootom = mPoiDetailView.getMeasuredHeight();
                    // 设置显示在规定宽高中的地图地理范围
                    overlay.zoomToSpanPaddingBounds(padding,padding,padding,PaddingBootom);
                    // 加载完后需要移除View的监听，否则会被多次触发
                    mPoiDetailView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

            // 获取poi结果
            mAllPoi = result.getAllPoi();
            PoiListAdapter poiListAdapter = new PoiListAdapter(this, mAllPoi);
            poiListAdapter.setOnGetChildrenLocationListener(this);
            // 把poi结果添加到适配器
            mPoiList.setAdapter(poiListAdapter);

            return;
        }

        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";

            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            Toast.makeText(PoiCitySearch.this, strInfo, Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult result) {

    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {

    }

    /**
     * poilist 点击处理
     *
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PoiInfo poiInfo = mAllPoi.get(position);
        if (poiInfo.getLocation() == null) {
            return;
        }
        address = poiInfo.getAddress();
        uid = poiInfo.getUid();
        city = poiInfo.getCity();
        name = poiInfo.getName();
        latitude = poiInfo.getLocation().latitude;
        longitude = poiInfo.getLocation().longitude;

        addPoiLoction(poiInfo.getLocation());
        goToNextPage.setEnabled(true);
        finish_btn.setEnabled(true);
    }

    /**
     * 点击子节点list 获取经纬添加poi更新地图
     *
     * @param childrenLocation 子节点经纬度
     */
    @Override
    public void getChildrenLocation(LatLng childrenLocation) {

        addPoiLoction(childrenLocation);
    }


    /**
     * 更新到子节点的位置
     *
     * latLng 子节点经纬度
     */
    private void addPoiLoction(LatLng latLng) {
        mBaiduMap.clear();
        showPoiDetailView(false);
        OverlayOptions markerOptions = new MarkerOptions().position(latLng).icon(mBitmap);
        mBaiduMap.addOverlay(markerOptions);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng);
        builder.zoom(18.0f);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     *
     * @return
     * @create 2021/6/1 17:09
     * @Param
     */
    public void finish(View view) {

        AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                .setTitle("确定是这儿咯？")
                .setMessage("您已选择："+address+"您确定是这儿？？")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("是的！", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        //name相当于一个key,content是返回的内容
                        intent.putExtra("address",address);
                        intent.putExtra("uid",uid);
                        intent.putExtra("city",city);
                        intent.putExtra("name",name);
                        intent.putExtra("latitude",latitude);
                        intent.putExtra("longitude",longitude);

                        //resultCode是返回码,用来确定是哪个页面传来的数据，这里设置返回码是2
                        //这个页面传来数据,要用到下面这个方法setResult(int resultCode,Intent intent)
                        setResult(2,intent);
                        //结束当前页面
                        finish();

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


    protected class MyPoiOverlay extends PoiOverlay {
        MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        /**
         * 点击搜索后地图上的marker事件  new OnMapClickListener()
	     *  @return boolean
         * @create 2021/6/2 13:25
         * @Param
         */
        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            Toast.makeText(PoiCitySearch.this,poi.address, Toast.LENGTH_LONG).show();
            return true;
        }
    }


    /**
     * 是否展示详情 view
     *
     */
    private void showPoiDetailView(boolean whetherShow) {
        if (whetherShow) {
            mPoiDetailView.setVisibility(View.VISIBLE);
        } else {
            mPoiDetailView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 隐藏控件
        showPoiDetailView(false);
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放bitmap
        mBitmap.recycle();
        // 释放检索对象
        mPoiSearch.destroy();
        // 清空地图所有的覆盖物
        mBaiduMap.clear();
        // 释放地图组件
        mMapView.onDestroy();
    }
}
