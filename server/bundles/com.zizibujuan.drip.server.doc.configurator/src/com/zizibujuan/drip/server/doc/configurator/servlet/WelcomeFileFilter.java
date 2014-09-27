package com.zizibujuan.drip.server.doc.configurator.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.zizibujuan.drip.client.doc.resource.MustacheTemplate;
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
			
			MustacheFactory mf = new DefaultMustacheFactory();
			Mustache mustache = mf.compile(MustacheTemplate.getReader("/doc/index.html"), "doc_index_html");
			
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
	public void init(FilterConfig config) throws ServletException {
		fileService = ServiceHolder.getDefault().getFileService();
	}

}