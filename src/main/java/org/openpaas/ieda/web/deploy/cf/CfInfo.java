package org.openpaas.ieda.web.deploy.cf;

import java.util.Date;

import lombok.Data;

@Data
public class CfInfo {

	private int recid;
	private int id;
	private String iaas;
	private Date createDate;
	private Date updateDate;

	// 1.1 Deployment 정보
	private String deploymentName;
	private String directorUuid;
	private String releaseName;
	private String releaseVersion;
	private String appSshFingerprint;

	// 1.2 기본정보
	private String domain;
	private String description;
	private String domainOrganization;

	// 1.3 HA프록시 정보
	private String proxyStaticIps;

	// 4. 네트워크 정보
	private String subnetRange;
	private String subnetGateway;
	private String subnetDns;
	private String subnetReservedFrom;
	private String subnetReservedTo;
	private String subnetStaticFrom;
	private String subnetStaticTo;
	private String subnetId;
	private String cloudSecurityGroups;

	// 5. 리소스 정보
	private String stemcellName;
	private String stemcellVersion;
	private String boshPassword;
	// Deploy 정보
	private String deploymentFile;
	private String deployStatus;
	private String deployLog;

}