package org.openpaas.ieda.web.deploy.bosh;

import java.util.Date;

import lombok.Data;

@Data
public class BoshInfo {

	private int recid;
	private int id;
	private String directorUuid;
	private String deploymentName;
	private String iaas;
	private String releaseVersion;
	private String stemcell;
	private String publicIp;
	private String subnetRange;
	private String gateway;
	private String dns;
	private String deployStatus;
	private Date createdDate;
}
