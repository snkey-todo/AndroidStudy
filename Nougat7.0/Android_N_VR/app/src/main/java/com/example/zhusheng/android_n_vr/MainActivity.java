package com.example.zhusheng.android_n_vr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //VR图片
        VrPanoramaView view = (VrPanoramaView) findViewById(R.id.vr_view);
        try {
            InputStream is = getAssets().open("b.png");
            Bitmap bmp = BitmapFactory.decodeStream(is);

            VrPanoramaView.Options options = new VrPanoramaView.Options();
            options.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;
            view.loadImageFromBitmap(bmp, options);

            view.setEventListener(new VrPanoramaEventListener() {
                @Override
                public void onLoadSuccess() {
                    super.onLoadSuccess();
                    Log.i(TAG, "onLoadSuccess");
                }

                @Override
                public void onLoadError(String errorMessage) {
                    super.onLoadError(errorMessage);
                    Log.i(TAG, "onLoadError");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


        //VR视频
        VrVideoView vr_video = (VrVideoView) findViewById(R.id.vr_video);
        VrVideoView.Options options = new VrVideoView.Options();
        options.inputType = VrVideoView.Options.TYPE_STEREO_OVER_UNDER;
        try {
            vr_video.loadVideoFromAsset("congo.mp4", options);
            vr_video.setEventListener(new VrVideoEventListener() {
                @Override
                public void onLoadSuccess() {
                    super.onLoadSuccess();
                    Log.i(TAG, "onLoadSuccess");
                }

                @Override
                public void onLoadError(String errorMessage) {
                    super.onLoadError(errorMessage);
                    Log.i(TAG, "onLoadError");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
