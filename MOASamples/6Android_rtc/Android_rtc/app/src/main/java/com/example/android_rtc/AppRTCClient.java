/*
 *  Copyright 2013 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.example.android_rtc;

import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.SessionDescription;

/**
 * AppRTCClient is the interface representing an AppRTC client.
 */
public interface AppRTCClient {

  /**
   * Struct holding the connection parameters of an AppRTC room.
   */
  class RoomConnectionParameters {
    public final String roomUrl;
    public final String roomId;
    public final String userId;
    public RoomConnectionParameters(
        String roomUrl, String roomId, String userId) {
      this.roomUrl = roomUrl;
      this.roomId = roomId;
      this.userId = userId;
    }
  }

  /**
   * Asynchronously connect to an AppRTC room URL using supplied connection
   * parameters. Once connection is established onConnectedToRoom()
   * callback with room parameters is invoked.
   */
  void connectToRoom(RoomConnectionParameters connectionParameters);

  /**
   * Send local SDP to the other participant.
   */
  void sendLocalSdp(final String userId, final SessionDescription sdp);

  /**
   * Send Ice candidate to the other participant.
   */
  void sendLocalIceCandidate(final String userId, final IceCandidate candidate);

  /**
   * Send removed ICE candidates to the other participant.
   */
  void sendLocalIceCandidateRemovals(final String userId, final IceCandidate[] candidates);

  /**
   * Disconnect from room.
   */
  void disconnectFromRoom();

  void startVideoSource();

  void stopVideoSource();

  void switchCamera();

  void changeCaptureFormat(final int width, final int height, final int framerate);

  void setAudioEnabled(final boolean enable);

  void setVideoEnabled(final boolean enable);

//  /**
//   * Struct holding the signaling parameters of an AppRTC room.
//   */
//  public static class SignalingParameters {
//    public final List<PeerConnection.IceServer> iceServers;
//    public final boolean initiator;
//    public final String clientId;
//    public final String wssUrl;
//    public final String wssPostUrl;
//    public final SessionDescription offerSdp;
//    public final List<IceCandidate> iceCandidates;
//
//    public SignalingParameters(
//        List<PeerConnection.IceServer> iceServers,
//        boolean initiator, String clientId,
//        String wssUrl, String wssPostUrl,
//        SessionDescription offerSdp, List<IceCandidate> iceCandidates) {
//      this.iceServers = iceServers;
//      this.initiator = initiator;
//      this.clientId = clientId;
//      this.wssUrl = wssUrl;
//      this.wssPostUrl = wssPostUrl;
//      this.offerSdp = offerSdp;
//      this.iceCandidates = iceCandidates;
//    }
//  }

  /**
   * Callback interface for messages delivered on signaling channel.
   *
   * <p>Methods are guaranteed to be invoked on the UI thread of |activity|.
   */
  interface SignalingEvents {
    /**
     * Callback fired once the room's signaling parameters
     * SignalingParameters are extracted.
     */
//    public void onConnectedToRoom(final SignalingParameters params);

    /**
     * Callback fired once remote SDP is received.
     */
//    public void onRemoteDescription(final SessionDescription sdp);

    /**
     * Callback fired once remote Ice candidate is received.
     */
//    public void onRemoteIceCandidate(final IceCandidate candidate);

    /**
     * Callback fired once remote Ice candidate removals are received.
     */
//    public void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates);

    void onLocalStream(final MediaStream stream);

    void onJoin(final String userId);

    void onLeave(final String userId);

    /**
     * Callback fired once channel is closed.
     */
    void onChannelClose();

    /**
     * Callback fired once channel error happened.
     */
    void onChannelError(final String description);
  }
}
