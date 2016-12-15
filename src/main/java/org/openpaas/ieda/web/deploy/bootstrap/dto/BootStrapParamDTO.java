package org.openpaas.ieda.web.deploy.bootstrap.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class BootStrapParamDTO{
	
	public static class Aws{
		private String id;//id
		@NotNull
		private String iaas;//iaas
		@NotNull 
		private String accessKeyId;//accessKeyId
		@NotNull 
		private String secretAccessId;//secretAccessId
		@NotNull 
		private String defaultSecurityGroups;//defaultSecurityGroups
		@NotNull 
		private String region;//region
		@NotNull
		private String availabilityZone;//availabilityZone
		@NotNull
		private String privateKeyName;//privateKeyName
		@NotNull
		private String privateKeyPath;//privateKeyPath
		
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
		public String getAccessKeyId() {
			return accessKeyId;
		}
		public void setAccessKeyId(String accessKeyId) {
			this.accessKeyId = accessKeyId;
		}
		public String getSecretAccessId() {
			return secretAccessId;
		}
		public void setSecretAccessId(String secretAccessId) {
			this.secretAccessId = secretAccessId;
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
		public String getAvailabilityZone() {
			return availabilityZone;
		}
		public void setAvailabilityZone(String availabilityZone) {
			this.availabilityZone = availabilityZone;
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
	}
	
	public static class Openstack{
		private String id; //id
		@NotNull
		private String iaas; //iaas
		@NotNull
		private String authUrl; //authUrl 
		@NotNull
		private String tenant; //tenant 
		@NotNull
		private String userName; //userName 
		@NotNull 
		private String apiKey; //apiKey 
		@NotNull
		private String defaultSecurityGroups; //defaultSecurityGroups 
		@NotNull
		private String privateKeyName; //privateKeyName 
		@NotNull
		private String privateKeyPath; //privateKeyPath 
		private Date createDate; // 생성일자
		private Date updateDate; // 수정일자

		
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
		public String getPrivateKeyPath() {
			return privateKeyPath;
		}
		public void setPrivateKeyPath(String privateKeyPath) {
			this.privateKeyPath = privateKeyPath;
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
	
	
	public static class Default{
		@NotNull
		private String id; //id
		@NotNull
		private String deploymentName; //배포명
		@NotNull
		private String directorName; //디렉터 명
		@NotNull
		private String boshRelease; //bosh 릴리즈
		@NotNull
		private String ntp; //내부 NTP
		@NotNull
		private String boshCpiRelease; //bosh cpi 릴리즈
		@NotNull
		private String enableSnapshots;//스냅샷 사용 유무
		@NotNull
		private String snapshotSchedule;//스냅샷 스케줄
		
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
		public String getNtp() {
			return ntp;
		}
		public void setNtp(String ntp) {
			this.ntp = ntp;
		}
		
		
	}
	
	
	public static class Network{
		@NotNull
		private String id; //id
		@NotNull
		private String privateStaticIp; //디렉터 내부 ip
		@NotNull
		private String subnetId; //내부 네트워크id
		@NotNull
		private String subnetRange; //내부 서브넷 범위 
		@NotNull
		private String subnetGateway; //내부 게이트웨이
		@NotNull
		private String subnetDns; //내부 DNS
		private String publicStaticIp; //디렉터 공인 ip
		private String publicSubnetId; //public 네트워크id
		private String publicSubnetRange; //public 서브넷 범위 
		private String publicSubnetGateway; //public 게이트웨이
		private String publicSubnetDns; //public DNS
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getPrivateStaticIp() {
			return privateStaticIp;
		}
		public void setPrivateStaticIp(String privateStaticIp) {
			this.privateStaticIp = privateStaticIp;
		}
		public String getSubnetId() {
			return subnetId;
		}
		public void setSubnetId(String subnetId) {
			this.subnetId = subnetId;
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
		
	}
	
	
	public static class Resource{
		@NotNull
		private String id; //id
		@NotNull
		private String stemcell; //스템셀
		private String cloudInstanceType; //인스턴스유형
		@NotNull
		private String boshPassword; //VM 비밀번호
		private String resourcePoolCpu;//리소스 풀 CPU
		private String resourcePoolRam;//리소스 풀 RAM
		private String resourcePoolDisk;//리소스 풀 DISK
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
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
	}
	
	public static class Install{
		@NotNull
		private String iaas; //Iaas
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
		private String id; //id
		@NotNull
		private String iaas; //Iaas
		
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
}