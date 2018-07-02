package com.hxsj.telephone.call;

import java.util.List;

import org.json.JSONArray;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

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
	   * Send offer SDP to the other participant.
	   */
	  void sendLocalSdp(final SessionDescription sdp);

	  /**
	   * Send Ice candidate to the other participant.
	   */
	  void sendLocalIceCandidate(final IceCandidate candidate);

	  /**
	   * Send removed ICE candidates to the other participant.
	   */
	  void sendLocalIceCandidateRemovals(final IceCandidate[] candidates);

	  /**
	   * Disconnect from room.
	   */
	  void disconnectFromRoom();

	  void send(final String to, final Object message);

	  void SetRemoteUserId(String userId);

	  void createPeerConnectionFactory();

	  /**
	   * Struct holding the signaling parameters of an AppRTC room.
	   */
	  class SignalingParameters {
	    public final List<PeerConnection.IceServer> iceServers;
	    public final boolean initiator;
	    public final String clientId;
	    public final String wssUrl;
	    public final String wssPostUrl;
	    public final SessionDescription offerSdp;
	    public final List<IceCandidate> iceCandidates;

	    public SignalingParameters(
	        List<PeerConnection.IceServer> iceServers,
	        boolean initiator, String clientId,
	        String wssUrl, String wssPostUrl,
	        SessionDescription offerSdp, List<IceCandidate> iceCandidates) {
	      this.iceServers = iceServers;
	      this.initiator = initiator;
	      this.clientId = clientId;
	      this.wssUrl = wssUrl;
	      this.wssPostUrl = wssPostUrl;
	      this.offerSdp = offerSdp;
	      this.iceCandidates = iceCandidates;
	    }
	  }

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
//	    void onConnectedToRoom(final SignalingParameters params);

	    void onLogin(final JSONArray userIds);

	    void onLeave(final String userId);

	    void onJoin(final String userId);

	    void onCall(final String userId, String username);

	    void onCalling(final String userId);

	    void onCancel(final String userId);

	    /**
	     * Callback fired once remote SDP is received.
	     */
	    //void onRemoteDescription(final SessionDescription sdp);

	    /**
	     * Callback fired once remote Ice candidate is received.
	     */
	    //void onRemoteIceCandidate(final IceCandidate candidate);

	    /**
	     * Callback fired once remote Ice candidate removals are received.
	     */
	   // void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates);

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