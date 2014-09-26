package com.zizibujuan.drip.server.doc.configurator.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.zizibujuan.drip.server.doc.model.FileInfo;
import com.zizibujuan.drip.server.doc.service.FileService;
import com.zizibujuan.drip.server.doc.servlet.ServiceHolder;
import com.zizibujuan.drip.server.util.PageInfo;



/**
 * 指向欢迎页面的过滤器。
 * 当访问首页时，如果用户没有登录，则跳转到公共首页；如果用户已经登录，则跳转到个人首页。
 * 
 * @author jinzw
 * @since 0.0.1
 */
public class WelcomeFileFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(WelcomeFileFilter.class);
	private FileService fileService;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws ServletException, IOException {
		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		final HttpServletResponse httpResponse = (HttpServletResponse)response;
		String requestPath = httpRequest.getServletPath() + (httpRequest.getPathInfo() == null ? "" : httpRequest.getPathInfo()); //$NON-NLS-1$
		
		if(requestPath.equals("/index.html")){
			requestPath = "/";
		}
		// 判断是否访问首页地址
		if (requestPath.equals("/")) { //$NON-NLS-1$
			// TODO: 将html文件缓存起来
			httpResponse.setHeader("Cache-Control", "no-cache"); //$NON-NLS-1$ //$NON-NLS-2$
			httpResponse.setHeader("Cache-Control", "no-store"); //$NON-NLS-1$ //$NON-NLS-2$
			
			InputStream in = httpRequest.getSession().getServletContext().getResourceAsStream("/doc/index.html");
			logger.info("/doc/index.html", in);
			InputStream blobStream = httpRequest.getSession().getServletContext().getResourceAsStream("/doc/files/blob.html");
			logger.info("/doc/files/blob.html", blobStream);
			Set<String> s =httpRequest.getSession().getServletContext().getResourcePaths("/");
			logger.info(s.toString());
			
			Writer sWriter = new StringWriter();
			IOUtils.copy(in, sWriter, "UTF-8");
			StringReader reader = new StringReader(sWriter.toString());
			
			MustacheFactory mf = new DefaultMustacheFactory();
			Mustache mustache = mf.compile(reader, "doc_index_html");
			
			response.setCharacterEncoding("utf-8");
			Writer writer = response.getWriter();
			
			
			String range = httpRequest.getHeader("Range");
			if(range == null){
				range = httpRequest.getHeader("range");
			}
			PageInfo pageInfo = null;
			if(range != null){
				pageInfo = new PageInfo(range);
			}
			List<FileInfo> files = fileService.get(pageInfo);
			Map<String, List<FileInfo>> fileMap = new HashMap<String, List<FileInfo>>();
			fileMap.put("files", files);
			mustache.execute(writer, fileMap);
			writer.flush();			
			return;
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// do nothing
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		fileService = ServiceHolder.getDefault().getFileService();
	}

}