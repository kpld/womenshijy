package com.hotcast.vr.tools;

import com.google.gson.Gson;

public class GsonUtil {
    public static <T> T Json2Bean(String result, Class<T> clz) {
        Gson gson = new Gson();
        return gson.fromJson(result, clz);
    }

//	public static String Bean2Json(Object obj){
//		Gson gson = new Gson();
//		return gson.toJson(obj);
//	}
}
