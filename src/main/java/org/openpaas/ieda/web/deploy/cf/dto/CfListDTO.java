package org.openpaas.ieda.web.deploy.cf.dto;

import java.util.Date;

public class CfListDTO {

	private int recid; //recid
	private int id; //ID
	private String iaas; //IaaS
	private Date createDate; //생성일자
	private Date updateDate; //수정일자

	// 1.1 Deployment 정보
	private String deploymentName; //배포명
	private String directorUuid; //설치관리자 UUID
	private String releaseName; //릴리즈명
	private String releaseVersion; //릴리즈버전
	private String appSshFingerprint; //SSH 핑거프린트
	private String diegoYn;
	// 1.2 기본정보
	private String domain; //도메인
	private String description; //도메인 설명
	private String domainOrganization; //도메인 그룹

	// 1.3 HA프록시 정보
	private String proxyStaticIps; //HAProxy 공인 IP 

	// 4. 네트워크 정보
	private String subnetRange; //서브넷 범위
	private String subnetGateway; //게이트웨이
	private String subnetDns; //DNS
	private String subnetReservedIp; //할당된 IP 대역 From ~to
	private String subnetStaticIp; //VM 할당 IP대역 From ~ to
	private String subnetId; //서브넷 ID(NET ID)
	private String cloudSecurityGroups; //시큐리티 그룹명
	private String availabilityZone;
	// 5. 리소스 정보
	private String stemcellName; //스템셀명
	private String stemcellVersion; //스템셀 버전
	private String boshPassword; //VM 비밀번호
	
	// Deploy 정보
	private String deploymentFile; //배포파일명
	private String deployStatus; //배포상태
	private String cfDiegoInstall;//CF & Diego 통합 설치 사용 여부
	private int taskId; //TASK ID
	
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
	public String getReleaseName() {
		return releaseName;
	}
	public void setReleaseName(String releaseName) {
		this.releaseName = releaseName;
	}
	public String getReleaseVersion() {
		return releaseVersion;
	}
	public void setReleaseVersion(String releaseVersion) {
		this.releaseVersion = releaseVersion;
	}
	public String getAppSshFingerprint() {
		return appSshFingerprint;
	}
	public void setAppSshFingerprint(String appSshFingerprint) {
		this.appSshFingerprint = appSshFingerprint;
	}
	
	public String getDiegoYn() {
		return diegoYn;
	}
	public void setDiegoYn(String diegoYn) {
		this.diegoYn = diegoYn;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDomainOrganization() {
		return domainOrganization;
	}
	public void setDomainOrganization(String domainOrganization) {
		this.domainOrganization = domainOrganization;
	}
	public String getProxyStaticIps() {
		return proxyStaticIps;
	}
	public void setProxyStaticIps(String proxyStaticIps) {
		this.proxyStaticIps = proxyStaticIps;
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
	public String getCfDiegoInstall() {
		return cfDiegoInstall;
	}
	public void setCfDiegoInstall(String cfDiegoInstall) {
		this.cfDiegoInstall = cfDiegoInstall;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
}