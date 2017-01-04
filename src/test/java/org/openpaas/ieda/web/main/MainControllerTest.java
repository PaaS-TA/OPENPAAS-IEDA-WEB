package org.openpaas.ieda.web.main;


import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.TestBeansConfiguration;
import org.openpaas.ieda.web.common.BaseTestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class, TestBeansConfiguration.class})
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
public class MainControllerTest extends BaseTestController  {
	
	@Autowired	WebApplicationContext wac;

	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	final private static Logger LOGGER = LoggerFactory.getLogger(MainControllerTest.class);
	
	/*************************************** URL *******************************************/
	final static String LAYOUT_URL = "/";//layout 화면
	final static String TOP_URL = "/top";//top 화면
	final static String MENU_URL = "/menu";//메뉴 화면
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 하나의 메소드가 실행되기 전 실행
	 * @title               : setUp
	 * @return            : void
	***************************************************/
	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
		
		getLoggined();
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 메인의 layout 화면 호출
	 * @title               : testGoLayout
	 * @return            : void
	***************************************************/
	@Test
	public void testGoLayout() throws Exception {
		if(LOGGER.isInfoEnabled()) LOGGER.info( "======================================layout 화면 호출 TEST START" );
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(LAYOUT_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()) LOGGER.info( "======================================layout 화면 호출 TEST END" );
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 메인의 top 화면 호출
	 * @title               : testGoTop
	 * @return            : void
	***************************************************/
	@Test
	public void testGoTop() throws Exception {
		if(LOGGER.isInfoEnabled()) LOGGER.info( "======================================top 화면 호출 TEST START" );
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(TOP_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()) LOGGER.info( "======================================top 화면 호출 TEST END" );
	}
	

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 메인의 menu 화면 호출
	 * @title               : testGoMenu
	 * @return            : void
	***************************************************/
	@Test
	public void testGoMenu() throws Exception {
		if(LOGGER.isInfoEnabled()) LOGGER.info( "======================================menu 화면 호출 TEST START" );
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(MENU_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()) LOGGER.info( "======================================menu 화면 호출 TEST END" );
	}
	

}
