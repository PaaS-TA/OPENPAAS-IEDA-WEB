package org.openpaas.ieda.web.deploy.cf;

import java.util.Date;

import lombok.Data;

@Data
public class CfListDto {

	private int recid;
	private int id;
	private String iaas;
	private Date createDate;
	private Date updateDate;
	
	private String deployStatus;
	private String deploymentName;
	
	private String releaseVersion;
	private String stemcellName;
	private String stemcellVersion;
	
	private String directorUuid;
	
	private String subnetRange;
	private String subnetGateway;
	private String subnetDns;
	
}
