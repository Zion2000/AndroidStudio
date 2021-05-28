package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.util.DemoInfo;


public class LayersList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menulist);


        // 添加ListItem，设置事件响应
        ListView demoList = (ListView) findViewById(R.id.mapList);
        demoList.setAdapter(new DemoListAdapter(LayersList.this,DEMOS));
        demoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
                onListItemClick(index);
            }
        });
    }

    void onListItemClick(int index) {
        Intent intent;
        intent = new Intent(this, DEMOS[index].demoClass);
        this.startActivity(intent);
    }

    private static final DemoInfo[] DEMOS = {
            new DemoInfo(R.string.demo_title_locationtype, R.string.demo_desc_locationtype, LocationTypeDemo.class),
          //  new DemoInfo(R.string.demo_title_locationcustom, R.string.demo_desc_locationcustom, LocationCustomDemo.class)
    };
}