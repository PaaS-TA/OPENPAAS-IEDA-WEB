package org.openpaas.ieda.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.web.deploy.cf.IEDACfAwsRepository;
import org.openpaas.ieda.web.deploy.cf.IEDACfAwsService;
import org.openpaas.ieda.web.deploy.cf.IEDACfOpenstackRepository;
import org.openpaas.ieda.web.deploy.cf.IEDACfOpenstackService;
import org.openpaas.ieda.web.deploy.cf.IEDACfService;
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
public class CfControllerTest {
	final String VIEW_URL = "/deploy/cfList";
	final String LIST_URL = "/deploy/cfList";
	final String AWS_DETAIL_URL = "/cf/aws/1";
	final String OPENSTACK_DETAIL_URL = "/cf/openstack/1";
	
	@Autowired
	WebApplicationContext wac;

	@Autowired
	ObjectMapper objectMapper;

	private MockMvc mockMvc;
	
	@Autowired private IEDACfAwsService awsService;
	@Autowired private IEDACfOpenstackService openstackService;
	@Autowired private IEDACfService cfService;
	
	@Autowired private IEDACfAwsRepository awsRepository;
	@Autowired private IEDACfOpenstackRepository openstackRepository;
	
	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void testMain() throws Exception {
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
//	@Test
//	public void testListCfs() throws Exception {
//		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(LIST_URL)
//				.contentType(MediaType.APPLICATION_JSON));
//		
//		result.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk());
//	}

//	@Test
//	public void testGetAwsCfInfoInt() throws Exception {
//		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(AWS_DETAIL_URL)
//				.contentType(MediaType.APPLICATION_JSON));
//		
//		result.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk());
//	}
//
//	@Test
//	public void testGetOpenstackCfInfo() throws Exception {
//		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(OPENSTACK_DETAIL_URL)
//				.contentType(MediaType.APPLICATION_JSON));
//		
//		result.andDo(MockMvcResultHandlers.print())
//		.andExpect(MockMvcResultMatchers.status().isOk());
//	}
	
//	@Test
//	public void testSaveAwsCfInfoDefault() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveAwsUaaCfInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveAwsConsulCfInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveAwsNetworkCfInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveAwsResourceCfInfo() {
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testSaveOpenstackCfInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveOpenstackUaaCfInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveOpenstackConsulCfInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveOpenstackNetworkCfInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveOpenstackResourceCfInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDoBoshInstall() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDeleteJustOnlyCfRecord() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDeleteBosh() {
//		fail("Not yet implemented");
//	}

}
