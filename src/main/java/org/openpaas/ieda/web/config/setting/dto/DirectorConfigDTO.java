package org.openpaas.ieda.web.config.setting.dto;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class DirectorConfigDTO {
	
	public static class Create {
		
		Integer iedaDirectorConfigSeq;
		@NotBlank
		@Size(min=4)
		private String  userId; //계정
		@NotBlank
		@Size(min=4)
		private String  userPassword; //비밀번호
		@NotBlank
		private String  directorUrl; //url
		@NotNull
		private Integer directorPort; //포트번호
		
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


	}
	
	
	public static class Update {
		
		private Integer iedaDirectorConfigSeq; //레코드키
		@NotBlank
		@Size(min=4)
		private String  userId; //계정
		@NotBlank
		@Size(min=4)
		private String  userPassword; //패스워드
		private Date updateDate;
		
		public Update(){}
		public Update( Date updateDate ){
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

		public Date getUpdateDate() {
			return new Date(updateDate.getTime());
		}

		public void setUpdateDate(Date updateDate) {
			this.updateDate = new Date(updateDate.getTime());
		}
	}

	
	public static class Response {
		private int recid; //recid
		
		private Integer iedaDirectorConfigSeq; //레코드키
		
		private String  userId; //계정
		
		private String  directorName; //관리자 이름
		
		private String  directorVersion; //버전
		
		private String  directorUrl; //url
		
		private Integer directorPort; //포트번호
		
		private String  directorUUID; //uuid
		
		private String  directorCpi; //cpi
		
		private String  currentDeployment;
		
		@NotBlank
		private String  defaultYn; //기본관리자 여부
		
		@Temporal(TemporalType.DATE)
		private Date    createDate; //생성날짜
		
		@Temporal(TemporalType.DATE)
		private Date    updateDate; //수정날짜

		public int getRecid() {
			return recid;
		}

		public void setRecid(int recid) {
			this.recid = recid;
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

		public String getDirectorUUID() {
			return directorUUID;
		}

		public void setDirectorUUID(String directorUUID) {
			this.directorUUID = directorUUID;
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

		public Date getUpdateDate() {
			return updateDate;
		}

		public void setCreateDate(Date createDate) {
			this.createDate = createDate;
		}

		public void setUpdateDate(Date updateDate) {
			this.updateDate = updateDate;
		}
		

	}
	
}
