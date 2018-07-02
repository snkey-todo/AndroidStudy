package com.example.android_rtc.client;

public class SocketIOException extends Exception {

    public SocketIOException() {
        super();
    }

    public SocketIOException(String message) {
        super(message);
    }

    public SocketIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public SocketIOException(Throwable cause) {
        super(cause);
    }
}
