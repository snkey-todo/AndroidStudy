package com.hxsl.contactsdemo.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.text.TextUtils;

import com.hxsl.contactsdemo.R;
import com.hxsl.contactsdemo.bean.ContactUserInfo;
import com.lidroid.xutils.BitmapUtils;

public class Util {

	public static final BitmapUtils getBitmapUtils(Context context) {
		BitmapUtils mBitmapUtils = new BitmapUtils(context, Constants.IMG_CACHE_PATH);
		mBitmapUtils.configDefaultLoadingImage(R.drawable.default_image);
		mBitmapUtils.configDefaultLoadFailedImage(R.drawable.default_image);
		mBitmapUtils.configDiskCacheEnabled(true);
		return mBitmapUtils;
	}

	public static final BitmapUtils getBitmapUtils(Context context, int id) {
		BitmapUtils mBitmapUtils = new BitmapUtils(context, Constants.IMG_CACHE_PATH);
		mBitmapUtils.configDefaultLoadingImage(id);
		mBitmapUtils.configDefaultLoadFailedImage(id);
		mBitmapUtils.configDiskCacheEnabled(true);
		return mBitmapUtils;
	}

	public static char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);

		return cb.array();
	}

	/**
	 * 删除文件，可以是文件或文件夹
	 * 
	 * @param fileName
	 *            要删除的文件名
	 * @return 删除成功返回true，否则返回false
	 */
	public static boolean delete(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			System.out.println("删除文件失败:" + fileName + "不存在！");
			return false;
		} else {
			if (file.isFile())
				return deleteFile(fileName);
			else
				return deleteDirectory(fileName);
		}
	}

	public static boolean isMobilePhone(String mobiles) {
		String telRegex = "[1][34578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}

	/**
	 * 删除单个文件
	 * 
	 * @param fileName
	 *            要删除的文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				System.out.println("删除单个文件" + fileName + "成功！");
				return true;
			} else {
				System.out.println("删除单个文件" + fileName + "失败！");
				return false;
			}
		} else {
			System.out.println("删除单个文件失败：" + fileName + "不存在！");
			return false;
		}
	}

	/**
	 * 删除目录及目录下的文件
	 * 
	 * @param dir
	 *            要删除的目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String dir) {
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符
		if (!dir.endsWith(File.separator))
			dir = dir + File.separator;
		File dirFile = new File(dir);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
			System.out.println("删除目录失败：" + dir + "不存在！");
			return false;
		}
		boolean flag = true;
		// 删除文件夹中的所有文件包括子目录
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
			// 删除子目录
			else if (files[i].isDirectory()) {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag) {
			System.out.println("删除目录失败！");
			return false;
		}
		// 删除当前目录
		if (dirFile.delete()) {
			System.out.println("删除目录" + dir + "成功！");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * bitmap 转化成 file文件
	 * 
	 * @param context
	 * @param bitmap
	 * @return file
	 */
	public static String bitmapToFile(Context context, Bitmap bitmap) {
		String fileName = String.valueOf(System.currentTimeMillis());
		// create a file to write bitmap data
		File file = new File(context.getCacheDir(), fileName + ".jpg");

		// Convert bitmap to byte array
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0 /* ignored for JPEG */, bos);
		byte[] bitmapdata = bos.toByteArray();

		// write the bytes in file
		FileOutputStream fos;
		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			fos = new FileOutputStream(file);
			fos.write(bitmapdata);
			fos.close();
			if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
			}
			return file.getAbsolutePath();
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (IOException e3) {
			e3.printStackTrace();
		}
		return null;
	}

	// public static ContactUserInfo getUserInfo(String loginid) {
	// List<ContactUserInfo> contactUserInfos =
	// AppLoader.getInstance().getmContactUserInfos();
	// if (contactUserInfos == null || contactUserInfos.size() == 0) {
	// return null;
	// }
	// for (ContactUserInfo info : contactUserInfos) {
	// if (loginid.equalsIgnoreCase(info.getLogin_id())) {
	// return info;
	// }
	// }
	// return null;
	// }

	public static String getTimeLine(String time) {
		if (TextUtils.isEmpty(time)) {

			return "";
		}
		long current = System.currentTimeMillis() / 1000;
		long temp = current - getSecondsFromDate(time);
		String str = "";
		if (temp < 60 * 60) {
			if (temp / 60 == 0)
				str = "刚刚";
			else
				str = temp / 60 + "分钟前";
		} else if (temp < 24 * 60 * 60)
			str = temp / 60 / 60 + "小时前";
		else if (temp < 24 * 2 * 60 * 60)
			str = "昨天";
		else if (temp < 30 * 60 * 60 * 24)
			str = temp / 24 / 60 / 60 + "天前";
		else
			str = time.substring(0, "yyyy-MM-dd".length());
		return str;
	}

	public static long getSecondsFromDate(String expireDate) {
		if (expireDate == null || expireDate.trim().equals(""))
			return 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(expireDate);
			return date.getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static String getSubTime(String time) {
		if (TextUtils.isEmpty(time)) {
			return "";
		}
		if ("yyyy-MM-dd HH:mm".length() < time.length()) {
			return time.substring(0, "yyyy-MM-dd HH:mm".length());
		}
		return time;
	}

	public static long getSeconds(String expireDate) {
		if (expireDate == null || expireDate.trim().equals(""))
			return 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = sdf.parse(expireDate);
			return date.getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static String getCurrenttime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间
	}

	// 通讯录数据去重复
	public static List<ContactUserInfo> removeLoop(List<ContactUserInfo> list) {
		List<ContactUserInfo> temp = new ArrayList<ContactUserInfo>();
		HashSet<String> set = new HashSet<>();
		// for (int i = 0; i <list.size(); i++) {
		// set.add(list.get(i).getId());
		// }
		for (Iterator<ContactUserInfo> iterator = list.iterator(); iterator.hasNext();) {
			ContactUserInfo element = (ContactUserInfo) iterator.next();
			if (set.add(element.getId())) {
				temp.add(element);
			}
		}
		list.clear();
		list.addAll(temp);
		return list;

	}

	public static boolean isImage(String str) {
		if (TextUtils.isEmpty(str))
			return false;
		if (str.endsWith(".jpg") || str.endsWith(".png") || str.endsWith(".JPG") || str.endsWith(".PNG")) {
			return true;
		}
		return false;
	}
}
