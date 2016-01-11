package org.openpaas.ieda.web.deploy.bootstrap;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity(name="IEDA_BOOTSTRAP_OPENSTACK")
@Data
public class IEDABootstrapOpenstackConfig {
	@Id @GeneratedValue
	private Integer id;
	
	@Temporal(TemporalType.DATE)
	private Date createdDate;
	
	@Temporal(TemporalType.DATE)
	private Date updatedDate;
	
	/** Openstack Info **/
	private String authUrl;
	private String tenant;
	private String userName;
	private String apiKey;
	private String defaultSecurityGroups;
	private String privateKeyName;
	private String privateKeyPath;

	/** Openstack Default Info **/
	private String deploymentName;
	private String directorName;
	private String boshRelease;
	private String boshCpiRelease;

	/** Network Info **/
	private String subnetId;
	private String privateStaticIp;
	private String publicStaticIp;
	private String subnetRange;
	private String subnetGateway;
	private String subnetDns;
	private String ntp;

	/** Resource Info **/
	private String stemcell;
	private String cloudInstanceType;
	private String boshPassword;
	
	private String deploymentFile;
	private String deployStatus;
	private String deployLog;
}