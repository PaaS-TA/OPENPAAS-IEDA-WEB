package org.openpaas.ieda.web.deploy.release;

import lombok.Data;

/**
 * Boot
 * @author jspark81@cloud4u.co.kr
 *
 */
public class BootStrapSettingData {

	@Data
	public static class Aws{
		private String awsKey;
		private String awsPw;
		private String securGroupName;
		private String privateKeyName;
		private String privateKeyPath;
	}
	
	@Data
	public static class Network{
		private String key;
		private String subnetRange;
		private String gateway;
		private String dns;
		private String subnetId;
		private String directorPrivateIp;
		private String directorPublicIp;
	}
	
	@Data
	public static class Stemcell{
		private String key;
		private String targetStemcell;
	}
	
	@Data
	public static class Resources{
		private String key;
		private String targetStemcell;
		private String instanceType;
		private String availabilityZone;
		private String microBoshPw;
	}
}
