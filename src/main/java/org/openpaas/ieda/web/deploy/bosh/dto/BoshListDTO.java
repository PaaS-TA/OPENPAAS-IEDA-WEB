package org.openpaas.ieda.web.deploy.bosh.dto;

import java.util.Date;


public class BoshListDTO {

	private int recid; //recid
	private int id; //id
	private String iaas; //IaaS

	private Date createDate; // 생성일자
	private Date updateDate; // 수정일자

	// BOSH
	private String directorUuid; //설치관리자 UUID
	private String deploymentName; //배포명
	private String releaseVersion; //BOSH 릴리즈
	private String directorName; //설치관리자 명
	private String ntp; //BOSH 릴리즈
	private String enableSnapshots; //BOSH 릴리즈
	private String snapshotSchedule; //BOSH 릴리즈

	// NETWORK
	private String publicStaticIp;
	private String subnetRange; //서브넷 범위
	private String subnetGateway; //게이트웨이
	private String subnetDns; //DNS
	private String subnetReservedIp; //할당된 IP 대역 From ~to
	private String subnetStaticIp; //VM 할당 IP대역 From ~ to
	private String subnetId; //서브넷 ID(NET ID)

	private String stemcellName; //스템셀명
	private String stemcellVersion; //스템셀 버전
	private String boshPassword;
	

	//DEPLOY
	private String deploymentFile; //배포파일명
	private String deployStatus; //배포상태
	private Integer taskId; //TASK ID
	public int getRecid() {
		return recid;
	}
	public void setRecid(int recid) {
		this.recid = recid;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIaas() {
		return iaas;
	}
	public void setIaas(String iaas) {
		this.iaas = iaas;
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
	public String getDirectorUuid() {
		return directorUuid;
	}
	public void setDirectorUuid(String directorUuid) {
		this.directorUuid = directorUuid;
	}
	public String getDeploymentName() {
		return deploymentName;
	}
	public void setDeploymentName(String deploymentName) {
		this.deploymentName = deploymentName;
	}
	public String getReleaseVersion() {
		return releaseVersion;
	}
	public void setReleaseVersion(String releaseVersion) {
		this.releaseVersion = releaseVersion;
	}
	public String getDirectorName() {
		return directorName;
	}
	public void setDirectorName(String directorName) {
		this.directorName = directorName;
	}
	public String getNtp() {
		return ntp;
	}
	public void setNtp(String ntp) {
		this.ntp = ntp;
	}
	public String getEnableSnapshots() {
		return enableSnapshots;
	}
	public void setEnableSnapshots(String enableSnapshots) {
		this.enableSnapshots = enableSnapshots;
	}
	public String getSnapshotSchedule() {
		return snapshotSchedule;
	}
	public void setSnapshotSchedule(String snapshotSchedule) {
		this.snapshotSchedule = snapshotSchedule;
	}
	public String getPublicStaticIp() {
		return publicStaticIp;
	}
	public void setPublicStaticIp(String publicStaticIp) {
		this.publicStaticIp = publicStaticIp;
	}
	public String getSubnetRange() {
		return subnetRange;
	}
	public void setSubnetRange(String subnetRange) {
		this.subnetRange = subnetRange;
	}
	public String getSubnetGateway() {
		return subnetGateway;
	}
	public void setSubnetGateway(String subnetGateway) {
		this.subnetGateway = subnetGateway;
	}
	public String getSubnetDns() {
		return subnetDns;
	}
	public void setSubnetDns(String subnetDns) {
		this.subnetDns = subnetDns;
	}
	public String getSubnetReservedIp() {
		return subnetReservedIp;
	}
	public void setSubnetReservedIp(String subnetReservedIp) {
		this.subnetReservedIp = subnetReservedIp;
	}
	public String getSubnetStaticIp() {
		return subnetStaticIp;
	}
	public void setSubnetStaticIp(String subnetStaticIp) {
		this.subnetStaticIp = subnetStaticIp;
	}
	public String getSubnetId() {
		return subnetId;
	}
	public void setSubnetId(String subnetId) {
		this.subnetId = subnetId;
	}
	public String getStemcellName() {
		return stemcellName;
	}
	public void setStemcellName(String stemcellName) {
		this.stemcellName = stemcellName;
	}
	
	public String getBoshPassword() {
		return boshPassword;
	}
	public void setBoshPassword(String boshPassword) {
		this.boshPassword = boshPassword;
	}
	public String getStemcellVersion() {
		return stemcellVersion;
	}
	public void setStemcellVersion(String stemcellVersion) {
		this.stemcellVersion = stemcellVersion;
	}
	public String getDeploymentFile() {
		return deploymentFile;
	}
	public void setDeploymentFile(String deploymentFile) {
		this.deploymentFile = deploymentFile;
	}
	public String getDeployStatus() {
		return deployStatus;
	}
	public void setDeployStatus(String deployStatus) {
		this.deployStatus = deployStatus;
	}
	public Integer getTaskId() {
		return taskId;
	}
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}
}
