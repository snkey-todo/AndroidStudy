/*
 *  Copyright 2014 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.hxsj.telephone.util;

import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * AppRTCUtils provides helper functions for managing thread safety.
 */
public final class AppRTCUtils {

  private AppRTCUtils() {
  }

  public static String getRandomString(int length) {
    String val = "";
    Random random = new Random();
    for (int i = 0; i < length; i++) {
      // 输出字母还是数字
//      String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
      String charOrNum = "num";
      // 字符串
      if ("char".equalsIgnoreCase(charOrNum)) {
        // 取得大写字母还是小写字母
        int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
        val += (char) (choice + random.nextInt(26));
      } else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
        val += String.valueOf(random.nextInt(10));
      }
    }
    return val;
  }

  /**
   * NonThreadSafe is a helper class used to help verify that methods of a
   * class are called from the same thread.
   */
  public static class NonThreadSafe {
    private final Long threadId;

    public NonThreadSafe() {
      // Store thread ID of the creating thread.
      threadId = Thread.currentThread().getId();
    }

   /** Checks if the method is called on the valid/creating thread. */
    public boolean calledOnValidThread() {
       return threadId.equals(Thread.currentThread().getId());
    }
  }

  /** Helper method which throws an exception  when an assertion has failed. */
  public static void assertIsTrue(boolean condition) {
    if (!condition) {
      throw new AssertionError("Expected condition to be true");
    }
  }

  /** Helper method for building a string of thread information.*/
  public static String getThreadInfo() {
    return "@[name=" + Thread.currentThread().getName()
        + ", id=" + Thread.currentThread().getId() + "]";
  }

  /** Information about the current build, taken from system properties. */
  public static void logDeviceInfo(String tag) {
    Log.d(tag, "Android SDK: " + Build.VERSION.SDK_INT + ", "
        + "Release: " + Build.VERSION.RELEASE + ", "
        + "Brand: " + Build.BRAND + ", "
        + "Device: " + Build.DEVICE + ", "
        + "Id: " + Build.ID + ", "
        + "Hardware: " + Build.HARDWARE + ", "
        + "Manufacturer: " + Build.MANUFACTURER + ", "
        + "Model: " + Build.MODEL + ", "
        + "Product: " + Build.PRODUCT);
  }

  public static void jsonPut(JSONObject json, String key, Object value) {
    try {
      json.put(key, value);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  public static String jsonGetString(JSONObject json, String key) {
    String value;
    try {
      value = json.getString(key);
    } catch (JSONException e) {
      value = "";
      //e.printStackTrace();
    }

    return value;
  }

  public static int jsonGetInt(JSONObject json, String key) {
    int value;
    try {
      value = json.getInt(key);
    } catch (JSONException e) {
      value = -1;
      e.printStackTrace();
    }

    return value;
  }

  public static JSONObject getJSONObject(JSONObject json, String key) {
    JSONObject obj;
    try {
      obj = (JSONObject)json.get(key);
    } catch (JSONException e) {
      obj = null;
      e.printStackTrace();
    }

    return obj;
  }

  public static JSONArray getJSONArray(JSONObject json, String key) {
    JSONArray jsonArray;
    try {
      jsonArray = json.getJSONArray(key);
    } catch (JSONException e) {
      jsonArray = null;
      e.printStackTrace();
    }
    return jsonArray;
  }

  public static JSONObject getJSONObject(JSONArray jsonArray, int index) {
    JSONObject json;
    try {
      json = jsonArray.getJSONObject(index);
    } catch (JSONException e) {
      json = null;
      e.printStackTrace();
    }
    return json;
  }
}
