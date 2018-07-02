package com.hxsj.telephone.http;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.hxsj.telephone.log.Logger;

import android.util.Base64;
import android.util.Log;


/**
 * 请求参数加密，解密封装
 */
public class AESUtils {

	/**
	 * 对数据进行加密处理
	 * @param sSrc 加密的数据
	 * @param sKey 秘钥
	 * **/
	public static String encrypt(String sSrc, String sKey) {
		if (sKey == null) {
			Logger.getLogger().d("Key为空null");
			return null;
		}
		// 判断Key是否为16位
		if (sKey.length() != 16) {
			Logger.getLogger().d("Key长度不是16位");
			return null;
		}
		try {
			byte[] raw = sKey.getBytes("utf-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// "算法/模式/补码方式"
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
			return Base64.encodeToString(encrypted, Base64.DEFAULT);// 此处使用BASE64做转码功能，同时能起到2次加密的作用。
		} catch (Exception e) {
			Log.e("AESUtils", "加密失败", e);
			return "";
		}
	}

	/**
	 * 加密字符串解密处理
	 * @param sSrc 解密的源数据
	 * @param sKey 解密的秘钥
	 * **/
	public static String decrypt(String sSrc, String sKey) {
		try {
			// 判断Key是否正确
			if (sKey == null) {
				Logger.getLogger().d("Key为空null");
				return null;
			}
			// 判断Key是否为16位
			if (sKey.length() != 16) {
				Logger.getLogger().d("Key长度不是16位");
				return null;
			}
			byte[] raw = sKey.getBytes("utf-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] encrypted1 = Base64.decode(sSrc, Base64.DEFAULT);// 先用base64解密
			try {
				byte[] original = cipher.doFinal(encrypted1);
				String originalString = new String(original, "utf-8");
				return originalString;
			} catch (Exception e) {
				Logger.getLogger().d(e.toString());
				return null;
			}
		} catch (Exception ex) {
			Logger.getLogger().d(ex.toString());
			return null;
		}
	}

	public static void test() {
		String secKey = "1234567891234567";
		String str = "WQNMLGB = 五千年磨了根棒 = 我去年买了个表 = wqnmlgb<>\\_";
		String encStr = AESUtils.encrypt(str, secKey);
		Logger.getLogger().d("Encrypt:" + encStr);
		Logger.getLogger().d("Decrypt:" + AESUtils.decrypt("sAcflDhKfcOx27JWK3pAWhJ8sqs/d2qv2XwUT3WD1rn3ZpLxmuAZCRxe1/Ch2cVQPnqxQ/AGJSglrL3LCmgmsw==", secKey));
	}

}
