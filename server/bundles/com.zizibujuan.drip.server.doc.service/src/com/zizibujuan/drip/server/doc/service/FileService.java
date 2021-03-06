package com.zizibujuan.drip.server.doc.service;

import java.util.List;

import com.zizibujuan.drip.server.doc.model.FileInfo;
import com.zizibujuan.drip.server.util.PageInfo;
import com.zizibujuan.useradmin.server.model.UserInfo;

/**
 * 文件管理服务接口
 * 
 * @author jzw
 * @since 0.0.1
 */
public interface FileService {

	/**
	 * 根据分页信息获取文档列表
	 * 
	 * @param pageInfo 分页信息
	 * @return 文档列表，按照文档创建时间倒排。
	 */
	List<FileInfo> get(PageInfo pageInfo);

	/**
	 * 创建文档，如果用户没有git仓库，则先为用户创建一个git仓库，仓库名称使用用户标识。
	 * 
	 * @param fileInfo 文件信息
	 * @return 成功标志
	 */
	boolean add(FileInfo fileInfo);

	/**
	 * 根据文件标识，获取文件基本信息
	 * 
	 * @param fileId 文件标识
	 * @return 文件基本信息
	 */
	FileInfo get(Long fileId);

	/**
	 * 更新文档内容，如果只修改了标题，则
	 * @param fileInfo 新内容
	 * @return 成功标识
	 */
	boolean update(FileInfo fileInfo);

	/**
	 * 获取笔记的协作者
	 * 
	 * @param fileId 笔记标识
	 * @return 协作者信息列表
	 */
	List<UserInfo> getAuthors(Long fileId);
}
