package org.openpaas.ieda.web.config.systemRelease.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class ReleaseManagementDTO {
	
	public static class Regist{
		private Integer id;
		@NotNull
		private String overlayCheck;//덮어쓰기 여부
		@NotNull
		private String releaseName;//릴리즈명
		@NotNull
		private String releaseType;//릴리즈 유형
		private String fileType;//릴리즈 파일 등록 유형
		private String releaseFileName;//릴리즈 파일명
		private String releaseSize;//릴리즈 파일 크기
		private String createUserId;//최초 등록 유저
		private String updateUserId;//릴리즈 수정 유저
		private String downloadStatus;//다운로드된 상태
		private Date createDate;//최초 등록일
		private Date updateDate;//릴리즈 수정일
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getOverlayCheck() {
			return overlayCheck;
		}
		public void setOverlayCheck(String overlayCheck) {
			this.overlayCheck = overlayCheck;
		}
		public String getReleaseName() {
			return releaseName;
		}
		public void setReleaseName(String releaseName) {
			this.releaseName = releaseName;
		}
		public String getReleaseType() {
			return releaseType;
		}
		public void setReleaseType(String releaseType) {
			this.releaseType = releaseType;
		}
		public String getFileType() {
			return fileType;
		}
		public void setFileType(String fileType) {
			this.fileType = fileType;
		}
		public String getReleaseFileName() {
			return releaseFileName;
		}
		public void setReleaseFileName(String releaseFileName) {
			this.releaseFileName = releaseFileName;
		}
		public String getReleaseSize() {
			return releaseSize;
		}
		public void setReleaseSize(String releaseSize) {
			this.releaseSize = releaseSize;
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
	
	}
	
	public static class Delete{
		
		private String id;
		@NotNull
		private String releaseFileName;//릴리즈 파일명
		
		public String getId() {
			return id;
		}
		public String getReleaseFileName() {
			return releaseFileName;
		}
		public void setId(String id) {
			this.id = id;
		}
		public void setReleaseFileName(String releaseFileName) {
			this.releaseFileName = releaseFileName;
		}
		
	}
}
