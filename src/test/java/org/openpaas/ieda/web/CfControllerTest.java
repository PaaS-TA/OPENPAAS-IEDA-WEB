package org.openpaas.ieda.web;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.web.deploy.cf.CfInfo;
import org.openpaas.ieda.web.deploy.cf.CfParam.Default;
import org.openpaas.ieda.web.deploy.cf.IEDACfAwsConfig;
import org.openpaas.ieda.web.deploy.cf.IEDACfAwsRepository;
import org.openpaas.ieda.web.deploy.cf.IEDACfAwsService;
import org.openpaas.ieda.web.deploy.cf.IEDACfOpenstackConfig;
import org.openpaas.ieda.web.deploy.cf.IEDACfOpenstackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OpenpaasIedaWebApplication.class)
@WebAppConfiguration
@Transactional
public class CfControllerTest {
	final String VIEW_URL = "/deploy/cf";
	final String LIST_URL = "/deploy/cfList";
	final String AWS_DETAIL_URL = "/cf/aws/1";
	final String OPENSTACK_DETAIL_URL = "/cf/openstack/1";
	final String SAVE_DEFAULT_URL =  "/cf/saveDefaultInfo";
	
	@Autowired
	WebApplicationContext wac;

	ObjectMapper objectMapper;
	
	private MockMvc mockMvc;
	
	
	@Autowired private IEDACfAwsService awsService;
	@Autowired private IEDACfAwsRepository awsRepository;
	@Autowired private IEDACfOpenstackRepository openstackRepository;
	
	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		this.objectMapper = new ObjectMapper();
	}

	@Test
	public void testMain() throws Exception {
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(print())
			.andExpect(status().isOk());
	}
	
	@Test
	public void testListCfs() throws Exception {
		IEDACfAwsConfig config = setCfAwsInfo();
		List<CfInfo> cfs = new ArrayList<>();
		CfInfo cfInfo = new CfInfo();
		cfInfo.setId(config.getId());
		cfInfo.setRecid(0);
		cfInfo.setDeploymentName(config.getDeploymentName());
		cfInfo.setDirectorUuid(config.getDirectorUuid());
		cfInfo.setCreateDate(config.getCreatedDate());
		
		ResultActions result = mockMvc.perform(get(LIST_URL)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(cfs)));
		
		result.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void testGetAwsCfInfoInt() throws Exception {
		IEDACfAwsConfig config = setCfAwsInfo();
		awsRepository.save(config);
		
		ResultActions result = mockMvc.perform(get(AWS_DETAIL_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void testGetOpenstackCfInfo() throws Exception {
		IEDACfOpenstackConfig config = setCfOpenstackInfo();
		openstackRepository.save(config);
		
		ResultActions result = mockMvc.perform(get(OPENSTACK_DETAIL_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(print())
			.andExpect(status().isOk());
	}
	
	@Test
	public void testSaveAwsCfInfoDefault() throws Exception {
		String testCert = "-----BEGIN CERTIFICATE-----\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n"; 
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "-----END CERTIFICATE-----";
		
		Default defaultInfo = new Default();
		//defaultInfo.setId("0");
		defaultInfo.setIaas("AWS");
		// 1.1 Deployment 정보
		defaultInfo.setDeploymentName("cfaws");
		defaultInfo.setDirectorUuid("cf-aws-directorUuid");
		defaultInfo.setReleaseName("cf-release");
		defaultInfo.setReleaseVersion("t1.10");
		defaultInfo.setAppSshFingerprint("cf-aws-appSshFingerprint");
		
		// 1.2 기본정보
		defaultInfo.setDomain("cf-domain");
		defaultInfo.setDescription("cf-domain-description");;
		defaultInfo.setDomainOrganization("cf-origin");
		
		// 1.3 프록시 정보
		defaultInfo.setProxyStaticIps("10.0.0.8");
		defaultInfo.setSslPemPub(testCert);
		defaultInfo.setSslPemRsa(testCert);
		
		ResultActions result = mockMvc.perform(put(SAVE_DEFAULT_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(defaultInfo)));
		
		result.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void testSaveOpenstackCfInfoDefault() throws Exception {
		String testCert = "-----BEGIN CERTIFICATE-----\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n"; 
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "-----END CERTIFICATE-----";
		
		Default defaultInfo = new Default();
		//defaultInfo.setId("0");
		defaultInfo.setIaas("OPENSTACK");
		// 1.1 Deployment 정보
		defaultInfo.setDeploymentName("cf-openstack");
		defaultInfo.setDirectorUuid("cf-openstack-directorUuid");
		defaultInfo.setReleaseName("cf-release");
		defaultInfo.setReleaseVersion("t1.10");
		defaultInfo.setAppSshFingerprint("cf-openstack-appSshFingerprint");
		
		// 1.2 기본정보
		defaultInfo.setDomain("cf-domain");
		defaultInfo.setDescription("cf-domain-description");;
		defaultInfo.setDomainOrganization("cf-origin");
		
		// 1.3 프록시 정보
		defaultInfo.setProxyStaticIps("10.0.0.8");
		defaultInfo.setSslPemPub(testCert);
		defaultInfo.setSslPemRsa(testCert);
		
		ResultActions result = mockMvc.perform(put(SAVE_DEFAULT_URL)
				.content(objectMapper.writeValueAsString(defaultInfo))
				.contentType(MediaType.APPLICATION_JSON)
				);
		
		result.andDo(print())
			.andExpect(status().isOk());
	}
	

	
	public IEDACfAwsConfig setCfAwsInfo() {
		IEDACfAwsConfig config = new IEDACfAwsConfig();
		Date now = new Date();
		String testCert = "-----BEGIN CERTIFICATE-----\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n"; 
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "-----END CERTIFICATE-----";
		
		config.setId(1);
		config.setCreatedDate(now);
		config.setUpdatedDate(now);
		
		config.setDeploymentName("my-cf");
		config.setDirectorUuid("test_uuid");
		config.setReleaseName("cf-release.tag");
		config.setReleaseVersion("t1.0");
		config.setAppSshFingerprint("cf-fingerprint");
		
		config.setDomain("cf.co.kr");
		config.setDescription("test_cf");
		config.setDomainOrganization("cf-bosh");
		config.setProxyStaticIps("10.0.0.1");
		config.setSslPemPub(testCert);
		config.setSslPemRsa(testCert);
		
		config.setLoginSecret("tst+0001111test222");
		config.setSigningKey(testCert);
		config.setVerificationKey(testCert);
		
		config.setAgentCert(testCert);
		config.setAgentKey(testCert);
		config.setCaCert(testCert);
		config.setEncryptKeys("test-encript-key");
		config.setServerCert(testCert);
		config.setServerKey(testCert);
		
		config.setSubnetRange("10.0.0.1/24");
		config.setSubnetGateway("10.0.0.2");
		config.setSubnetDns("8.8.8.8");
		config.setSubnetReservedFrom("10.0.0.10");
		config.setSubnetReservedTo("10.0.0.20");
		config.setSubnetStaticFrom("10.0.0.30");
		config.setSubnetStaticTo("10.0.0.30");
		config.setSubnetId("subnet_test_cf");
		config.setCloudSecurityGroups("cf");
		
		config.setStemcellName("stemcell-release.taz");
		config.setStemcellVersion("t1.0");
		config.setBoshPassword("test-cf-bosh-ppppppp212");
		
		config.setDeploymentFile("cf-test-deploy.taz");
		config.setDeployStatus("TEST");
		config.setTaskId(1);
		
		return config;
	}
	
	public IEDACfOpenstackConfig setCfOpenstackInfo() {
		IEDACfOpenstackConfig config = new IEDACfOpenstackConfig();
		Date now = new Date();
		String testCert = "-----BEGIN CERTIFICATE-----\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n"; 
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest\n";
		testCert += "-----END CERTIFICATE-----";
		
		config.setId(1);
		config.setCreatedDate(now);
		config.setUpdatedDate(now);
		
		config.setDeploymentName("my-cf");
		config.setDirectorUuid("test_uuid");
		config.setReleaseName("cf-release.tag");
		config.setReleaseVersion("t1.0");
		config.setAppSshFingerprint("cf-fingerprint");
		
		config.setDomain("cf.co.kr");
		config.setDescription("test_cf");
		config.setDomainOrganization("cf-bosh");
		config.setProxyStaticIps("10.0.0.1");
		config.setSslPemPub(testCert);
		config.setSslPemRsa(testCert);
		
		config.setLoginSecret("tst+0001111test222");
		config.setSigningKey(testCert);
		config.setVerificationKey(testCert);
		
		config.setAgentCert(testCert);
		config.setAgentKey(testCert);
		config.setCaCert(testCert);
		config.setEncryptKeys("test-encript_key");
		config.setServerCert(testCert);
		config.setServerKey(testCert);
		
		config.setSubnetRange("10.0.0.1/24");
		config.setSubnetGateway("10.0.0.2");
		config.setSubnetDns("8.8.8.8");
		config.setSubnetReservedFrom("10.0.0.10");
		config.setSubnetReservedTo("10.0.0.20");
		config.setSubnetStaticFrom("10.0.0.30");
		config.setSubnetStaticTo("10.0.0.30");
		config.setCloudNetId("subnet_test_cf");
		config.setCloudSecurityGroups("cf");
		
		config.setStemcellName("stemcell-release.taz");
		config.setStemcellVersion("t1.0");
		config.setBoshPassword("test-cf-bosh-ppppppp212");
		
		config.setDeploymentFile("cf-test-deploy.taz");
		config.setDeployStatus("TEST");
		config.setTaskId(1);
		return config;
	}
}
