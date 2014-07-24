package com.zizibujuan.drip.server.doc.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zizibujuan.drip.server.doc.dao.FileDao;
import com.zizibujuan.drip.server.doc.model.FileInfo;
import com.zizibujuan.drip.server.doc.service.FileService;
import com.zizibujuan.drip.server.util.PageInfo;

/**
 * 文件管理服务实现类
 * 
 * @author jzw
 * @since 0.0.1
 */
public class FileServiceImpl implements FileService {

	private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
	
	private FileDao fileDao;
	
	@Override
	public List<FileInfo> get(PageInfo pageInfo) {
		return fileDao.get(pageInfo);
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
