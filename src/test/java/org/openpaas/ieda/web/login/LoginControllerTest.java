package org.openpaas.ieda.web.login;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class})
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
public class LoginControllerTest extends BaseTestController{
	
@Autowired WebApplicationContext wac;
	
	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), 
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	private final static Logger LOGGER = LoggerFactory.getLogger(LoginControllerTest.class);
	
	/*************************************** URL *******************************************/
	final static String VIEW_URL = "/login"; //로그인 화면 URL
	final static String ABUSE_URL = "/abuse";//부적절한 로그인 URL
	final static String LOGOUT_URL = "/louout";//로그가웃 url

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 하나의 메소드가 동작하기 전 실행
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
	 * @description   : 로그인 화면으로 이동
	 * @title               : testGoLogIn
	 * @return            : void
	***************************************************/
	@Test
	public void testGoLogIn() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  로그인 화면 이동 요청  ================="); }
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON)); 
		
		result.andDo(MockMvcResultHandlers.print()) 
		.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  로그인 화면 이동 성공  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로그아웃
	 * @title               : testLogout
	 * @return            : void
	***************************************************/
	@Test
	public void testLogout() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  로그아웃 화면 이동 요청  ================="); }
		ResultActions result = mockMvc.perform(get(LOGOUT_URL)
				.contentType(MediaType.APPLICATION_JSON)); 
		
		result.andDo(MockMvcResultHandlers.print()).andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  로그아웃 화면 이동 성공  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 부적절한 접근 시도시 로그아웃
	 * @title               : testAbuse
	 * @return            : void
	***************************************************/
	@Test
	public void testAbuse() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  부적절한 접근 화면 이동 요청  ================="); }
		ResultActions result = mockMvc.perform(get(ABUSE_URL)
				.contentType(MediaType.APPLICATION_JSON)); 
		
		result.andDo(MockMvcResultHandlers.print()).andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  부적절한 접근 화면 이동 끝  ================="); }
	}
}
