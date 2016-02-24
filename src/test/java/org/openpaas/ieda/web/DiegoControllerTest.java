package org.openpaas.ieda.web;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import javax.validation.constraints.NotNull;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.web.deploy.diego.DiegoInfo;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoAwsConfig;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoAwsRepository;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoOpenstackConfig;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoOpenstackRepository;
import org.openpaas.ieda.web.deploy.diego.DiegoParam.Cf;
import org.openpaas.ieda.web.deploy.diego.DiegoParam.Diego;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OpenpaasIedaWebApplication.class)
@WebAppConfiguration
@Transactional
@IntegrationTest
public class DiegoControllerTest {
	final String VIEW_URL = "/deploy/diego";
	final String LIST_URL = "/deploy/diegoList";
	final String AWS_DETAIL_URL = "/diego/aws/1";
	final String OPENSTACK_DETAIL_URL = "/diego/openstack/1";
	final String SAVE_DIEGO_AWS_URL = "/diego/saveAwsDiego";
	final String SAVE_DIEGO_OPENSTACK_URL = "/diego/saveOpenstackDiego";
	
	@Autowired
	WebApplicationContext wac;

	@Autowired
	ObjectMapper objectMapper;

	private MockMvc mockMvc;

	@Autowired private IEDADiegoAwsRepository awsRepository;
	@Autowired private IEDADiegoOpenstackRepository openstackRepository;
	
	protected MockRestServiceServer mockServer;
	
	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		objectMapper = new ObjectMapper();
	}

	@Test
	public void testMain() throws Exception{
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(print())
			.andExpect(status().isOk());
	}
	
	@Test
	public void testListDiego() throws Exception{
		IEDADiegoAwsConfig awsInfo = setDiegoAwsInfo();
		awsRepository.save(awsInfo);
		IEDADiegoOpenstackConfig openstackInfo = setDiegoOpenstackInfo();
		openstackRepository.save(openstackInfo);
		
		ResultActions result = mockMvc.perform(get(LIST_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void testGetAwsDiegoInfo() throws Exception {
		IEDADiegoAwsConfig awsInfo = setDiegoAwsInfo();
		awsRepository.save(awsInfo);
		
		ResultActions result = mockMvc.perform(get(AWS_DETAIL_URL)
				.contentType(MediaType.APPLICATION_JSON));

		result.andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	public void testGetOpenstackDiegoInfo() throws Exception {
		IEDADiegoOpenstackConfig openstackInfo = setDiegoOpenstackInfo();
		openstackRepository.save(openstackInfo);
		
		ResultActions result = mockMvc.perform(get(OPENSTACK_DETAIL_URL)
				.contentType(MediaType.APPLICATION_JSON));

		result.andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	public void testSaveAwsInfo() throws Exception {
		IEDADiegoAwsConfig awsInfo = setDiegoAwsInfo();
		IEDADiegoAwsConfig getAwsInfo = awsRepository.save(awsInfo);
		
		Diego diegoInfo = new Diego();
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
		
		diegoInfo.setId(String.valueOf(getAwsInfo.getId()));
		diegoInfo.setIaas("AWS");
		//2.1 Diego 정보	
		diegoInfo.setDiegoCaCert(testCert);
		//2.2 프록시 정보
		diegoInfo.setDiegoHostKey(testCert);
		//2.3 BBS 인증정보
		diegoInfo.setDiegoClientCert(testCert);
		diegoInfo.setDiegoClientKey(testCert);
		diegoInfo.setDiegoEncryptionKeys(testCert);
		diegoInfo.setDiegoServerCert(testCert);
		diegoInfo.setDiegoServerKey(testCert);
		
		ResultActions result = mockMvc.perform(put(SAVE_DIEGO_AWS_URL)
				.content(objectMapper.writeValueAsString(diegoInfo))
				.contentType(MediaType.APPLICATION_JSON)
				);

		result.andDo(print())
		.andExpect(status().isOk());
	}
	
	@Test
	public void testSaveOpenstackInfo() throws Exception {
		IEDADiegoOpenstackConfig openstackInfo = setDiegoOpenstackInfo();
		IEDADiegoOpenstackConfig getOpenstackInfo = openstackRepository.save(openstackInfo);
		
		Diego diegoInfo = new Diego();
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
		
		diegoInfo.setId(String.valueOf(getOpenstackInfo.getId()));
		diegoInfo.setIaas("AWS");
		//2.1 Diego 정보	
		diegoInfo.setDiegoCaCert(testCert);
		//2.2 프록시 정보
		diegoInfo.setDiegoHostKey(testCert);
		//2.3 BBS 인증정보
		diegoInfo.setDiegoClientCert(testCert);
		diegoInfo.setDiegoClientKey(testCert);
		diegoInfo.setDiegoEncryptionKeys(testCert);
		diegoInfo.setDiegoServerCert(testCert);
		diegoInfo.setDiegoServerKey(testCert);
		
		ResultActions result = mockMvc.perform(put(SAVE_DIEGO_OPENSTACK_URL)
				.content(objectMapper.writeValueAsString(diegoInfo))
				.contentType(MediaType.APPLICATION_JSON));

		result.andDo(print())
		.andExpect(status().isOk());
	}
	
	public IEDADiegoAwsConfig setDiegoAwsInfo(){
		IEDADiegoAwsConfig config = new IEDADiegoAwsConfig();
		Date now = new Date();
		config.setCreatedDate(now);
		config.setUpdatedDate(now);
		
		//1.1 기본정보			
		config.setDeploymentName("diego-aws");
		config.setDirectorUuid("diego-directorUuid");
		config.setDiegoReleaseName("diego-release");
		config.setDiegoReleaseVersion("t1.0");
		config.setCfReleaseName("cf-release");
		config.setCfReleaseVersion("c1.0");
		config.setGardenLinuxReleaseName("garden-linux-release");
		config.setGardenLinuxReleaseVersion("g1.0");;
		config.setEtcdReleaseName("etcd-release");;
		config.setEtcdReleaseVersion("e1.0");
		return config;		
	}
	
	public IEDADiegoOpenstackConfig setDiegoOpenstackInfo(){
		IEDADiegoOpenstackConfig config = new IEDADiegoOpenstackConfig();
		Date now = new Date();
		config.setCreatedDate(now);
		config.setUpdatedDate(now);
		
		//1.1 기본정보			
		config.setDeploymentName("diego-openstack");
		config.setDirectorUuid("diego-directorUuid");
		config.setDiegoReleaseName("diego-release");
		config.setDiegoReleaseVersion("t1.0");
		config.setCfReleaseName("cf-release");
		config.setCfReleaseVersion("c1.0");
		config.setGardenLinuxReleaseName("garden-linux-release");
		config.setGardenLinuxReleaseVersion("g1.0");;
		config.setEtcdReleaseName("etcd-release");;
		config.setEtcdReleaseVersion("e1.0");
		return config;
	}
	

}
