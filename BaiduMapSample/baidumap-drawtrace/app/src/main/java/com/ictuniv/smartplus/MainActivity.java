package com.ictuniv.smartplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ictuniv.smartplus.baidumap.DrawTraceActivity;
import com.ictuniv.smartplus.baidumap.LocationActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view){
        Intent i;
        switch (view.getId()) {
            case R.id.btn_way:
                i = new Intent(this, DrawTraceActivity.class);
                startActivity(i);
                Toast.makeText(MainActivity.this, "根据经纬度绘制轨迹", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_location:
                i = new Intent(this, LocationActivity.class);
                startActivity(i);
                Toast.makeText(MainActivity.this, "根据设备位置绘制轨迹，并上传位置数据", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
