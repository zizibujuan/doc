package com.zizibujuan.drip.server.doc.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zizibujuan.cm.server.service.ApplicationPropertyService;
import com.zizibujuan.drip.server.doc.dao.FileDao;
import com.zizibujuan.drip.server.doc.model.FileInfo;
import com.zizibujuan.drip.server.doc.service.FileService;
import com.zizibujuan.drip.server.util.PageInfo;
import com.zizibujuan.drip.server.util.constant.GitConstants;

/**
 * 文件管理服务实现类
 * 
 * @author jzw
 * @since 0.0.1
 */
public class FileServiceImpl implements FileService {

	private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
	
	private FileDao fileDao;
	private ApplicationPropertyService applicationPropertyService;
	
	@Override
	public List<FileInfo> get(PageInfo pageInfo) {
		return fileDao.get(pageInfo);
	}
	
	@Override
	public boolean add(FileInfo fileInfo) {
		String docRootPath = applicationPropertyService.getForString(GitConstants.KEY_GIT_ROOT);
		return false;
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

}
