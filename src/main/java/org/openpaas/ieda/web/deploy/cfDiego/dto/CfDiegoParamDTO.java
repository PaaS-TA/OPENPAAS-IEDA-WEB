package org.openpaas.ieda.web.deploy.cfDiego.dto;

import javax.validation.constraints.NotNull;

public class CfDiegoParamDTO {
	
	public static class Default{
		private String id; //id
		@NotNull
		private String iaas; //IaaS
		@NotNull
		private String platform;//cf 및 diego 구분
		private String diegoYn;//diego 사용 유무
		
		// 1.1 Deployment 정보
		@NotNull
		private String deploymentName; //배포명
		@NotNull
		private String directorUuid; //설치관리자 UUID
		
		// 1.2 CF 기본정보
		private String releaseName; //릴리즈명
		private String releaseVersion; //릴리즈 버전
		private String appSshFingerprint; //SSH 핑거프린트
		private String deaMemoryMB; //deaMemoryMB
		private String deaDiskMB; //deaDiskMB
		private String domain; //도메인
		private String description; //도메인 설명
		private String domainOrganization; //도메인 그룹
        private String loginSecret; //로그인 비밀번호
		
		//1.3 Diego 기본 정보
		private String diegoReleaseName; //DIEGO 릴리즈명
		private String diegoReleaseVersion; //DIEGO 릴리즈 버전
		private String gardenReleaseName; //Garden-Linux 릴리즈명
		private String gardenReleaseVersion; //Garden-Linux 릴리즈 버전
		private String etcdReleaseName; //ETCD 릴리즈명
		private String etcdReleaseVersion; //ETCD 릴리즈 버전
		private int cfId; //cf 아아디
		private String cfDeployment; //cf 파일명
		private String cfDeploymentName;//cf 배포명
		
		private String cflinuxfs2rootfsreleaseName; //cflinuxfs2rootf 릴리즈 명
		private String cflinuxfs2rootfsreleaseVersion; //cflinuxfs2rootf 릴리즈 버전
		
		private String paastaMonitoringUse;//PaaS-TA 모니터링 사용 유무
		private String cadvisorDriverIp;//PaaS-TA 모니터링 DB 서버 IP
		private String cadvisorDriverPort;//PaaS-TA 모니터링 DB 서버 PORT

		private String keyFile;
		
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
		public String getPlatform() {
			return platform;
		}
		public void setPlatform(String platform) {
			this.platform = platform;
		}

		public String getDiegoYn() {
			return diegoYn;
		}
		public void setDiegoYn(String diegoYn) {
			this.diegoYn = diegoYn;
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
		public String getDeaMemoryMB() {
			return deaMemoryMB;
		}
		public void setDeaMemoryMB(String deaMemoryMB) {
			this.deaMemoryMB = deaMemoryMB;
		}
		public String getDeaDiskMB() {
			return deaDiskMB;
		}
		public void setDeaDiskMB(String deaDiskMB) {
			this.deaDiskMB = deaDiskMB;
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
		public String getCfDeploymentName() {
			return cfDeploymentName;
		}
		public void setCfDeploymentName(String cfDeploymentName) {
			this.cfDeploymentName = cfDeploymentName;
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
		public String getLoginSecret() {
			return loginSecret;
		}
		public void setLoginSecret(String loginSecret) {
			this.loginSecret = loginSecret;
		}
		public String getPaastaMonitoringUse() {
			return paastaMonitoringUse;
		}
		public void setPaastaMonitoringUse(String paastaMonitoringUse) {
			this.paastaMonitoringUse = paastaMonitoringUse;
		}
		public String getCadvisorDriverIp() {
			return cadvisorDriverIp;
		}
		public void setCadvisorDriverIp(String cadvisorDriverIp) {
			this.cadvisorDriverIp = cadvisorDriverIp;
		}
		public String getCadvisorDriverPort() {
			return cadvisorDriverPort;
		}
		public void setCadvisorDriverPort(String cadvisorDriverPort) {
			this.cadvisorDriverPort = cadvisorDriverPort;
		}
		
	}
	
	public static class Install{
		@NotNull
		private String id; //id
		@NotNull
		private String iaas;//
		@NotNull
		private String platform;//플랫폼 유형

		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getPlatform() {
			return platform;
		}
		public void setPlatform(String platform) {
			this.platform = platform;
		}
		public String getIaas() {
			return iaas;
		}
		public void setIaas(String iaas) {
			this.iaas = iaas;
		}
		
	}
	
	public static class Delete{
		@NotNull
		private String iaas; //IaaS
		@NotNull
		private String id; //id
		@NotNull
		private String platform;//플랫폼 유형
		
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
		public String getPlatform() {
			return platform;
		}
		public void setPlatform(String platform) {
			this.platform = platform;
		}
	}

}
