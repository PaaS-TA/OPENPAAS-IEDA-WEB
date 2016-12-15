package org.openpaas.ieda.web.information.release.dto;

import org.hibernate.validator.constraints.NotBlank;

public class ReleaseContentDTO {
	
	public static class Upload{
		@NotBlank
		private String fileName; //릴리즈 파일명

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		
		
	}
	
	
	public static class Delete{
		@NotBlank
		private String fileName; //릴리즈명
		
		@NotBlank
		private String version; //릴리즈 버전

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}
		
		
	}
	
	
	public static class DeleteLocal{
		@NotBlank
		private String fileName; //릴리즈 파일명

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		
		
	}
	
	
	public static class Download {
		@NotBlank
		private String key; //key
		
		@NotBlank
		private String fileName; //릴리즈 파일명
		
		@NotBlank
		private String fileSize; //릴리즈 파일크기

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
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
		
		
	}
}
