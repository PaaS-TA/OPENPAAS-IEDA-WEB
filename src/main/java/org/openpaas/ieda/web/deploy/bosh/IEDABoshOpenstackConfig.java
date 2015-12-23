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
	
	//openstack
	private String authUrl;
	private String tenant;
	private String userName;
	private String apiKey;
	private String privateKeyName;
	private String privateKeyPath;	
	private String defaultSecurityGroups;
	
	//bosh
	private String deploymentName;
	private String directorUuid;
	private String releaseVersion;
	
	//network
	private String publicStaticIp;
	private String subnetRange;
	private String subnetStaticFrom;
	private String subnetStaticTo;
	private String subnetGateway;
	private String subnetDns;
	private String subnetId;
	
	//resource
	private String stemcellName;
	private String stemcellVersion;
	private String cloudInstanceType;
	
	private String boshPassword;
	//deploy
	private String deploymentFile;
	private String deployStatus;

}
