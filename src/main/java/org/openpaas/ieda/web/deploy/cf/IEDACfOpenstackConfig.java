package org.openpaas.ieda.web.deploy.cf;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;


@Entity(name="IEDA_CF_OPENSTACK")
@Data
public class IEDACfOpenstackConfig {
	@Id @GeneratedValue
	private Integer id;

	@Temporal(TemporalType.DATE)
	private Date createdDate;

	@Temporal(TemporalType.DATE)
	private Date updatedDate;

	// 1.1 Deployment 정보
	private String deploymentName;
	private String directorUuid;
	private String releaseName;
	private String releaseVersion;
	private String appSshFingerprint;

	// 1.2 기본정보
	private String domain;
	private String description;
	private String domainOrganization;

	// 1.3 프록시 정보
	private String proxyStaticIps;
	private String sslPemPub; //Big
	private String sslPemRsa; //Big

	// 2. UAA 정보
	private String loginSecret;
	private String signingKey;
	private String verificationKey;

	// 3. Consul 정보
	private String agentCert;
	private String agentKey;
	private String caCert;
	private String encryptKeys;
	private String serverCert;
	private String serverKey;

	// 4. 네트워크 정보
	private String subnetRange;
	private String subnetGateway;
	private String subnetDns;
	private String subnetReservedFrom;
	private String subnetReservedTo;
	private String subnetStaticFrom;
	private String subnetStaticTo;
	private String cloudNetId;
	private String cloudSecurityGroups;

	// 5. 리소스 정보
	private String stemcellName;
	private String stemcellVersion;
	private String boshPassword;
	// Deploy 정보
	private String deploymentFile;
	private String deployStatus;
	private String deployLog;

}