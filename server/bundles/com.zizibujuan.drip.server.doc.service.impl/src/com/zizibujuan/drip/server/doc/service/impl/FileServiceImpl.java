package com.zizibujuan.drip.server.doc.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zizibujuan.cm.server.service.ApplicationPropertyService;
import com.zizibujuan.drip.server.doc.dao.FileDao;
import com.zizibujuan.drip.server.doc.model.FileInfo;
import com.zizibujuan.drip.server.doc.service.FileService;
import com.zizibujuan.drip.server.util.PageInfo;
import com.zizibujuan.drip.server.util.constant.GitConstants;
import com.zizibujuan.server.git.GitUtils;
import com.zizibujuan.useradmin.server.model.UserInfo;
import com.zizibujuan.useradmin.server.service.UserService;

/**
 * 文件管理服务实现类
 * 
 * @author jzw
 * @since 0.0.1
 */
public class FileServiceImpl implements FileService {

	private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
	private static final String DEFAULT_DOC_GIT_NAME = "default";
	
	private FileDao fileDao;
	private UserService userService;
	private ApplicationPropertyService applicationPropertyService;
	
	@Override
	public List<FileInfo> get(PageInfo pageInfo) {
		return fileDao.get(pageInfo);
	}
	

	@Override
	public FileInfo get(Long fileId) {
		return fileDao.get(fileId);
	}

	
	@Override
	public boolean add(FileInfo fileInfo) {
		Long userId = fileInfo.getCreateUserId();
		
		UserInfo userInfo = userService.getById(userId);
		if(!existDefaultGitRepo(userId)){
			createDefaultGitRepo(userInfo);
			registerDefaultGitRepo(userId);
		}
		
		// 文件内容
		Long fileId = fileDao.add(fileInfo);
		if(fileId != null){
			fileInfo.setId(fileId);
			addFileToDefaultGitRepo(userInfo, fileInfo);
			return true;
		}
		
		return false;
	}
	
	private boolean existDefaultGitRepo(Long userId){
		return fileDao.contains(userId, DEFAULT_DOC_GIT_NAME);
	}
	
	private void registerDefaultGitRepo(Long userId){
		fileDao.addGitRepoInfo(userId, DEFAULT_DOC_GIT_NAME);
	}
	
	//仓库路径  root/userName/default
	private void createDefaultGitRepo(UserInfo userInfo){
		String docRootPath = applicationPropertyService.getForString(GitConstants.KEY_DOC_REPO_ROOT);
		String gitRepoPath = docRootPath + userInfo.getLoginName() + "/" + DEFAULT_DOC_GIT_NAME;
		logger.info("git repo path: " + gitRepoPath);
		GitUtils.init(gitRepoPath, userInfo.getLoginName(), userInfo.getEmail());
	}
	
	private void addFileToDefaultGitRepo(UserInfo userInfo, FileInfo fileInfo){
		String docRootPath = applicationPropertyService.getForString(GitConstants.KEY_DOC_REPO_ROOT);
		String relativePath = userInfo.getLoginName() + "/" + DEFAULT_DOC_GIT_NAME;
		GitUtils.commit(docRootPath, 
						relativePath, 
						fileInfo.getFileName(), 
						fileInfo.getContent(), 
						userInfo.getLoginName(), 
						userInfo.getEmail(), 
						fileInfo.getCommitMessage());
	}
	
	private void addFileToDefaultGitRepo(UserInfo userInfo, String firstCommiterLoginName, FileInfo fileInfo){
		String docRootPath = applicationPropertyService.getForString(GitConstants.KEY_DOC_REPO_ROOT);
		String relativePath = firstCommiterLoginName + "/" + DEFAULT_DOC_GIT_NAME;
		GitUtils.commit(docRootPath, 
						relativePath, 
						fileInfo.getFileName(), 
						fileInfo.getContent(), 
						userInfo.getLoginName(), 
						userInfo.getEmail(), 
						fileInfo.getCommitMessage());
	}
	
	@Override
	public boolean update(FileInfo fileInfo) {
		boolean result = false;
		// 如果文件内容发生了变化，则保存；如果没有变化，则不保存。
		UserInfo userInfo = userService.getById(fileInfo.getUpdateUserId()); // 当前编辑的用户信息
		
		addFileToDefaultGitRepo(userInfo, fileInfo.getCreateUserName(), fileInfo);
		// 更新文件标题等基本信息
		result = fileDao.update(fileInfo.getId(), fileInfo);
		return result;
	}
	
	@Override
	public List<UserInfo> getAuthors(Long fileId) {
		List<Long> userIds = fileDao.getAuthors(fileId);
		List<UserInfo> users = new ArrayList<UserInfo>();
		for(Long id : userIds){
			users.add(userService.getById(id));
		}
		return users;
	}
	
	public void setFileDao(FileDao fileDao) {
		logger.info("注入fileDao");
		this.fileDao = fileDao;
	}

	public void unsetFileDao(FileDao fileDao) {
		if (this.fileDao == fileDao) {
			logger.info("注销fileDao");
			this.fileDao = null;
		}
	}

	public void setUserService(UserService userService) {
		logger.info("注入userService");
		this.userService = userService;
	}

	public void unsetUserService(UserService userService) {
		if (this.userService == userService) {
			logger.info("注销userService");
			this.userService = null;
		}
	}
	
	public void setApplicationPropertyService(ApplicationPropertyService applicationPropertyService) {
		logger.info("注入applicationPropertyService");
		this.applicationPropertyService = applicationPropertyService;
	}

	public void unsetApplicationPropertyService(ApplicationPropertyService applicationPropertyService) {
		if (this.applicationPropertyService == applicationPropertyService) {
			logger.info("注销applicationPropertyService");
			this.applicationPropertyService = null;
		}
	}


}
