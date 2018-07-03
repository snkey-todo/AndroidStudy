package com.ictuniv.eventbus3_sample.event;

/**
 * Created by Administrator on 2017/1/5.
 */

public class LoginInfoBean {
    public String msg = "第几次发布的信息：";

    public LoginInfoBean(String msg) {
        this.msg = this.msg + msg;
    }
}
