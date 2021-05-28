package com.example.myapplication.util;

import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.Poi;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class LocationStr {

    public static String getLocationStr(BDLocation location) {
        if (null == location) {
            return null;
        }
        StringBuffer sb = new StringBuffer(256);
       /* sb.append("\n定位时间 : ");
        sb.append(location.getTime());
        sb.append("\n回调时间: " + formatDateTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
        sb.append("\n定位类型 : ");// 定位类型*/
        sb.append(location.getLocType());
        sb.append("\n经度 : ");// 纬度
        sb.append(location.getLongitude());
        sb.append("\n纬度 : ");// 经度
        sb.append(location.getLatitude());
        sb.append("\n城市 : ");// 城市
        sb.append(location.getCity());
        sb.append("\n区县 : ");// 区
        sb.append(location.getDistrict());
      /*  sb.append("\n精度 : ");// 半径
        sb.append(location.getRadius());

        sb.append("\n方向: ");
        sb.append(location.getDirection());// 方向
        sb.append("\n国家编码 : ");// 国家码
        sb.append(location.getCountryCode());
        sb.append("\n国家 : ");// 国家名称
        sb.append(location.getCountry());
        sb.append("\n省份 : ");// 获取省份
        sb.append(location.getProvince());
        sb.append("\n城市编码 : ");// 城市编码
        sb.append(location.getCityCode());
        sb.append("\n城市 : ");// 城市
        sb.append(location.getCity());
        sb.append("\n区县 : ");// 区
        sb.append(location.getDistrict());
        sb.append("\n乡镇街道 : ");// 获取镇信息
        sb.append(location.getTown());
        sb.append("\n地址 : ");// 地址信息
        sb.append(location.getAddrStr());
        sb.append("\n附近街道 : ");// 街道
        sb.append(location.getStreet());
        sb.append("\n室内外结果 : ");// *****返回用户室内外判断结果*****
        sb.append(location.getUserIndoorState());
        sb.append("\n位置语义化 : ");
        sb.append(location.getLocationDescribe());// 位置语义化信息*/


        return  sb.toString();
    }


    private static SimpleDateFormat simpleDateFormat = null;
    public  static String formatDateTime(long time, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (simpleDateFormat == null) {
            try {
                simpleDateFormat = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
            }
        } else {
            simpleDateFormat.applyPattern(strPattern);
        }
        return simpleDateFormat == null ? "NULL" : simpleDateFormat.format(time);
    }
}
