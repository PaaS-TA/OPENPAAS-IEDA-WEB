package org.openpaas.ieda.web.config.bootstrap;

import java.util.Date;

import lombok.Data;

@Data
public class BootstrapListDto {

	private Integer recid;
	private Integer id;
	private String iaas;
	
	private String deploymentName;
	private String directorName;
	private String boshRelease;
	private String boshCpiRelease;
	private String subnetId;
	private String privateStaticIp;
	private String publicStaticIp;
	
	private Date createdDate;
	private Date updatedDate;
}