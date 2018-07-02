package com.hxsl.contactsdemo.http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.hxsl.contactsdemo.AppLoader;
import com.lidroid.xutils.http.RequestParams;

public class ParamUtils {

	private BizParams bizParams;
	private RequestParams reqParams;
	private static final String SEC_KEY = UrlUtils.getHttpSecret();
	private boolean secret = false;

	public ParamUtils() {
		bizParams = new BizParams();
		reqParams = new RequestParams();
	}

	public static List<NameValuePair> getBaseParams(String content) {
		long timestamp = System.currentTimeMillis();
		String signSrc = "AppID=%1$s&UserID=%2$s&Secret=%3$s&Content=%4$s&Timestamp=%5$s";
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("app_id", UrlUtils.getAppID()));
		nvps.add(new BasicNameValuePair("user_id", UrlUtils.getUserId()));
		nvps.add(new BasicNameValuePair("timestamp", String.valueOf(timestamp)));
		String str = String.format(signSrc, UrlUtils.getAppID(), UrlUtils.getUserId(), UrlUtils.getSecret(), TextUtils.isEmpty(content) ? "" : content, timestamp);
		nvps.add(new BasicNameValuePair("sign", MD5.encrypt(String.format(signSrc, UrlUtils.getAppID(), UrlUtils.getUserId(), UrlUtils.getSecret(), TextUtils.isEmpty(content) ? "" : content,
				timestamp))));

		nvps.add(new BasicNameValuePair("v", String.valueOf(AppLoader.versionCode)));
		return nvps;
	}

	public static List<NameValuePair> getBaseParams(JSONObject bizParams) {
		if (bizParams == null)
			return getBaseParams();
		return getBaseParams(bizParams.toString());
	}

	public static List<NameValuePair> getBaseParams(BizParams bizParams) {
		if (bizParams == null)
			return getBaseParams();
		return getBaseParams(bizParams.toString());
	}

	public static List<NameValuePair> getBaseParams() {
		return getBaseParams("");
	}

	public static RequestParams getBaseRequestParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter(getBaseParams());
		return params;
	}

	/*******************************************************
	 * 说明：添加业务参数
	 * @param name
	 * @param value
	 ******************************************************* 
	 */
	public void addBizParam(String name, Object value) {
		bizParams.addParam(name, value);
	}

	/*******************************************************
	 * 说明：设置本次提交是否采用加密的方式，默认为false
	 * @param secret
	 *******************************************************/
	public void setSecret(boolean secret) {
		this.secret = secret;
	}

	/*******************************************************
	 * 说明：取得基础的请求RequestParams
	 * @return
	 ******************************************************* 
	 */
	public RequestParams getRequestParams() {
		reqParams.setContentType("application/x-www-form-urlencoded");
		// String str=bizParams.toString();
		// str=str.replaceAll("\\", "");

		String paramsStr = secret ? AESUtils.encrypt(bizParams.toString(), SEC_KEY).replaceAll("[\r\n]", "") : bizParams.toString();
		reqParams.addQueryStringParameter(getBaseParams(paramsStr));
		try {
			reqParams.setBodyEntity(new StringEntity(paramsStr, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return reqParams;
	}

	public RequestParams getRequestParams(JSONArray array) {
		reqParams.setContentType("application/x-www-form-urlencoded");
		String paramsStr = secret ? AESUtils.encrypt(array.toString(), SEC_KEY).replaceAll("[\r\n]", "") : array.toString();
		reqParams.addQueryStringParameter(getBaseParams(paramsStr));
		try {
			reqParams.setBodyEntity(new StringEntity(paramsStr, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return reqParams;
	}
}
