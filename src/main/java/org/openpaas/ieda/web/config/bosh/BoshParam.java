package org.openpaas.ieda.web.config.bosh;

import javax.validation.constraints.NotNull;

import lombok.Data;

public class BoshParam {

	@Data
	public static class AWS{
		private String id;
		private String iaas;
		private String accessKeyId;
		private String secretAccessKey;
		private String defaultKeyName;
		private String defaultSecurityGroups;
		private String region;
	}
	
	@Data
	public static class Bosh{
		@NotNull
		private int id;
		@NotNull
		private String iaas;
		@NotNull
		private String boshName;
		@NotNull
		private String directorUuid;
		@NotNull
		private String releaseVersion;
	}
	
	@Data
	public static class NetWork{
		@NotNull
		private int id;
		@NotNull
		private String iaas;
		@NotNull
		private String subnetReserved;
		@NotNull
		private String subnetStatic;
		@NotNull
		private String publicStaticIps;
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
	public static class Resource{
		@NotNull
		private int id;
		@NotNull
		private String iaas;
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
}