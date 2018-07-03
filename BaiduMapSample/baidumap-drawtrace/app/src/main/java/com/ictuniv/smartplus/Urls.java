package com.ictuniv.smartplus;

/**
 * Created by Administrator on 2016/9/20.
 */
public class Urls {
    //admin用户
    public static String BASE = "http://112.124.28.131:8000/restapi/" +
            "smartplus/17e73e2c-a7eb-4edd-8745-df417649a2a5/" +
            "datastream/49310c6c-6716-4e2a-8863-f7bb1e7de316";

    /**
     * 点数据-float
     */
    public static String DATAPOINT_NEWEST = BASE + "/datapoint/newest/";
    public static String DATAPOINT_SUBMIT = BASE + "/datapoint/submit/";
    public static String DATAPOINT_MINUTE = BASE + "/datapoint/minute/";

    /**
     * 消息型数据-string
     */
    public static String MESSAGE_NEWEST = BASE + "/message/newest/";
    public static String MESSAGE_SUBMIT = BASE + "/message/submit/";

    /**
     * 开关型数据-switch [on/off]
     */
    public static String SWITCH_NEWEST = BASE + "/switch/newest/";
    public static String SWITCH_SUBMIT = BASE + "/switch/submit/";
    public static String switch_minute = BASE + "/switch/minute/";

    /**
     * way,地图数据
     */
    public static String WAY_NEWEST = BASE + "/way/newest/";
    public static String WAY_SUBMIT = BASE + "/way/submit/";

    /**
     * 访问用户信息时，需要添加请求头信息：
     * 'Authorization': 'Token 3e5d9cc5be8b880b1e104e37dec843fe787e8a7f'
     */
}
