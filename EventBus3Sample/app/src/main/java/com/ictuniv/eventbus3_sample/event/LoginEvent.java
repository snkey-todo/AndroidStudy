package com.ictuniv.eventbus3_sample.event;

/**
 * Created by Administrator on 2017/1/5.
 */

public class LoginEvent {
    private String username;
    private String password;
    private boolean isLogin;

    public LoginEvent() {
    }

    public LoginEvent(boolean isLogin, String username,String password) {
        this.isLogin = isLogin;
        this.password = password;
        this.username = username;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "LoginEvent{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isLogin=" + isLogin +
                '}';
    }
}
