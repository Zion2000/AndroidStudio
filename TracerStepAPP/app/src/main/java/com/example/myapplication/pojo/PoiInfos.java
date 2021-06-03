package com.example.myapplication.pojo;

import java.util.HashMap;

public class PoiInfos {

    private int _id; //primary key
    private String _PoiID;
    private int _Uid  ;
    private String  city ;
    private String   name ;
    private String   address;
    private Double   atitude ;
    private Double    longitude ;
    private String stuImg;
    private String details;

    public PoiInfos(int _id, String _PoiID, int _Uid, String city, String name, String address, Double atitude, Double longitude, String stuImg, String details) {
        this._id = _id;
        this._PoiID = _PoiID;
        this._Uid = _Uid;
        this.city = city;
        this.name = name;
        this.address = address;
        this.atitude = atitude;
        this.longitude = longitude;
        this.stuImg = stuImg;
        this.details = details;
    }

    public PoiInfos() {
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_PoiID() {
        return _PoiID;
    }

    public void set_PoiID(String _PoiID) {
        this._PoiID = _PoiID;
    }

    public int get_Uid() {
        return _Uid;
    }

    public void set_Uid(int _Uid) {
        this._Uid = _Uid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getAtitude() {
        return atitude;
    }

    public void setAtitude(Double atitude) {
        this.atitude = atitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getStuImg() {
        return stuImg;
    }

    public void setStuImg(String stuImg) {
        this.stuImg = stuImg;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "PoiInfos{" +
                "_id=" + _id +
                ", _PoiID='" + _PoiID + '\'' +
                ", _Uid=" + _Uid +
                ", city='" + city + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", atitude=" + atitude +
                ", longitude=" + longitude +
                ", stuImg=" + stuImg +
                ", details='" + details + '\'' +
                '}';
    }
}
