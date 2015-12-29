package org.openpaas.ieda.web.deploy.cf;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;


@Entity(name="IEDA_CF_OPENSTACK")
@Data
public class IEDACfOpenstackConfig {
	@Id @GeneratedValue
	private Integer id;
	
	@Temporal(TemporalType.DATE)
	private Date createdDate;
	
	@Temporal(TemporalType.DATE)
	private Date updatedDate;
	
	private String deploymentName;
	private String directorUuid;
	private String releaseVersion;
	private String domain;
	private String sslPem;
	// NETWORK
	private String subnetRange;
	private String subnetGateway;
	private String subnetDns;
	private String subnetReserved;
	private String subnetStatic;
	private String cloudSubnet;

	// RESOURCE
	private String stemcellName;
	private String stemcellVersion;
	private String boshPassword;
	private String availabilityZone;
	private String instanceType;

	private String deploymentFile;
	private String deployStatus;


}
