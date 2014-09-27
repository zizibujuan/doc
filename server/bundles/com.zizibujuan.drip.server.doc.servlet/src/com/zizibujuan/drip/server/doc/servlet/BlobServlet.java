package com.zizibujuan.drip.server.doc.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.zizibujuan.cm.server.service.ApplicationPropertyService;
import com.zizibujuan.cm.server.servlets.CMServiceHolder;
import com.zizibujuan.drip.server.doc.model.FileInfo;
import com.zizibujuan.drip.server.doc.service.FileService;
import com.zizibujuan.drip.server.util.constant.GitConstants;
import com.zizibujuan.drip.server.util.servlet.BaseServlet;

/**
 * 查看文件内容
 * 
 * @author jzw
 * @since 0.0.2
 */
public class BlobServlet extends BaseServlet {
	private static final long serialVersionUID = -2241539925566713677L;

	private static final Logger logger = LoggerFactory.getLogger(BlobServlet.class);
	private static final String DEFAULT_DOC_GIT_NAME = "default";
	private ApplicationPropertyService applicationPropertyService;
	private FileService fileService;
	
	public BlobServlet(){
		applicationPropertyService = CMServiceHolder.getDefault().getApplicationPropertyService();
		fileService = ServiceHolder.getDefault().getFileService();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		traceRequest(req);
		IPath path = getPath(req);
		// blob/loginName/fileName
		if(path.segmentCount() == 2){
			String sFileId = path.segment(1);
			if(sFileId.endsWith(".md")){
				sFileId = sFileId.substring(0, sFileId.length()-3);
			}
			

			
			
			Long fileId = Long.valueOf(sFileId);
			FileInfo fileInfo = fileService.get(fileId);
			
			String docRootPath = applicationPropertyService.getForString(GitConstants.KEY_DOC_REPO_ROOT);
			String gitRepoPath = docRootPath + path.segment(0) + "/" + DEFAULT_DOC_GIT_NAME;
			String realFilePath = gitRepoPath + "/" + path.segment(1);
			StringWriter fileWriter = new StringWriter();
			File file = new File(realFilePath);
			InputStream input = new FileInputStream(file);
			IOUtils.copy(input, fileWriter);

			//fileInfo.setContent(writer.toString());
			//fileInfo.setLongSize(file.length());

			Reader reader = new StringReader(MustacheTemplate.get("/doc/files/blob.html"));
			MustacheFactory mf = new DefaultMustacheFactory();
			Mustache mustache = mf.compile(reader, "doc_files_blob_html");
			
			resp.setCharacterEncoding("utf-8");
			Writer writer = resp.getWriter();
			
			
			Map<String, String> blob = new HashMap<String, String>();
			blob.put("content", fileWriter.toString());
			blob.put("title", fileInfo.getTitle());
			mustache.execute(writer, blob);
			writer.flush();		
			
			// TODO: 获取更多详细信息
			//ResponseUtil.toJSON(req, resp, fileInfo);
			// 提交信息
			
			// 所有参与编写文件的用户
			return;
		}
		super.doGet(req, resp);
	}

}
