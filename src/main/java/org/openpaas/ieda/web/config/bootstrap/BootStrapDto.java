package org.openpaas.ieda.web.config.bootstrap;

import javax.validation.constraints.NotNull;

import lombok.Data;

public class BootStrapDto{
	
	@Data
	public static class Aws{
		private String id;
		@NotNull 
		private String accessKeyId;
		@NotNull 
		private String secretAccessId;
		@NotNull 
		private String defaultSecurityGroups;
		@NotNull 
		private String region;
		@NotNull
		private String availabilityZone;
		@NotNull
		private String privateKeyName;
		@NotNull
		private String privateKeyPath;

	}
	
	@Data
	public static class AwsDefault{
		@NotNull
		private String id;
		@NotNull
		private String deploymentName;
		@NotNull
		private String directorName;
		@NotNull
		private String boshRelease;
		@NotNull
		private String boshCpiRelease;

	}
	
	@Data
	public static class AwsNetwork{
		@NotNull
		private String id;
		@NotNull
		private String subnetId;
		@NotNull
		private String privateStaticIp;
		@NotNull
		private String publicStaticIp;
		@NotNull
		private String subnetRange;
		@NotNull
		private String subnetGateway;
		@NotNull
		private String subnetDns;
		@NotNull
		private String ntp;
	}
	
	@Data
	public static class AwsResource{
		@NotNull
		private String id;
		@NotNull
		private String stemcell;
		@NotNull
		private String cloudInstanceType;
		@NotNull
		private String boshPassword;
	}
	
	@Data 
	public static class OpenStack{
		private String id;
		@NotNull
		private String authUrl;
		@NotNull
		private String tenant;
		@NotNull
		private String userName;
		@NotNull
		private String apiKey;
		@NotNull
		private String defaultSecurityGroup;
		@NotNull
		private String defaultKeyName;
		@NotNull
		private String cloudPrivateKey;
	}
	
	@Data 
	public static class OpenstackDefault{
		@NotNull
		private String id;
		@NotNull
		private String deploymentName;
		@NotNull
		private String directorName;
		@NotNull
		private String boshRelease;
		@NotNull
		private String boshCpiRelease;
	}
	
	@Data 
	public static class OpenstackNetwork{
		@NotNull
		private String id;
		@NotNull
		private String subnetId;
		@NotNull
		private String privateStaticIp;
		@NotNull
		private String publicStaticIp;
		@NotNull
		private String subnetRange;
		@NotNull
		private String subnetGateway;
		@NotNull
		private String subnetDns;
		@NotNull
		private String ntp;
	}
	
	@Data 
	public static class OpenstackResource{
		@NotNull
		private String id;
		@NotNull
		private String stemcell;
		@NotNull
		private String cloudInstanceType;
		@NotNull
		private String boshPassword;
	}
	
	@Data
	public static class Install{
		@NotNull
		private String iaas;
		@NotNull
		private String id;
	}
	
	@Data
	public static class Delete{
		@NotNull
		private String id;
		@NotNull
		private String iaas;
	}
	
	@Data
	public static class Deployment{
		@NotNull
		private String deploymentFile;
	}
}