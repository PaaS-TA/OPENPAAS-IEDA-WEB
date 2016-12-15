package org.openpaas.ieda.api.stemcell;

public class StemcellInfoDTO {
	private Integer recid; //recid
	private String name; //스템셀명
	private String operatingSystem; //운영체계
	private String version; //스템셀 버전
	private String cid; //cid
	private String deploymentInfo; //배포명
	
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
	public String getOperatingSystem() {
		return operatingSystem;
	}
	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getDeploymentInfo() {
		return deploymentInfo;
	}
	public void setDeploymentInfo(String deploymentInfo) {
		this.deploymentInfo = deploymentInfo;
	}
	
	
}
