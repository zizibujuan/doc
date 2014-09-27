package com.zizibujuan.drip.server.doc.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * 把所有需要mustcache处理的html文件缓存起来，在各个servlet中从缓存中读取，
 * 因为在servlet中无法获取到这些文件
 * 
 * @author jinzw
 *
 */
public class CacheMustacheFilter implements Filter{

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		String[] templates = {"/doc/index.html", "/doc/files/blob.html"};
		for(String tmpl : templates){
			InputStream in = config.getServletContext().getResourceAsStream(tmpl);
			Writer sWriter = new StringWriter();
			try {
				IOUtils.copy(in, sWriter, "UTF-8");
				MustacheTemplate.cache(tmpl, sWriter.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
