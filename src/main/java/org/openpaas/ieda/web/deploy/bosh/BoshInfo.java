package org.openpaas.ieda.web.deploy.bosh;

import java.util.Date;

import lombok.Data;

@Data
public class BoshInfo {

	private int recid;
	private int id;
	private String iaas;
	private Date createdDate;
	
	// BOSH
	private String directorUuid;
	private String deploymentName;
	private String releaseVersion;

	// NETWORK
	private String publicStaticIp;
	private String subnetRange;
	private String subnetStaticFrom;
	private String subnetStaticTo;
	private String subnetGateway;
	private String subnetDns;
	private String subnetId;

	private String stemcellName;
	private String stemcellVersion;
	private String cloudInstanceType;

	//DEPLOY
	private String deploymentFile;
	private String deployStatus;
	private String deployLog;
}
