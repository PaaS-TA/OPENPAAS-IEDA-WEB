package org.openpaas.ieda.web.config.bootstrap;

import javax.validation.constraints.NotNull;

import lombok.Data;

public class BootStrapDto{
	
	@Data
	public static class Aws{
		private String id;
		@NotNull
		private String iaas;
		@NotNull
		private String awsKey;
		@NotNull
		private String awsPw;
		@NotNull
		private String secretGroupName;
		@NotNull
		private String privateKeyName;
		@NotNull
		private String privateKeyPath;
	}
	
	@Data
	public static class AwsNetwork{
		@NotNull
		private String id;
		@NotNull
		private String subnetRange;
		@NotNull
		private String gateway;
		@NotNull
		private String dns;
		@NotNull
		private String subnetId;
		@NotNull
		private String directorPrivateIp;
		@NotNull
		private String directorPublicIp;
	}
	
	@Data
	public static class AwsResources{
		@NotNull
		private String id;
		@NotNull
		private String targetStemcell;
		@NotNull
		private String instanceType;
		@NotNull
		private String region;
		@NotNull
		private String availabilityZone;
		@NotNull
		private String microBoshPw;
	}
	
	@Data
	public static class Install{
		@NotNull
		private String deployFileName;
	}
	
	@Data
	public static class Delete{
		@NotNull
		private String iaas;
		@NotNull
		private int id;
	}
	
	@Data 
	public static class OpenStack{
		private int id;		
		private String privateStaticIp;
		private String publicStaticIp;
		private String directorName;
		private String authUrl;
		private String tenant;
		private String userName;
		private String apiKey;
		private String defaultKeyName;
		private String defaultSecurityGroup;
		private String ntp;
	}
	
	@Data 
	public static class OsBosh{
		private int id;
		private String boshName;
		private String boshUrl;
		private String boshCpiUrl;
		private String privateKeyPath;
	}
	
	@Data 
	public static class OsNetwork{
		private int id;
		private String subnetRange;
		private String subnetGateway;
		private String subnetDns;
		private String cloudNetId;
	}
	
	@Data 
	public static class OsResource{
		private int id;
		private String stemcellUrl;
		private String envPassword;
		private String cloudInstanceType;;
	}
}
