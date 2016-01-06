package org.openpaas.ieda.web.deploy.diego;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity(name="IEDA_DIEGO_OPENSTACK")
@Data
public class IEDADiegoOpenstackConfig {
	@Id @GeneratedValue
	private Integer id;
	
	@Temporal(TemporalType.DATE)
	private Date createdDate;
	
	@Temporal(TemporalType.DATE)
	private Date updatedDate;
	
	//1.1 기본정보	
	private String deploymentName;
	private String directorUuid;
	private String diegoReleaseName;
	private String diegoReleaseVersion;
	private String cfReleaseName;
	private String cfReleaseVersion;
	private String gardenLinuxReleaseName;
	private String gardenLinuxReleaseVersion;
	private String etcdReleaseName;
	private String etcdReleaseVersion;
	//1.2 CF 정보	
	private String domain;
	private String deployment;
	private String secret;
	private String etcdMachines;
	private String natsMachines;
	private String consulServersLan;
	private String consulAgentCert;
	private String consulAgentKey;
	private String consulCaCert;
	private String consulEncryptKeys;
	private String consulServerCert;
	private String consulServerKey;
	
	//2.1 Diego 정보	
	private String diegoCaCert;
	private String diegoClientCert;
	private String diegoClientKey;
	private String diegoEncryptionKeys;
	private String diegoServerCert;
	private String diegoServerKey;
	//2.2 ETCD 정보	
	private String etcdClientCert;
	private String etcdClientKey;
	private String etcdPeerCaCert;
	private String etcdPeerCert;
	private String etcdPeerKey;
	private String etcdServerCert;
	private String etcdServerKey;
	
	//3.1 네트워크 정보	
	private String subnetStaticFrom;
	private String subnetStaticTo;
	private String subnetReservedFrom;
	private String subnetReservedTo;
	private String subnetRange;
	private String subnetGateway;
	private String subnetDns;
	private String cloudNetId;
	private String cloudSecurityGroups;	
	//3.2 프록시 정보
	private String diegoHostKey;
 	private String diegoServers;
	private String diegoUaaSecret;
	
	//4 리소스 정보	
	private String boshPassword;
	private String stemcellName;
	private String stemcellVersion;

	// Deploy 정보
	private String deploymentFile;
	private String deployStatus;
	private String deployLog;
}