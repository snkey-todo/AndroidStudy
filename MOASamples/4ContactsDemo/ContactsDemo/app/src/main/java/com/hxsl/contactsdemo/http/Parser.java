package com.hxsl.contactsdemo.http;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hxsl.contactsdemo.log.Logger;
import com.lidroid.xutils.http.ResponseInfo;

public class Parser {

	private static final String SEC_KEY = UrlUtils.getHttpSecret();

	/*******************************************************
	 * 说明：根据返回参数的字符串转换成RespEntity对象
	 ********************************************************/
	@SuppressWarnings("unchecked")
	public static <E> RespEntity<E> toRespEntity(String str, Class<E> cls) {
		Logger.getLogger().d(cls.getSimpleName());
		Gson gson = new Gson();
		RespEntity<E> entity = gson.fromJson(str, new TypeToken<RespEntity<E>>() {
		}.getType());
		String dataJsonStr = null;
		if (entity.getIs_encrypt() == 1) {
			String encryptStr = entity.getData().toString();
			// 数据有经过加密，需要解密处理
			// 解密操作
			dataJsonStr = AESUtils.decrypt(encryptStr, SEC_KEY);
		} else {
			dataJsonStr = gson.toJson(entity.getData());
		}
		E dataEntity = gson.fromJson(dataJsonStr, cls);
		entity.setData(dataEntity);
		return entity;
	}

	/*******************************************************
	 * 说明：取得返回的实体类
	 * 
	 * @return
	 ********************************************************/
	public static <E> RespEntity<E> toRespEntity(ResponseInfo<String> responseInfo, Class<E> cls) {
		String result = (String) responseInfo.result;
		return toRespEntity(result, cls);
	}

	/*******************************************************
	 * 说明：取得对应的data实体类
	 * 
	 * @param cls
	 * @return
	 ********************************************************/
	@SuppressWarnings("unchecked")
	public static <E> E toDataEntity(ResponseInfo<String> responseInfo, Class<E> cls) {
		RespEntity<E> respEntity = toRespEntity(responseInfo, cls);
		return respEntity.getData();
	}

	/*******************************************************
	 * 说明：取得Data对应的List集合类
	 * 
	 * @param cls
	 *            List中的实体类对象
	 * @return
	 ********************************************************/
	@SuppressWarnings("unchecked")
	public static <E> List<E> toListEntity(ResponseInfo<String> responseInfo, Class<E> cls) {
		RespEntity<?> respEntity = toRespEntity(responseInfo, List.class);

		Gson gson = new Gson();
		String dataStr = gson.toJson(respEntity.getData());
		Type type = new TypeToken<ArrayList<JsonObject>>() {
		}.getType();
		List<JsonObject> dataJsonList = gson.fromJson(dataStr, type);
		List<E> list = new ArrayList<E>();
		for (JsonObject jsonObj : dataJsonList) {
			list.add(gson.fromJson(jsonObj, cls));
		}

		return list;
	}

	/*******************************************************
	 * 说明：判断返回的数据是否是正确数据(code = 0)，或者是其他错误数据
	 * 
	 * @return
	 ********************************************************/
	public static boolean isSuccess(ResponseInfo<String> responseInfo) {
		JsonObject jsonObject = new Gson().fromJson(responseInfo.result, JsonObject.class);
		return jsonObject.get("code").getAsInt() == 0 ? true : false;
	}

	/*******************************************************
	 * 说明：当返回数据 onSuccess 的时候获取错误信息
	 * 
	 * @return
	 ********************************************************/
	public static String getMsg(String str) {
		String msg = null;
		JSONObject object;
		try {
			object = new JSONObject(str);
			msg = object.getString("msg");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return msg;
	}

}
