package org.openpaas.ieda.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = OpenpaasIedaWebApplication.class)
//@WebAppConfiguration
//@Transactional
//@IntegrationTest
public class TaskControllerTest {
	final String VIEW_URL = "/releases";
	final String TASKS_URL = "/tasks";
	
	@Autowired
	WebApplicationContext wac;

	@Autowired
	ObjectMapper objectMapper;

	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	
//	@Before
//	public void setUp() throws Exception {
//		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
//	}

//	@Test
//	public void testList() throws Exception {
//		ResultActions result = mockMvc.perform(get(VIEW_URL)
//				.contentType(MediaType.APPLICATION_JSON));
//
//		result.andDo(MockMvcResultHandlers.print())
//		.andExpect(MockMvcResultMatchers.status().isOk());
//	}

//	@Test
//	public void testListTaskHistory() throws Exception {
//		ResultActions result = mockMvc.perform(get(VIEW_URL)
//				.contentType(MediaType.APPLICATION_JSON));
//
//		result.andDo(MockMvcResultHandlers.print())
//		.andExpect(MockMvcResultMatchers.status().isOk());
//	}
//	@Test
//	public void testDoGetTaskLog() {
//		fail("Not yet implemented");
//	}

}
