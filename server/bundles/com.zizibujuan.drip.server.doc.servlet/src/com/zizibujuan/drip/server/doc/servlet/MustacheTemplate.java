package com.zizibujuan.drip.server.doc.servlet;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存mustache模板
 * 
 * @author jinzw
 * @since 0.0.1
 */
public class MustacheTemplate {

	private static Map<String, String> templates = new HashMap<String, String>();

	public static void cache(String name, String templateString) {
		templates.put(name, templateString);
	}
	
	public static String get(String relativPath){
		return templates.get(relativPath);
	}
	
	
}
