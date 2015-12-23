package org.openpaas.ieda.web.deploy.bosh;

import java.util.Date;

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
	
	private String accessKeyId;
	private String secretAccessKey;
	private String privateKeyName;
	private String defaultSecurityGroups;
	private String region;
	private String privateKeyPath;
	private String deploymentName;
	private String directorUuid;
	private String publicStaticIp;
	private String releaseVersion;
	private String subnetStaticFrom;
	private String subnetStaticTo;
	private String subnetRange;
	private String subnetGateway;
	private String subnetDns;
	private String subnetId;
	private String cloudInstanceType;
	private String stemcellName;
	private String stemcellVersion;
	private String boshPassword;
	private String deploymentFile;
	private String deployStatus;
}
