package org.openpaas.ieda.web.deploy.bosh;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity(name="IEDA_BOSH_OPENSTACK")
@Data
public class IEDABoshOpenstackConfig {

	@Id @GeneratedValue
	private Integer id;
	
	@Temporal(TemporalType.DATE)
	private Date createdDate;
	
	@Temporal(TemporalType.DATE)
	private Date updatedDate;
	
	//bosh
	private String boshName;
	private String directorUuid;
	private String releaseVersion;
	//network
	private String subnetStatic;
	private String subnetRange;
	private String subnetGateway;
	private String subnetDns;
	private String cloudNetId;
	private String cloudSecurityGroups;
	private String cloudSubnet;
	//resource
	private String stemcellName;
	private String stemcellVersion;
	private String cloudInstanceType;
	
	private String boshPassword;
	//opensrack
	private String directorName;
	private String directorStaticIp;
	private String dnsRecursor;
	private String authUrl;
	private String tenant;
	private String userName;
	private String apiKey;
	private String defaultKeyName;
	private String defaultSecurityGroups;
	private String ntp;
	private String directorRecursor;
	private String privateKeyPath;
	//deploy
	private String deploymentFile;

}
