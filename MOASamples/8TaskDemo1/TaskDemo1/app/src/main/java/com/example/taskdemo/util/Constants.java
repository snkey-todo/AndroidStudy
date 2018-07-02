package com.example.taskdemo.util;

import android.os.Environment;

import java.io.File;

public class Constants {

	public static final String SD_CARD = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static final String CACHE_PATH = SD_CARD.concat(File.separator + "Android/data" + File.separator + File.separator + "cache");
	public static final String IMG_CACHE_PATH = CACHE_PATH.concat(File.separator + "imgs" + File.separator);

}
