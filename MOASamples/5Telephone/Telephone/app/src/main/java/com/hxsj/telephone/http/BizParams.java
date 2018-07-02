
package com.hxsj.telephone.http;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * 把请求数据以键值对的形式封装成对应的json对象
 * **/
public class BizParams {
	
	JSONObject bizParams = new JSONObject();
	
	public BizParams(){
		
	}
	
	public BizParams addParam(String key, Object value){
		try{
			bizParams.put(key, value);
		}catch(JSONException e){
			e.printStackTrace();
		}
		return this;
	}
	
	@Override
	public String toString() {
		return bizParams.toString();
	}
	
}
