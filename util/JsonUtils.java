package com.rf.gjframe.base.util;


import java.lang.reflect.Type;

import com.google.gson.Gson;

/**
 * Json工具类，提供了对象与json字符串相互转换的基本方法
 * 如果在开发过程中,JsonUtils工具类满足不了需要,则可以直接调用
 * Gson 来实现相关对象与json字符串的相互转换
 * @author wangjw
 *
 * @param <T>
 */
public class JsonUtils<T> {

	/**
	 * 实现对象转换为json字符串的方法
	 * @param obj 对象
	 * @return
	 */
	public static String  objecToJson(Object obj){
		Gson gson =  new Gson();
		String result = gson.toJson(obj);
		return result;
	}
	
	/**
	 * 从json字符串中转换为一般简单对象类
	 * @param json 对象的json字符串
	 * @param clazz 目标对象的class类
	 * @return
	 */
	public static Object objectFromJson(String json,Class clazz){
		Gson gson = new Gson();
		Object result = gson.fromJson(json, clazz);
		return result;
	}
	
	/**
	 * 实现从json字符串中转换对象
	 * @param json 对象的json字符串
	 * @param type  目标对象的类型，例如json字符串中保存着一个List<Person>数据,
	 * 通过 传递 new TypeToken<List<Person>>(){}.getType() 作为类型参数
	 * 就可以将数据对象转换
	 * @return
	 */
	public static Object objectFromJson(String json,Type type){
		Gson gson = new Gson();
		Object result = gson.fromJson(json, type);
		return result;
	}
}
