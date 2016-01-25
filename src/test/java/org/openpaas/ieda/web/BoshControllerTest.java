package org.openpaas.ieda.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshAwsRepository;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshAwsService;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshOpenstackRepository;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshOpenstackService;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshService;
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
public class BoshControllerTest {
	final String VIEW_URL = "/deploy/bosh";
	final String LIST_URL = "/deploy/boshList";
	final String AWS_DETAIL_URL = "/bosh/aws/1";
	final String OPENSTACK_DETAIL_URL = "/bosh/openstack/1";
	final String BOSH_RELEASES_URL = "/bosh/releases";
	
	@Autowired WebApplicationContext wac;
	@Autowired ObjectMapper objectMapper;

	private MockMvc mockMvc;
	
	@Autowired private IEDABoshAwsService awsService;
	@Autowired private IEDABoshOpenstackService openstackService;
	@Autowired private IEDABoshService boshService;
	
	@Autowired private IEDABoshAwsRepository awsRepository;
	@Autowired private IEDABoshOpenstackRepository openstackRepository;
	
	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void testList() throws Exception{
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	public void testListBosh() throws Exception{
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
//	public void testSaveAwsInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveBoshInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveNetworkInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveResourceInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveOpenstackInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveOpenstackBoshInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveOsNetworkInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSaveOsResourceInfo() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDoBoshInstall() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDeleteJustOnlyBoshRecord() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDeleteBosh() {
//		fail("Not yet implemented");
//	}

//	@Test
//	public void testListRelease() throws Exception{
//		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(BOSH_RELEASES_URL)
//				.contentType(MediaType.APPLICATION_JSON));
//		
//		result.andDo(MockMvcResultHandlers.print())
//		.andExpect(MockMvcResultMatchers.status().isOk());
//	}

}
