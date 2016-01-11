package org.openpaas.ieda.web.deploy.diego;

import java.util.Date;

import lombok.Data;

@Data
public class DiegoInfo {

	private int recid;
	private int id;
	private String iaas;
	private Date createDate;
	private Date updateDate;

	//1.1 기본정보	
	private String deploymentName;
	private String directorUuid;
	private String diegoReleaseName;
	private String diegoReleaseVersion;
	private String cfReleaseName;
	private String cfReleaseVersion;
	private String gardenLinuxReleaseName;
	private String gardenLinuxReleaseVersion;
	private String etcdReleaseName;
	private String etcdReleaseVersion;
	//1.2 CF 정보	
	private String domain;
	private String deployment;
	private String etcdMachines;
	private String natsMachines;
	private String consulServersLan;

	//3.1 네트워크 정보	
	private String subnetStaticFrom;
	private String subnetStaticTo;
	private String subnetReservedFrom;
	private String subnetReservedTo;
	private String subnetRange;
	private String subnetGateway;
	private String subnetDns;
	private String subnetId;
	private String cloudSecurityGroups;	
	//3.2 프록시 정보
	private String diegoServers;

	//4 리소스 정보	
	private String stemcellName;
	private String stemcellVersion;

	// Deploy 정보
	private String deploymentFile;
	private String deployStatus;
	private String deployLog;	
}
