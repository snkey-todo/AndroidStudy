package com.example.zhusheng.aandroid_n_directory;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final String TAG ="MainActivity" ;
    private StorageManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        ((Button) findViewById(R.id.btn1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageVolume storageVolume = sm.getPrimaryStorageVolume();
                Intent intent = storageVolume.createAccessIntent(Environment.DIRECTORY_DOWNLOADS);
                startActivityForResult(intent,1001);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1001){
            Log.i(TAG,"用户操作了");

        }
    }
}
