package org.openpaas.ieda.web.deploy.bootstrap.dao;

import java.util.Date;

public class BootstrapVO {

	private Integer id; // id
	private String iaasType; // iaas 유형

	private String createUserId; // 설치 사용자
	private String updateUserId; // 설치 수정 사용자
	private Date createDate; // 생성일자
	private Date updateDate; // 수정일자
	
	/** AWS Setting Info **/
	private String awsAccessKeyId; // accessKeyId
	private String awsSecretAccessId; // secretAccessId
	private String awsRegion;// region
	private String awsAvailabilityZone;// availabilityZone

	/** Openstack Info **/
	private String openstackAuthUrl; // authUrl
	private String openstackTenant; // tenant
	private String openstackUserName; // userName
	private String openstackApiKey; // apiKey

	/** vSphere **/
	private String vCenterAddress;// vCenter IP
	private String vCenterUser;// vCenter 로그인 ID
	private String vCenterPassword;// vCenter 로그인 비밀번호
	private String vCenterDatacenterName;// vCenter DataCenter명
	private String vCenterVMFolder;// DataCenter VM 폴더명
	private String vCenterTemplateFolder; // DataCenter VM 스템셀 폴더명
	private String vCenterDatastore;// DataCenter 데이터 스토어
	private String vCenterPersistentDatastore;// DataCenter 영구 데이터 스토어
	private String vCenterDiskPath;// DataCenter 디스크 경로
	private String vCenterCluster;// DataCenter 클러스터명

	private String defaultSecurityGroups; // defaultSecurityGroups
	private String privateKeyName;// privateKeyName
	private String privateKeyPath;// privateKeyPath

	/** Default Info **/
	private String deploymentName; // 배포명
	private String directorName; // 디렉터명
	private String boshRelease; // BOSH 릴리즈
	private String boshCpiRelease; // BOSH API 릴리즈
	private String snapshotSchedule;//스냅샷 스케줄
	private String enableSnapshots;//스냅샷 사용 유무
	private String ntp; // NTP

	/** Network Info **/
	private String subnetId; // 네트워크id
	private String privateStaticIp; // 디렉터 내부 ip
	private String subnetRange; // 서브넷 범위
	private String subnetGateway; // 게이트웨이
	private String subnetDns; // DNS
	
	private String publicStaticIp; //디렉터 공인 ip
	private String publicSubnetId; //public 네트워크id
	private String publicSubnetRange; //public 서브넷 범위 
	private String publicSubnetGateway; //public 게이트웨이
	private String publicSubnetDns; //public DNS

	/** Resource Info **/
	private String stemcell; // 스템셀
	private String cloudInstanceType; // 인스턴스 유형
	private String boshPassword; //VM 비밀번호
	private String resourcePoolCpu;//리소스 풀 CPU
	private String resourcePoolRam;//리소스 풀 RAM
	private String resourcePoolDisk;//리소스 풀 DISK

	private String deploymentFile; // 배포파일
	private String deployStatus; // 배포상태
	private String deployLog; // 배포로그
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIaasType() {
		return iaasType;
	}
	public void setIaasType(String iaasType) {
		this.iaasType = iaasType;
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
	public String getAwsAccessKeyId() {
		return awsAccessKeyId;
	}
	public void setAwsAccessKeyId(String awsAccessKeyId) {
		this.awsAccessKeyId = awsAccessKeyId;
	}
	public String getAwsSecretAccessId() {
		return awsSecretAccessId;
	}
	public void setAwsSecretAccessId(String awsSecretAccessId) {
		this.awsSecretAccessId = awsSecretAccessId;
	}
	public String getAwsRegion() {
		return awsRegion;
	}
	public void setAwsRegion(String awsRegion) {
		this.awsRegion = awsRegion;
	}
	public String getAwsAvailabilityZone() {
		return awsAvailabilityZone;
	}
	public void setAwsAvailabilityZone(String awsAvailabilityZone) {
		this.awsAvailabilityZone = awsAvailabilityZone;
	}
	public String getOpenstackAuthUrl() {
		return openstackAuthUrl;
	}
	public void setOpenstackAuthUrl(String openstackAuthUrl) {
		this.openstackAuthUrl = openstackAuthUrl;
	}
	public String getOpenstackTenant() {
		return openstackTenant;
	}
	public void setOpenstackTenant(String openstackTenant) {
		this.openstackTenant = openstackTenant;
	}
	public String getOpenstackUserName() {
		return openstackUserName;
	}
	public void setOpenstackUserName(String openstackUserName) {
		this.openstackUserName = openstackUserName;
	}
	public String getOpenstackApiKey() {
		return openstackApiKey;
	}
	public void setOpenstackApiKey(String openstackApiKey) {
		this.openstackApiKey = openstackApiKey;
	}
	public String getvCenterAddress() {
		return vCenterAddress;
	}
	public void setvCenterAddress(String vCenterAddress) {
		this.vCenterAddress = vCenterAddress;
	}
	public String getvCenterUser() {
		return vCenterUser;
	}
	public void setvCenterUser(String vCenterUser) {
		this.vCenterUser = vCenterUser;
	}
	public String getvCenterPassword() {
		return vCenterPassword;
	}
	public void setvCenterPassword(String vCenterPassword) {
		this.vCenterPassword = vCenterPassword;
	}
	public String getvCenterDatacenterName() {
		return vCenterDatacenterName;
	}
	public void setvCenterDatacenterName(String vCenterDatacenterName) {
		this.vCenterDatacenterName = vCenterDatacenterName;
	}
	public String getvCenterVMFolder() {
		return vCenterVMFolder;
	}
	public void setvCenterVMFolder(String vCenterVMFolder) {
		this.vCenterVMFolder = vCenterVMFolder;
	}
	public String getvCenterTemplateFolder() {
		return vCenterTemplateFolder;
	}
	public void setvCenterTemplateFolder(String vCenterTemplateFolder) {
		this.vCenterTemplateFolder = vCenterTemplateFolder;
	}
	public String getvCenterDatastore() {
		return vCenterDatastore;
	}
	public void setvCenterDatastore(String vCenterDatastore) {
		this.vCenterDatastore = vCenterDatastore;
	}
	public String getvCenterPersistentDatastore() {
		return vCenterPersistentDatastore;
	}
	public void setvCenterPersistentDatastore(String vCenterPersistentDatastore) {
		this.vCenterPersistentDatastore = vCenterPersistentDatastore;
	}
	public String getvCenterDiskPath() {
		return vCenterDiskPath;
	}
	public void setvCenterDiskPath(String vCenterDiskPath) {
		this.vCenterDiskPath = vCenterDiskPath;
	}
	public String getvCenterCluster() {
		return vCenterCluster;
	}
	public void setvCenterCluster(String vCenterCluster) {
		this.vCenterCluster = vCenterCluster;
	}
	public String getDefaultSecurityGroups() {
		return defaultSecurityGroups;
	}
	public void setDefaultSecurityGroups(String defaultSecurityGroups) {
		this.defaultSecurityGroups = defaultSecurityGroups;
	}
	public String getPrivateKeyName() {
		return privateKeyName;
	}
	public void setPrivateKeyName(String privateKeyName) {
		this.privateKeyName = privateKeyName;
	}
	public String getPrivateKeyPath() {
		return privateKeyPath;
	}
	public void setPrivateKeyPath(String privateKeyPath) {
		this.privateKeyPath = privateKeyPath;
	}
	public String getDeploymentName() {
		return deploymentName;
	}
	public void setDeploymentName(String deploymentName) {
		this.deploymentName = deploymentName;
	}
	public String getDirectorName() {
		return directorName;
	}
	public void setDirectorName(String directorName) {
		this.directorName = directorName;
	}
	public String getBoshRelease() {
		return boshRelease;
	}
	public void setBoshRelease(String boshRelease) {
		this.boshRelease = boshRelease;
	}
	public String getBoshCpiRelease() {
		return boshCpiRelease;
	}
	public void setBoshCpiRelease(String boshCpiRelease) {
		this.boshCpiRelease = boshCpiRelease;
	}
	public String getSnapshotSchedule() {
		return snapshotSchedule;
	}
	public void setSnapshotSchedule(String snapshotSchedule) {
		this.snapshotSchedule = snapshotSchedule;
	}
	public String getEnableSnapshots() {
		return enableSnapshots;
	}
	public void setEnableSnapshots(String enableSnapshots) {
		this.enableSnapshots = enableSnapshots;
	}
	public String getSubnetId() {
		return subnetId;
	}
	public void setSubnetId(String subnetId) {
		this.subnetId = subnetId;
	}
	public String getPrivateStaticIp() {
		return privateStaticIp;
	}
	public void setPrivateStaticIp(String privateStaticIp) {
		this.privateStaticIp = privateStaticIp;
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
	public String getNtp() {
		return ntp;
	}
	public void setNtp(String ntp) {
		this.ntp = ntp;
	}
	public String getPublicStaticIp() {
		return publicStaticIp;
	}
	public void setPublicStaticIp(String publicStaticIp) {
		this.publicStaticIp = publicStaticIp;
	}
	public String getPublicSubnetId() {
		return publicSubnetId;
	}
	public void setPublicSubnetId(String publicSubnetId) {
		this.publicSubnetId = publicSubnetId;
	}
	public String getPublicSubnetRange() {
		return publicSubnetRange;
	}
	public void setPublicSubnetRange(String publicSubnetRange) {
		this.publicSubnetRange = publicSubnetRange;
	}
	public String getPublicSubnetGateway() {
		return publicSubnetGateway;
	}
	public void setPublicSubnetGateway(String publicSubnetGateway) {
		this.publicSubnetGateway = publicSubnetGateway;
	}
	public String getPublicSubnetDns() {
		return publicSubnetDns;
	}
	public void setPublicSubnetDns(String publicSubnetDns) {
		this.publicSubnetDns = publicSubnetDns;
	}
	public String getStemcell() {
		return stemcell;
	}
	public void setStemcell(String stemcell) {
		this.stemcell = stemcell;
	}
	public String getCloudInstanceType() {
		return cloudInstanceType;
	}
	public void setCloudInstanceType(String cloudInstanceType) {
		this.cloudInstanceType = cloudInstanceType;
	}
	public String getBoshPassword() {
		return boshPassword;
	}
	public void setBoshPassword(String boshPassword) {
		this.boshPassword = boshPassword;
	}
	public String getResourcePoolCpu() {
		return resourcePoolCpu;
	}
	public void setResourcePoolCpu(String resourcePoolCpu) {
		this.resourcePoolCpu = resourcePoolCpu;
	}
	public String getResourcePoolRam() {
		return resourcePoolRam;
	}
	public void setResourcePoolRam(String resourcePoolRam) {
		this.resourcePoolRam = resourcePoolRam;
	}
	public String getResourcePoolDisk() {
		return resourcePoolDisk;
	}
	public void setResourcePoolDisk(String resourcePoolDisk) {
		this.resourcePoolDisk = resourcePoolDisk;
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
	public String getDeployLog() {
		return deployLog;
	}
	public void setDeployLog(String deployLog) {
		this.deployLog = deployLog;
	}
	

}