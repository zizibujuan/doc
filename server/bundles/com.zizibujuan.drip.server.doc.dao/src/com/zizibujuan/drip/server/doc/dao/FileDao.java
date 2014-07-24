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
	
}
