package org.openpaas.ieda.web.deploy.bosh;

import javax.validation.constraints.NotNull;

import lombok.Data;

public class BoshParam {

	@Data
	public static class AWS{
		private String id;
		private String accessKeyId;
		private String secretAccessKey;
		private String privateKeyName;
		private String defaultSecurityGroups;
		private String region;	
		@NotNull
		private String privateKeyPath;
		
	}
	
	@Data
	public static class AwsBosh{
		@NotNull
		private String id;
		@NotNull
		private String deploymentName;
		@NotNull
		private String directorUuid;
		@NotNull
		private String releaseVersion;
	}
	
	@Data
	public static class AwsNetwork{
		@NotNull
		private String id;
		@NotNull
		private String publicStaticIp;
		@NotNull
		private String subnetStaticFrom;
		@NotNull
		private String subnetStaticTo;
		@NotNull
		private String subnetRange;
		@NotNull
		private String subnetGateway;
		@NotNull
		private String subnetDns;
		@NotNull
		private String subnetId;
	}
	
	@Data
	public static class AwsResource{
		@NotNull
		private String id;
		@NotNull
		private String stemcellName;
		@NotNull
		private String stemcellVersion;
		@NotNull
		private String cloudInstanceType;
		@NotNull
		private String boshPassword;
	}
	
	@Data
	public static class Delete{
		@NotNull
		private String iaas;
		@NotNull
		private String id;
	}
	
	@Data
	public static class Openstack{
		private String id;
		private String authUrl;
		private String tenant;
		private String userName;
		private String apiKey;
		private String defaultSecurityGroups;
		private String privateKeyName;
		private String privateKeyPath;		
	}
	
	@Data
	public static class OpenstackBosh{
		private String id;
		@NotNull
		private String deploymentName;
		@NotNull
		private String directorUuid;
		@NotNull
		private String releaseVersion;
	}
	
	@Data
	public static class OpenstackNetwork{
		@NotNull
		private String id;
		@NotNull
		private String publicStaticIp;
		@NotNull
		private String subnetId;
		@NotNull
		private String subnetStaticFrom;
		@NotNull
		private String subnetStaticTo;
		@NotNull
		private String subnetRange;
		@NotNull
		private String subnetGateway;
		@NotNull
		private String subnetDns;
	}
	
	@Data
	public static class OpenstackResource{
		private String id;
		private String stemcellName;
		private String stemcellVersion;
		private String cloudInstanceType;
		private String boshPassword;	
	}
	
	@Data
	public static class Deployment{
		@NotNull
		private String deploymentFile;
	}
	
	@Data
	public static class Install{
		@NotNull
		private String iaas;
		@NotNull
		private String id;
	}
}