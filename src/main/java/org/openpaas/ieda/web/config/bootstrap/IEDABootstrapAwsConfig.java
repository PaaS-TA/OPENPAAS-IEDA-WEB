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
	private String accessKey;
	
	private String secretAccessKey;
	
	private String defaultKeyName;
	
	private String defaultSecurityGroups;
	
	private String privateKeyPath;
	
	/** Network Info **/
	private String subnetRange;
	
	private String gateway;
	
	private String dns;
	
	private String subnetId;
	
	private String directorPrivateIp;
	
	private String directorPublicIp;
	
	private String stemcellName;
	
	private String stemcellVersion;
	
	private String instanceType;
	
	private String region;
	
	private String availabilityZone;
	
	private String microBoshPw;
	
	private String deploymentFile;

}
