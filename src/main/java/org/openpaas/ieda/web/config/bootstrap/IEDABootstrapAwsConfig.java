package org.openpaas.ieda.web.config.bootstrap;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity(name="IEDA_BOOTSTRAP_AWS")
@Data
public class IEDABootstrapAwsConfig {
	@Id @GeneratedValue
	private Integer id;
	
	@Temporal(TemporalType.DATE)
	private Date createdDate;
	
	@Temporal(TemporalType.DATE)
	private Date updatedDate;
	
	/** AWS Setting Info **/
	private String accessKeyId;
	private String secretAccessId;
	private String defaultSecurityGroups;
	private String region;
	private String availabilityZone;
	private String privateKeyName;
	private String privateKeyPath;
	
	/** AWS Default Info **/
	private String deploymentName;
	private String directorName;
	private String boshRelease;
	private String boshCpiRelease;
	
	/** Network Info **/
	private String subnetId;
	private String privateStaticIp;
	private String publicStaticIp;
	private String subnetRangeFrom;
	private String subnetRangeTo;
	private String subnetGateway;
	private String subnetDns;
	private String ntp;
	
	/** Resource Info **/
	private String stemcell;
	private String cloudInstanceType;
	private String boshPassword;
	
	private String deploymentFile;

}