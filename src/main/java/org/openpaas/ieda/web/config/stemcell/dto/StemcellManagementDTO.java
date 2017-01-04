package org.openpaas.ieda.web.config.stemcell.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class StemcellManagementDTO {
	public static class Regist {
		private Integer id; //스템셀 아이디
		private Integer recid; 
		@NotNull
		private String stemcellName; //스템셀 명
		private String stemcellFileName; // 스템셀 파일 이름
		private String stemcellUrl; // 스템셀 url
		private String stemcellVersion; //스템셀 버전
		@NotNull
		private String osName; // 스템셀 Os
		@NotNull
		private String osVersion; // 스템셀 Os 버전
		@NotNull
		private String fileType; // 스템셀 다운로드 유형
		private String overlayCheck; //파일 덮어 스기
		private String stemcellSize; //스템셀 사이즈
		private String awsLight; //스템셀 Light유형
		private String downloadStatus; //스템셀 다운로드 상태
		private String iaasType;
		private String createUserId;//최초 등록 유저
		private String updateUserId;//릴리즈 수정 유저
		private Date createDate;//최초 등록일
		private Date updateDate;//릴리즈 수정일
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public Integer getRecid() {
			return recid;
		}
		public void setRecid(Integer recid) {
			this.recid = recid;
		}
		public String getStemcellName() {
			return stemcellName;
		}
		public void setStemcellName(String stemcellName) {
			this.stemcellName = stemcellName;
		}
		public String getStemcellFileName() {
			return stemcellFileName;
		}
		
		public String getIaasType() {
			return iaasType;
		}
		public void setIaasType(String iaasType) {
			this.iaasType = iaasType;
		}
		public void setStemcellFileName(String stemcellFileName) {
			this.stemcellFileName = stemcellFileName;
		}
		public String getStemcellUrl() {
			return stemcellUrl;
		}
		public void setStemcellUrl(String stemcellUrl) {
			this.stemcellUrl = stemcellUrl;
		}
		public String getStemcellVersion() {
			return stemcellVersion;
		}
		public void setStemcellVersion(String stemcellVersion) {
			this.stemcellVersion = stemcellVersion;
		}
		public String getOsName() {
			return osName;
		}
		public void setOsName(String osName) {
			this.osName = osName;
		}
		public String getOsVersion() {
			return osVersion;
		}
		public void setOsVersion(String osVersion) {
			this.osVersion = osVersion;
		}
		public String getFileType() {
			return fileType;
		}
		public void setFileType(String fileType) {
			this.fileType = fileType;
		}
		public String getOverlayCheck() {
			return overlayCheck;
		}
		public void setOverlayCheck(String overlayCheck) {
			this.overlayCheck = overlayCheck;
		}
		public String getStemcellSize() {
			return stemcellSize;
		}
		public void setStemcellSize(String stemcellSize) {
			this.stemcellSize = stemcellSize;
		}
		public String getAwsLight() {
			return awsLight;
		}
		public void setAwsLight(String awsLight) {
			this.awsLight = awsLight;
		}
		public String getDownloadStatus() {
			return downloadStatus;
		}
		public void setDownloadStatus(String downloadStatus) {
			this.downloadStatus = downloadStatus;
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
		public Date getCreateDate() {
			return createDate;
		}
		public void setCreateDate(Date createDate) {
			this.createDate = createDate;
		}
		public Date getUpdateDate() {
			return updateDate;
		}
		public void setUpdateDate(Date updateDate) {
			this.updateDate = updateDate;
		}
	}
	public static class Delete {
		
		private Integer id; // 삭제 스템셀 아이디
		private String stemcellFileName;//삭제 스템셀 파일 명
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getStemcellFileName() {
			return stemcellFileName;
		}
		public void setStemcellFileName(String stemcellFileName) {
			this.stemcellFileName = stemcellFileName;
		}
	}
}
