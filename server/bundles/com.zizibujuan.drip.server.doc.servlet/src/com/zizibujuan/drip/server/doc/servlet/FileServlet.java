package com.zizibujuan.drip.server.doc.servlet;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.zizibujuan.cm.server.service.ApplicationPropertyService;
import com.zizibujuan.cm.server.servlets.CMServiceHolder;
import com.zizibujuan.drip.server.doc.model.FileInfo;
import com.zizibujuan.drip.server.doc.model.NewFileForm;
import com.zizibujuan.drip.server.doc.service.FileService;
import com.zizibujuan.drip.server.util.PageInfo;
import com.zizibujuan.drip.server.util.constant.GitConstants;
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
	
	private ApplicationPropertyService applicationPropertyService;
	
	private FileService fileService; 

	public FileServlet(){
		applicationPropertyService = CMServiceHolder.getDefault().getApplicationPropertyService();
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
		if(path.segmentCount() > 2){
			NewFileForm fileForm = RequestUtil.fromJsonObject(req, NewFileForm.class);
			// files/userName/projectName/[filePath]
			String rootPath = applicationPropertyService.getForString(GitConstants.KEY_DOC_REPO_ROOT);
			// 文件信息
			URI fileLocation;
			try {
				fileLocation = new URI(rootPath + path.toString());
			} catch (URISyntaxException e) {
				handleException(resp, "路径语法错误", e);
				return;
			}
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileLocation);
			StringReader reader = new StringReader(fileForm.getFileInfo().getContent());
			
			
			try {
				IOUtils.copy(reader, fileStore.openOutputStream(EFS.NONE, null));
				
				// 获取仓库名称
				String repoPath = path.uptoSegment(2).toString();
				// 获取相对仓库根目录的目录路径
				String relativePath = path.removeFirstSegments(2).toString();
				UserInfo currentUser = (UserInfo) UserSession.getUser(req);
				// 获取git仓库
				Repository repo = FileRepositoryBuilder.create(new File(rootPath + repoPath, Constants.DOT_GIT));
				repo.resolve(Constants.HEAD);
				Git git = new Git(repo);
				// 往git仓库中提交新建的文件
				git.add().addFilepattern(relativePath + fileStore.fetchInfo().getName()).call();
				// loginName和email在这里是必须要输入的
				git.commit().setAuthor(currentUser.getLoginName(), currentUser.getEmail())
					.setMessage(fileForm.getCommitInfo().getSummary())// 加一个空行，追加详细信息？ 暂时不支持录入扩展的提交信息
					.call();
				
				// 保存成功之后跳转到项目首页
				ResponseUtil.toJSON(req, resp);
			} catch (CoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoHeadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoMessageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnmergedPathsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConcurrentRefUpdateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WrongRepositoryStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
