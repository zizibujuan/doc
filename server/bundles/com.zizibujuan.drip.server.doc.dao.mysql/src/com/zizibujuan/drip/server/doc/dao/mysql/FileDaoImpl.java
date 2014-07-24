package com.zizibujuan.drip.server.doc.dao.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.zizibujuan.drip.server.doc.dao.FileDao;
import com.zizibujuan.drip.server.doc.model.FileInfo;
import com.zizibujuan.drip.server.util.PageInfo;
import com.zizibujuan.drip.server.util.dao.AbstractDao;
import com.zizibujuan.drip.server.util.dao.DatabaseUtil;
import com.zizibujuan.drip.server.util.dao.RowMapper;

/**
 * 文档管理数据访问实现类
 * 
 * @author jzw
 * @since 0.0.1
 */
public class FileDaoImpl extends AbstractDao implements FileDao {

	private static final String SQL_LIST_FILE = "SELECT "
			+ "DBID, "
			+ "FILE_ID, "
			+ "DOC_TITLE, "
			+ "CRT_TM, "
			+ "CRT_USER_ID "
			+ "FROM "
			+ "DRIP_DOC_FILE "
			+ "ORDER BY DBID DESC";
	@Override
	public List<FileInfo> get(PageInfo pageInfo) {
		return DatabaseUtil.query(getDataSource(), SQL_LIST_FILE, new RowMapper<FileInfo>() {

			@Override
			public FileInfo mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				FileInfo fileInfo = new FileInfo();
				fileInfo.setId(rs.getLong(1));
				fileInfo.setFileName(rs.getString(2));
				fileInfo.setTitle(rs.getString(3));
				fileInfo.setCreateTime(rs.getTimestamp(4));
				fileInfo.setCreateUserId(rs.getString(5));
				return fileInfo;
			}
			
		}, pageInfo);
	}

}
