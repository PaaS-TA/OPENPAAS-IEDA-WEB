package org.openpaas.ieda.web.deploy.bosh.dao;

import java.util.Date;
import java.util.List;


import org.openpaas.ieda.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.web.deploy.common.dao.resource.ResourceVO;

public class BoshVO {

	private Integer id; //id
	private String iaasType; //iaas 유형
	
	private String createUserId;//생성자
	private String updateUserId;//수정자
	private Date createDate; // 생성일자
	private Date updateDate; // 수정일자

	
	//AWS
	private String awsAccessKeyId; //accessKeyId
	private String awsSecretAccessId; //secretAccessKey
	private String awsRegion; //region
	private String awsAvailabilityZone; //awsAvailabilityZone
	
	//OPENSTACK
	private String openstackAuthUrl; //authUrl
	private String openstackTenant; //openstack 프로젝트명
	private String openstackUserName; //사용자명
	private String openstackApiKey; //api키
	
	//OPENSTACK
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

	//공통
	private String privateKeyName; //privateKeyName
	private String defaultSecurityGroups; //defaultSecurityGroups
	
	//BOSH
	private String deploymentName; //배포명
	private String directorUuid; //설치관리자 UUID
	private String releaseVersion; //BOSH 릴리즈
	private String directorName; //설치관리자 명
	private String ntp; //BOSH 릴리즈
	private String enableSnapshots; //BOSH 릴리즈
	private String snapshotSchedule; //BOSH 릴리즈
	
	
	//NETWORK
	private List<NetworkVO> networks;
	
	//RESOURCE
	ResourceVO resource;
	
	//DEPLOY
	private String deploymentFile; //배포파일
	private String deployStatus; //배포상태
	private Integer taskId; //taskId
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
	public String getPrivateKeyName() {
		return privateKeyName;
	}
	public void setPrivateKeyName(String privateKeyName) {
		this.privateKeyName = privateKeyName;
	}
	public String getDefaultSecurityGroups() {
		return defaultSecurityGroups;
	}
	public void setDefaultSecurityGroups(String defaultSecurityGroups) {
		this.defaultSecurityGroups = defaultSecurityGroups;
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
	public List<NetworkVO> getNetworks() {
		return networks;
	}
	public void setNetworks(List<NetworkVO> networks) {
		this.networks = networks;
	}
	public ResourceVO getResource() {
		return resource;
	}
	public void setResource(ResourceVO resource) {
		this.resource = resource;
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
	public String getAwsAvailabilityZone() {
		return awsAvailabilityZone;
	}
	public void setAwsAvailabilityZone(String awsAvailabilityZone) {
		this.awsAvailabilityZone = awsAvailabilityZone;
	}
}
