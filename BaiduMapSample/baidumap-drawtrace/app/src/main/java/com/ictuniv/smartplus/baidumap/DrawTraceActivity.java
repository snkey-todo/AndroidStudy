package com.ictuniv.smartplus.baidumap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.ictuniv.smartplus.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据模拟的数据点（经度，维度）来绘制连线轨迹，并为每个点设置标记
 */
public class DrawTraceActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "DrawTraceActivity";
    MapView mMapView;
    BaiduMap mBaiduMap;
    private Button drawBtn;
    private double latitude = 22.600655;
    private double longtitute = 114.308644;

    List<LatLng> points = new ArrayList<LatLng>();
    private SimulateThread mThread;
    private LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_trace);

        initViews();
    }


    private void initViews() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        drawBtn = (Button) findViewById(R.id.button1);
        drawBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1: //使用模拟数据绘制轨迹
                mMapView.getMap().clear();
                mThread = new SimulateThread();
                mThread.start();
                break;
        }
    }

    /**
     * 绘制轨迹
     *
     * @param points
     */
    private void drawLine(List<LatLng> points) {
        OverlayOptions ooPolyline = new PolylineOptions()
                .width(8)
                .color(0xAAFF0000)
                .points(points);
        int size = points.size();
        LatLng point = points.get(size - 1);

        Polyline mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);

        mPolyline.setDottedLine(true);//设置为虚线绘制
        //setMarker(point); //设置标记
        update(point);   //移动到最后一个点的位置

    }

    /**
     * 添加marker
     */
    private void setMarker(LatLng point) {

        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding40);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    /**
     * 更新显示位置
     *
     * @param point
     */
    private void update(LatLng point) {
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(point)
                .zoom(15)   //缩放比例
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
       // mBaiduMap.setMapStatus(mMapStatusUpdate);
        mBaiduMap.animateMapStatus(mMapStatusUpdate);
    }

    /**
     * 异步模拟位置数据
     */
    private class SimulateThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    Thread.sleep(2000);

                    latitude += 0.002;

                    LatLng p = new LatLng(latitude, longtitute);
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.obj = p;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    LatLng p = (LatLng) msg.obj;
                    points.add(p);
                    if (points.size() < 2) {
                        LatLng p1 = new LatLng(latitude + 0.002, longtitute);
                        LatLng p2 = new LatLng(latitude + 0.003, longtitute);
                        points.add(p1);
                        points.add(p2);
                    }
                    drawLine(points);
                }
            }
        };
    }
}
