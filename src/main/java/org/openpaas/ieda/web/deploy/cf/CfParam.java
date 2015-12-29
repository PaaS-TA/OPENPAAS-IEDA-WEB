package org.openpaas.ieda.web.deploy.cf;

import javax.validation.constraints.NotNull;

import lombok.Data;

public class CfParam {

	@Data
	public static class Cf{
		private String id;
		@NotNull
		private String deploymentName;
		@NotNull
		private String directorUuid;
		@NotNull
		private String releaseVersion;
		@NotNull
		private String domain;
		@NotNull
		private String sslPem;
	}
	
	@Data
	public static class Network{
		@NotNull
		private String id;
		@NotNull
		private String subnetRange;
		@NotNull
		private String subnetGateway;
		@NotNull
		private String subnetDns;
		@NotNull
		private String subnetReserved;
		@NotNull
		private String subnetStatic;
		@NotNull
		private String cloudSubnet;
	}
	
	@Data
	public static class Resource{
		@NotNull
		private String id;
		@NotNull
		private String stemcellName;
		@NotNull
		private String stemcellVersion;
		@NotNull
		private String boshPassword;
		@NotNull
		private String availabilityZone;
		@NotNull
		private String instanceType;
		@NotNull
		private String deploymentFile;
		@NotNull
		private String deployStatus;
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
