package org.openpaas.ieda.web.config.systemRelease.dao;

import java.util.Date;

import javax.persistence.Transient;

public class ReleaseManagementVO {
	private Integer recid;//recid
	private Integer id;//id
	private String releaseName;//릴리즈명
	private String releaseType;//릴리즈 유형
	private String releaseSize;//릴리즈 크기
	private String releaseFileName;//릴리즈 파일명
	private String createUserId;//최초 생성한 유저id
	private Date createDate;//생성일
	private String updateUserId;//수정한 유저id
	private Date updateDate;//수정일
	@Transient
	private String downloadStatus;//다운로드 상태
	
	public Integer getRecid() {
		return recid;
	}
	public Integer getId() {
		return id;
	}
	public String getReleaseName() {
		return releaseName;
	}
	public String getReleaseType() {
		return releaseType;
	}
	public String getReleaseSize() {
		return releaseSize;
	}
	public String getReleaseFileName() {
		return releaseFileName;
	}
	public void setRecid(Integer recid) {
		this.recid = recid;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setReleaseName(String releaseName) {
		this.releaseName = releaseName;
	}
	public void setReleaseType(String releaseType) {
		this.releaseType = releaseType;
	}
	public void setReleaseSize(String releaseSize) {
		this.releaseSize = releaseSize;
	}
	public void setReleaseFileName(String releaseFileName) {
		this.releaseFileName = releaseFileName;
	}
	public String getDownloadStatus() {
		return downloadStatus;
	}
	public void setDownloadStatus(String downloadStatus) {
		this.downloadStatus = downloadStatus;
	}
	public void setUpdateDate(Date updateDate) {
		if(updateDate == null) {
			this.updateDate = null;
		} else {
			this.updateDate = new Date(updateDate.getTime());
		}
	}

	public Date getUpdateDate() {
		if(updateDate == null) {
			return null;
		} else {
			return new Date(updateDate.getTime());
		}
	}

	public Date getCreateDate() {
		if(createDate == null) {
			return null;
		} else {
			return new Date(createDate.getTime());
		}
	}
	
	public void setCreateDate(Date createDate) {
		if(createDate == null) {
			this.createDate = null;
		} else {
			this.createDate = new Date(createDate.getTime());
		}
	}
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public String getUpdateUserId() {
		return updateUserId;
	}
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}
}
