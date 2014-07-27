package com.zizibujuan.drip.server.doc.dao;

import java.util.List;

import com.zizibujuan.drip.server.doc.model.FileInfo;
import com.zizibujuan.drip.server.util.PageInfo;

/**
 * 文档管理数据访问接口
 * 
 * @author jzw
 * @since 0.0.1
 */
public interface FileDao {

	/**
	 * 根据分页信息获取文档列表
	 * 
	 * @param pageInfo 分页信息
	 * @return 文档列表，按照文档创建时间倒排。
	 */
	List<FileInfo> get(PageInfo pageInfo);

	/**
	 * 判断用户是否有指定的git仓库
	 * 
	 * @param userId 用户标识
	 * @param gitRepoName 仓库名称
	 * @return 如果存在则返回true;否则返回false
	 */
	boolean contains(Long userId, String gitRepoName);

	/**
	 * 添加git仓库信息
	 * 
	 * @param userId 用户标识
	 * @param gitRepoName 仓库名称
	 */
	void addGitRepoInfo(Long userId, String gitRepoName);

	/**
	 * 登记新增的文档信息
	 * 
	 * @param fileInfo 文档信息
	 * @return 如果添加成功，则返回文档标识；否则返回null。
	 */
	Long add(FileInfo fileInfo);
	
}
