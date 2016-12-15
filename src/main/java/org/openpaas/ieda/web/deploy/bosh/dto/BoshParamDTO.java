package org.openpaas.ieda.web.deploy.bosh.dto;

import javax.validation.constraints.NotNull;

public class BoshParamDTO {

	
	public static class AWS{
		private String id; //id
		private String iaas; //iaas
		private String accessKeyId; //accessKeyId
		private String secretAccessKey; //secretAccessKey
		private String privateKeyName; //privateKeyName
		private String defaultSecurityGroups; //defaultSecurityGroups
		private String region;	 //region
		private String availabilityZone; //availabilityZone
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getAccessKeyId() {
			return accessKeyId;
		}
		public void setAccessKeyId(String accessKeyId) {
			this.accessKeyId = accessKeyId;
		}
		public String getSecretAccessKey() {
			return secretAccessKey;
		}
		public void setSecretAccessKey(String secretAccessKey) {
			this.secretAccessKey = secretAccessKey;
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
		public String getRegion() {
			return region;
		}
		public void setRegion(String region) {
			this.region = region;
		}
		public String getIaas() {
			return iaas;
		}
		public void setIaas(String iaas) {
			this.iaas = iaas;
		}
		public String getAvailabilityZone() {
			return availabilityZone;
		}
		public void setAvailabilityZone(String availabilityZone) {
			this.availabilityZone = availabilityZone;
		}
	}
	
	public static class Openstack{
		private String id; //id
		private String iaas; //iaas
		private String authUrl; //authUrl
		private String tenant; //tenant
		private String userName; //userName
		private String apiKey; //apiKey
		private String defaultSecurityGroups; //defaultSecurityGroups
		private String privateKeyName; //privateKeyName
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getIaas() {
			return iaas;
		}
		public void setIaas(String iaas) {
			this.iaas = iaas;
		}
		public String getAuthUrl() {
			return authUrl;
		}
		public void setAuthUrl(String authUrl) {
			this.authUrl = authUrl;
		}
		public String getTenant() {
			return tenant;
		}
		public void setTenant(String tenant) {
			this.tenant = tenant;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getApiKey() {
			return apiKey;
		}
		public void setApiKey(String apiKey) {
			this.apiKey = apiKey;
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
	}
	
	public static class VSphere{
		private String id;//id
		@NotNull
		private String iaas; //iaas;
		@NotNull
		private String vCenterAddress; //vCenter IP
		@NotNull
		private String vCenterUser;//vCenter 로그인 ID
		@NotNull
		private String vCenterPassword;//vCenter 로그인 비밀번호
		@NotNull
		private String vCenterName;//vCenter DataCenter명
		@NotNull
		private String vCenterVMFolder;// DataCenter VM 폴더명
		@NotNull
		private String vCenterTemplateFolder;//DataCenter VM 스템셀 폴더명
		@NotNull
		private String vCenterDatastore;//DataCenter 데이터 스토어
		@NotNull
		private String vCenterPersistentDatastore;//DataCenter 영구 데이터 스토어
		@NotNull
		private String vCenterDiskPath;//DataCenter 디스크 경로
		@NotNull
		private String vCenterCluster;//DataCenter 클러스터명
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getIaas() {
			return iaas;
		}
		public void setIaas(String iaas) {
			this.iaas = iaas;
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
		public String getvCenterName() {
			return vCenterName;
		}
		public void setvCenterName(String vCenterName) {
			this.vCenterName = vCenterName;
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
		
	}
	
	public static class DefaultInfo{
		private String id; //id
		@NotNull
		private String deploymentName; //배포명
		@NotNull
		private String directorUuid; //설치관리자 UUID
		@NotNull
		private String releaseVersion; //BOSH 릴리즈
		private String directorName; //디렉터 명
		@NotNull
		private String ntp; //ntp
		@NotNull
		private String enableSnapshots; //스냅샷 사용 여부
		@NotNull
		private String snapshotSchedule; //스냅샷 스케쥴
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
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
	}
	
	
	public static class Deployment{
		@NotNull
		private String deploymentFile; //배포파일

		public String getDeploymentFile() {
			return deploymentFile;
		}

		public void setDeploymentFile(String deploymentFile) {
			this.deploymentFile = deploymentFile;
		}
	}
	
	
	public static class Install{
		@NotNull
		private String iaas; //IaaS
		@NotNull
		private String id; //id
		public String getIaas() {
			return iaas;
		}
		public void setIaas(String iaas) {
			this.iaas = iaas;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		
	}
	public static class Delete{
		@NotNull
		private String iaas; //IaaS
		@NotNull
		private String id;//id
		public String getIaas() {
			return iaas;
		}
		public void setIaas(String iaas) {
			this.iaas = iaas;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		
	}
}