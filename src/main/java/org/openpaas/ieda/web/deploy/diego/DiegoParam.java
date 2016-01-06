package org.openpaas.ieda.web.deploy.diego;

import javax.validation.constraints.NotNull;

import lombok.Data;

public class DiegoParam {

	@Data
	public static class Default{
		private String id;
		private String iaas;
		
		//1.1 기본정보	
		@NotNull
		private String deploymentName;
		@NotNull
		private String directorUuid;
		@NotNull
		private String diegoReleaseName;
		@NotNull
		private String diegoReleaseVersion;
		@NotNull
		private String cfReleaseName;
		@NotNull
		private String cfReleaseVersion;
		@NotNull
		private String gardenLinuxReleaseName;
		@NotNull
		private String gardenLinuxReleaseVersion;
		@NotNull
		private String etcdReleaseName;
		@NotNull
		private String etcdReleaseVersion;
		//1.2 CF 정보
		@NotNull
		private String domain;
		@NotNull
		private String deployment;
		@NotNull
		private String secret;
		@NotNull
		private String etcdMachines;
		@NotNull
		private String natsMachines;
		@NotNull
		private String consulServersLan;
		@NotNull
		private String consulAgentCert;
		@NotNull
		private String consulAgentKey;
		@NotNull
		private String consulCaCert;
		@NotNull
		private String consulEncryptKeys;
		@NotNull
		private String consulServerCert;
		@NotNull
		private String consulServerKey;
	}
	
	@Data
	public static class Diego {
		@NotNull
		private String id;
		private String iaas;
		//2.1 Diego 정보	
		@NotNull
		private String diegoCaCert;
		@NotNull
		private String diegoClientCert;
		@NotNull
		private String diegoClientKey;
		@NotNull
		private String diegoEncryptionKeys;
		@NotNull
		private String diegoServerCert;
		@NotNull
		private String diegoServerKey;
		//2.2 ETCD 정보	
		@NotNull
		private String etcdClientCert;
		@NotNull
		private String etcdClientKey;
		@NotNull
		private String etcdPeerCaCert;
		@NotNull
		private String etcdPeerCert;
		@NotNull
		private String etcdPeerKey;
		@NotNull
		private String etcdServerCert;
		@NotNull
		private String etcdServerKey;		
	}
	
	@Data
	public static class AwsNetwork {
		@NotNull
		private String id;
		private String iaas;
		//3.1 네트워크 정보	
		@NotNull
		private String subnetStaticFrom;
		@NotNull
		private String subnetStaticTo;
		@NotNull
		private String subnetReservedFrom;
		@NotNull
		private String subnetReservedTo;
		@NotNull
		private String subnetRange;
		@NotNull
		private String subnetGateway;
		@NotNull
		private String subnetDns;
		@NotNull
		private String subnetId;
		@NotNull
		private String cloudSecurityGroups;	
		//3.2 프록시 정보
		@NotNull
		private String diegoHostKey;
		@NotNull
		private String diegoServers;
		@NotNull
		private String diegoUaaSecret;
	}
	
	@Data
	public static class OpenstackNetwork {
		@NotNull
		private String id;
		private String iaas;
		//3.1 네트워크 정보	
		@NotNull
		private String subnetStaticFrom;
		@NotNull
		private String subnetStaticTo;
		@NotNull
		private String subnetReservedFrom;
		@NotNull
		private String subnetReservedTo;
		@NotNull
		private String subnetRange;
		@NotNull
		private String subnetGateway;
		@NotNull
		private String subnetDns;
		@NotNull
		private String cloudNetId;
		@NotNull
		private String cloudSecurityGroups;	
		//3.2 프록시 정보
		@NotNull
		private String diegoHostKey;
		@NotNull
		private String diegoServers;
		@NotNull
		private String diegoUaaSecret;
	}
	
	@Data
	public static class Resource {
		@NotNull
		private String id;
		private String iaas;
		//4 리소스 정보	
		@NotNull
		private String boshPassword;
		@NotNull
		private String stemcellName;
		@NotNull
		private String stemcellVersion;

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
