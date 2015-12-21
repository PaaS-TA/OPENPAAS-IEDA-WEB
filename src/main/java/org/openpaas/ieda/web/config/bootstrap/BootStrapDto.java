package org.openpaas.ieda.web.config.bootstrap;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

public class BootStrapDto{
	
	@Data
	public static class Aws{
		private String id;
		@NotNull @NotEmpty
		private String iaas;
		@NotNull @NotEmpty
		private String awsKey;
		@NotNull @NotEmpty
		private String awsPw;
		@NotNull @NotEmpty
		private String defaultSecurityGroups;
		@NotNull @NotEmpty
		private String privateKeyName;
		@NotNull @NotEmpty
		private String privateKeyPath;
	}
	
	@Data
	public static class Network{
		@NotNull @NotEmpty
		private String id;
		@NotNull @NotEmpty
		private String subnetRange;
		@NotNull @NotEmpty
		private String gateway;
		@NotNull @NotEmpty
		private String dns;
		@NotNull @NotEmpty
		private String subnetId;
		@NotNull @NotEmpty
		private String directorPrivateIp;
		@NotNull @NotEmpty
		private String directorPublicIp;
	}
	
	@Data
	public static class Resources{
		@NotNull @NotEmpty
		private String id;
		@NotNull @NotEmpty
		private String targetStemcell;
		@NotNull @NotEmpty
		private String instanceType;
		@NotNull @NotEmpty
		private String region;
		@NotNull @NotEmpty
		private String availabilityZone;
		@NotNull @NotEmpty
		private String microBoshPw;
		@NotNull @NotEmpty
		private String ntp;
	}
	
	@Data
	public static class Install{
		@NotNull @NotEmpty
		private String deployFileName;
	}
	
	@Data
	public static class Delete{
		@NotNull @NotEmpty
		private String id;
		@NotNull @NotEmpty
		private String iaas;
	}
	
	@Data 
	public static class OpenStack{
		@NotNull @NotEmpty
		private String id;		
		private String privateStaticIp;
		private String publicStaticIp;
		private String directorName;
		private String authUrl;
		private String tenant;
		private String userName;
		private String apiKey;
		private String defaultKeyName;
		private String defaultSecurityGroups;
		private String ntp;
	}
	
	@Data 
	public static class OsBosh{
		private String id;
		private String boshName;
		private String boshUrl;
		private String boshCpiUrl;
		private String privateKeyPath;
	}
	
	@Data 
	public static class OsNetwork{
		@NotNull @NotEmpty
		private String id;
		private String subnetRange;
		private String subnetGateway;
		private String subnetDns;
		private String cloudNetId;
	}
	
	@Data 
	public static class OsResource{
		@NotNull @NotEmpty
		private String id;
		private String stemcellUrl;
		private String envPassword;
		private String cloudInstanceType;;
	}
	
	@Data
	public static class Deployment{
		@NotNull @NotEmpty
		private String deploymentFile;
	}
}
