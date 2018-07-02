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


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;


import com.hxsj.telephone.bean.MsgType;
import com.hxsj.telephone.observer.MessageObserver;
import com.hxsj.telephone.observer.ObserverFilter;
import com.hxsj.telephone.call.PeerConnectionClient.PeerConnectionParameters;
import com.hxsj.telephone.call.WebSocketChannelClient.WebSocketChannelEvents;
import com.hxsj.telephone.util.AppRTCUtils;
import com.hxsj.telephone.util.Const;
import com.hxsj.telephone.util.LooperExecutor;


public class WebSocketRTCClient implements AppRTCClient,
        WebSocketChannelEvents,
        PeerConnectionClient.PeerConnectionEvents {

  private static final String TAG = "WSRTCClient";
  private static final String ROOM_LOGIN  = "login";
  private static final String ROOM_JOIN   = "join";
  private static final String ROOM_LEAVE  = "leave";
  private static final String ROOM_ERROR  = "error";
  private static final String ROOM_CALL   = "call";
  private static final String ROOM_CANCEL = "cancel";
  private static final String ROOM_CALLING = "calling";
  private final LooperExecutor executor;
  private SignalingEvents events;
  private WebSocketChannelClient wsClient;
  private RoomConnectionParameters connectionParameters;
  private String remoteUserId;
  Context context;

  public WebSocketRTCClient(SignalingEvents events, LooperExecutor executor,
                            Context context) {
    this.events = events;
    this.executor = executor;
    this.context = context;
    executor.requestStart();
  }

  // --------------------------------------------------------------------
  // AppRTCClient interface implementation.
  // Asynchronously connect to an AppRTC room URL using supplied connection
  // parameters, retrieves room parameters and connect to WebSocket server.
  @Override
  public void connectToRoom(RoomConnectionParameters connectionParameters) {
    this.connectionParameters = connectionParameters;
    executor.execute(new Runnable() {
      @Override
      public void run() {
        connectToRoomInternal();
      }
    });
  }

  @Override
  public void disconnectFromRoom() {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        disconnectFromRoomInternal();
      }
    });
    executor.requestStop();
  }

  @Override
  public void send(final String to, final Object message) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        wsClient.send(to, message);
      }
    });
  }

  @Override
  public void SetRemoteUserId(String userId) {
    remoteUserId = userId;
  }

  @Override
  public void createPeerConnectionFactory() {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        PeerConnectionParameters peerConnectionParameters = new PeerConnectionParameters(
                false, false, false, 0, 0, 0, 0, "VP8",
                true, false, 0, "OPUS", false, false, true);
        PeerConnectionClient.getInstance().createPeerConnectionFactory(
                context, peerConnectionParameters, WebSocketRTCClient.this);
      }
    });
  }

  private void connectToRoomInternal() {
    wsClient = new WebSocketChannelClient(this);
    wsClient.connect(
            connectionParameters.roomUrl,
            connectionParameters.userId,
            connectionParameters.roomId
    );
  }

  // Disconnect from room and send bye messages - runs on a local looper thread.
  private void disconnectFromRoomInternal() {
    if (wsClient != null) {
      wsClient.disconnect();
    }
  }

  // Send local answer SDP to the other participant.
  @Override
  public void sendLocalSdp(final SessionDescription sdp) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        JSONObject json = new JSONObject();
        AppRTCUtils.jsonPut(json, "sdp", sdp.description);
        AppRTCUtils.jsonPut(json, "type", sdp.type.canonicalForm());
        wsClient.send(remoteUserId, json);
      }
    });
  }

  // Send Ice candidate to the other participant.
  @Override
  public void sendLocalIceCandidate(final IceCandidate candidate) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        JSONObject json = new JSONObject();
        AppRTCUtils.jsonPut(json, "type", "candidate");
        AppRTCUtils.jsonPut(json, "id", candidate.sdpMid);
        AppRTCUtils.jsonPut(json, "label", candidate.sdpMLineIndex);
        AppRTCUtils.jsonPut(json, "candidate", candidate.sdp);
        wsClient.send(remoteUserId, json);
      }
    });
  }

  // Send removed Ice candidates to the other participant.
  @Override
  public void sendLocalIceCandidateRemovals(final IceCandidate[] candidates) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        JSONObject json = new JSONObject();
        AppRTCUtils.jsonPut(json, "type", "remove-candidates");
        JSONArray jsonArray =  new JSONArray();
        for (final IceCandidate candidate : candidates) {
          jsonArray.put(toJsonCandidate(candidate));
        }
        AppRTCUtils.jsonPut(json, "candidates", jsonArray);
        wsClient.send(remoteUserId, json);
      }
    });
  }

  // Converts a Java candidate to a JSONObject.
  private JSONObject toJsonCandidate(final IceCandidate candidate) {
    JSONObject json = new JSONObject();
    AppRTCUtils.jsonPut(json, "id", candidate.sdpMid);
    AppRTCUtils.jsonPut(json, "label", candidate.sdpMLineIndex);
    AppRTCUtils.jsonPut(json, "candidate", candidate.sdp);
    return json;
  }

  // Converts a JSON candidate to a Java object.
  IceCandidate toJavaCandidate(JSONObject json) {
    return new IceCandidate(
            AppRTCUtils.jsonGetString(json, "id"),
            AppRTCUtils.jsonGetInt(json, "label"),
            AppRTCUtils.jsonGetString(json, "candidate")
    );
  }

  @Override
  public void onMessage(final String userId, final JSONObject json) {
    executor.execute(new Runnable(){
      @Override
      public void run() {
        PeerConnectionClient peerConnectionClient = PeerConnectionClient.getInstance();
        String type = AppRTCUtils.jsonGetString(json, "type");
        if (TextUtils.isEmpty(type))
          return;
        switch (type) {
          case ROOM_LOGIN:
            events.onLogin(AppRTCUtils.getJSONArray(json, "userIds"));
            break;
          case ROOM_JOIN:
            events.onJoin(userId);
            break;
          case ROOM_LEAVE:
            events.onLeave(userId);
            break;
          case ROOM_ERROR:
            events.onChannelError(AppRTCUtils.jsonGetString(json, "error"));
            break;
          case ROOM_CALL:
        	String username=AppRTCUtils.jsonGetString(json, "username");
            events.onCall(userId,username);
            break;
          case ROOM_CALLING:
        	
            events.onCalling(userId);
            break;
          case ROOM_CANCEL:
            events.onCancel(userId);
            break;
          case "offer":
            SessionDescription offerSdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(type),
                    AppRTCUtils.jsonGetString(json, "sdp"));
            //events.onRemoteDescription(offerSdp);
            peerConnectionClient.setRemoteDescription(offerSdp);
            peerConnectionClient.createAnswer();
            break;
          case "answer":
            SessionDescription answerSdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(type),
                    AppRTCUtils.jsonGetString(json, "sdp"));
            //events.onRemoteDescription(answerSdp);
            peerConnectionClient.setRemoteDescription(answerSdp);
            break;
          case "candidate":
            //events.onRemoteIceCandidate(toJavaCandidate(json));
            peerConnectionClient.addRemoteIceCandidate(toJavaCandidate(json));
            break;
          case "remove-candidates":
            JSONArray candidateArray = AppRTCUtils.getJSONArray(json, "candidates");
            IceCandidate[] candidates = new IceCandidate[candidateArray.length()];
            for (int i = 0; i < candidateArray.length(); ++i) {
              candidates[i] = toJavaCandidate(AppRTCUtils.getJSONObject(candidateArray, i));
            }
            //events.onRemoteIceCandidatesRemoved(candidates);
            peerConnectionClient.removeRemoteIceCandidates(candidates);
            break;
        }
      }
    });
  }

  @Override
  public void onConnectError(String description) {
    events.onChannelError(description);
  }

  @Override
  public void onDisconnect() {
    Log.d(TAG, "onDisconnect");
    events.onChannelClose();
  }

  @Override
  public void onLeave(String userId) {
    events.onLeave(userId);
  }

  @Override
  public void onJoin(String userId) {
    events.onJoin(userId);
  }

  @Override
  public void onLogin(JSONArray userIds) {
    events.onLogin(userIds);
  }

  // -----Implementation of PeerConnectionClient.PeerConnectionEvents.---------
  // Send local peer connection SDP and ICE candidates to remote party.
  // All callbacks are invoked from peer connection client looper thread and
  // are routed to UI thread.
  @Override
  public void onLocalDescription(final SessionDescription sdp) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
          sendLocalSdp(sdp);
      }
    });
  }

  @Override
  public void onIceCandidate(final IceCandidate candidate) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        sendLocalIceCandidate(candidate);
      }
    });
  }

  @Override
  public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
          sendLocalIceCandidateRemovals(candidates);
      }
    });
  }


  @Override
  public void onIceConnected() {
	  MessageObserver.getInstance().notifyDataChanged(MsgType.CONNECTING, new ObserverFilter(Const.APPRTC_CANCEL));

  }

  @Override
  public void onIceDisconnected() {

  }

  @Override
  public void onPeerConnectionClosed() {
  }

  @Override
  public void onPeerConnectionStatsReady(final StatsReport[] reports) {

  }

  @Override
  public void onPeerConnectionError(final String description) {

  }
}
