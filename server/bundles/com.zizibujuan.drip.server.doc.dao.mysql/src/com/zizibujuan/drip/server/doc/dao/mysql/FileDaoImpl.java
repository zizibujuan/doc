package com.zizibujuan.drip.server.doc.dao.mysql;

import java.sql.Connection;
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
import com.zizibujuan.drip.server.util.dao.exception.DataAccessException;

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
	
	private static final String SQL_GET_FILE = "SELECT "
			+ "df.DBID, "
			+ "df.DOC_TITLE, "
			+ "df.CRT_TM, "
			+ "df.CRT_USER_ID, "
			+ "ui.LOGIN_NAME "
			+ "FROM "
			+ "DRIP_DOC_FILE df,"
			+ "DRIP_USER_INFO ui "
			+ "WHERE "
			+ "df.CRT_USER_ID=ui.DBID AND "
			+ "df.DBID=?";
	@Override
	public FileInfo get(Long fileId) {
		return DatabaseUtil.queryForObject(getDataSource(), SQL_GET_FILE, new RowMapper<FileInfo>() {

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
		}, fileId);
	}
	
	private static final String SQL_GET_FIRST_MATCH_REPO = "SELECT "
			+ "DBID "
			+ "FROM "
			+ "DRIP_USER_GIT_REPO "
			+ "WHERE "
			+ "CRT_USER_ID=? AND "
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
		DatabaseUtil.insert(getDataSource(), SQL_INSERT_REPO, gitRepoName, userId);
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
		Long id = null;
		Connection con = null;
		try{
			con = getDataSource().getConnection();
			con.setAutoCommit(false);
			id = DatabaseUtil.insert(con, SQL_INSERT_FILE_INFO, new PreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, fileInfo.getTitle());
					ps.setLong(2, fileInfo.getCreateUserId());
				}
			});
			final Long finalId = id;
			DatabaseUtil.insert(con, SQL_INSERT_UPDATE_FILE_LOG, new PreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setLong(1, finalId);
					ps.setLong(2, fileInfo.getCreateUserId());
				}
			} );
			con.commit();
		}catch(Exception e){
			DatabaseUtil.safeRollback(con);
			throw new DataAccessException(e);
		}finally{
			DatabaseUtil.closeConnection(con);
		}
		return id;
	}
	
	private static final String SQL_UPDATE_FILE_TITLE = "UPDATE "
			+ "DRIP_DOC_FILE "
			+ "SET DOC_TITLE=? "
			+ "WHERE DBID=?";

	private static final String SQL_INSERT_UPDATE_FILE_LOG = "INSERT INTO "
			+ "DRIP_DOC_FILE_UPDATE_LOG "
			+ "(DOC_FILE_ID, "
			+ "UPT_TM, "
			+ "UPT_USER_ID) "
			+ "VALUES "
			+ "(?, now(), ?)";
	@Override
	public boolean update(final Long id, final FileInfo fileInfo) {
		boolean result = false;
		Connection con = null;
		try{
			con = getDataSource().getConnection();
			con.setAutoCommit(false);
			DatabaseUtil.update(con, SQL_UPDATE_FILE_TITLE, new PreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, fileInfo.getTitle());
					ps.setLong(2, id);
				}
			});
			DatabaseUtil.insert(con, SQL_INSERT_UPDATE_FILE_LOG, new PreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setLong(1, id);
					ps.setLong(2, fileInfo.getUpdateUserId());
				}
			} );
			con.commit();
			result = true;
		}catch(Exception e){
			DatabaseUtil.safeRollback(con);
			throw new DataAccessException(e);
		}finally{
			DatabaseUtil.closeConnection(con);
		}
		return result;
	}
	
	private static final String SQL_LIST_AUTHOR = "select distinct UPT_USER_ID from DRIP_DOC_FILE_UPDATE_LOG where DOC_FILE_ID=? order by UPT_TM";
	@Override
	public List<Long> getAuthors(final Long fileId) {
		
		return DatabaseUtil.query(getDataSource(), SQL_LIST_AUTHOR, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, fileId);
			}
		}, new RowMapper<Long>() {

			@Override
			public Long mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				Long userId = rs.getLong(1);
				return userId;
			}
		});
	}
	

}
