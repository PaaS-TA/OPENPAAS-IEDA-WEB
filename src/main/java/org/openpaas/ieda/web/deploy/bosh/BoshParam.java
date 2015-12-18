package org.openpaas.ieda.web.deploy.bosh;

import javax.validation.constraints.NotNull;

import lombok.Data;

public class BoshParam {

	@Data
	public static class AWS{
		private String id;
		private String dnsRecursor;
		private String accessKeyId;
		private String secretAccessKey;
		private String defaultKeyName;
		private String defaultSecurityGroups;
		private String region;		
		private String privateKeyPath;
	}
	
	@Data
	public static class AwsBosh{
		@NotNull
		private String id;
		@NotNull
		private String boshName;
		@NotNull
		private String directorUuid;
		@NotNull
		private String releaseVersion;
	}
	
	@Data
	public static class AwsNetwork{
		@NotNull
		private int id;
		@NotNull
		private String subnetStatic;
		@NotNull
		private String subnetRange;
		@NotNull
		private String subnetGateway;
		@NotNull
		private String subnetDns;
		@NotNull
		private String cloudSubnet;
		@NotNull
		private String cloudSecurityGroups;		
	}
	
	@Data
	public static class AwsResource{
		@NotNull
		private int id;
		@NotNull
		private String stemcellName;
		@NotNull
		private String stemcellVersion;
		@NotNull
		private String boshPassword;
	}
	
	@Data
	public static class Delete{
		@NotNull
		private String iaas;
		@NotNull
		private int id;
	}
	
	@Data
	public static class OsBosh{
		private String id;
		private String boshName;
		private String directorUuid;
		private String releaseVersion;
	}
	
	@Data
	public static class Openstack{
		private String id;
		private String directorName;
		private String directorStaticIps;
		private String dnsRecursor;
		private String authUrl;
		private String tenant;
		private String userName;
		private String apiKey;
		private String defaultKeyName;
		private String defaultSecurityGroups;
		private String ntp;		
	}
	
	@Data
	public static class OsNetwork{
		private String id;
		private String subnetStatic;
		private String subnetRange;
		private String subnetGateway;
		private String subnetDns; 
		private String cloudNetId;
	}
	
	@Data
	public static class OsResource{
		private String id;
		private String stemcellName;
		private String stemcellVersion;
		private String cloudInstanceType;
		private String boshPassword;	
	}
	
}