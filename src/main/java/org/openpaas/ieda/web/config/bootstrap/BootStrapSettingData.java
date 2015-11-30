package org.openpaas.ieda.web.config.bootstrap;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Boot
 * @author jspark81@cloud4u.co.kr
 *
 */
public class BootStrapSettingData {

	@Data
	public static class Aws{
		@NotNull
		private String awsKey;
		@NotNull
		private String awsPw;
		@NotNull
		private String securGroupName;
		@NotNull
		private String privateKeyName;
		@NotNull
		private String privateKeyPath;
	}
	
	@Data
	public static class Network{
		@NotNull
		private String key;
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
		private String key;
		@NotNull
		private String targetStemcell;
		@NotNull
		private String instanceType;
		@NotNull
		private String availabilityZone;
		@NotNull
		private String microBoshPw;
	}
}
