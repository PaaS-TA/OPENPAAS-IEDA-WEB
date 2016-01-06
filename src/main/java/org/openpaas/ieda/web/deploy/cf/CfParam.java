package org.openpaas.ieda.web.deploy.cf;

import javax.validation.constraints.NotNull;

import lombok.Data;

public class CfParam {

	@Data
	public static class Default{
		private String id;
		// 1.1 Deployment 정보
		@NotNull
		private String deploymentName;
		@NotNull
		private String directorUuid;
		@NotNull
		private String releaseName;
		@NotNull
		private String releaseVersion;
		private String appSshFingerprint;
		
		// 1.2 기본정보
		@NotNull
		private String domain;
		@NotNull
		private String description;
		@NotNull
		private String domainOrganization;
		
		// 1.3 프록시 정보
		@NotNull
		private String proxyStaticIps;
		@NotNull
		private String sslPemPub; //Big
		@NotNull
		private String sslPemRsa; //Big
	}
	
	@Data
	public static class Uaa{
		@NotNull
		private String id;
		@NotNull
		private String loginSecret;
		@NotNull
		private String signingKey;
		@NotNull
		private String verificationKey;
	}
	
	@Data
	public static class Consul{
		@NotNull
		private String id;
		@NotNull
		private String agentCert;
		@NotNull
		private String agentKey;
		@NotNull
		private String caCert;
		@NotNull
		private String encryptKeys;
		@NotNull
		private String serverCert;
		@NotNull
		private String serverKey;
	}
	
	@Data
	public static class AwsNetwork{
		@NotNull
		private String id;
		@NotNull
		private String subnetRange;
		@NotNull
		private String subnetGateway;
		@NotNull
		private String subnetDns;
		@NotNull
		private String subnetReservedFrom;
		@NotNull
		private String subnetReservedTo;
		@NotNull
		private String subnetStaticFrom;
		@NotNull
		private String subnetStaticTo;
		@NotNull
		private String subnetId;
		@NotNull
		private String cloudSecurityGroups;
	}
	
	@Data
	public static class OpenstackNetwork{
		@NotNull
		private String id;
		@NotNull
		private String subnetRange;
		@NotNull
		private String subnetGateway;
		@NotNull
		private String subnetDns;
		@NotNull
		private String subnetReservedFrom;
		@NotNull
		private String subnetReservedTo;
		@NotNull
		private String subnetStaticFrom;
		@NotNull
		private String subnetStaticTo;
		@NotNull
		private String cloudNetId;
		@NotNull
		private String cloudSecurityGroups;
	}
	
	@Data
	public static class Resource{
		@NotNull
		private String id;
		// 5. 리소스 정보
		@NotNull
		private String stemcellName;
		@NotNull
		private String stemcellVersion;
		@NotNull
		private String boshPassword;
		// Deploy 정보
		private String deploymentFile;
		private String deployStatus;
		private String deployLog;
	}
		
	@Data
	public static class Deployment{
		@NotNull
		private String deploymentFile;
	}
	
	@Data
	public static class Delete{
		@NotNull
		private String iaas;
		@NotNull
		private String id;
	}
	
	@Data
	public static class Install{
		@NotNull
		private String iaas;
		@NotNull
		private String id;
	}
	
}