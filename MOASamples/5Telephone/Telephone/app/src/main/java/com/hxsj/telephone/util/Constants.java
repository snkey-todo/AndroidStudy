package com.hxsj.telephone.util;

import java.io.File;

import com.hxsj.telephone.AppLoader;

import android.os.Environment;

public class Constants {

	public static final String SD_CARD = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static final String CACHE_PATH = SD_CARD.concat(File.separator + "Android/data" + File.separator + AppLoader.packageName + File.separator + "cache");
	public static final String IMG_CACHE_PATH = CACHE_PATH.concat(File.separator + "imgs" + File.separator);

	public static final String SIGN_SRC="UserID=%1$s&PSW=%2$s&Timestamp=%3$s&ID=oa_pic";
	public static final String FILE_SRC="UserID=%1$s&PSW=%2$s&Timestamp=%3$s&ID=oa_file";
	// msg key
	public static final String SHARED_PREFERENCE_NAME = "client_preferences";
	// PREFERENCE KEYS
	public static final String CALLBACK_ACTIVITY_PACKAGE_NAME = "CALLBACK_ACTIVITY_PACKAGE_NAME";
	public static final String CALLBACK_ACTIVITY_CLASS_NAME = "CALLBACK_ACTIVITY_CLASS_NAME";
	public static final String API_KEY = "API_KEY";
	public static final String VERSION = "VERSION";
	public static final String XMPP_HOST = "XMPP_HOST";
	public static final String SERVICE_NAME = "SERVICE_NAME";
	public static final String XMPP_PORT = "XMPP_PORT";
	public static final String XMPP_USERNAME = "XMPP_USERNAME";
	public static final String XMPP_PASSWORD = "XMPP_PASSWORD";
	// public static final String USER_KEY = "USER_KEY";
    public static final String DEVICE_ID = "DEVICE_ID";
    public static final String EMULATOR_DEVICE_ID = "EMULATOR_DEVICE_ID";
    public static final String NOTIFICATION_ICON = "NOTIFICATION_ICON";
    public static final String SETTINGS_NOTIFICATION_ENABLED = "SETTINGS_NOTIFICATION_ENABLED";
    public static final String SETTINGS_SOUND_ENABLED = "SETTINGS_SOUND_ENABLED";
    public static final String SETTINGS_VIBRATE_ENABLED = "SETTINGS_VIBRATE_ENABLED";
    public static final String SETTINGS_TOAST_ENABLED = "SETTINGS_TOAST_ENABLED";

    // NOTIFICATION FIELDS
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    public static final String NOTIFICATION_API_KEY = "NOTIFICATION_API_KEY";
    public static final String NOTIFICATION_TITLE = "NOTIFICATION_TITLE";
    public static final String NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE";
    public static final String NOTIFICATION_URI = "NOTIFICATION_URI";

    // INTENT ACTIONS
    public static final String ACTION_SHOW_NOTIFICATION = "org.androidpn.client.SHOW_NOTIFICATION";
    public static final String ACTION_NOTIFICATION_CLICKED = "org.androidpn.client.NOTIFICATION_CLICKED";
    public static final String ACTION_NOTIFICATION_CLEARED = "org.androidpn.client.NOTIFICATION_CLEARED";
    //observer listener
    public static final String ACTION_INVITATION_LISTENER="com.hxsj.smarteducation.invitionlistener";
    public static final String ACTION_USER_INFO_CHANGE="com.hxsj.smarteducation.infochange";
    public static final String tomcat_AttHome_Key=SD_CARD +File.separator+"tomcat_AttHome_Key";
    public static final String server_attHome_Key=SD_CARD+File.separator+"server_attHome_Key";
}
