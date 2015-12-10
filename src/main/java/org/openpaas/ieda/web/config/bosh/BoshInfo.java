package org.openpaas.ieda.web.config.bosh;

import java.util.Date;

import lombok.Data;

@Data
public class BoshInfo {

	private int recid;
	private int id;
	private String iaas;
	private String publicStaticIps;
	private String subnetRange;
	private Date createdDate;
	private Date updatedDate;
}
