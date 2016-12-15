package org.openpaas.ieda.api.deployment;

public class DeploymentInfoDTO {
	private Integer recid; //recid
	private String name; //배포 이름
	private String releaseInfo; //릴리즈 정보
	private String stemcellInfo; //스템셀 정보
	
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
	public String getReleaseInfo() {
		return releaseInfo;
	}
	public void setReleaseInfo(String releaseInfo) {
		this.releaseInfo = releaseInfo;
	}
	public String getStemcellInfo() {
		return stemcellInfo;
	}
	public void setStemcellInfo(String stemcellInfo) {
		this.stemcellInfo = stemcellInfo;
	}
	
	
}
