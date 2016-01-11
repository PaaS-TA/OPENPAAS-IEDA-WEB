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
	
	//OPENSTACK
	private String authUrl;
	private String tenant;
	private String userName;
	private String apiKey;
	private String privateKeyName;
	private String privateKeyPath;	
	private String defaultSecurityGroups;
	
	//BOSH
	private String deploymentName;
	private String directorUuid;
	private String releaseVersion;
	
	//NETWORK
	private String publicStaticIp;
	private String subnetRange;
	private String subnetStaticFrom;
	private String subnetStaticTo;
	private String subnetGateway;
	private String subnetDns;
	private String subnetId;
	
	//RESOURCE
	private String stemcellName;
	private String stemcellVersion;
	private String cloudInstanceType;
	
	private String boshPassword;
	//DEPLOY
	private String deploymentFile;
	private String deployStatus;
	private String deployLog;

}
