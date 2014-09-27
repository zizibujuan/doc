package com.zizibujuan.drip.server.doc.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IPath;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.zizibujuan.cm.server.service.ApplicationPropertyService;
import com.zizibujuan.cm.server.servlets.CMServiceHolder;
import com.zizibujuan.drip.client.doc.resource.MustacheTemplate;
import com.zizibujuan.drip.server.doc.model.FileInfo;
import com.zizibujuan.drip.server.doc.service.FileService;
import com.zizibujuan.drip.server.util.PageInfo;
import com.zizibujuan.drip.server.util.constant.GitConstants;
import com.zizibujuan.drip.server.util.json.JsonUtil;
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
	private ApplicationPropertyService applicationPropertyService;
	private static final String DEFAULT_DOC_GIT_NAME = "default";
	
	public FileServlet(){
		fileService = ServiceHolder.getDefault().getFileService();
		applicationPropertyService = CMServiceHolder.getDefault().getApplicationPropertyService();
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
		}else if(path.segmentCount() == 3){
			if(path.segment(0).equals("edit")){
				String loginName = path.segment(1);
				String fileName = path.segment(2);
				String sFileId = fileName;
				if(sFileId.endsWith(".md")){
					sFileId = sFileId.substring(0, sFileId.length()-3);
				}
				
				Long fileId = Long.valueOf(sFileId);
				FileInfo fileInfo = fileService.get(fileId);
				
				String docRootPath = applicationPropertyService.getForString(GitConstants.KEY_DOC_REPO_ROOT);
				String gitRepoPath = docRootPath + loginName + "/" + DEFAULT_DOC_GIT_NAME;
				String realFilePath = gitRepoPath + "/" + fileName;
				StringWriter fileWriter = new StringWriter();
				File file = new File(realFilePath);
				InputStream input = new FileInputStream(file);
				IOUtils.copy(input, fileWriter);

				fileInfo.setContent(fileWriter.toString());
				fileInfo.setLongSize(file.length());

				MustacheFactory mf = new DefaultMustacheFactory();
				Mustache mustache = mf.compile(MustacheTemplate.getReader("/doc/files/edit.html"), "doc_files_edit_html");
				
				resp.setCharacterEncoding("utf-8");
				Writer writer = resp.getWriter();
				
				
				Map<String, String> blob = new HashMap<String, String>();
				blob.put("content", fileWriter.toString());
				blob.put("title", fileInfo.getTitle());
				blob.put("fileInfoJson", JsonUtil.toJson(fileInfo));
				mustache.execute(writer, blob);
				writer.flush();	
				return;
			}
		}
		super.doGet(req, resp);
	}


	
}
