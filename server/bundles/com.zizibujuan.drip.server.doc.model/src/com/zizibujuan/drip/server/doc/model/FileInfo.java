package com.zizibujuan.drip.server.doc.model;

import java.util.Date;

/**
 * 文件信息
 * 
 * @author jzw
 * @since 0.0.1
 */
public class FileInfo {
	
	private Long id;
	
	private String path;
	
	private String fileName;
	
	private String title;
	
	private String content;
	
	private String size; // 后面带单位
	
	private String commitMessage;
	
	private Date createTime;
	
	private Long createUserId;

	/**
	 * 获取文件标识
	 * @return 文件标识
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 设置文件标识
	 * @param id 文件标识
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 获取文件相对项目根目录的路径，不包含文件名
	 * @return 文件所在目录
	 */
	public String getPath() {
		return path;
	}

	/**
	 * 设置文件相对项目根目录的路径，包含文件名
	 * @param path 文件完整路径
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * 获取文件相对项目根目录的路径，包含文件名
	 * @return 文件完整路径
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 设置文件名
	 * @param fileName 文件名
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 获取文件内容
	 * @return 文件内容
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 设置文件内容
	 * @param content 文件内容
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 获取文件大小，转换为用户友好的字符串，后面加上kb,m,g等单位
	 * @return
	 */
	public String getSize() {
		return this.size;
	}

	public void setSize(String size) {
		this.size = size;
	}
	
	/**
	 * 设置文件大小
	 * @param size 文件大小
	 */
	public void setLongSize(long size) {
		if(size < 1024){
			this.size = size + "字节";
		}else if(size < 1024 * 1024){
			this.size = size/1024 + "KB";
		}else if(size < 1024 * 1024 * 1024){
			this.size = size/(1024*1024) + "MB";
		}else{
			this.size = size/(1024*1024*1024) +"GB";
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCommitMessage() {
		return commitMessage;
	}

	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	
}
