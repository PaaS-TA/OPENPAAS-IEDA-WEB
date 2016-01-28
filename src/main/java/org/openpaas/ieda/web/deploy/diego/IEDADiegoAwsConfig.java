package org.openpaas.ieda.web.deploy.diego;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity(name="IEDA_DIEGO_AWS")
@Data
public class IEDADiegoAwsConfig {
	@Id @GeneratedValue
	private Integer id;
	
	@Temporal(TemporalType.DATE)
	private Date createdDate;
	
	@Temporal(TemporalType.DATE)
	private Date updatedDate;
	
	//1.1 기본정보	
	@Column(length = 100)
	private String deploymentName;
	@Column(length = 100)
	private String directorUuid;
	@Column(length = 100)
	private String diegoReleaseName;
	@Column(length = 100)
	private String diegoReleaseVersion;
	@Column(length = 100)
	private String cfReleaseName;
	@Column(length = 100)
	private String cfReleaseVersion;
	@Column(length = 100)
	private String gardenLinuxReleaseName;
	@Column(length = 100)
	private String gardenLinuxReleaseVersion;
	@Column(length = 100)
	private String etcdReleaseName;
	@Column(length = 100)
	private String etcdReleaseVersion;
	//1.2 CF 정보
	@Column(length = 100)
	private String domain;
	@Column(length = 100)
	private String deployment;
	@Column(length = 100)
	private String secret;
	@Column(length = 100)
	private String etcdMachines;
	@Column(length = 100)
	private String natsMachines;
	@Column(length = 100)
	private String consulServersLan;
	@Column(columnDefinition = "TEXT")
	private String consulAgentCert;
	@Column(columnDefinition = "TEXT")
	private String consulAgentKey;
	@Column(columnDefinition = "TEXT")
	private String consulCaCert;
	@Column(length = 200)
	private String consulEncryptKeys;
	@Column(columnDefinition = "TEXT")
	private String consulServerCert;
	@Column(columnDefinition = "TEXT")
	private String consulServerKey;
	
	//2.1 Diego 정보	
	@Column(columnDefinition = "TEXT")
	private String diegoCaCert;
	@Column(columnDefinition = "TEXT")
	private String diegoClientCert;
	@Column(columnDefinition = "TEXT")
	private String diegoClientKey;
	@Column(length = 200)
	private String diegoEncryptionKeys;
	@Column(columnDefinition = "TEXT")
	private String diegoServerCert;
	@Column(columnDefinition = "TEXT")
	private String diegoServerKey;
	//2.2 ETCD 정보	
	@Column(columnDefinition = "TEXT")
	private String etcdClientCert;
	@Column(columnDefinition = "TEXT")
	private String etcdClientKey;
	@Column(columnDefinition = "TEXT")
	private String etcdPeerCaCert;
	@Column(columnDefinition = "TEXT")
	private String etcdPeerCert;
	@Column(columnDefinition = "TEXT")
	private String etcdPeerKey;
	@Column(columnDefinition = "TEXT")
	private String etcdServerCert;
	@Column(columnDefinition = "TEXT")
	private String etcdServerKey;
	
	//3.1 네트워크 정보	
	@Column(length = 100)
	private String subnetStaticFrom;
	@Column(length = 100)
	private String subnetStaticTo;
	@Column(length = 100)
	private String subnetReservedFrom;
	@Column(length = 100)
	private String subnetReservedTo;
	@Column(length = 100)
	private String subnetRange;
	@Column(length = 100)
	private String subnetGateway;
	@Column(length = 100)
	private String subnetDns;
	@Column(length = 100)
	private String subnetId;
	@Column(length = 100)
	private String cloudSecurityGroups;	
	//3.2 프록시 정보
	@Column(columnDefinition = "TEXT")
	private String diegoHostKey;
	
	//4 리소스 정보	
	@Column(length = 200)
	private String boshPassword;
	@Column(length = 100)
	private String stemcellName;
	@Column(length = 100)
	private String stemcellVersion;

	// Deploy 정보
	@Column(length = 100)
	private String deploymentFile;
	@Column(length = 100)
	private String deployStatus;
	private Integer taskId;
}