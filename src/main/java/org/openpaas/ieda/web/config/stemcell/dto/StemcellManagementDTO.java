package org.openpaas.ieda.web.config.stemcell.dto;

import javax.validation.constraints.NotNull;

public class StemcellManagementDTO {
	public static class Download {
		
		@NotNull
		private Integer recid; //recid
		@NotNull
		private Integer id;
		@NotNull
		private String sublink; //sublink
		@NotNull
		private String fileName; //파일명
		@NotNull
		private String fileSize; //파일크기
		private String downloadStatus; //다운로드 상태
		private String updateUserId; //수정자
		private String createUserId; //생성자
		
		public Integer getRecid() {
			return recid;
		}
		public void setRecid(Integer recid) {
			this.recid = recid;
		}
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getSublink() {
			return sublink;
		}
		public void setSublink(String sublink) {
			this.sublink = sublink;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getFileSize() {
			return fileSize;
		}
		public void setFileSize(String fileSize) {
			this.fileSize = fileSize;
		}
		public String getUpdateUserId() {
			return updateUserId;
		}
		public void setUpdateUserId(String updateUserId) {
			this.updateUserId = updateUserId;
		}
		public String getCreateUserId() {
			return createUserId;
		}
		public void setCreateUserId(String createUserId) {
			this.createUserId = createUserId;
		}
		public String getDownloadStatus() {
			return downloadStatus;
		}
		public void setDownloadStatus(String downloadStatus) {
			this.downloadStatus = downloadStatus;
		}
		
		
	}
}
