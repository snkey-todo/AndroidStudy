/*
 *  Copyright 2014 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.hxsj.telephone.call;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hxsj.telephone.util.AppRTCUtils;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * WebSocket client implementation.
 * <p/>
 * <p>All public methods should be called from a looper executor thread
 * passed in a constructor, otherwise exception will be thrown.
 * All events are dispatched on the same thread.
 */

public class WebSocketChannelClient {
  private static final String TAG = "WebSocketChannelClient";
  private final WebSocketChannelEvents events;
  private Socket client;
  //private final Object closeEventLock = new Object();
  //private boolean closeEvent;


  public interface WebSocketChannelEvents {
    void onMessage(final String userId, final JSONObject data);

    void onConnectError(final String description);

    void onDisconnect();

    void onLeave(final String userId);

    void onJoin(final String userId);

    void onLogin(final JSONArray userIds);
  }

  public WebSocketChannelClient(WebSocketChannelEvents events) {
    Log.d(TAG, "WebSocketChannelClient construction");
    this.events = events;
  }

  public void connect(String wsServerUrl, String userId, String roomId) {
    assert (client == null);
    assert (wsServerUrl != null);
    assert (roomId != null);
    assert (userId != null);

    class MyTrustManager implements X509TrustManager {
      @Override
      public void checkClientTrusted(X509Certificate[] chain, String authType)
              throws CertificateException {
        // TODO Auto-generated method stub
      }
      @Override
      public void checkServerTrusted(X509Certificate[] chain, String authType)

              throws CertificateException {
        // TODO Auto-generated method stub
      }
      @Override
      public X509Certificate[] getAcceptedIssuers() {
        // TODO Auto-generated method stub
        return null;
      }
    }

    SSLContext mySSLContext = null;
    try {
      mySSLContext = SSLContext.getInstance("TLS");
      mySSLContext.init(null, new TrustManager[]{new MyTrustManager()}, null);
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      e.printStackTrace();
    }

    IO.setDefaultSSLContext(mySSLContext);
    IO.setDefaultHostnameVerifier(new HostnameVerifier() {
      @Override
      public boolean verify(String hostname, SSLSession session) {
        Log.d(TAG, "Verifying host name ::: " + hostname);
        return true;
      }
    });

    IO.Options ops = new IO.Options();
    ops.forceNew = true;
//    ops.reconnection = false;
    ops.reconnection=true;
    ops.query = "roomId=" + roomId + "&userId=" + userId;

    try {
      client = IO.socket(wsServerUrl, ops);
      client.on(Socket.EVENT_CONNECT_ERROR, onConnectErrorListener);
      client.on(Socket.EVENT_DISCONNECT, onDisconnectListener);
      client.on(Socket.EVENT_MESSAGE, onMessageListener);
      client.on("join", onJoinListener);
      client.on("leave", onLeaveListener);
      client.on("login", onLoginListener);

      client.connect();
      Log.d(TAG, "connect: " + "roomId=" + roomId + ", userId=" + userId);
    } catch (URISyntaxException e) {
      e.printStackTrace();
      client.close();
      client = null;
    }
  }

  public void disconnect() {
//    client.off();
    client.disconnect();
  }

  public void send(String to, Object message) {
    JSONObject json = new JSONObject();
    AppRTCUtils.jsonPut(json, "to", to);
    AppRTCUtils.jsonPut(json, "data", message);
    client.emit(Socket.EVENT_MESSAGE, json);
    Log.e("send", json.toString());
  }

  private final Emitter.Listener onConnectErrorListener = new Emitter.Listener() {
    @Override
    public void call(Object... args) {
      events.onConnectError(args.length > 0 ? args[0].toString() : "null");
    }
  };

  private final Emitter.Listener onDisconnectListener = new Emitter.Listener() {
    @Override
    public void call(Object... args) {
      events.onDisconnect();
    }
  };

  private final Emitter.Listener onJoinListener = new Emitter.Listener() {
    @Override
    public void call(Object... args) {
      if (args.length == 0) return;
      assert (args.length == 1);
      JSONObject msg = (JSONObject) args[0];
      events.onJoin(AppRTCUtils.jsonGetString(msg, "userId"));
      Log.e("join", msg.toString());
    }
  };

  private final Emitter.Listener onLeaveListener = new Emitter.Listener() {
    @Override
    public void call(Object... args) {
      if (args.length == 0) return;
      assert (args.length == 1);
      JSONObject msg = (JSONObject) args[0];
      events.onLeave(AppRTCUtils.jsonGetString(msg, "userId"));
      Log.e("leave", msg.toString());
    }
  };

  private final Emitter.Listener onLoginListener = new Emitter.Listener() {
    @Override
    public void call(Object... args) {
      if (args.length == 0) return;
      assert (args.length == 1);
      JSONObject msg = (JSONObject) args[0];
      events.onLogin(AppRTCUtils.getJSONArray(msg, "userIds"));
      Log.e("login", msg.toString());
    }
  };

  private final Emitter.Listener onMessageListener = new Emitter.Listener() {
    @Override
    public void call(Object... args) {
      if (args.length == 0) return;
      assert (args.length == 1);
      JSONObject msg = (JSONObject) args[0];

      Log.e("message", msg.toString());
      JSONObject data = AppRTCUtils.getJSONObject(msg, "data");
      String userId = AppRTCUtils.jsonGetString(msg, "userId");

      if (/*TextUtils.isEmpty(userId) ||*/ data == null) {
        events.onConnectError(msg.toString());
        return;
      }
      events.onMessage(userId, data);
    }
  };
}
