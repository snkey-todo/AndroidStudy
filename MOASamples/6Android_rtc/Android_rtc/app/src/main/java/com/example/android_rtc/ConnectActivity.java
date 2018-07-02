package com.example.android_rtc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;

/**
 * Handles the initial setup where the user selects which room to join.
 */
public class ConnectActivity extends AppCompatActivity {
    private static final String TAG = "ConnectActivity";
    private static final int CONNECTION_REQUEST = 1;
    private static boolean commandLineRun = false;

    private Button addRoomButton;  //添加视频会议房间号
    private Button removeRoomButton; //移除视频会议房间号
    private Button connectButton;     //进入视频会议房间
    private Button connectLoopbackButton;  //随机进入视频会议房间号
    private EditText roomEditText;  //新建视频会议房间
    private ListView roomListView; //创建的视频房间列表

    //房间号保存的本地临时文件
    private SharedPreferences sharedPref;

    //视频会议需要的相关参数
    private String keyprefVideoCallEnabled;
    private String keyprefResolution;
    private String keyprefFps;
    private String keyprefCaptureQualitySlider;
    private String keyprefVideoBitrateType;
    private String keyprefVideoBitrateValue;
    private String keyprefVideoCodec;
    private String keyprefAudioBitrateType;
    private String keyprefAudioBitrateValue;
    private String keyprefAudioCodec;
    private String keyprefHwCodecAcceleration;
    private String keyprefCaptureToTexture;
    private String keyprefNoAudioProcessingPipeline;
    private String keyprefAecDump;
    private String keyprefOpenSLES;
    private String keyprefDisplayHud;
    private String keyprefTracing;
    private String keyprefRoomServerUrl;
    private String keyprefRoom;
    private String keyprefRoomList;


    private ArrayList<String> roomList; //视频会议房间列表
    private ArrayAdapter<String> adapter;//视频会议房间适配器

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        initData();  //获取视频会议的相关参数
        initView(); //初始化控件
    }
    private void initData(){
        //    获取关键的视频会议存储数据   PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        keyprefVideoCallEnabled = getString(R.string.pref_videocall_key);
        keyprefResolution = getString(R.string.pref_resolution_key);
        keyprefFps = getString(R.string.pref_fps_key);
        keyprefCaptureQualitySlider = getString(R.string.pref_capturequalityslider_key);
        keyprefVideoBitrateType = getString(R.string.pref_startvideobitrate_key);
        keyprefVideoBitrateValue = getString(R.string.pref_startvideobitratevalue_key);
        keyprefVideoCodec = getString(R.string.pref_videocodec_key);
        keyprefHwCodecAcceleration = getString(R.string.pref_hwcodec_key);
        keyprefCaptureToTexture = getString(R.string.pref_capturetotexture_key);
        keyprefAudioBitrateType = getString(R.string.pref_startaudiobitrate_key);
        keyprefAudioBitrateValue = getString(R.string.pref_startaudiobitratevalue_key);
        keyprefAudioCodec = getString(R.string.pref_audiocodec_key);
        keyprefNoAudioProcessingPipeline = getString(R.string.pref_noaudioprocessing_key);
        keyprefAecDump = getString(R.string.pref_aecdump_key);
        keyprefOpenSLES = getString(R.string.pref_opensles_key);
        keyprefDisplayHud = getString(R.string.pref_displayhud_key);
        keyprefTracing = getString(R.string.pref_tracing_key);
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);
        keyprefRoom = getString(R.string.pref_room_key);
        keyprefRoomList = getString(R.string.pref_room_list_key);
    }
    private void initView(){
        roomEditText = (EditText) findViewById(R.id.room_edittext);
        roomEditText.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(
                            TextView textView, int i, KeyEvent keyEvent) {
                        if (i == EditorInfo.IME_ACTION_DONE) {
                            addRoomButton.performClick();
                            return true;
                        }
                        return false;
                    }
                });
        roomEditText.requestFocus();

        roomListView = (ListView) findViewById(R.id.room_listview);
        roomListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        addRoomButton = (Button) findViewById(R.id.add_room_button);
        addRoomButton.setOnClickListener(addRoomListener);
        removeRoomButton = (Button) findViewById(R.id.remove_room_button);
        removeRoomButton.setOnClickListener(removeRoomListener);
        connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectListener);
        connectLoopbackButton =
                (Button) findViewById(R.id.connect_loopback_button);
        connectLoopbackButton.setOnClickListener(connectListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        String room = roomEditText.getText().toString();
        String roomListJson = new JSONArray(roomList).toString();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(keyprefRoom, room);
        editor.putString(keyprefRoomList, roomListJson);
//    editor.commit();
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        String room = sharedPref.getString(keyprefRoom, "");
        roomEditText.setText(room);
        roomList = new ArrayList<String>();
        String roomListJson = sharedPref.getString(keyprefRoomList, null);
        if (roomListJson != null) {
            try {
                JSONArray jsonArray = new JSONArray(roomListJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    roomList.add(jsonArray.get(i).toString());
                }
            } catch (JSONException e) {
                Log.e(TAG, "Failed to load room list: " + e.toString());
            }
        }
        if (!roomList.contains("200")) {
            roomList.add("200");
        }
        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, roomList);
        roomListView.setAdapter(adapter);
        if (adapter.getCount() > 0) {
            roomListView.requestFocus();
            roomListView.setItemChecked(0, true);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (requestCode == CONNECTION_REQUEST && commandLineRun) {
            Log.d(TAG, "Return: " + resultCode);
            setResult(resultCode);
            commandLineRun = false;
            finish();
        }
    }

    private final OnClickListener connectListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            boolean loopback = false;
            if (view.getId() == R.id.connect_loopback_button) {
                loopback = true;
            }
            commandLineRun = false;
            connectToRoom(loopback, 0);
        }
    };

    private void connectToRoom(boolean loopback, int runTimeMs) {
        // 获取房间号(random for loopback).
        String roomId;
        if (loopback) {
            roomId = Integer.toString((new Random()).nextInt(100000000));
        } else {
            roomId = getSelectedItem();
            if (roomId == null) {
                roomId = roomEditText.getText().toString();
            }
        }
        //获取视频会议地址
        String roomUrl = sharedPref.getString(keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default));
        // Video call enabled flag.
        boolean videoCallEnabled = sharedPref.getBoolean(keyprefVideoCallEnabled, Boolean.valueOf(getString(R.string.pref_videocall_default)));

        // Get default codecs.
        String videoCodec = sharedPref.getString(keyprefVideoCodec, getString(R.string.pref_videocodec_default));
        String audioCodec = sharedPref.getString(keyprefAudioCodec, getString(R.string.pref_audiocodec_default));
        // Check HW codec flag.
        boolean hwCodec = sharedPref.getBoolean(keyprefHwCodecAcceleration, Boolean.valueOf(getString(R.string.pref_hwcodec_default)));
        // Check Capture to texture.
        boolean captureToTexture = sharedPref.getBoolean(keyprefCaptureToTexture, Boolean.valueOf(getString(R.string.pref_capturetotexture_default)));
        // Check Disable Audio Processing flag.
        boolean noAudioProcessing = sharedPref.getBoolean(keyprefNoAudioProcessingPipeline,
                Boolean.valueOf(getString(R.string.pref_noaudioprocessing_default)));

        // Check Disable Audio Processing flag.
        boolean aecDump = sharedPref.getBoolean(keyprefAecDump, Boolean.valueOf(getString(R.string.pref_aecdump_default)));

        // Check OpenSL ES enabled flag.
        boolean useOpenSLES = sharedPref.getBoolean(keyprefOpenSLES, Boolean.valueOf(getString(R.string.pref_opensles_default)));

        // Get video resolution from settings.
        int videoWidth = 0;
        int videoHeight = 0;
        String resolution = sharedPref.getString(keyprefResolution, getString(R.string.pref_resolution_default));
        String[] dimensions = resolution.split("[ x]+");
        if (dimensions.length == 2) {
            try {
                videoWidth = Integer.parseInt(dimensions[0]);
                videoHeight = Integer.parseInt(dimensions[1]);
            } catch (NumberFormatException e) {
                videoWidth = 0;
                videoHeight = 0;
                Log.e(TAG, "Wrong video resolution setting: " + resolution);
            }
        }

        //  从设置文件中获取相机不过帧数cameraFps
        int cameraFps = 0;
        String fps = sharedPref.getString(keyprefFps, getString(R.string.pref_fps_default));
        String[] fpsValues = fps.split("[ x]+");
        if (fpsValues.length == 2) {
            try {
                cameraFps = Integer.parseInt(fpsValues[0]);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Wrong camera fps setting: " + fps);
            }
        }

        //  视频捕获质量标识
        boolean captureQualitySlider = sharedPref.getBoolean(keyprefCaptureQualitySlider, Boolean.valueOf(getString(R.string.pref_capturequalityslider_default)));

        //  获取视频流跟音频流的初始帧数
        int videoStartBitrate = 0;
        String bitrateTypeDefault = getString(
                R.string.pref_startvideobitrate_default);
        String bitrateType = sharedPref.getString(keyprefVideoBitrateType, bitrateTypeDefault);
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = sharedPref.getString(keyprefVideoBitrateValue, getString(R.string.pref_startvideobitratevalue_default));
            videoStartBitrate = Integer.parseInt(bitrateValue);
        }
        int audioStartBitrate = 0;
        bitrateTypeDefault = getString(R.string.pref_startaudiobitrate_default);
        bitrateType = sharedPref.getString(keyprefAudioBitrateType, bitrateTypeDefault);
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = sharedPref.getString(keyprefAudioBitrateValue, getString(R.string.pref_startaudiobitratevalue_default));
            audioStartBitrate = Integer.parseInt(bitrateValue);
        }
        // 视频显示的参数
        boolean displayHud = sharedPref.getBoolean(keyprefDisplayHud, Boolean.valueOf(getString(R.string.pref_displayhud_default)));
        boolean tracing = sharedPref.getBoolean(keyprefTracing, Boolean.valueOf(getString(R.string.pref_tracing_default)));

        // 跳转到视频会议界面.
        if (validateUrl(roomUrl)) {
            Uri uri = Uri.parse(roomUrl);
            Intent intent = new Intent(this, CallActivity.class);
            intent.setData(uri);
            intent.putExtra(CallActivity.EXTRA_ROOMID, roomId);
            intent.putExtra(CallActivity.EXTRA_LOOPBACK, loopback);
            intent.putExtra(CallActivity.EXTRA_VIDEO_CALL, videoCallEnabled);
            intent.putExtra(CallActivity.EXTRA_VIDEO_WIDTH, videoWidth);
            intent.putExtra(CallActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
            intent.putExtra(CallActivity.EXTRA_VIDEO_FPS, cameraFps);
            intent.putExtra(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, captureQualitySlider);
            intent.putExtra(CallActivity.EXTRA_VIDEO_BITRATE, videoStartBitrate);
            intent.putExtra(CallActivity.EXTRA_VIDEOCODEC, videoCodec);
            intent.putExtra(CallActivity.EXTRA_HWCODEC_ENABLED, hwCodec);
            intent.putExtra(CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture);
            intent.putExtra(CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing);
            intent.putExtra(CallActivity.EXTRA_AECDUMP_ENABLED, aecDump);
            intent.putExtra(CallActivity.EXTRA_OPENSLES_ENABLED, useOpenSLES);
            intent.putExtra(CallActivity.EXTRA_AUDIO_BITRATE, audioStartBitrate);
            intent.putExtra(CallActivity.EXTRA_AUDIOCODEC, audioCodec);
            intent.putExtra(CallActivity.EXTRA_DISPLAY_HUD, displayHud);
            intent.putExtra(CallActivity.EXTRA_TRACING, tracing);
            intent.putExtra(CallActivity.EXTRA_CMDLINE, commandLineRun);
            intent.putExtra(CallActivity.EXTRA_RUNTIME, runTimeMs);
            startActivityForResult(intent, CONNECTION_REQUEST);
        }
    }

    private boolean validateUrl(String url) {
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return true;
        }

        new AlertDialog.Builder(this)
                .setTitle(getText(R.string.invalid_url_title))
                .setMessage(getString(R.string.invalid_url_text, url))
                .setCancelable(false)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create().show();
        return false;
    }

    private final OnClickListener addRoomListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String newRoom = roomEditText.getText().toString();
            if (newRoom.length() > 0 && !roomList.contains(newRoom)) {
                adapter.add(newRoom);
                adapter.notifyDataSetChanged();
            }
        }
    };

    private final OnClickListener removeRoomListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String selectedRoom = getSelectedItem();
            if (selectedRoom != null) {
                adapter.remove(selectedRoom);
                adapter.notifyDataSetChanged();
            }
        }
    };

    private String getSelectedItem() {
        int position = AdapterView.INVALID_POSITION;
        if (roomListView.getCheckedItemCount() > 0 && adapter.getCount() > 0) {
            position = roomListView.getCheckedItemPosition();
            if (position >= adapter.getCount()) {
                position = AdapterView.INVALID_POSITION;
            }
        }
        if (position != AdapterView.INVALID_POSITION) {
            return adapter.getItem(position);
        } else {
            return null;
        }
    }

}
