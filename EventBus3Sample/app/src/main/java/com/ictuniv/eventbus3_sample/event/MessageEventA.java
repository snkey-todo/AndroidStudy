package com.ictuniv.eventbus3_sample.event;

/**
 * Created by Kevin on 2016/5/30.
 * EventBus的事件：信息
 */
public class MessageEventA {

    private String msg;
    private String time;

    public MessageEventA(String msg,String time) {
        this.msg = msg;
        this.time = time;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
