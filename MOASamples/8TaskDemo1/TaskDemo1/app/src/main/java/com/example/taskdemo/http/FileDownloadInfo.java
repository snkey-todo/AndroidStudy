package com.example.taskdemo.http;

import java.io.File;

import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;
import com.lidroid.xutils.http.HttpHandler;

/**
 * 下载过程的的数据库持久化�? Created by gumengfu on 2014/10/24.
 */
@Table(name = "file_download_info")
public class FileDownloadInfo {

	private long id; // 数据库Id

	@Transient
	// Transient注解，不添加到序列中，也就不会在数据库中找到
	private HttpHandler<File> handler;
	private HttpHandler.State state;
	private String url; // 下载url
	private String fileName;
	private String fileSavePath; // 文件保存路径
	private long progress; // 已下载的进度
	private long fileLength;
	private boolean autoResume; // 是否自动从断点处继续下载
	private boolean autoRename; // 是否根据后台返回的文件名来重命名下载好的文件

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public HttpHandler<File> getHandler() {
		return handler;
	}

	public void setHandler(HttpHandler<File> handler) {
		this.handler = handler;
	}

	public HttpHandler.State getState() {
		return state;
	}

	public void setState(HttpHandler.State state) {
		this.state = state;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSavePath() {
		return fileSavePath;
	}

	public void setFileSavePath(String fileSavePath) {
		this.fileSavePath = fileSavePath;
	}

	public long getProgress() {
		return progress;
	}

	public void setProgress(long progress) {
		this.progress = progress;
	}

	public long getFileLength() {
		return fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public boolean isAutoResume() {
		return autoResume;
	}

	public void setAutoResume(boolean autoResume) {
		this.autoResume = autoResume;
	}

	public boolean isAutoRename() {
		return autoRename;
	}

	public void setAutoRename(boolean autoRename) {
		this.autoRename = autoRename;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof FileDownloadInfo))
			return false;

		FileDownloadInfo that = (FileDownloadInfo) o;
		if (that.getId() != this.getId())
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}
}
