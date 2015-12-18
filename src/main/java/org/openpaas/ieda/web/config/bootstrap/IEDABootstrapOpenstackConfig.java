package org.openpaas.ieda.web.config.bootstrap;

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
	
	/** Openstack Bosh Info **/
	private String boshName;
	private String boshUrl;
	private String boshCpiUrl;
	private String cloudPrivateKey;

	/** Openstack Info **/
	private String privateStaticIp;
	private String publicStaticIp;
	private String directorName;
	private String authUrl;
	private String tenant;
	private String userName;
	private String apiKey;
	private String defaultKeyName;
	private String defaultSecurityGroup;
	private String ntp;

	/** Network Info **/
	private String subnetRange;
	private String subnetGateway;
	private String subnetDns;
	private String cloudNetId;

	/** Resource Info **/
	private String stemcellUrl;
	private String envPassword;
	private String cloudInstanceType;

	private String deploymentFile;
}
