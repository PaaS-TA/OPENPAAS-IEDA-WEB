package org.openpaas.ieda.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigDto;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
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
public class DirectorConfigurationControllerTest {

	@Autowired
	WebApplicationContext wac;

	@Autowired
	ObjectMapper objectMapper;

	private MockMvc mockMvc;

	@Autowired
	private IEDADirectorConfigService service;

	protected MockRestServiceServer mockServer;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	private IEDADirectorConfigDto.Create CreateDirectorConfigDto() {
		IEDADirectorConfigDto.Create directorDto = new IEDADirectorConfigDto.Create();
		directorDto.setUserId("admin");
		directorDto.setUserPassword("admin");
		directorDto.setDirectorUrl("52.21.37.184");
		directorDto.setDirectorPort(25555);
		
		return directorDto;
	}
	
//	 Do not delete...
//	@Rollback
//	@Test
//	public void createDirector() throws Exception {
//		IEDADirectorConfigDto.Create directorDto = CreateDirectorConfigDto();
//		
//		ResultActions result = mockMvc.perform(post("/directors")
//				.contentType(MediaType.APPLICATION_JSON).content(
//						objectMapper.writeValueAsString(directorDto)));
//		result.andDo(print());
//		result.andExpect(status().isCreated());
//	}
	
	
/*	@Test
	public void getDefaultDirector() throws Exception {
		ResultActions result = mockMvc.perform(get("/directors/default"));
		
		result.andDo(print());
		result.andExpect(status().isOk());
	}*/
	
//	@Rollback
//	@Test
//	public void createDirector_checkMandatoryArgument() throws Exception {
//		//IEDADirectorConfigDto.Create directorDto = CreateDirectorConfigDto();
//		
//		IEDADirectorConfigDto.Create directorDto = new IEDADirectorConfigDto.Create();
//		directorDto.setUserId("admin");
//		directorDto.setUserPassword("admin");
//		directorDto.setDirectorUrl("52.21.37.184");
//		///directorDto.setDirectorPort(25555);
//		
//		ResultActions result = mockMvc.perform(post("/directors")
//				.contentType(MediaType.APPLICATION_JSON).content(
//						objectMapper.writeValueAsString(directorDto)));
//		result.andDo(print());
//		result.andExpect(status().isBadRequest());
//	}
//
//	@Rollback
//	@Test
//	public void createDirector_notfound() throws Exception {
//		IEDADirectorConfigDto.Create directorDto = new IEDADirectorConfigDto.Create();
//		directorDto.setUserId("admin");
//		directorDto.setUserPassword("admin");
//		directorDto.setDirectorUrl("52.21.37.186");
//		directorDto.setDirectorPort(25555);	
//		
//		ResultActions result = mockMvc.perform(post("/directors")
//							.contentType(MediaType.APPLICATION_JSON)
//							.content(objectMapper.writeValueAsString(directorDto)));
//		result.andDo(print());
//		result.andExpect(status().isBadRequest());
//	}
//
//	@Rollback
//	@Test
//	public void createDirector_duplicated() throws Exception {
//
//		IEDADirectorConfigDto.Create directorDto = new IEDADirectorConfigDto.Create();
//		directorDto.setUserId("admin");
//		directorDto.setUserPassword("admin");
//		directorDto.setDirectorUrl("52.21.37.184");
//		directorDto.setDirectorPort(25555);
//		
//		ResultActions result = mockMvc.perform(post("/directors")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(objectMapper.writeValueAsString(directorDto)));
//		result.andDo(print());
//		result.andExpect(status().isBadRequest());
//	}
//	
//	@Test
//	public void listDirector() throws Exception {
//		
//		ResultActions result = mockMvc.perform(get("/directors"));
//		
//		result.andDo(print());
//		result.andExpect(status().isOk());
//	}
//	
//	@Test
//	public void getDirector() throws Exception {
//		ResultActions result = mockMvc.perform(get("/directors/7"));
//		
//		result.andDo(print());
//		result.andExpect(status().isOk());
//	}
//	
//	@Test
//	public void updateDirector() throws Exception {
//		IEDADirectorConfigDto.Update directorDto = new IEDADirectorConfigDto.Update();
//
//	}
//	
//	@Test
//	public void deleteDirector() throws Exception {
//		
//	}
}
