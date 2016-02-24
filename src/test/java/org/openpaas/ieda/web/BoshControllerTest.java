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

import javax.validation.constraints.NotNull;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.openpaas.ieda.web.deploy.bootstrap.BootStrapDto.OpenstackDefault;
import org.openpaas.ieda.web.deploy.bosh.BoshInfo;
import org.openpaas.ieda.web.deploy.bosh.BoshParam;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshAwsConfig;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshAwsRepository;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshAwsService;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshOpenstackConfig;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshOpenstackRepository;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshOpenstackService;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshService;
import org.openpaas.ieda.web.deploy.bosh.BoshParam.AwsBosh;
import org.openpaas.ieda.web.deploy.bosh.BoshParam.OpenstackBosh;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
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
public class BoshControllerTest {
	final String VIEW_URL = "/deploy/bosh";
	final String LIST_URL = "/deploy/boshList";
	final String AWS_DETAIL_URL = "/bosh/aws/1";
	final String OPENSTACK_DETAIL_URL = "/bosh/openstack/1";
	final String SAVE_AWS_DEFAULT_URL = "/bosh/saveAwsDefaultInfo";
	final String SAVE_OPENSTACK_DEFAULT_URL = "/bosh/saveOpenstackDefaultInfo";
	
	
	final String BOSH_RELEASES_URL = "/bosh/releases";
	
	@Autowired private WebApplicationContext wac;
	@Autowired private ModelMapper modelMapper;
	@Autowired ObjectMapper objectMapper;
	MockMvc mockMvc;
	
	@Autowired private IEDABoshAwsRepository awsRepository;
	@Autowired private IEDABoshOpenstackRepository openstackRepository;
	
	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		objectMapper = new ObjectMapper();
	}

	@Test
	public void testList() throws Exception{
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(print())
			.andExpect(status().isOk());
	}
	
	@Test
	public void testListBosh() throws Exception{
		IEDABoshAwsConfig awsInfo = setBoshAwsInfo();
		awsRepository.save(awsInfo);
		
		IEDABoshOpenstackConfig openstackInfo = setBoshOpenstackInfo();
		openstackRepository.save(openstackInfo);
		
		ResultActions result = mockMvc.perform(get(LIST_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void testGetAwsInfo() throws Exception{
		IEDABoshAwsConfig awsBosh = setBoshAwsInfo();
		awsRepository.save(awsBosh);
		
		ResultActions result = mockMvc.perform(get(AWS_DETAIL_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void testGetOpenstackInfo() throws Exception{
		IEDABoshOpenstackConfig openstackBosh = setBoshOpenstackInfo();
		openstackRepository.save(openstackBosh); 
		
		ResultActions result = mockMvc.perform(get(OPENSTACK_DETAIL_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void testSaveAwsInfo() throws Exception{
		IEDABoshAwsConfig config= setBoshAwsInfo();
		IEDABoshAwsConfig getAwsInfo = awsRepository.save(config);
		
		AwsBosh awsBoshInfo = new AwsBosh(); 
		awsBoshInfo.setId( String.valueOf(getAwsInfo.getId()) );
		awsBoshInfo.setDeploymentName("test-bosh-aws");
		awsBoshInfo.setDirectorUuid("test-save-directorUuid");
		awsBoshInfo.setReleaseVersion("test-save-release");;
		
		ResultActions result = mockMvc.perform(put(SAVE_AWS_DEFAULT_URL)
				.content(objectMapper.writeValueAsString(awsBoshInfo))
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(print())
		.andExpect(status().isOk());
		
	}

	@Test
	public void testSaveOpenstackDefaultInfo() throws Exception{
		IEDABoshOpenstackConfig config = setBoshOpenstackInfo();
		IEDABoshOpenstackConfig getOpenstackInfo = openstackRepository.save(config);
				
		OpenstackBosh openstackBoshInfo = new OpenstackBosh();
		openstackBoshInfo.setId( String.valueOf(getOpenstackInfo.getId()) );
		openstackBoshInfo.setDeploymentName("test-bosh-openstack");
		openstackBoshInfo.setDirectorUuid("test-save-directorUuid");
		openstackBoshInfo.setReleaseVersion("test-save-release");
		
		ResultActions result = mockMvc.perform(put(SAVE_OPENSTACK_DEFAULT_URL)
				.content(objectMapper.writeValueAsString(openstackBoshInfo))
				.contentType(MediaType.APPLICATION_JSON)
				);
		
		result.andDo(print())
		.andExpect(status().isOk());
	}

	public IEDABoshAwsConfig setBoshAwsInfo(){
		IEDABoshAwsConfig awsBosh = new IEDABoshAwsConfig();
		Date now = new Date();
		awsBosh.setId(1);
		awsBosh.setCreatedDate(now);
		awsBosh.setUpdatedDate(now);
		
		awsBosh.setAccessKeyId("test_aws_bosh");
		awsBosh.setSecretAccessKey("test-secretAccessKey");
		awsBosh.setRegion("m1.test");
		awsBosh.setDefaultSecurityGroups("awsbosh");
		awsBosh.setPrivateKeyName("test-key");
		awsBosh.setPrivateKeyPath("./shh/test.pem");
		
		awsBosh.setDeploymentName("testDeploymentName");
		awsBosh.setDirectorUuid("test_uuid");
		awsBosh.setReleaseVersion("test_releaseVersion");
		
		awsBosh.setPublicStaticIp("10.0.0.1");
		awsBosh.setSubnetRange("10.0.0.30/24");
		awsBosh.setSubnetStaticFrom("10.0.0.10");
		awsBosh.setSubnetStaticTo("10.0.0.20");
		awsBosh.setSubnetGateway("10.0.0.8");
		awsBosh.setSubnetDns("8.8.8.8");
		awsBosh.setSubnetId("subnet-test_id");
		
		awsBosh.setStemcellName("test_stemcell");
		awsBosh.setStemcellVersion("t0.1");
		awsBosh.setCloudInstanceType("testCloudType");
		
		awsBosh.setBoshPassword("testboshkey");
		awsBosh.setDeploymentFile("test_deployFile");
		awsBosh.setDeployStatus("TEST");
		awsBosh.setTaskId(0);
		return awsBosh;
	}
	
	public IEDABoshOpenstackConfig setBoshOpenstackInfo(){
		IEDABoshOpenstackConfig openstackBosh = new IEDABoshOpenstackConfig();
		Date now = new Date();
		openstackBosh.setId(1);
		openstackBosh.setCreatedDate(now);
		openstackBosh.setUpdatedDate(now);
		
		openstackBosh.setAuthUrl("www.testOpenstack.org");
		openstackBosh.setTenant("testTenat");
		openstackBosh.setUserName("testUser");
		openstackBosh.setApiKey("test-apiKey");
		openstackBosh.setDefaultSecurityGroups("openstackBosh");
		openstackBosh.setPrivateKeyName("test-key");
		openstackBosh.setPrivateKeyPath("./shh/test.pem");
		
		openstackBosh.setDeploymentName("testDeploymentName");
		openstackBosh.setDirectorUuid("test_uuid");
		openstackBosh.setReleaseVersion("test_releaseVersion");
		
		openstackBosh.setPublicStaticIp("10.0.0.1");
		openstackBosh.setSubnetRange("10.0.0.30/24");
		openstackBosh.setSubnetStaticFrom("10.0.0.10");
		openstackBosh.setSubnetStaticTo("10.0.0.20");
		openstackBosh.setSubnetGateway("10.0.0.8");
		openstackBosh.setSubnetDns("8.8.8.8");
		openstackBosh.setSubnetId("subnet-test_id");
		
		openstackBosh.setStemcellName("test_stemcell");
		openstackBosh.setStemcellVersion("t0.1");
		openstackBosh.setCloudInstanceType("testCloudType");
		
		openstackBosh.setBoshPassword("testboshkey");
		openstackBosh.setDeploymentFile("test_deployFile");
		openstackBosh.setDeployStatus("TEST");
		openstackBosh.setTaskId(0);
		return openstackBosh;
	}
	
}
