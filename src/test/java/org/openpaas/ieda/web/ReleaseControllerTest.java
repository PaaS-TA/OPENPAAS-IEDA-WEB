package org.openpaas.ieda.web;

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
public class ReleaseControllerTest {
	final String VIEW_URL = "/releases";
	final String RELEASE_LIST_URL = "/releases";
	final String LOCAL_RELEASE_LIST_URL = "/localReleases";
	final String LOCAL_BOSH_RELEASE_LIST_URL = "/release/localBoshList";
	final String LOCAL_BOSH_AWS_CPI_LIST_URL = "/release/localBoshAwsCpiList";
	final String LOCAL_BOSH_OPENSTACK_CPI_LIST_URL = "/release/localBoshOpenstackCpiList";
	final String LOCAL_FILTER_CF_RELEASE_LIST_URL = "/release/getReleaseList/cf";
	final String LOCAL_FILTER_DIEGO_RELEASE_LIST_URL = "/release/getReleaseList/diego";
	
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
	public void testList() throws Exception {
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));

		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testListRelease() throws Exception {
		ResultActions result = mockMvc.perform(get(RELEASE_LIST_URL)
				.contentType(MediaType.APPLICATION_JSON));

		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testListLocalRelease()  throws Exception {
		ResultActions result = mockMvc.perform(get(LOCAL_RELEASE_LIST_URL)
				.contentType(MediaType.APPLICATION_JSON));

		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testListLocalBoshRelease() throws Exception {
		ResultActions result = mockMvc.perform(get(LOCAL_BOSH_RELEASE_LIST_URL)
				.contentType(MediaType.APPLICATION_JSON));

		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testListLocalBoshAwsCpiRelease() throws Exception {
		ResultActions result = mockMvc.perform(get(LOCAL_BOSH_AWS_CPI_LIST_URL)
				.contentType(MediaType.APPLICATION_JSON));

		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testListLocalBoshOpenstackCpiRelease() throws Exception {
		ResultActions result = mockMvc.perform(get(LOCAL_BOSH_OPENSTACK_CPI_LIST_URL)
				.contentType(MediaType.APPLICATION_JSON));

		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testListLocalFilterCfReleaseList() throws Exception {
		ResultActions result = mockMvc.perform(get(LOCAL_FILTER_CF_RELEASE_LIST_URL)
				.contentType(MediaType.APPLICATION_JSON));

		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	public void testListLocalFilterDiegoReleaseList() throws Exception {
		ResultActions result = mockMvc.perform(get(LOCAL_FILTER_DIEGO_RELEASE_LIST_URL)
				.contentType(MediaType.APPLICATION_JSON));

		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());
	}

//	@Test
//	public void testDoUploadRelease() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDoDeleteRelease() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDoDeleteLocalRelease() {
//		fail("Not yet implemented");
//	}

}
