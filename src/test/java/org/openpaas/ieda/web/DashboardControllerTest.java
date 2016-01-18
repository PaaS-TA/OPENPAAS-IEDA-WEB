package org.openpaas.ieda.web;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class DashboardControllerTest {

	final String VIEW_URL = "/dashboard";
	final String DEPLOYMENTS_URL = "/dashboard/deployments";
	final String RELEASES_URL = "/dashboard/releases";
	final String STEMCELLS_URL = "/dashboard/stemcells";
	
	@Autowired
	WebApplicationContext wac;

	@Autowired
	ObjectMapper objectMapper;

	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	
	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void testMain() throws Exception {
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));

		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testListDeployment() throws Exception {
		ResultActions result = mockMvc.perform(get(DEPLOYMENTS_URL)
				.contentType(MediaType.APPLICATION_JSON));

		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	public void testListRelease() throws Exception {
		ResultActions result = mockMvc.perform(get(RELEASES_URL)
				.contentType(MediaType.APPLICATION_JSON));

		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());
	}
	@Test
	public void testListStemcell() throws Exception {
		ResultActions result = mockMvc.perform(get(STEMCELLS_URL)
				.contentType(MediaType.APPLICATION_JSON));

		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());
	}

}
