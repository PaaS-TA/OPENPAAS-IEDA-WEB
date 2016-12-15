package org.openpaas.ieda.api.release;

public class ReleaseInfoDTO {
	private Integer recid; //recid
	private String name; //릴리즈명 
	private String version; //릴리즈버전
	private String currentDeployed; //배포 사용중 여부
	private String jobNames; //Job템플릿
	public Integer getRecid() {
		return recid;
	}
	public void setRecid(Integer recid) {
		this.recid = recid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getCurrentDeployed() {
		return currentDeployed;
	}
	public void setCurrentDeployed(String currentDeployed) {
		this.currentDeployed = currentDeployed;
	}
	public String getJobNames() {
		return jobNames;
	}
	public void setJobNames(String jobNames) {
		this.jobNames = jobNames;
	}
	
	
}
