package com.example.myapplication.util;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiChildrenInfo;
import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.example.myapplication.R;

import java.util.List;



public class PoiListAdapter extends BaseAdapter  {

    private Context mcontext;
    private List<PoiInfo> mPoilist;
    private OnGetChildrenLocationListener mOnGetChildrenLocationListener = null;

    public PoiListAdapter(Context mcontext, List<PoiInfo> mPoilist) {
        this.mcontext = mcontext;
        this.mPoilist = mPoilist;
    }

    @Override
    public int getCount() {
        return mPoilist.size();
    }

    @Override
    public Object getItem(int position) {
        return mPoilist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //当没有使用LayoutInflater进行View的扩充的时候，是没有必要用的，虽然也可以用。
        //这样提高性能~
          ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mcontext, R.layout.poi_item, null);
            viewHolder.poiName = (TextView) convertView.findViewById(R.id.poi_name);
            viewHolder.poiAddress = (TextView) convertView.findViewById(R.id.poi_address);
           // viewHolder.poiChilderList = (GridView) convertView.findViewById(R.id.childer_poi_gridview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.poiName.setText(mPoilist.get(position).getName());
        viewHolder.poiName.setTextColor(Color.rgb(65,105,225));
        viewHolder.poiAddress.setText(mPoilist.get(position).getAddress());
        viewHolder.poiAddress.setTextColor(Color.rgb(65,105,225));




        return convertView;
    }




    private static class ViewHolder {
        TextView poiName;
        TextView poiAddress;
       // GridView poiChilderList;
    }


    public interface OnGetChildrenLocationListener {
        void getChildrenLocation(LatLng childrenLocation);
    }

    public void setOnGetChildrenLocationListener(OnGetChildrenLocationListener onGetChildrenLocationListener) {
        this.mOnGetChildrenLocationListener = onGetChildrenLocationListener;
    }

}