
package com.example.taskdemo.http;

import org.json.JSONException;
import org.json.JSONObject;
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
