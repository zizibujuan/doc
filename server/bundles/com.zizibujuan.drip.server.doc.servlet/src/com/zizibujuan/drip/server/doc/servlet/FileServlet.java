package com.zizibujuan.drip.server.doc.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.IPath;

import com.zizibujuan.drip.server.doc.model.FileInfo;
import com.zizibujuan.drip.server.doc.service.FileService;
import com.zizibujuan.drip.server.util.PageInfo;
import com.zizibujuan.drip.server.util.servlet.BaseServlet;
import com.zizibujuan.drip.server.util.servlet.RequestUtil;
import com.zizibujuan.drip.server.util.servlet.ResponseUtil;
import com.zizibujuan.drip.server.util.servlet.UserSession;
import com.zizibujuan.useradmin.server.model.UserInfo;


// FIXME: 简化操作，不划分目录，不按项目分类，不按用户名分类
/**
 * 文件管理
 * 
 * @author jzw
 * @since 0.0.1
 */
public class FileServlet extends BaseServlet {

	private static final long serialVersionUID = 3134000969079271759L;
	
	private FileService fileService; 

	public FileServlet(){
		fileService = ServiceHolder.getDefault().getFileService();
	}

	/**
	 * 新建文件, 如果当前用户没有git仓库，则先创建git仓库
	 * 
	 * files
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		traceRequest(req);
		IPath path = getPath(req);
		if(path.segmentCount() == 0){
			// 具体逻辑放到service中实现
			Long userId = ((UserInfo)UserSession.getUser(req)).getId();
			FileInfo fileInfo = RequestUtil.fromJsonObject(req, FileInfo.class);
			fileInfo.setCreateUserId(userId);
			fileService.add(fileInfo);
			return;
		}
		
		super.doPost(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		traceRequest(req);
		IPath path = getPath(req);
		if(path.segmentCount() == 0){
			FileInfo fileInfo = RequestUtil.fromJsonObject(req, FileInfo.class);
			Long userId = ((UserInfo)UserSession.getUser(req)).getId();
			fileInfo.setUpdateUserId(userId);
			fileService.update(fileInfo);
			return;
		}
		super.doPut(req, resp);
	}

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		traceRequest(req);
		IPath path = getPath(req);
		if(path.segmentCount() == 0){
			PageInfo pageInfo = getPageInfo(req);
			List<FileInfo> result = fileService.get(pageInfo);
			ResponseUtil.toJSON(req, resp, result);
			return;
		}
		super.doGet(req, resp);
	}


	
}
