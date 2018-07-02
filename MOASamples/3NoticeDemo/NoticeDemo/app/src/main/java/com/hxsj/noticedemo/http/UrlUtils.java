package com.hxsj.noticedemo.http;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

public class UrlUtils {

	private static Properties props = new Properties();

	static {
		init();
	}

	public static String getUserId() {
		if (props.containsKey("userId"))
			return props.getProperty("userId");
		return "";
	}
	public static String getUploadUserId(){
		if(props.containsKey("uploadUserId"))
			return props.getProperty("uploadUserId");
		return "";
	}

	public static String getSecret() {
		if (props.containsKey("secret"))
			return props.getProperty("secret");
		return "";
	}
	public static String getUploadSecret(){
		if (props.containsKey("uploadsecret"))
			return props.getProperty("uploadsecret");
		return "";
	}
	public static String getAppID(){
		if (props.containsKey("AppID")) {
			return props.getProperty("AppID");
		}
		return "";
	}



	public static String getXmppHost() {
		if (props.containsKey("xmppHost"))
			return props.getProperty("xmppHost");
		return "127.0.0.1";
	}
	public static String getXmppService(){
		if (props.containsKey("xmppService")) {
			return props.getProperty("xmppService");
		}
		return "xmpp200.com";
	}

	public static String getXmppPort() {
		if (props.containsKey("xmppPort"))
			return props.getProperty("xmppPort");
		return "5224";
	}

	public static String getServiceName() {
		if (props.containsKey("serviceName"))
			return props.getProperty("serviceName");
		return "";
	}

	public static String getApiKey() {
		if (props.containsKey("apiKey"))
			return props.getProperty("apiKey");
		return "1234567890";
	}

	/**
	 * 获取其他端口配置的特殊地址
	 * **/
	public static String getOtherUrl(String param, int id, int uid) {
		return String.format(props.getProperty(param), id, uid);
	}

	/*******************************************************
	 * 说明：获取Http的内容加密秘钥
	 * @return
	 *******************************************************/
	public static String getHttpSecret() {
		if (props.containsKey("httpSecret"))
			return props.getProperty("httpSecret");
		return "";
	}

	/*******************************************************
	 * 说明：获取上传文件链接
	 * @return
	 *******************************************************/
	public static String getUploadUrl() {
		return props.getProperty("uploadUrl", "");
	}
	public static String getUploadFileUrl(){
		return props.getProperty("uploadFile", "");
	}
	public static String getMeetingUrl() {
		if (props.containsKey("meetingUrl")) {
			return props.getProperty("meetingUrl");	
		}
		return "";
	}
	


	/*******************************************************
	 * 说明：根据键名取得对应的值

	 * @param name
	 * @return
	 ********************************************************/
	public static String getUrl(String name) {
		if (!props.containsKey("host"))
			return null;
		if (props.containsKey(name))
			return props.getProperty("host").concat(props.getProperty(name));
		return null;
	}

	/*******************************************************
	 * 说明：根据objs自动格式化url
	 * 
	 * @author gumengfu
	 * @param name
	 * @param objs
	 * @return
	 ********************************************************/
	public static String getUrl(String name, Object... objs) {
		return String.format(getUrl(name), objs);
	}

	@SuppressWarnings("unchecked")
	public static Set<String> getKeys() {
		return (Set<String>) props.keySet().iterator();
	}

	@SuppressWarnings("unchecked")
	public static Collection<String> getValues() {
		return (Collection<String>) props.values().iterator();
	}

	/*******************************************************
	 * 说明：初始化urlapi
	 * 
	 * @author gumengfu
	 ********************************************************/
	private static void init() {
		try {
			props.load(UrlBuilder.read("urlapi.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
