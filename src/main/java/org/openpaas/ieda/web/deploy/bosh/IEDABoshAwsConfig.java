package org.openpaas.ieda.web.deploy.bosh;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity(name="IEDA_BOSH_AWS")
@Data
public class IEDABoshAwsConfig {

	@Id @GeneratedValue
	private Integer id;
	
	@Temporal(TemporalType.DATE)
	private Date createdDate;
	
	@Temporal(TemporalType.DATE)
	private Date updatedDate;
	
	// AWS
	@Column(length = 100)
	private String accessKeyId;
	@Column(length = 100)
	private String secretAccessKey;
	@Column(length = 100)
	private String region;
	@Column(length = 100)
	private String defaultSecurityGroups;
	@Column(length = 100)
	private String privateKeyName;
	@Column(length = 100)
	private String privateKeyPath;
	
	// BOSH
	@Column(length = 100)
	private String deploymentName;
	@Column(length = 100)
	private String directorUuid;
	@Column(length = 100)
	private String releaseVersion;
	
	// NETWORK
	@Column(length = 100)
	private String publicStaticIp;
	@Column(length = 100)
	private String subnetRange;
	@Column(length = 100)
	private String subnetStaticFrom;
	@Column(length = 100)
	private String subnetStaticTo;
	@Column(length = 100)
	private String subnetGateway;
	@Column(length = 100)
	private String subnetDns;
	@Column(length = 100)
	private String subnetId;
	
	// RESOURCE
	@Column(length = 100)
	private String stemcellName;
	@Column(length = 100)
	private String stemcellVersion;
	@Column(length = 100)
	private String cloudInstanceType;
	
	@Column(length = 200)
	private String boshPassword;
	
	@Column(length = 100)
	private String deploymentFile;
	@Column(length = 100)
	private String deployStatus;
	
	@Column(columnDefinition = "TEXT")
	private String deployLog;
}
