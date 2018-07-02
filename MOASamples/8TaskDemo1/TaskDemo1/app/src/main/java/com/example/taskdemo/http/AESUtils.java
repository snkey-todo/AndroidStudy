package com.example.taskdemo.http;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.example.taskdemo.log.Logger;

import android.util.Base64;
import android.util.Log;


/**

 */
public class AESUtils {

	public static String encrypt(String sSrc, String sKey) {
		if (sKey == null) {
			Logger.getLogger().d("Key涓虹┖null");
			return null;
		}
		// 鍒ゆ柇Key鏄惁涓?16浣?
		if (sKey.length() != 16) {
			Logger.getLogger().d("Key闀垮害涓嶆槸16浣?");
			return null;
		}
		try {
			byte[] raw = sKey.getBytes("utf-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// "绠楁硶/妯″紡/琛ョ爜鏂瑰紡"
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

			return Base64.encodeToString(encrypted, Base64.DEFAULT);// 姝ゅ浣跨敤BASE64鍋氳浆鐮佸姛鑳斤紝鍚屾椂鑳借捣鍒?2娆″姞瀵嗙殑浣滅敤銆?
		} catch (Exception e) {
			Log.e("AESUtils", "鍔犲瘑澶辫触", e);
			return "";
		}
	}

	public static String decrypt(String sSrc, String sKey) {
		try {
			// 鍒ゆ柇Key鏄惁姝ｇ‘
			if (sKey == null) {
				Logger.getLogger().d("Key涓虹┖null");
				return null;
			}
			// 鍒ゆ柇Key鏄惁涓?16浣?
			if (sKey.length() != 16) {
				Logger.getLogger().d("Key闀垮害涓嶆槸16浣?");
				return null;
			}
			byte[] raw = sKey.getBytes("utf-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] encrypted1 = Base64.decode(sSrc, Base64.DEFAULT);// 鍏堢敤base64瑙ｅ瘑
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
		String str = "WQNMLGB = 浜斿崈骞寸（浜嗘牴妫? = 鎴戝幓骞翠拱浜嗕釜琛? = wqnmlgb<>\\_";
		String encStr = AESUtils.encrypt(str, secKey);
		Logger.getLogger().d("Encrypt:" + encStr);
		Logger.getLogger().d("Decrypt:" + AESUtils.decrypt("sAcflDhKfcOx27JWK3pAWhJ8sqs/d2qv2XwUT3WD1rn3ZpLxmuAZCRxe1/Ch2cVQPnqxQ/AGJSglrL3LCmgmsw==", secKey));
	}

}
