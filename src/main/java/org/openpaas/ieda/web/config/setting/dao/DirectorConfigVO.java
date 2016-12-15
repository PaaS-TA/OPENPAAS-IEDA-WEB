package org.openpaas.ieda.web.config.setting.dao;

import java.util.Date;

public class DirectorConfigVO {
	
	private Integer iedaDirectorConfigSeq; //레코드키
	private String userId; //관리자 계정
	private String userPassword; //패스워드
	private String directorName; //관리자 이름
	private String directorVersion; //버전
	private String directorUrl; //URL
	private Integer directorPort; //포트번호
	private String directorUuid; //관리자 UUID
	private String directorCpi; //CPI
	private String currentDeployment; 
	private String defaultYn; //기본관리자 여부
	private Date createDate; //생성일
	private Date updateDate; //수정일
	private String createUserId; //생성 사용자
	private String updateUserId;//수정 사용자 
	private boolean connect;//기본 설치관리자 클라이언트 요청 여부
	
	public DirectorConfigVO(){}
	
	public DirectorConfigVO( Date createDate, Date updateDate ){
		this.createDate =  createDate;
		this.updateDate = updateDate;
	}
	
	public Integer getIedaDirectorConfigSeq() {
		return iedaDirectorConfigSeq;
	}

	public void setIedaDirectorConfigSeq(Integer iedaDirectorConfigSeq) {
		this.iedaDirectorConfigSeq = iedaDirectorConfigSeq;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getDirectorName() {
		return directorName;
	}

	public void setDirectorName(String directorName) {
		this.directorName = directorName;
	}

	public String getDirectorVersion() {
		return directorVersion;
	}

	public void setDirectorVersion(String directorVersion) {
		this.directorVersion = directorVersion;
	}

	public String getDirectorUrl() {
		return directorUrl;
	}

	public void setDirectorUrl(String directorUrl) {
		this.directorUrl = directorUrl;
	}

	public Integer getDirectorPort() {
		return directorPort;
	}

	public void setDirectorPort(Integer directorPort) {
		this.directorPort = directorPort;
	}

	public String getDirectorUuid() {
		return directorUuid;
	}

	public void setDirectorUuid(String directorUuid) {
		this.directorUuid = directorUuid;
	}

	public String getDirectorCpi() {
		return directorCpi;
	}

	public void setDirectorCpi(String directorCpi) {
		this.directorCpi = directorCpi;
	}

	public String getCurrentDeployment() {
		return currentDeployment;
	}

	public void setCurrentDeployment(String currentDeployment) {
		this.currentDeployment = currentDeployment;
	}

	public String getDefaultYn() {
		return defaultYn;
	}

	public void setDefaultYn(String defaultYn) {
		this.defaultYn = defaultYn;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return new Date(updateDate.getTime());
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public String getUpdateUserId() {
		return updateUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	public boolean isConnect() {
		return connect;
	}

	public void setConnect(boolean connect) {
		this.connect = connect;
	}
	
	
}
