package com.zizibujuan.drip.server.doc.dao.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.zizibujuan.drip.server.doc.dao.FileDao;
import com.zizibujuan.drip.server.doc.model.FileInfo;
import com.zizibujuan.drip.server.util.PageInfo;
import com.zizibujuan.drip.server.util.dao.AbstractDao;
import com.zizibujuan.drip.server.util.dao.DatabaseUtil;
import com.zizibujuan.drip.server.util.dao.PreparedStatementSetter;
import com.zizibujuan.drip.server.util.dao.RowMapper;

/**
 * 文档管理数据访问实现类
 * 
 * @author jzw
 * @since 0.0.1
 */
public class FileDaoImpl extends AbstractDao implements FileDao {

	private static final String SQL_LIST_FILE = "SELECT "
			+ "df.DBID, "
			+ "df.DOC_TITLE, "
			+ "df.CRT_TM, "
			+ "df.CRT_USER_ID, "
			+ "ui.LOGIN_NAME "
			+ "FROM "
			+ "DRIP_DOC_FILE df, "
			+ "DRIP_USER_INFO ui "
			+ "WHERE "
			+ "df.CRT_USER_ID=ui.DBID "
			+ "ORDER BY df.DBID DESC";
	@Override
	public List<FileInfo> get(PageInfo pageInfo) {
		return DatabaseUtil.query(getDataSource(), SQL_LIST_FILE, new RowMapper<FileInfo>() {

			@Override
			public FileInfo mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				FileInfo fileInfo = new FileInfo();
				fileInfo.setId(rs.getLong(1));
				fileInfo.setTitle(rs.getString(2));
				fileInfo.setCreateTime(rs.getTimestamp(3));
				fileInfo.setCreateUserId(rs.getLong(4));
				fileInfo.setCreateUserName(rs.getString(5));
				return fileInfo;
			}
			
		}, pageInfo);
	}
	
	private static final String SQL_GET_FIRST_MATCH_REPO = "SELECT "
			+ "DBID "
			+ "FROM "
			+ "DRIP_USER_GIT_REPO "
			+ "WHERE "
			+ "CREATE_USER_ID=? AND "
			+ "REPO_NAME=? "
			+ "LIMIT 1";
	@Override
	public boolean contains(Long userId, String gitRepoName) {
		Long result = DatabaseUtil.queryForLong(getDataSource(), SQL_GET_FIRST_MATCH_REPO, userId, gitRepoName);
		return result != null;
	}
	
	private static final String SQL_INSERT_REPO = "INSERT INTO "
			+ "DRIP_USER_GIT_REPO "
			+ "(REPO_NAME, "
			+ "CRT_TM, "
			+ "CRT_USER_ID) "
			+ "VALUES "
			+ "(?, now(), ?)";
	@Override
	public void addGitRepoInfo(Long userId, String gitRepoName) {
		DatabaseUtil.insert(getDataSource(), SQL_INSERT_REPO, userId, gitRepoName);
	}
	
	private static final String SQL_INSERT_FILE_INFO = "INSERT INTO "
			+ "DRIP_DOC_FILE "
			+ "(DOC_TITLE, "
			+ "CRT_TM, "
			+ "CRT_USER_ID) "
			+ "VALUES "
			+ "(?, now(), ?)";
	@Override
	public Long add(final FileInfo fileInfo) {
		return DatabaseUtil.insert(getDataSource(), SQL_INSERT_FILE_INFO, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, fileInfo.getTitle());
				ps.setLong(2, fileInfo.getCreateUserId());
			}
		});
	}

}
