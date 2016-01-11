package org.openpaas.ieda.web.deploy.bootstrap;

import java.util.Date;

import lombok.Data;

@Data
public class BootstrapListDto {

	private Integer recid;
	private Integer id;
	
	private String deployStatus;
	private String deploymentName;
	private String directorName;
	private String iaas;
	private String boshRelease;
	private String boshCpiRelease;
	private String subnetId;
	private String subnetRange;
	private String publicStaticIp;
	private String privateStaticIp;
	private String subnetGateway;
	private String subnetDns;
	private String ntp;
	private String stemcell;
	private String instanceType;
	private String boshPassword;
	private String deploymentFile;
	private String deployLog;
	
	private Date createdDate;
	private Date updatedDate;
}