/*
 *  Copyright 2014 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.example.android_rtc;

import android.util.Log;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.IceConnectionState;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Peer connection client implementation.
 * <p>
 * <p>All public methods are routed to local looper thread.
 * All PeerConnectionEvents callbacks are invoked from the same looper thread.
 * This class is a singleton.
 */
public class PeerConnectionClient {
  private static final String TAG = PeerConnectionClient.class.getName();
  private static final String VIDEO_CODEC_VP8 = "VP8";
  private static final String VIDEO_CODEC_VP9 = "VP9";
  private static final String VIDEO_CODEC_H264 = "H264";
  private static final String AUDIO_CODEC_OPUS = "opus";
  private static final String AUDIO_CODEC_ISAC = "ISAC";
  private static final String VIDEO_CODEC_PARAM_START_BITRATE =
          "x-google-start-bitrate";
  private static final String AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate";

  private final PCObserver pcObserver = new PCObserver();
  private final SDPObserver sdpObserver = new SDPObserver();

  private int videoStartBitrate;
  private int audioStartBitrate;
  private PeerConnection peerConnection;
  private boolean videoCallEnabled;
  private boolean preferIsac;
  private String preferredVideoCodec;
  private boolean isError;
//  private Timer statsTimer;
  private MediaConstraints sdpMediaConstraints;
  // Queued remote ICE candidates are consumed only after both local and
  // remote descriptions are set. Similarly local ICE candidates are sent to
  // remote peer after both local and remote description are set.
  private LinkedList<IceCandidate> queuedRemoteCandidates;

  private PeerConnectionEvents events;
  private boolean isInitiator;
  private SessionDescription localSdp; // either offer or answer SDP

  private String remoteUserId;

  /**
   * Peer connection parameters.
   */
  public static class PeerConnectionParameters {
    public final boolean videoCallEnabled;
    public final boolean loopback;
    public final boolean tracing;
    public final int videoWidth;
    public final int videoHeight;
    public final int videoFps;
    public final int videoStartBitrate;
    public final String videoCodec;
    public final boolean videoCodecHwAcceleration;
    public final boolean captureToTexture;
    public final int audioStartBitrate;
    public final String audioCodec;
    public final boolean noAudioProcessing;
    public final boolean aecDump;
    public final boolean useOpenSLES;

    public PeerConnectionParameters(
            boolean videoCallEnabled, boolean loopback, boolean tracing,
            int videoWidth, int videoHeight, int videoFps, int videoStartBitrate,
            String videoCodec, boolean videoCodecHwAcceleration, boolean captureToTexture,
            int audioStartBitrate, String audioCodec,
            boolean noAudioProcessing, boolean aecDump, boolean useOpenSLES) {
      this.videoCallEnabled = videoCallEnabled;
      this.loopback = loopback;
      this.tracing = tracing;
      this.videoWidth = videoWidth;
      this.videoHeight = videoHeight;
      this.videoFps = videoFps;
      this.videoStartBitrate = videoStartBitrate;
      this.videoCodec = videoCodec;
      this.videoCodecHwAcceleration = videoCodecHwAcceleration;
      this.captureToTexture = captureToTexture;
      this.audioStartBitrate = audioStartBitrate;
      this.audioCodec = audioCodec;
      this.noAudioProcessing = noAudioProcessing;
      this.aecDump = aecDump;
      this.useOpenSLES = useOpenSLES;
    }
  }

  /**
   * Peer connection events.
   */
  public interface PeerConnectionEvents {
    /**
     * Callback fired once local SDP is created and set.
     */
    void onLocalDescription(final String userId, final SessionDescription sdp);

    /**
     * Callback fired once local Ice candidate is generated.
     */
    void onIceCandidate(final String userId, final IceCandidate candidate);

    /**
     * Callback fired once local ICE candidates are removed.
     */
    void onIceCandidatesRemoved(final String userId, final IceCandidate[] candidates);

    /**
     * Callback fired once connection is established (IceConnectionState is
     * CONNECTED).
     */
    void onIceConnected();

    /**
     * Callback fired once connection is closed (IceConnectionState is
     * DISCONNECTED).
     */
    void onIceDisconnected();

    /**
     * Callback fired once peer connection is closed.
     */
    void onPeerConnectionClosed();

    /**
     * Callback fired once peer connection statistics is ready.
     */
    void onPeerConnectionStatsReady(final StatsReport[] reports);

    /**
     * Callback fired once peer connection error happened.
     */
    void onPeerConnectionError(final String description);

    void onAddRemoteStream(final MediaStream stream);
  }

  public PeerConnectionClient(String remoteUserId, boolean isInitiator) {
    this.remoteUserId = remoteUserId;
    this.isInitiator = isInitiator;
  }

  public void init(PeerConnectionParameters peerConnectionParameters,
                   PeerConnectionEvents events,
                   boolean preferIsac,
                   String preferredVideoCodec,
                   MediaConstraints sdpMediaConstraints) {
    this.events = events;
    this.sdpMediaConstraints = sdpMediaConstraints;
    videoCallEnabled = peerConnectionParameters.videoCallEnabled;
    videoStartBitrate = peerConnectionParameters.videoStartBitrate;
    audioStartBitrate = peerConnectionParameters.audioStartBitrate;
    // Reset variables to initial states
    queuedRemoteCandidates = null;
    peerConnection = null;
    this.preferIsac = preferIsac;
    this.preferredVideoCodec = preferredVideoCodec;
    isError = false;
    localSdp = null; // either offer or answer SDP
//    statsTimer = new Timer();
  }

  public void createPeerConnection(PeerConnectionFactory factory,
                                   MediaStream mediaStream,
                                   PeerConnection.RTCConfiguration rtcConfig,
                                   MediaConstraints pcConstraints) {
    peerConnection = factory.createPeerConnection(
            rtcConfig, pcConstraints, pcObserver);

    peerConnection.addStream(mediaStream);
    if (isInitiator)
      createOffer();
    Log.d(TAG, "Peer connection created.");
  }

  public void close() {
    Log.d(TAG, "Closing peer connection.");
//    statsTimer.cancel();
    if (peerConnection != null) {
      peerConnection.dispose();
      peerConnection = null;
    }
    Log.d(TAG, "Closing peer connection done.");
    events.onPeerConnectionClosed();
  }

//  private void getStats() {
//    if (peerConnection == null || isError) {
//      return;
//    }
//    boolean success = peerConnection.getStats(new StatsObserver() {
//      @Override
//      public void onComplete(final StatsReport[] reports) {
//        events.onPeerConnectionStatsReady(reports);
//      }
//    }, null);
//    if (!success) {
//      Log.e(TAG, "getStats() returns false!");
//    }
//  }

//  public void enableStatsEvents(boolean enable, int periodMs) {
//    if (enable) {
//      try {
//        statsTimer.schedule(new TimerTask() {
//          @Override
//          public void run() {
//            executor.execute(new Runnable() {
//              @Override
//              public void run() {
//                getStats();
//              }
//            });
//          }
//        }, 0, periodMs);
//      } catch (Exception e) {
//        Log.e(TAG, "Can not schedule statistics timer", e);
//      }
//    } else {
//      statsTimer.cancel();
//    }
//  }

  public void createOffer() {
    if (peerConnection != null && !isError) {
      Log.d(TAG, "PC Create OFFER");
//    isInitiator = true;
      peerConnection.createOffer(sdpObserver, sdpMediaConstraints);
    }
  }

  public void createAnswer() {
    if (peerConnection != null && !isError) {
      Log.d(TAG, "PC create ANSWER");
//          isInitiator = false;
      peerConnection.createAnswer(sdpObserver, sdpMediaConstraints);
    }
  }

  public void addRemoteIceCandidate(final IceCandidate candidate) {
    if (peerConnection != null && !isError) {
      if (queuedRemoteCandidates != null) {
        queuedRemoteCandidates.add(candidate);
      } else {
        peerConnection.addIceCandidate(candidate);
      }
    }
  }

  public void removeRemoteIceCandidates(final IceCandidate[] candidates) {
    if (peerConnection == null || isError) {
      return;
    }
    // Drain the queued remote candidates if there is any so that
    // they are processed in the proper order.
    drainCandidates();
    peerConnection.removeIceCandidates(candidates);
  }

  public void setRemoteDescription(final SessionDescription sdp) {
    if (peerConnection == null || isError) {
      return;
    }
    String sdpDescription = sdp.description;
    if (preferIsac) {
      sdpDescription = preferCodec(sdpDescription, AUDIO_CODEC_ISAC, true);
    }
    if (videoCallEnabled) {
      sdpDescription = preferCodec(sdpDescription, preferredVideoCodec, false);
    }
    if (videoCallEnabled && videoStartBitrate > 0) {
      sdpDescription = setStartBitrate(VIDEO_CODEC_VP8, true,
              sdpDescription, videoStartBitrate);
      sdpDescription = setStartBitrate(VIDEO_CODEC_VP9, true,
              sdpDescription, videoStartBitrate);
      sdpDescription = setStartBitrate(VIDEO_CODEC_H264, true,
              sdpDescription, videoStartBitrate);
    }
    if (audioStartBitrate > 0) {
      sdpDescription = setStartBitrate(AUDIO_CODEC_OPUS, false,
              sdpDescription, audioStartBitrate);
    }
    Log.d(TAG, "Set remote SDP.");
    SessionDescription sdpRemote = new SessionDescription(
            sdp.type, sdpDescription);
    peerConnection.setRemoteDescription(sdpObserver, sdpRemote);
  }

  private void reportError(final String errorMessage) {
    Log.e(TAG, "Peerconnection error: " + errorMessage);
    if (!isError) {
      events.onPeerConnectionError(errorMessage);
      isError = true;
    }
  }

  private static String setStartBitrate(String codec, boolean isVideoCodec,
                                        String sdpDescription, int bitrateKbps) {
    String[] lines = sdpDescription.split("\r\n");
    int rtpmapLineIndex = -1;
    boolean sdpFormatUpdated = false;
    String codecRtpMap = null;
    // Search for codec rtpmap in format
    // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
    String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
    Pattern codecPattern = Pattern.compile(regex);
    for (int i = 0; i < lines.length; i++) {
      Matcher codecMatcher = codecPattern.matcher(lines[i]);
      if (codecMatcher.matches()) {
        codecRtpMap = codecMatcher.group(1);
        rtpmapLineIndex = i;
        break;
      }
    }
    if (codecRtpMap == null) {
      Log.w(TAG, "No rtpmap for " + codec + " codec");
      return sdpDescription;
    }
    Log.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap
            + " at " + lines[rtpmapLineIndex]);

    // Check if a=fmtp string already exist in remote SDP for this codec and
    // update it with new bitrate parameter.
    regex = "^a=fmtp:" + codecRtpMap + " \\w+=\\d+.*[\r]?$";
    codecPattern = Pattern.compile(regex);
    for (int i = 0; i < lines.length; i++) {
      Matcher codecMatcher = codecPattern.matcher(lines[i]);
      if (codecMatcher.matches()) {
        Log.d(TAG, "Found " + codec + " " + lines[i]);
        if (isVideoCodec) {
          lines[i] += "; " + VIDEO_CODEC_PARAM_START_BITRATE
                  + "=" + bitrateKbps;
        } else {
          lines[i] += "; " + AUDIO_CODEC_PARAM_BITRATE
                  + "=" + (bitrateKbps * 1000);
        }
        Log.d(TAG, "Update remote SDP line: " + lines[i]);
        sdpFormatUpdated = true;
        break;
      }
    }

    StringBuilder newSdpDescription = new StringBuilder();
    for (int i = 0; i < lines.length; i++) {
      newSdpDescription.append(lines[i]).append("\r\n");
      // Append new a=fmtp line if no such line exist for a codec.
      if (!sdpFormatUpdated && i == rtpmapLineIndex) {
        String bitrateSet;
        if (isVideoCodec) {
          bitrateSet = "a=fmtp:" + codecRtpMap + " "
                  + VIDEO_CODEC_PARAM_START_BITRATE + "=" + bitrateKbps;
        } else {
          bitrateSet = "a=fmtp:" + codecRtpMap + " "
                  + AUDIO_CODEC_PARAM_BITRATE + "=" + (bitrateKbps * 1000);
        }
        Log.d(TAG, "Add remote SDP line: " + bitrateSet);
        newSdpDescription.append(bitrateSet).append("\r\n");
      }

    }
    return newSdpDescription.toString();
  }

  private static String preferCodec(
          String sdpDescription, String codec, boolean isAudio) {
    String[] lines = sdpDescription.split("\r\n");
    int mLineIndex = -1;
    String codecRtpMap = null;
    // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
    String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
    Pattern codecPattern = Pattern.compile(regex);
    String mediaDescription = "m=video ";
    if (isAudio) {
      mediaDescription = "m=audio ";
    }
    for (int i = 0; (i < lines.length)
            && (mLineIndex == -1 || codecRtpMap == null); i++) {
      if (lines[i].startsWith(mediaDescription)) {
        mLineIndex = i;
        continue;
      }
      Matcher codecMatcher = codecPattern.matcher(lines[i]);
      if (codecMatcher.matches()) {
        codecRtpMap = codecMatcher.group(1);
      }
    }
    if (mLineIndex == -1) {
      Log.w(TAG, "No " + mediaDescription + " line, so can't prefer " + codec);
      return sdpDescription;
    }
    if (codecRtpMap == null) {
      Log.w(TAG, "No rtpmap for " + codec);
      return sdpDescription;
    }
    Log.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap + ", prefer at "
            + lines[mLineIndex]);
    String[] origMLineParts = lines[mLineIndex].split(" ");
    if (origMLineParts.length > 3) {
      StringBuilder newMLine = new StringBuilder();
      int origPartIndex = 0;
      // Format is: m=<media> <port> <proto> <fmt> ...
      newMLine.append(origMLineParts[origPartIndex++]).append(" ");
      newMLine.append(origMLineParts[origPartIndex++]).append(" ");
      newMLine.append(origMLineParts[origPartIndex++]).append(" ");
      newMLine.append(codecRtpMap);
      for (; origPartIndex < origMLineParts.length; origPartIndex++) {
        if (!origMLineParts[origPartIndex].equals(codecRtpMap)) {
          newMLine.append(" ").append(origMLineParts[origPartIndex]);
        }
      }
      lines[mLineIndex] = newMLine.toString();
      Log.d(TAG, "Change media description: " + lines[mLineIndex]);
    } else {
      Log.e(TAG, "Wrong SDP media description format: " + lines[mLineIndex]);
    }
    StringBuilder newSdpDescription = new StringBuilder();
    for (String line : lines) {
      newSdpDescription.append(line).append("\r\n");
    }
    return newSdpDescription.toString();
  }

  private void drainCandidates() {
    if (queuedRemoteCandidates != null) {
      Log.d(TAG, "Add " + queuedRemoteCandidates.size() + " remote candidates");
      for (IceCandidate candidate : queuedRemoteCandidates) {
        peerConnection.addIceCandidate(candidate);
      }
      queuedRemoteCandidates = null;
    }
  }

  // Implementation detail: observe ICE & stream changes and react accordingly.
  private class PCObserver implements PeerConnection.Observer {
    @Override
    public void onIceCandidate(final IceCandidate candidate) {
      events.onIceCandidate(remoteUserId, candidate);
    }

    @Override
    public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
      events.onIceCandidatesRemoved(remoteUserId, candidates);
    }

    @Override
    public void onSignalingChange(
            PeerConnection.SignalingState newState) {
      Log.d(TAG, "SignalingState: " + newState);
    }

    @Override
    public void onIceConnectionChange(
            final PeerConnection.IceConnectionState newState) {
      Log.d(TAG, "IceConnectionState: " + newState);
      if (newState == IceConnectionState.CONNECTED) {
        events.onIceConnected();
      } else if (newState == IceConnectionState.DISCONNECTED) {
        events.onIceDisconnected();
      } else if (newState == IceConnectionState.FAILED) {
        reportError("ICE connection failed.");
      } else if (newState == IceConnectionState.CLOSED) {
        //
      }
    }

    @Override
    public void onIceGatheringChange(
            PeerConnection.IceGatheringState newState) {
      Log.d(TAG, "IceGatheringState: " + newState);
    }

    @Override
    public void onIceConnectionReceivingChange(boolean receiving) {
      Log.d(TAG, "IceConnectionReceiving changed to " + receiving);
    }

    @Override
    public void onAddStream(final MediaStream stream) {
      if (peerConnection == null || isError) {
        return;
      }
      if (stream.audioTracks.size() > 1 || stream.videoTracks.size() > 1) {
        reportError("Weird-looking stream: " + stream);
        return;
      }
      if (stream.videoTracks.size() == 1) {
        events.onAddRemoteStream(stream);
      }
    }

    @Override
    public void onRemoveStream(final MediaStream stream) {
      //VideoTrack videoTrack = stream.videoTracks.get(0);
    }

    @Override
    public void onDataChannel(final DataChannel dc) {
      reportError("AppRTC doesn't use data channels, but got: " + dc.label()
              + " anyway!");
    }

    @Override
    public void onRenegotiationNeeded() {
      // No need to do anything; AppRTC follows a pre-agreed-upon
      // signaling/negotiation protocol.
    }
  }

  // Implementation detail: handle offer creation/signaling and answer setting,
  // as well as adding remote ICE candidates once the answer SDP is set.
  private class SDPObserver implements SdpObserver {
    @Override
    public void onCreateSuccess(final SessionDescription origSdp) {
      if (localSdp != null) {
        reportError("Multiple SDP create.");
        return;
      }
      String sdpDescription = origSdp.description;
      sdpDescription = sdpDescription.replaceAll("UDP/TLS/RTP/SAVPF", "RTP/SAVPF");
      if (preferIsac) {
        sdpDescription = preferCodec(sdpDescription, AUDIO_CODEC_ISAC, true);
      }
      if (videoCallEnabled) {
        sdpDescription = preferCodec(sdpDescription, preferredVideoCodec, false);
      }
      final SessionDescription sdp = new SessionDescription(
              origSdp.type, sdpDescription);
      localSdp = sdp;
      if (peerConnection != null && !isError) {
        Log.d(TAG, "Set local SDP from " + sdp.type);
        peerConnection.setLocalDescription(sdpObserver, sdp);
      }
    }

    @Override
    public void onSetSuccess() {
      if (peerConnection == null || isError) {
        return;
      }
      if (isInitiator) {
        // For offering peer connection we first create offer and set
        // local SDP, then after receiving answer set remote SDP.
        if (peerConnection.getRemoteDescription() == null) {
          // We've just set our local SDP so time to send it.
          Log.d(TAG, "Local SDP set succesfully");
          events.onLocalDescription(remoteUserId, localSdp);
        } else {
          // We've just set remote description, so drain remote
          // and send local ICE candidates.
          Log.d(TAG, "Remote SDP set succesfully");
          drainCandidates();
        }
      } else {
        // For answering peer connection we set remote SDP and then
        // create answer and set local SDP.
        if (peerConnection.getLocalDescription() != null) {
          // We've just set our local SDP so time to send it, drain
          // remote and send local ICE candidates.
          Log.d(TAG, "Local SDP set succesfully");
          events.onLocalDescription(remoteUserId, localSdp);
          drainCandidates();
        } else {
          // We've just set remote SDP - do nothing for now -
          // answer will be created soon.
          Log.d(TAG, "Remote SDP set succesfully");
          createAnswer();
        }
      }
    }

    @Override
    public void onCreateFailure(final String error) {
      reportError("createSDP error: " + error);
    }

    @Override
    public void onSetFailure(final String error) {
      reportError("setSDP error: " + error);
    }
  }
}
