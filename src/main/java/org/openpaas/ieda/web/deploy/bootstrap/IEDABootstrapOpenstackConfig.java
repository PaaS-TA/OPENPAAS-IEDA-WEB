package org.openpaas.ieda.web.deploy.bootstrap;

import java.util.Date;

import javax.persistence.Column;
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
	@Column(length = 100)
	private String authUrl;
	@Column(length = 100)
	private String tenant;
	@Column(length = 100)
	private String userName;
	@Column(length = 100)
	private String apiKey;
	@Column(length = 100)
	private String defaultSecurityGroups;
	@Column(length = 100)
	private String privateKeyName;
	@Column(length = 100)
	private String privateKeyPath;

	/** Openstack Default Info **/
	@Column(length = 100)
	private String deploymentName;
	@Column(length = 100)
	private String directorName;
	@Column(length = 100)
	private String boshRelease;
	@Column(length = 100)
	private String boshCpiRelease;

	/** Network Info **/
	@Column(length = 100)
	private String subnetId;
	@Column(length = 100)
	private String privateStaticIp;
	@Column(length = 100)
	private String publicStaticIp;
	@Column(length = 100)
	private String subnetRange;
	@Column(length = 100)
	private String subnetGateway;
	@Column(length = 100)
	private String subnetDns;
	@Column(length = 100)
	private String ntp;

	/** Resource Info **/
	@Column(length = 100)
	private String stemcell;
	@Column(length = 100)
	private String cloudInstanceType;
	@Column(length = 200)
	private String boshPassword;
	
	@Column(length = 100)
	private String deploymentFile;
	@Column(length = 100)
	private String deployStatus;
	
	@Column(length = 100000)
	private String deployLog;
}