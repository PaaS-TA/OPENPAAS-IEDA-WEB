package org.openpaas.ieda.web.config.bootstrap;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Boot
 * @author jspark81@cloud4u.co.kr
 *
 */
public class IDEABootStrapInfoDto{
	
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
	public static class Network{
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
	public static class Resources{
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
}
