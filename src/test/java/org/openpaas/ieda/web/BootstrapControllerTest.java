package org.openpaas.ieda.web;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import javax.validation.constraints.NotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.neo4j.cypher.internal.compiler.v2_1.perty.printToString;
import org.openpaas.ieda.web.deploy.bootstrap.BootStrapDto.AwsDefault;
import org.openpaas.ieda.web.deploy.bootstrap.BootStrapDto.OpenstackDefault;
import org.openpaas.ieda.web.deploy.bootstrap.BootstrapListDto;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapAwsConfig;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapAwsRepository;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapAwsService;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapOpenstackConfig;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapOpenstackRepository;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapOpenstackService;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OpenpaasIedaWebApplication.class)
@WebAppConfiguration
@Transactional
public class BootstrapControllerTest {
	final String VIEW_URL = "/deploy/bootstrap";
	final String LIST_URL = "/bootstraps";
	final String AWS_DETAIL_URL = "/bootstrap/aws/1";
	final String OPENSTACK_DETAIL_URL = "/bootstrap/openstack/1";
	final String SAVE_AWS_DEFAULT_URL= "/bootstrap/awsDefault";
	final String SAVE_OPENSTACK_DEFAULT_URL = "/bootstrap/setOpenstackDefaultInfo";
	@Autowired
	WebApplicationContext wac;

	@Autowired
	ObjectMapper objectMapper;

	private MockMvc mockMvc;
	
	@Autowired private IEDABootstrapAwsService awsService;
	@Autowired private IEDABootstrapOpenstackService openstackService;
	@Autowired private IEDABootstrapService bootstrapService;
	
	@Autowired private IEDABootstrapAwsRepository awsRepository;
	@Autowired private IEDABootstrapOpenstackRepository openstackRepository;
	
	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		objectMapper = new ObjectMapper();
	}

	@Test
	public void testMain() throws Exception{
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	public void testListBootstrap() throws Exception{
		IEDABootstrapAwsConfig awsConfig = setBootstrapAwsInfo();
		IEDABootstrapOpenstackConfig openstackConfig = setBootstrapOpenstackInfo();
		awsRepository.save(awsConfig);
		openstackRepository.save(openstackConfig);
				
		ResultActions result = mockMvc.perform(get(LIST_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(print())
			.andExpect(status().isOk());
	}
	
	@Test
	public void testGetAwsInfo() throws Exception{
		IEDABootstrapAwsConfig awsConfig = setBootstrapAwsInfo();
		awsRepository.save(awsConfig);
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(AWS_DETAIL_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testGetOpenstackInfo() throws Exception{
		IEDABootstrapOpenstackConfig openstackConfig = setBootstrapOpenstackInfo();
		openstackRepository.save(openstackConfig);
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(OPENSTACK_DETAIL_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testDoBootstrapAwsDefaultSave() throws Exception{
		IEDABootstrapAwsConfig awsConfig = setBootstrapAwsInfo();
		awsRepository.save(awsConfig);
		
		AwsDefault awsDefaultInfo = new AwsDefault();
		awsDefaultInfo.setId("1");
		awsDefaultInfo.setDeploymentName("bosh");
		awsDefaultInfo.setDirectorName("myBosh");
		awsDefaultInfo.setBoshRelease("bosh-release");
		awsDefaultInfo.setBoshCpiRelease("bosh-cpi-release");
		
		
		ResultActions result = mockMvc.perform(put(SAVE_AWS_DEFAULT_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(awsDefaultInfo))
				);
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());	
	}
	
	@Test
	public void testDoOpenstackDefaultInfoSave() throws Exception{
		IEDABootstrapOpenstackConfig openstackConfig = setBootstrapOpenstackInfo();
		openstackRepository.save(openstackConfig);
		
		OpenstackDefault openstackDefaultInfo = new OpenstackDefault();
		openstackDefaultInfo.setId("1");
		openstackDefaultInfo.setDeploymentName("bosh");
		openstackDefaultInfo.setDirectorName("myBosh");
		openstackDefaultInfo.setBoshRelease("bosh-release");
		openstackDefaultInfo.setBoshCpiRelease("bosh-cpi-release");
		
		
		ResultActions result = mockMvc.perform(put(SAVE_OPENSTACK_DEFAULT_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(openstackDefaultInfo))
				);
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());	
	}
	
	public IEDABootstrapAwsConfig setBootstrapAwsInfo(){
		IEDABootstrapAwsConfig config = new IEDABootstrapAwsConfig();
		config.setAccessKeyId("bootstrap-aws");
		config.setSecretAccessId("bootstrap-aws-secret");
		config.setDefaultSecurityGroups("bosh");
		config.setRegion("m.east_1");
		config.setAvailabilityZone("bosh");;
		config.setPrivateKeyName("bosh-key");
		config.setPrivateKeyPath("./ssh/bosh.pem");
		return config;
	}
	
	public IEDABootstrapOpenstackConfig setBootstrapOpenstackInfo(){
		IEDABootstrapOpenstackConfig config = new IEDABootstrapOpenstackConfig();
		config.setAuthUrl("bootstrap-aws-authUrl");
		config.setTenant("bootstrap-aws-tenant");
		config.setUserName("bootstrap-aws-userName");
		config.setApiKey("bootstrap-aws-apiKey");
		config.setDefaultSecurityGroups("bosh");;
		config.setPrivateKeyName("bosh-key");
		config.setPrivateKeyPath("./ssh/bosh.pem");
		return config;
	}

}
