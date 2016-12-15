package org.openpaas.ieda.api.release;

public class ReleaseFileDTO {
	private Integer recid; //recid
	private String releaseFile; //릴리즈 파일명
	private String releaseFileSize; //릴리즈 파일크기
	public Integer getRecid() {
		return recid;
	}
	public void setRecid(Integer recid) {
		this.recid = recid;
	}
	public String getReleaseFile() {
		return releaseFile;
	}
	public void setReleaseFile(String releaseFile) {
		this.releaseFile = releaseFile;
	}
	public String getReleaseFileSize() {
		return releaseFileSize;
	}
	public void setReleaseFileSize(String releaseFileSize) {
		this.releaseFileSize = releaseFileSize;
	}
	
	
}
