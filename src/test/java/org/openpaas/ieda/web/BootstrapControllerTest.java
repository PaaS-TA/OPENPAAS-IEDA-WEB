package org.openpaas.ieda.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapAwsRepository;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapAwsService;
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

	final String LIST_URL = "/bootstraps";
	final String AWS_DETAIL_URL = "/bootstrap/aws/1";
	final String OPENSTACK_DETAIL_URL = "/bootstrap/openstack/1";
	
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
	}

	@Test
	public void testListBootstrap() throws Exception{
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(LIST_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testGetAwsInfo() throws Exception{
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(AWS_DETAIL_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testGetOpenstackInfo() throws Exception{
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(OPENSTACK_DETAIL_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());
	}

//	@Test
//	public void testDoBootstrapAwsSave() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDoBootstrapAwsDefaultSave() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDoBootstrapNetworkSave() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDoBootstrapResourcesSave() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDoInstallBootstrap() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDeleteBootstrap() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDeleteJustOnlyBootstrapRecord() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDoOpenstackInfoSave() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDoOpenstackBoshInfoSave() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDoOpenstackNetworkInfoSave() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDoOpenstackResourcesInfoSave() {
//		fail("Not yet implemented");
//	}

}
