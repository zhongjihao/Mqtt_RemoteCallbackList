package com.openplatform.adas.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.text.TextUtils;

public class JsonUtil {
	
	
	public static String getString(JSONObject object,String key) throws JSONException{
		if(object!=null && object.has(key)){
			return object.getString(key);
		}
		return "";
	}
	
	public static int getInt(JSONObject object,String key) throws JSONException{
		if(object!=null && object.has(key)){
			return object.getInt(key);
		}
		return -1;
	}
	
	public static double getDouble(JSONObject object,String key) throws JSONException{
		if(object!=null && object.has(key)){
			return object.getDouble(key);
		}
		return 0.0;
	}
	
	public static long getLong(JSONObject object,String key) throws JSONException{
		if(object!=null && object.has(key)){
			return object.getLong(key);
		}
		return -1;
	}
	
	public static boolean getBoolean(JSONObject object,String key) throws JSONException{
		if(object!=null && object.has(key)){
			return object.getBoolean(key);
		}
		return false;
	}
	
	public static JSONArray getJSONArray(JSONObject object,String key) throws JSONException{
		if(object!=null && object.has(key)){
			return object.getJSONArray(key);
		}
		return null;
	}
	
	public static JSONObject getJSONObject(JSONObject object,String key) throws JSONException{
		if(object!=null && object.has(key)){
			return object.getJSONObject(key);
		}
		return null;
	}
	
    /** 
     * 把相对应节点的内容封装为对象 
     * @param jsonString json字符串 
     * @param beanClazz  要封装成的目标对象 
     * @return 目标对象 
     */  
    public static <T> T parserJsonToBean(String jsonString,Class<T> clazzBean){  
        if(TextUtils.isEmpty(jsonString)){  
            throw new RuntimeException("json字符串为空");  
        }  
        JsonElement jsonElement = new JsonParser().parse(jsonString);  
        if(jsonElement.isJsonNull()){  
            throw new RuntimeException("json字符串为空");  
        }  
        if(!jsonElement.isJsonObject()){  
            throw new RuntimeException("json不是一个对象");  
        }  
        return new Gson().fromJson(jsonElement, clazzBean);  
    }
	
}
