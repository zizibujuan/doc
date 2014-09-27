package com.zizibujuan.drip.client.doc.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zizibujuan.drip.client.doc.Activator;

/**
 * 缓存mustache模板
 * 
 * @author jinzw
 * @since 0.0.1
 */
public class MustacheTemplate {

	private static final Logger logger = LoggerFactory.getLogger(MustacheTemplate.class);
	private static Map<String, String> templates = new HashMap<String, String>();

	public static void cache(String name, String templateString) {
		templates.put(name, templateString);
	}
	
	public static String get(String relativePath) throws IOException{
		if(templates.containsKey(relativePath)){
			return templates.get(relativePath);
		}
		String result = getHtml(relativePath);
		templates.put(relativePath, result);
		return result;
	}
	
	public static Reader getReader(String relativePath) throws IOException{
		return new StringReader(get(relativePath));
	}
	
	private static String getHtml(String relativePath) throws IOException{
		Bundle bundle = Activator.getContext().getBundle();
		URL url = bundle.getResource("/web/" + relativePath);
		logger.info("url is:" + url);
		InputStream in = url.openStream();
		Writer writer = new StringWriter();
		IOUtils.copy(in, writer, "utf-8");
		return writer.toString();
	}
	
}
