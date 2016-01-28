package org.openpaas.ieda.web.deploy.cf;

import java.util.Date;

import javax.persistence.Column;
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
	@Column(length = 100)
	private String deploymentName;
	@Column(length = 100)
	private String directorUuid;
	@Column(length = 100)
	private String releaseName;
	@Column(length = 100)
	private String releaseVersion;
	@Column(length = 100)
	private String appSshFingerprint;

	// 1.2 기본정보
	@Column(length = 100)
	private String domain;
	@Column(length = 100)
	private String description;
	@Column(length = 100)
	private String domainOrganization;

	// 1.3 프록시 정보
	@Column(length = 100)
	private String proxyStaticIps;
	@Column(columnDefinition = "TEXT")
	private String sslPemPub; //Big
	@Column(columnDefinition = "TEXT")
	private String sslPemRsa; //Big

	// 2. UAA 정보
	@Column(length = 100)
	private String loginSecret;
	@Column(columnDefinition = "TEXT")
	private String signingKey;
	@Column(columnDefinition = "TEXT")
	private String verificationKey;

	// 3. Consul 정보
	@Column(columnDefinition = "TEXT")
	private String agentCert;
	@Column(columnDefinition = "TEXT")
	private String agentKey;
	@Column(columnDefinition = "TEXT")
	private String caCert;
	@Column(length = 200)
	private String encryptKeys;
	@Column(columnDefinition = "TEXT")
	private String serverCert;
	@Column(columnDefinition = "TEXT")
	private String serverKey;

	// 4. 네트워크 정보
	@Column(length = 100)
	private String subnetRange;
	@Column(length = 100)
	private String subnetGateway;
	@Column(length = 100)
	private String subnetDns;
	@Column(length = 100)
	private String subnetReservedFrom;
	@Column(length = 100)
	private String subnetReservedTo;
	@Column(length = 100)
	private String subnetStaticFrom;
	@Column(length = 100)
	private String subnetStaticTo;
	@Column(length = 100)
	private String cloudNetId;
	@Column(length = 100)
	private String cloudSecurityGroups;

	// 5. 리소스 정보
	@Column(length = 100)
	private String stemcellName;
	@Column(length = 100)
	private String stemcellVersion;
	@Column(length = 200)
	private String boshPassword;
	// Deploy 정보
	@Column(length = 100)
	private String deploymentFile;
	@Column(length = 100)
	private String deployStatus;
	private Integer taskId;
}