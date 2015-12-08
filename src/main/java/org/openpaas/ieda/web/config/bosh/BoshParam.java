package org.openpaas.ieda.web.config.bosh;

import lombok.Data;

public class BoshParam {

	@Data
	public static class AWS{
		private String accessKeyId;
		private String secretAccesskey;
		private String defaultKqyName;
		private String defaultSecurityGroups;
		private String resion;
	}
	
	@Data
	public static class Bosh{
		private String boshName;
		private String directorUuid;
		private String releaseVersion;
	}
	
	@Data
	public static class NetWork{
		private String subnetReserved;
		private String subnetStatic;
		private String publicStaticIps;
		private String subnetRange;
		private String subnetGateWay;
		private String subnetDns;
		private String cloudSubnet;
		private String cloudSecurityGroups;		
	}
	
	@Data
	public static class Resource{
		private String stemcellName;
		private String stemcellVersion;
		private String cloudInstanceType;
	}
}
