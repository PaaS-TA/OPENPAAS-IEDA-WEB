package org.openpaas.ieda.web.deploy.cf.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class CfParamDTO {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Default{
		private String id; //id
		@NotNull
		private String iaas; //IaaS
		@NotNull
		private String diegoYn;//diego 사용 유무
		
		// 1.1 Deployment 정보
		@NotNull
		private String deploymentName; //배포명
		@NotNull
		private String directorUuid; //설치관리자 UUID
		@NotNull
		private String releaseName; //릴리즈명
		@NotNull
		private String releaseVersion; //릴리즈 버전
		private String appSshFingerprint; //SSH 핑거프린트
		@NotNull
		private String deaMemoryMB; //deaMemoryMB
		@NotNull
		private String deaDiskMB; //deaDiskMB
		
		// 1.2 기본정보
		@NotNull
		private String domain; //도메인
		@NotNull
		private String description; //도메인 설명
		@NotNull
		private String domainOrganization; //도메인 그룹
		
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
	}
	
	public static class Uaa{
		@NotNull
		private String id; //id
		@NotNull
		private String iaas; //IaaS
		@NotNull
		private String loginSecret; //로그인 비밀번호
		@NotNull
		private String signingKey; //개인키
		@NotNull
		private String verificationKey; //공개키
		
		// 1.3 프록시 정보
		@NotNull
		private String proxyStaticIps; //HAProxy 공인 IP
		@NotNull
		private String sslPemPub; //HAProxy 인증서
		@NotNull
		private String sslPemRsa; //HAProxy 개인키
		
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
		public String getLoginSecret() {
			return loginSecret;
		}
		public void setLoginSecret(String loginSecret) {
			this.loginSecret = loginSecret;
		}
		public String getSigningKey() {
			return signingKey;
		}
		public void setSigningKey(String signingKey) {
			this.signingKey = signingKey;
		}
		public String getVerificationKey() {
			return verificationKey;
		}
		public void setVerificationKey(String verificationKey) {
			this.verificationKey = verificationKey;
		}
		public String getProxyStaticIps() {
			return proxyStaticIps;
		}
		public void setProxyStaticIps(String proxyStaticIps) {
			this.proxyStaticIps = proxyStaticIps;
		}
		public String getSslPemPub() {
			return sslPemPub;
		}
		public void setSslPemPub(String sslPemPub) {
			this.sslPemPub = sslPemPub;
		}
		public String getSslPemRsa() {
			return sslPemRsa;
		}
		public void setSslPemRsa(String sslPemRsa) {
			this.sslPemRsa = sslPemRsa;
		}
	}
	
	
	public static class Consul{
		@NotNull
		private String id; //id
		@NotNull
		private String iaas; //IaaS
		@NotNull
		private String agentCert; //에이전트 인증서
		@NotNull
		private String agentKey; //에이전트 개인키
		@NotNull
		private String caCert; //서버 CA 인증서
		@NotNull
		private String encryptKeys; //암호화 키
		@NotNull
		private String serverCert; //서버 인증서
		@NotNull
		private String serverKey; //서버 개인키
		
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
		public String getAgentCert() {
			return agentCert;
		}
		public void setAgentCert(String agentCert) {
			this.agentCert = agentCert;
		}
		public String getAgentKey() {
			return agentKey;
		}
		public void setAgentKey(String agentKey) {
			this.agentKey = agentKey;
		}
		public String getCaCert() {
			return caCert;
		}
		public void setCaCert(String caCert) {
			this.caCert = caCert;
		}
		public String getEncryptKeys() {
			return encryptKeys;
		}
		public void setEncryptKeys(String encryptKeys) {
			this.encryptKeys = encryptKeys;
		}
		public String getServerCert() {
			return serverCert;
		}
		public void setServerCert(String serverCert) {
			this.serverCert = serverCert;
		}
		public String getServerKey() {
			return serverKey;
		}
		public void setServerKey(String serverKey) {
			this.serverKey = serverKey;
		}
		
	}
	
	
	public static class Blobstore{
		@NotNull
		private String id;
		@NotNull
		private String blobstoreTlsCert;//blobstoreTlsCert
		@NotNull
		private String blobstorePrivateKey;//blobstorePrivateKey
		@NotNull
		private String blobstoreCaCert;//blobstoreCaCert
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getBlobstoreTlsCert() {
			return blobstoreTlsCert;
		}
		public void setBlobstoreTlsCert(String blobstoreTlsCert) {
			this.blobstoreTlsCert = blobstoreTlsCert;
		}
		public String getBlobstorePrivateKey() {
			return blobstorePrivateKey;
		}
		public void setBlobstorePrivateKey(String blobstorePrivateKey) {
			this.blobstorePrivateKey = blobstorePrivateKey;
		}
		public String getBlobstoreCaCert() {
			return blobstoreCaCert;
		}
		public void setBlobstoreCaCert(String blobstoreCaCert) {
			this.blobstoreCaCert = blobstoreCaCert;
		}
		
	}
	
	public static class Hm9000{
		@NotNull
		private String id;//id
		@NotNull
		private String hm9000ServerKey;//서버키
		@NotNull
		private String hm9000ServerCert;//서버 인증서
		@NotNull
		private String hm9000ClientKey;//클라이언트키
		@NotNull
		private String hm9000ClientCert;//클라이언트 인증서
		@NotNull
		private String hm9000CaCert;//CA 인증서
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getHm9000ServerKey() {
			return hm9000ServerKey;
		}
		public void setHm9000ServerKey(String hm9000ServerKey) {
			this.hm9000ServerKey = hm9000ServerKey;
		}
		public String getHm9000ServerCert() {
			return hm9000ServerCert;
		}
		public void setHm9000ServerCert(String hm9000ServerCert) {
			this.hm9000ServerCert = hm9000ServerCert;
		}
		public String getHm9000ClientKey() {
			return hm9000ClientKey;
		}
		public void setHm9000ClientKey(String hm9000ClientKey) {
			this.hm9000ClientKey = hm9000ClientKey;
		}
		public String getHm9000ClientCert() {
			return hm9000ClientCert;
		}
		public void setHm9000ClientCert(String hm9000ClientCert) {
			this.hm9000ClientCert = hm9000ClientCert;
		}
		public String getHm9000CaCert() {
			return hm9000CaCert;
		}
		public void setHm9000CaCert(String hm9000CaCert) {
			this.hm9000CaCert = hm9000CaCert;
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
	
	public static class Install{
		@NotNull
		private String iaas;//IaaS
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