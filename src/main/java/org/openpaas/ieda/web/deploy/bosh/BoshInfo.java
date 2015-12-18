package org.openpaas.ieda.web.deploy.bosh;

import java.util.Date;

import lombok.Data;

@Data
public class BoshInfo {

	private int recid;
	private int id;
	private String iaas;
	private String boshName;
	//private String publicStaticIps;
	private String subnetRange;
	private Date createdDate;
	private Date updatedDate;
}
