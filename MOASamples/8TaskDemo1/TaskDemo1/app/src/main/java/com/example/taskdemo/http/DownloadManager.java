package com.example.taskdemo.http;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.converter.ColumnConverter;
import com.lidroid.xutils.db.converter.ColumnConverterFactory;
import com.lidroid.xutils.db.sqlite.ColumnDbType;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

/**
 * Created by gumengfu on 2014/10/24.
 */
public class DownloadManager {

	// 下载列表信息
	private List<FileDownloadInfo> fileDownloadInfoList;
	private Context context;
	private DbUtils dbUtils;

	// 最大下载线程数
	private int maxDownloadThread = 3;

	public DownloadManager(Context context) {
		ColumnConverterFactory.registerColumnConverter(HttpHandler.State.class,
				new HttpHandlerStateConverter());
		this.context = context;
		this.dbUtils = DbUtils.create(context);
		try {
			// 取得下载信息表单的所有数据
			fileDownloadInfoList = dbUtils.findAll(Selector
					.from(FileDownloadInfo.class));
		} catch (DbException e) {
			e.printStackTrace();
		}
		if (fileDownloadInfoList == null) {
			fileDownloadInfoList = new ArrayList<FileDownloadInfo>();
		}
	}

	public int getDownloadInfoListCount() {
		return fileDownloadInfoList.size();
	}

	public FileDownloadInfo getFileDownloadInfo(int index) {
		return fileDownloadInfoList.get(index);
	}

	public void addNewDownload(String url, String savePath, String fileName,
			boolean autoResume, boolean autoRename,
			final RequestCallBack<File> callback) throws DbException {
		FileDownloadInfo downloadInfo = new FileDownloadInfo();
		downloadInfo.setAutoRename(autoRename);
		downloadInfo.setAutoResume(autoResume);
		downloadInfo.setFileName(fileName);
		downloadInfo.setFileSavePath(savePath);
		downloadInfo.setUrl(url);

		HttpUtils http = new HttpUtils();
		http.configRequestThreadPoolSize(maxDownloadThread);
		HttpHandler<File> handler = http
				.download(url, savePath, autoResume, autoRename,
						new DownloadManagerCallback(downloadInfo, callback));
		downloadInfo.setHandler(handler);
		downloadInfo.setState(handler.getState());
		fileDownloadInfoList.add(downloadInfo);
		dbUtils.saveBindingId(downloadInfo);
	}

	public void resumeDownload(int index, final RequestCallBack<File> callback)
			throws DbException {
		FileDownloadInfo downloadInfo = fileDownloadInfoList.get(index);
		resumeDownload(downloadInfo, callback);
	}

	public void resumeDownload(FileDownloadInfo downloadInfo,
			RequestCallBack<File> callback) throws DbException {
		HttpUtils http = new HttpUtils();
		http.configRequestThreadPoolSize(maxDownloadThread);
		HttpHandler<File> handler = http.download(downloadInfo.getUrl(),
				downloadInfo.getFileSavePath(), downloadInfo.isAutoResume(),
				downloadInfo.isAutoRename(), new DownloadManagerCallback(
						downloadInfo, callback));
		downloadInfo.setHandler(handler);
		downloadInfo.setState(handler.getState());
		dbUtils.saveOrUpdate(downloadInfo);
	}

	public void removeDownload(int index) throws DbException {
		FileDownloadInfo downloadInfo = fileDownloadInfoList.get(index);
		removeDownload(downloadInfo);
	}

	public void removeDownload(FileDownloadInfo downloadInfo)
			throws DbException {
		HttpHandler<File> handler = downloadInfo.getHandler();
		if (handler != null && !handler.isCancelled())
			handler.cancel();
		fileDownloadInfoList.remove(downloadInfo);
		dbUtils.delete(downloadInfo);
	}

	public void stopDownload(int index) throws DbException {
		FileDownloadInfo downloadInfo = fileDownloadInfoList.get(index);
		stopDownload(downloadInfo);
	}

	public void stopDownload(FileDownloadInfo downloadInfo) throws DbException {
		HttpHandler<File> handler = downloadInfo.getHandler();
		if (handler != null && !handler.isCancelled()) {
			handler.cancel();
		} else {
			downloadInfo.setState(HttpHandler.State.CANCELLED);
		}
		dbUtils.saveOrUpdate(downloadInfo);
	}

	/**
	 * 停止全部下载进程
	 * 
	 * @throws DbException
	 */
	public void stopAllDownload() throws DbException {
		for (FileDownloadInfo downloadInfo : fileDownloadInfoList) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null && !handler.isCancelled()) {
				handler.cancel();
			} else {
				downloadInfo.setState(HttpHandler.State.CANCELLED);
			}
		}
		dbUtils.saveOrUpdateAll(fileDownloadInfoList);
	}

	/**
	 * 恢复所有下载进度
	 */
	public void backupDownloadInfoList() throws DbException {
		for (FileDownloadInfo downloadInfo : fileDownloadInfoList) {
			HttpHandler handler = downloadInfo.getHandler();
			if (handler != null)
				downloadInfo.setState(handler.getState());
		}
		dbUtils.saveOrUpdateAll(fileDownloadInfoList);
	}

	public int getMaxDownloadThread() {
		return maxDownloadThread;
	}

	public void setMaxDownloadThread(int maxDownloadThread) {
		this.maxDownloadThread = maxDownloadThread;
	}

	/**
	 * 下载进度管理类，实时更新数据库当前数据的状�?�和进度
	 */
	public class DownloadManagerCallback extends RequestCallBack<File> {

		private FileDownloadInfo downloadInfo;
		private RequestCallBack<File> baseCallback;

		public DownloadManagerCallback(FileDownloadInfo downloadInfo,
				RequestCallBack<File> baseCallback) {
			this.downloadInfo = downloadInfo;
			this.baseCallback = baseCallback;
		}

		public RequestCallBack<File> getBaseCallback() {
			return baseCallback;
		}

		public void setBaseCallback(RequestCallBack<File> baseCallback) {
			this.baseCallback = baseCallback;
		}

		@Override
		public Object getUserTag() {
			if (baseCallback == null)
				return null;
			return baseCallback.getUserTag();
		}

		@Override
		public void setUserTag(Object userTag) {
			if (baseCallback == null)
				return;
			baseCallback.setUserTag(userTag);
		}

		@Override
		public void onStart() {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			try {
				dbUtils.saveOrUpdate(downloadInfo);
			} catch (DbException e) {
				e.printStackTrace();
			}
			if (baseCallback != null) {
				baseCallback.onStart();
			}
		}

		@Override
		public void onCancelled() {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			try {
				dbUtils.saveOrUpdate(downloadInfo);
			} catch (DbException e) {
				e.printStackTrace();
			}
			if (baseCallback != null) {
				baseCallback.onCancelled();
			}
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			downloadInfo.setFileLength(total);
			downloadInfo.setProgress(current);
			try {
				dbUtils.saveOrUpdate(downloadInfo);
			} catch (DbException e) {
				e.printStackTrace();
			}
			if (baseCallback != null) {
				baseCallback.onLoading(total, current, isUploading);
			}
		}

		@Override
		public void onSuccess(ResponseInfo<File> fileResponseInfo) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null)
				downloadInfo.setState(handler.getState());
			try {
				dbUtils.saveOrUpdate(downloadInfo);
			} catch (DbException e) {
				e.printStackTrace();
			}
			if (baseCallback != null)
				baseCallback.onSuccess(fileResponseInfo);
		}

		@Override
		public void onFailure(HttpException e, String msg) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null)
				downloadInfo.setState(handler.getState());
			try {
				dbUtils.saveOrUpdate(downloadInfo);
			} catch (DbException e1) {
				e1.printStackTrace();
			}
			if (baseCallback != null)
				baseCallback.onFailure(e, msg);
		}
	}

	private class HttpHandlerStateConverter implements
			ColumnConverter<HttpHandler.State> {

		@Override
		public HttpHandler.State getFieldValue(Cursor cursor, int i) {
			return HttpHandler.State.valueOf(cursor.getInt(i));
		}

		@Override
		public HttpHandler.State getFieldValue(String s) {
			if (s == null)
				return null;
			return HttpHandler.State.valueOf(s);
		}

		@Override
		public Object fieldValue2ColumnValue(HttpHandler.State state) {
			return state.value();
		}

		@Override
		public ColumnDbType getColumnDbType() {
			return ColumnDbType.INTEGER;
		}
	}

}
