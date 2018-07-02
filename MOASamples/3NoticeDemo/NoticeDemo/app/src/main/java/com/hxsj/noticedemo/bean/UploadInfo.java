/**********************************************************
 * Copyright © 2013-1014 深圳市美传网络科技有限公司版权所有
 * 创 建 人：gumengfu
 * 创 建 日 期：2015-1-7 下午4:09:48
 * 版 本 号：
 * 修 改 人：
 * 描 述：
 * <p>
 *	
 * </p>
 **********************************************************/
package com.hxsj.noticedemo.bean;

/**
 * <p>
 * 	上传返回的信息
 * </p>
 * @date 2015-1-7
 * @version 1.0
 * @since 1.0
 */
public class UploadInfo {
	private int state;
	private String msg;
	private String url;
	private String thumb_url;
	private String name;
	private String size;
	private String ext;
	private String md5;

	

	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}
	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the thumb_url
	 */
	public String getThumb_url() {
		return thumb_url;
	}
	/**
	 * @param thumb_url the thumb_url to set
	 */
	public void setThumb_url(String thumb_url) {
		this.thumb_url = thumb_url;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}
	/**
	 * @return the ext
	 */
	public String getExt() {
		return ext;
	}
	/**
	 * @param ext the ext to set
	 */
	public void setExt(String ext) {
		this.ext = ext;
	}
	/**
	 * @return the md5
	 */
	public String getMd5() {
		return md5;
	}
	/**
	 * @param md5 the md5 to set
	 */
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	
	
}
