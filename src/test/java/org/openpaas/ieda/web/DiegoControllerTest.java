package org.openpaas.ieda.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoAwsRepository;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoAwsService;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoOpenstackRepository;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoOpenstackService;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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
@IntegrationTest
public class DiegoControllerTest {
	final String VIEW_URL = "/deploy/diegoList";
	final String LIST_URL = "/deploy/diegoList";
	final String AWS_DETAIL_URL = "/diego/aws/1";
	final String OPENSTACK_DETAIL_URL = "/diego/openstack/1";

	@Autowired
	WebApplicationContext wac;

	@Autowired
	ObjectMapper objectMapper;

	private MockMvc mockMvc;

	@Autowired private IEDADiegoAwsService awsService;
	@Autowired private IEDADiegoOpenstackService openstackService;
	@Autowired private IEDADiegoService diegoService;

	@Autowired private IEDADiegoAwsRepository awsRepository;
	@Autowired private IEDADiegoOpenstackRepository openstackRepository;
	
	protected MockRestServiceServer mockServer;
	
	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void testMain() throws Exception{
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
//	@Test
//	public void testListDiego() throws Exception{
//		ResultActions result = mockMvc.perform(get(LIST_URL)
//				.contentType(MediaType.APPLICATION_JSON));
//		
//		result.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk());
//	}

//	@Test
//	public void testGetAwsDiegoInfoInt() throws Exception {
//		ResultActions result = mockMvc.perform(get(AWS_DETAIL_URL)
//				.contentType(MediaType.APPLICATION_JSON));
//
//		result.andDo(MockMvcResultHandlers.print())
//		.andExpect(MockMvcResultMatchers.status().isOk());
//	}
//
//	@Test
//	public void testGetOpenstackDiegoInfoInt() throws Exception {
//		ResultActions result = mockMvc.perform(get(OPENSTACK_DETAIL_URL)
//				.contentType(MediaType.APPLICATION_JSON));
//
//		result.andDo(MockMvcResultHandlers.print())
//		.andExpect(MockMvcResultMatchers.status().isOk());
//	}

	/*@Test
	public void testSaveAwsInfo() {
		fail("Not yet implemented");
	}*/

//	@Test
//	public void testSaveAwsCfInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveAwsDiegoInfoDiego() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveAwsEtcdInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveAwsNetworkInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveAwsResourceInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveOpenstackDiegoInfoDefault() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveOpenstackCfInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveOpenstackDiegoInfoDiego() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveOpenstackEtcdInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveOpenstackNetworkInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveOpenstackResourceInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDoBoshInstall() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDeleteJustOnlyDiegoRecord() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDeleteBosh() {
//		fail("Not yet implemented");
//	}

}
