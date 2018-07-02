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

import android.content.Context;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.MediaCodecVideoEncoder;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.voiceengine.WebRtcAudioManager;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;

public class WebSocketRTCClient implements AppRTCClient,
        WebSocketChannelClient.WebSocketChannelEvents {
  private static final String TAG = WebSocketRTCClient.class.getName();
  public static final String VIDEO_TRACK_ID = "ARDAMSv0";
  public static final String AUDIO_TRACK_ID = "ARDAMSa0";
  private static final String FIELD_TRIAL_AUTOMATIC_RESIZE =
          "WebRTC-MediaCodecVideoEncoder-AutomaticResize/Enabled/";
  private static final String VIDEO_CODEC_VP8 = "VP8";
  private static final String VIDEO_CODEC_VP9 = "VP9";
  private static final String VIDEO_CODEC_H264 = "H264";
  private static final String AUDIO_CODEC_OPUS = "opus";
  private static final String AUDIO_CODEC_ISAC = "ISAC";
  private static final String VIDEO_CODEC_PARAM_START_BITRATE =
          "x-google-start-bitrate";
  private static final String AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate";
  private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
  private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
  private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
  private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";
  private static final String MAX_VIDEO_WIDTH_CONSTRAINT = "maxWidth";
  private static final String MIN_VIDEO_WIDTH_CONSTRAINT = "minWidth";
  private static final String MAX_VIDEO_HEIGHT_CONSTRAINT = "maxHeight";
  private static final String MIN_VIDEO_HEIGHT_CONSTRAINT = "minHeight";
  private static final String MAX_VIDEO_FPS_CONSTRAINT = "maxFrameRate";
  private static final String MIN_VIDEO_FPS_CONSTRAINT = "minFrameRate";
  private static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";
  private static final int HD_VIDEO_WIDTH = 1280;
  private static final int HD_VIDEO_HEIGHT = 720;
  private static final int MAX_VIDEO_WIDTH = 1280;
  private static final int MAX_VIDEO_HEIGHT = 720;
  private static final int MAX_VIDEO_FPS = 30;

  private final LooperExecutor executor;
  private SignalingEvents events;
  private PeerConnectionClient.PeerConnectionEvents peerConnectionEvents;
  private WebSocketChannelClient wsClient;
  private RoomConnectionParameters connectionParameters;
  private PeerConnectionClient.PeerConnectionParameters peerConnectionParameters;
  private HashMap<String, PeerConnectionClient> peers;
  private Context context;
  private EglBase rootEglBase;

  private PeerConnectionFactory factory;
  private PeerConnection.RTCConfiguration rtcConfig;
  private MediaConstraints pcConstraints;
  private MediaConstraints videoConstraints;
  private MediaConstraints audioConstraints;
  private MediaConstraints sdpMediaConstraints;
  private MediaStream localMediaStream;
  private VideoCapturerAndroid videoCapturer;
  private VideoSource videoSource;
  private int numberOfCameras;

  private boolean videoCallEnabled;
  private boolean preferIsac;
  private String preferredVideoCodec;
  private boolean videoSourceStopped;
  private boolean isError;
  // enableVideo is set to true if video should be rendered and sent.
  private boolean renderVideo;
  private VideoTrack localVideoTrack;
  // enableAudio is set to true if audio should be sent.
  private boolean enableAudio;
  private AudioTrack localAudioTrack;

  public WebSocketRTCClient(SignalingEvents events,
                            PeerConnectionClient.PeerConnectionParameters peerConnectionParameters,
                            Context context,
                            EglBase rootEglBase,
                            CallActivity peerConnectionEvents) {
    this.events = events;
    this.executor = new LooperExecutor();
    executor.setName("wsRTCClientThread");
    this.peerConnectionParameters = peerConnectionParameters;
    this.context = context;
    this.peerConnectionEvents = peerConnectionEvents;
    peers = new HashMap<>();
    this.rootEglBase = rootEglBase;
    numberOfCameras = CameraEnumerationAndroid.getDeviceCount();
    localMediaStream = null;
    videoCapturer = null;
    isError = false;
    videoSourceStopped = false;
    renderVideo = true;
    enableAudio = true;
    videoCallEnabled = peerConnectionParameters.videoCallEnabled;

    executor.requestStart();
  }

  private void initPeerEnvironment() {
    createPeerConnectionFactory(context);
    createMediaConstraints();
    createPeerConnection(rootEglBase.getEglBaseContext());
    Log.d(TAG, "initPeerEnvironment success!");
  }

  // --------------------------------------------------------------------
  // AppRTCClient interface implementation.
  // Asynchronously connect to an AppRTC room URL using supplied connection
  // parameters, retrieves room parameters and connect to WebSocket server.
  @Override
  public void connectToRoom(RoomConnectionParameters connectionParameters) {
    this.connectionParameters = connectionParameters;
    initPeerEnvironment();
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
    if (factory != null && peerConnectionParameters.aecDump) {
      factory.stopAecDump();
    }
    for (PeerConnectionClient peer : peers.values()) {
      peer.close();
    }
    if (videoSource != null) {
      videoSource.stop();
      videoSource = null;
    }
    if (videoCapturer != null) {
      videoCapturer.dispose();
      videoCapturer = null;
    }
    if (factory != null) {
      factory.dispose();
      factory = null;
    }
    if (wsClient != null) {
      wsClient.disconnect();
    }
    PeerConnectionFactory.stopInternalTracingCapture();
    PeerConnectionFactory.shutdownInternalTracer();
    Log.e(TAG, "------disconnectFromRoomInternal-------");
  }

  public boolean isHDVideo() {
    if (!videoCallEnabled) {
      return false;
    }
    int minWidth = 0;
    int minHeight = 0;
    for (MediaConstraints.KeyValuePair keyValuePair : videoConstraints.mandatory) {
      if (keyValuePair.getKey().equals("minWidth")) {
        try {
          minWidth = Integer.parseInt(keyValuePair.getValue());
        } catch (NumberFormatException e) {
          Log.e(TAG, "Can not parse video width from video constraints");
        }
      } else if (keyValuePair.getKey().equals("minHeight")) {
        try {
          minHeight = Integer.parseInt(keyValuePair.getValue());
        } catch (NumberFormatException e) {
          Log.e(TAG, "Can not parse video height from video constraints");
        }
      }
    }
    return minWidth * minHeight >= 1280 * 720;
  }

  @Override
  public void startVideoSource() {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        if (videoSource != null && videoSourceStopped) {
          Log.d(TAG, "Restart video source.");
          videoSource.restart();
          videoSourceStopped = false;
        }
      }
    });
  }

  @Override
  public void stopVideoSource() {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        if (videoSource != null && !videoSourceStopped) {
          Log.d(TAG, "Stop video source.");
          videoSource.stop();
          videoSourceStopped = true;
        }
      }
    });
  }

  @Override
  public void switchCamera() {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        switchCameraInternal();
      }
    });
  }

  @Override
  public void changeCaptureFormat(final int width, final int height, final int framerate) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        changeCaptureFormatInternal(width, height, framerate);
      }
    });
  }

  @Override
  public void setAudioEnabled(final boolean enable) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        enableAudio = enable;
        if (localAudioTrack != null) {
          localAudioTrack.setEnabled(enableAudio);
        }
      }
    });
  }

  @Override
  public void setVideoEnabled(final boolean enable) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        renderVideo = enable;
        if (localVideoTrack != null) {
          localVideoTrack.setEnabled(renderVideo);
        }
      }
    });
  }

  private AudioTrack createAudioTrack() {
    AudioSource audioSource = factory.createAudioSource(audioConstraints);
    localAudioTrack = factory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
    localAudioTrack.setEnabled(enableAudio);
    return localAudioTrack;
  }

  private VideoTrack createVideoTrack(VideoCapturerAndroid capturer) {
    videoSource = factory.createVideoSource(capturer, videoConstraints);
    localVideoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
    localVideoTrack.setEnabled(renderVideo);
    return localVideoTrack;
  }

  private void switchCameraInternal() {
    if (!videoCallEnabled || numberOfCameras < 2 || isError || videoCapturer == null) {
      Log.e(TAG, "Failed to switch camera. Video: " + videoCallEnabled + ". Error : "
              + isError + ". Number of cameras: " + numberOfCameras);
      return;  // No video is sent or only one camera is available or error happened.
    }
    Log.d(TAG, "Switch camera");
    videoCapturer.switchCamera(null);
  }

  private void changeCaptureFormatInternal(int width, int height, int framerate) {
    if (!videoCallEnabled || isError || videoCapturer == null) {
      Log.e(TAG, "Failed to change capture format. Video: " + videoCallEnabled + ". Error : "
              + isError);
      return;
    }
    Log.d(TAG, "changeCaptureFormat: " + width + "x" + height + "@" + framerate);
    videoCapturer.onOutputFormatRequest(width, height, framerate);
  }

  // Send local answer SDP to the other participant.
  @Override
  public void sendLocalSdp(final String userId, final SessionDescription sdp) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        JSONObject json = new JSONObject();
        AppRTCUtils.jsonPut(json, "sdp", sdp.description);
        AppRTCUtils.jsonPut(json, "type", sdp.type.canonicalForm());
        wsClient.send(userId, json);
      }
    });
  }

  // Send Ice candidate to the other participant.
  @Override
  public void sendLocalIceCandidate(final String userId, final IceCandidate candidate) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        JSONObject json = new JSONObject();
        AppRTCUtils.jsonPut(json, "type", "candidate");
        AppRTCUtils.jsonPut(json, "id", candidate.sdpMid);
        AppRTCUtils.jsonPut(json, "label", candidate.sdpMLineIndex);
        AppRTCUtils.jsonPut(json, "candidate", candidate.sdp);
        wsClient.send(userId, json);
      }
    });
  }

  // Send removed Ice candidates to the other participant.
  @Override
  public void sendLocalIceCandidateRemovals(final String userId, final IceCandidate[] candidates) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        JSONObject json = new JSONObject();
        AppRTCUtils.jsonPut(json, "type", "remove-candidates");
        JSONArray jsonArray = new JSONArray();
        for (final IceCandidate candidate : candidates) {
          jsonArray.put(toJsonCandidate(candidate));
        }
        AppRTCUtils.jsonPut(json, "candidates", jsonArray);
        wsClient.send(userId, json);
      }
    });
  }

  // Converts a Java candidate to a JSONObject.
  private static JSONObject toJsonCandidate(final IceCandidate candidate) {
    JSONObject json = new JSONObject();
    AppRTCUtils.jsonPut(json, "id", candidate.sdpMid);
    AppRTCUtils.jsonPut(json, "label", candidate.sdpMLineIndex);
    AppRTCUtils.jsonPut(json, "candidate", candidate.sdp);
    return json;
  }

  // Converts a JSON candidate to a Java object.
  private static IceCandidate toJavaCandidate(JSONObject json) {
    return new IceCandidate(
            AppRTCUtils.jsonGetString(json, "id"),
            AppRTCUtils.jsonGetInt(json, "label"),
            AppRTCUtils.jsonGetString(json, "candidate")
    );
  }

  private PeerConnectionClient openPeerConnectionClient(String userId, boolean isInitiator) {
    PeerConnectionClient peer = peers.get(userId);
    if (peer != null)
      return peer;
    peer = new PeerConnectionClient(userId, isInitiator);
    peers.put(userId, peer);

    peer.init(peerConnectionParameters, peerConnectionEvents,
            preferIsac, preferredVideoCodec, sdpMediaConstraints);
    peer.createPeerConnection(factory, localMediaStream, rtcConfig, pcConstraints);
    return peer;
  }

  @Override
  public void onMessage(final String userId, final JSONObject json) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        onMessageInternal(userId, json);
      }
    });
  }

  private void onMessageInternal(String userId, JSONObject json) {
    PeerConnectionClient peer = openPeerConnectionClient(userId, false);
    String type = AppRTCUtils.jsonGetString(json, "type");
    if (TextUtils.isEmpty(type))
      return;
    switch (type) {
      case "offer":
        String offerSdp = AppRTCUtils.jsonGetString(json, "sdp");
        peer.setRemoteDescription(
                new SessionDescription(SessionDescription.Type.OFFER, offerSdp)
        );
        break;
      case "answer":
        String answerSdp = AppRTCUtils.jsonGetString(json, "sdp");
        peer.setRemoteDescription(
                new SessionDescription(SessionDescription.Type.ANSWER, answerSdp)
        );
        break;
      case "candidate":
        peer.addRemoteIceCandidate(toJavaCandidate(json));
        break;
      case "remove-candidates":
        JSONArray candidateArray = AppRTCUtils.getJSONArray(json, "candidates");
        IceCandidate[] candidates = new IceCandidate[candidateArray.length()];
        for (int i = 0; i < candidateArray.length(); ++i) {
          candidates[i] = toJavaCandidate(AppRTCUtils.getJSONObject(candidateArray, i));
        }
        peer.removeRemoteIceCandidates(candidates);
        break;
    }
  }

  @Override
  public void onJoin(final String userId) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        events.onJoin(userId);
        if (connectionParameters != null && connectionParameters.userId.equals(userId))
          return;
        openPeerConnectionClient(userId, true);
      }
    });
  }

  @Override
  public void onLeave(final String userId) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        PeerConnectionClient peer = peers.get(userId);
        assert (peer != null);
        peer.close();
        peers.remove(userId);
        events.onLeave(userId);
      }
    });
  }

  @Override
  public void onConnect() {
    Log.d(TAG, "onConnect");
  }

  @Override
  public void onConnectError(String description) {
    Log.e(TAG, "onConnectError: " + description);
    events.onChannelError(description);
  }

  @Override
  public void onDisconnect() {
    Log.d(TAG, "onDisconnect");
    events.onChannelClose();
  }

  private void reportError(final String errorMessage) {
    Log.e(TAG, "WebSocketRTCClient error: " + errorMessage);
    if (!isError) {
      events.onChannelError(errorMessage);
      isError = true;
    }
  }

  private void createPeerConnectionFactory(Context context) {
    PeerConnectionFactory.initializeInternalTracer();
    if (peerConnectionParameters.tracing) {
      PeerConnectionFactory.startInternalTracingCapture(
              Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                      + "webrtc-trace.txt");
    }
    Log.d(TAG, "Create peer connection factory. Use video: " +
            peerConnectionParameters.videoCallEnabled);
    isError = false;

    // Initialize field trials.
    PeerConnectionFactory.initializeFieldTrials(FIELD_TRIAL_AUTOMATIC_RESIZE);

    // Check preferred video codec.
    preferredVideoCodec = VIDEO_CODEC_VP8;
    if (videoCallEnabled && peerConnectionParameters.videoCodec != null) {
      if (peerConnectionParameters.videoCodec.equals(VIDEO_CODEC_VP9)) {
        preferredVideoCodec = VIDEO_CODEC_VP9;
      } else if (peerConnectionParameters.videoCodec.equals(VIDEO_CODEC_H264)) {
        preferredVideoCodec = VIDEO_CODEC_H264;
      }
    }
    Log.d(TAG, "Pereferred video codec: " + preferredVideoCodec);

    // Check if ISAC is used by default.
    preferIsac = peerConnectionParameters.audioCodec != null
            && peerConnectionParameters.audioCodec.equals(AUDIO_CODEC_ISAC);

    // Enable/disable OpenSL ES playback.
    if (!peerConnectionParameters.useOpenSLES) {
      Log.d(TAG, "Disable OpenSL ES audio even if device supports it");
      WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(true /* enable */);
    } else {
      Log.d(TAG, "Allow OpenSL ES audio if device supports it");
      WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(false);
    }

    // Create peer connection factory.
    if (!PeerConnectionFactory.initializeAndroidGlobals(context, true, true,
            peerConnectionParameters.videoCodecHwAcceleration)) {
      reportError("Failed to initializeAndroidGlobals");
    }

    PeerConnectionFactory.Options options = null;
    if (peerConnectionParameters.loopback) {
      options = new PeerConnectionFactory.Options();
      options.networkIgnoreMask = 0;
    }
    factory = new PeerConnectionFactory(options);
    Log.d(TAG, "Peer connection factory created.");
  }

  private void createMediaConstraints() {
    // Create peer connection constraints.
    pcConstraints = new MediaConstraints();
    // Enable DTLS for normal calls and disable for loopback calls.
    if (peerConnectionParameters.loopback) {
      pcConstraints.optional.add(
              new MediaConstraints.KeyValuePair(DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, "false"));
    } else {
      pcConstraints.optional.add(
              new MediaConstraints.KeyValuePair(DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, "true"));
    }

    // Check if there is a camera on device and disable video call if not.
    numberOfCameras = CameraEnumerationAndroid.getDeviceCount();
    if (numberOfCameras == 0) {
      Log.w(TAG, "No camera on device. Switch to audio only call.");
      videoCallEnabled = false;
    }
    // Create video constraints if video call is enabled.
    if (videoCallEnabled) {
      videoConstraints = new MediaConstraints();
      int videoWidth = peerConnectionParameters.videoWidth;
      int videoHeight = peerConnectionParameters.videoHeight;

      // If VP8 HW video encoder is supported and video resolution is not
      // specified force it to HD.
      if ((videoWidth == 0 || videoHeight == 0)
              && peerConnectionParameters.videoCodecHwAcceleration
              && MediaCodecVideoEncoder.isVp8HwSupported()) {
        videoWidth = HD_VIDEO_WIDTH;
        videoHeight = HD_VIDEO_HEIGHT;
      }

      // Add video resolution constraints.
      if (videoWidth > 0 && videoHeight > 0) {
        videoWidth = Math.min(videoWidth, MAX_VIDEO_WIDTH);
        videoHeight = Math.min(videoHeight, MAX_VIDEO_HEIGHT);
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MIN_VIDEO_WIDTH_CONSTRAINT, Integer.toString(videoWidth)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MAX_VIDEO_WIDTH_CONSTRAINT, Integer.toString(videoWidth)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MIN_VIDEO_HEIGHT_CONSTRAINT, Integer.toString(videoHeight)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MAX_VIDEO_HEIGHT_CONSTRAINT, Integer.toString(videoHeight)));
      }

      // Add fps constraints.
      int videoFps = peerConnectionParameters.videoFps;
      if (videoFps > 0) {
        videoFps = Math.min(videoFps, MAX_VIDEO_FPS);
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MIN_VIDEO_FPS_CONSTRAINT, Integer.toString(videoFps)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MAX_VIDEO_FPS_CONSTRAINT, Integer.toString(videoFps)));
      }
    }

    // Create audio constraints.
    audioConstraints = new MediaConstraints();
    // added for audio performance measurements
    if (peerConnectionParameters.noAudioProcessing) {
      Log.d(TAG, "Disabling audio processing");
      audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
              AUDIO_ECHO_CANCELLATION_CONSTRAINT, "false"));
      audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
              AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false"));
      audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
              AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "false"));
      audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
              AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "false"));
    }
    // Create SDP constraints.
    sdpMediaConstraints = new MediaConstraints();
    sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
            "OfferToReceiveAudio", "true"));
    if (videoCallEnabled || peerConnectionParameters.loopback) {
      sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
              "OfferToReceiveVideo", "true"));
    } else {
      sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
              "OfferToReceiveVideo", "false"));
    }
  }

  private void createPeerConnection(EglBase.Context renderEGLContext) {
    Log.d(TAG, "PCConstraints: " + pcConstraints.toString());
    if (videoConstraints != null) {
      Log.d(TAG, "VideoConstraints: " + videoConstraints.toString());
    }

    if (videoCallEnabled) {
      Log.d(TAG, "EGLContext: " + renderEGLContext);
      factory.setVideoHwAccelerationOptions(renderEGLContext, renderEGLContext);
    }

    LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
    iceServers.add(new PeerConnection.IceServer("stun:120.76.43.57:3478"));
    iceServers.add(new PeerConnection.IceServer("stun:23.21.150.121"));
    iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
    iceServers.add(new PeerConnection.IceServer("turn:120.76.43.57:3478?transport=udp", "test", "123456"));
    iceServers.add(new PeerConnection.IceServer("turn:120.76.43.57:3478?transport=tcp", "test", "123456"));

    rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
    // TCP candidates are only useful when connecting to a server that supports
    // ICE-TCP.
    rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
    rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
    rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
    rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
    // Use ECDSA encryption.
    rtcConfig.keyType = PeerConnection.KeyType.ECDSA;

    // Set default WebRTC tracing and INFO libjingle logging.
    // NOTE: this _must_ happen while |factory| is alive!
    Logging.enableTracing(
            "logcat:",
            EnumSet.of(Logging.TraceLevel.TRACE_DEFAULT),
            Logging.Severity.LS_INFO);

    localMediaStream = factory.createLocalMediaStream(AppRTCUtils.getRandomString(10));
    if (videoCallEnabled) {
      String cameraDeviceName = CameraEnumerationAndroid.getDeviceName(0);
      String frontCameraDeviceName =
              CameraEnumerationAndroid.getNameOfFrontFacingDevice();
      if (numberOfCameras > 1 && frontCameraDeviceName != null) {
        cameraDeviceName = frontCameraDeviceName;
      }
      Log.d(TAG, "Opening camera: " + cameraDeviceName);
      videoCapturer = VideoCapturerAndroid.create(cameraDeviceName, null,
              peerConnectionParameters.captureToTexture ? renderEGLContext : null);
      if (videoCapturer == null) {
        reportError("Failed to open camera");
        return;
      }
      localMediaStream.addTrack(createVideoTrack(videoCapturer));
      events.onLocalStream(localMediaStream);
    }

    localMediaStream.addTrack(createAudioTrack());

    if (peerConnectionParameters.aecDump) {
      try {
        String storageDirectory = Environment.getExternalStorageDirectory().getPath();
        ParcelFileDescriptor aecDumpFileDescriptor = ParcelFileDescriptor.open(
                new File(storageDirectory + "Download/audio.aecdump"),
                ParcelFileDescriptor.MODE_READ_WRITE |
                        ParcelFileDescriptor.MODE_CREATE |
                        ParcelFileDescriptor.MODE_TRUNCATE);
        factory.startAecDump(aecDumpFileDescriptor.getFd(), -1);
      } catch (IOException e) {
        Log.e(TAG, "Can not open aecdump file", e);
      }
    }
  }
}
