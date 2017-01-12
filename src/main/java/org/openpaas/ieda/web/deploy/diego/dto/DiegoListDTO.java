package org.openpaas.ieda.web.deploy.diego.dto;

import java.util.Date;


public class DiegoListDTO {

	private int recid; //recid
	private int id; //ID
	private String iaas; //IaaS
	private Date createDate; //생성일자
	private Date updateDate; //수정일자

	//1.1 기본정보	
	private String deploymentName; //배포명
	private String directorUuid; //설치관리자 UUID
	private String diegoReleaseName; // 릴리즈명
	private String diegoReleaseVersion; //Diego 릴리즈 버전
	private int cfId; //CF 릴리즈명
	private String cfDeployment; //CF 배포 파일명
	private String gardenReleaseName; //Garden Linux 릴리즈명
	private String gardenReleaseVersion; //Garden Linux 릴리즈 버전
	private String etcdReleaseName; //ETCD 릴리즈명
	private String etcdReleaseVersion; //ETCD 릴리즈 버전
	private String cflinuxfs2rootfsreleaseName; //cflinuxfs2rootf 릴리즈 명
	private String cflinuxfs2rootfsreleaseVersion; //cflinuxfs2rootf 릴리즈 버전
	private String keyFile;

	//3.1 네트워크 정보
	private String publicStaticIp;
	private String subnetRange; //서브넷 범위
	private String subnetGateway; //게이트웨이
	private String subnetDns; //DNS
	private String subnetReservedIp; //할당된 IP 대역 From ~to
	private String subnetStaticIp; //VM 할당 IP대역 From ~ to
	private String subnetId; //서브넷 ID(NET ID)
	private String cloudSecurityGroups; //시큐리티 그룹명
	private String availabilityZone;
	
	//4 리소스 정보	
	private String stemcellName; //스템셀명
	private String stemcellVersion; //스템셀 버전
	private String boshPassword;

	// Deploy 정보
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
	public String getDeploymentName() {
		return deploymentName;
	}
	public void setDeploymentName(String deploymentName) {
		this.deploymentName = deploymentName;
	}
	public String getDirectorUuid() {
		return directorUuid;
	}
	public void setDirectorUuid(String directorUuid) {
		this.directorUuid = directorUuid;
	}
	public String getDiegoReleaseName() {
		return diegoReleaseName;
	}
	public void setDiegoReleaseName(String diegoReleaseName) {
		this.diegoReleaseName = diegoReleaseName;
	}
	public String getDiegoReleaseVersion() {
		return diegoReleaseVersion;
	}
	public void setDiegoReleaseVersion(String diegoReleaseVersion) {
		this.diegoReleaseVersion = diegoReleaseVersion;
	}
	public int getCfId() {
		return cfId;
	}
	public void setCfId(int cfId) {
		this.cfId = cfId;
	}
	public String getCfDeployment() {
		return cfDeployment;
	}
	public void setCfDeployment(String cfDeployment) {
		this.cfDeployment = cfDeployment;
	}
	public String getGardenReleaseName() {
		return gardenReleaseName;
	}
	public void setGardenReleaseName(String gardenReleaseName) {
		this.gardenReleaseName = gardenReleaseName;
	}
	public String getGardenReleaseVersion() {
		return gardenReleaseVersion;
	}
	public void setGardenReleaseVersion(String gardenReleaseVersion) {
		this.gardenReleaseVersion = gardenReleaseVersion;
	}
	public String getEtcdReleaseName() {
		return etcdReleaseName;
	}
	public void setEtcdReleaseName(String etcdReleaseName) {
		this.etcdReleaseName = etcdReleaseName;
	}
	public String getEtcdReleaseVersion() {
		return etcdReleaseVersion;
	}
	public void setEtcdReleaseVersion(String etcdReleaseVersion) {
		this.etcdReleaseVersion = etcdReleaseVersion;
	}
	public String getCflinuxfs2rootfsreleaseName() {
		return cflinuxfs2rootfsreleaseName;
	}
	public void setCflinuxfs2rootfsreleaseName(String cflinuxfs2rootfsreleaseName) {
		this.cflinuxfs2rootfsreleaseName = cflinuxfs2rootfsreleaseName;
	}
	public String getCflinuxfs2rootfsreleaseVersion() {
		return cflinuxfs2rootfsreleaseVersion;
	}
	public void setCflinuxfs2rootfsreleaseVersion(String cflinuxfs2rootfsreleaseVersion) {
		this.cflinuxfs2rootfsreleaseVersion = cflinuxfs2rootfsreleaseVersion;
	}
	public String getKeyFile() {
		return keyFile;
	}
	public void setKeyFile(String keyFile) {
		this.keyFile = keyFile;
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
	public String getCloudSecurityGroups() {
		return cloudSecurityGroups;
	}
	public void setCloudSecurityGroups(String cloudSecurityGroups) {
		this.cloudSecurityGroups = cloudSecurityGroups;
	}
	public String getAvailabilityZone() {
		return availabilityZone;
	}
	public void setAvailabilityZone(String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}
	public String getStemcellName() {
		return stemcellName;
	}
	public void setStemcellName(String stemcellName) {
		this.stemcellName = stemcellName;
	}
	public String getStemcellVersion() {
		return stemcellVersion;
	}
	public void setStemcellVersion(String stemcellVersion) {
		this.stemcellVersion = stemcellVersion;
	}
	public String getBoshPassword() {
		return boshPassword;
	}
	public void setBoshPassword(String boshPassword) {
		this.boshPassword = boshPassword;
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