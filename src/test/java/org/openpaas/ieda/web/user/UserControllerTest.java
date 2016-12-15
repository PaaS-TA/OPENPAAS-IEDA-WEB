package org.openpaas.ieda.web.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.web.common.BaseTestController;
import org.openpaas.ieda.web.management.user.dao.UserManagementVO;
import org.openpaas.ieda.web.management.user.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class})
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
public class UserControllerTest extends BaseTestController{
	
	@Autowired WebApplicationContext wac;
	
	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), 
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	private final static Logger LOGGER = LoggerFactory.getLogger(UserControllerTest.class);
	
	/*************************************** URL *******************************************/
	final static String VIEW_URL = "/common/user/resetPassword";//비밀번호 변경 화면 이동
	final static String RESET_PASSWORD_URL = "/common/user/savePassword";//비밀번호 변경
	final static String USER_ADD = "/admin/user/add"; //사용자 등록

	
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
	 * @description   : 패스워드 변경 화면 이동
	 * @title               : testGoResetPassword
	 * @return            : void
	***************************************************/
	@Test
	public void testGoResetPassword() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  사용자 관리 화면 이동 요청  ================="); }
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  사용자 관리 화면 이동 성공  ================="); }
	}
	
   	/***************************************************
   	 * @project          : Paas 플랫폼 설치 자동화
   	 * @description   : 사용자 정보 등록
   	 * @title               : testSaveUserInfo
   	 * @return            : void
   	***************************************************/
   	@Rollback(true)
   	@Test
   	public void testSaveUserInfo() throws Exception{
   		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  사용자 정보 등록 테스트 요청"); }
   		String requestJson = setUserRegistInfo();
		 ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(USER_ADD)
		    		.contentType(APPLICATION_JSON_UTF8)
					.content(requestJson));
			
			result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andReturn();
   		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  사용자 정보 등록 테스트 성공"); }
   	}
   	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비밀번호 변경 화면으로 이동
	 * @title               : testSavePassword
	 * @return            : void
	***************************************************/
	@Test
	public void testSavePassword() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  비밀번호 변경 요청  ================="); }
		testSaveUserInfo();
		String requestJson = setResetPasswordInfo();
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(RESET_PASSWORD_URL)
				.contentType(MediaType.APPLICATION_JSON)
		 		.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print()) 
		.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  비밀번호 변경  성공  ================="); }
	}
   	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 사용자 등록 정보 설정
	 * @title               : setUserRegistInfo
	 * @return            : String
	***************************************************/
	private String setUserRegistInfo() throws Exception{
		UserManagementVO userVO = new UserManagementVO();
		userVO.setUserId("tester");
		userVO.setUserName("tester");
		userVO.setUserPassword("1234");
		userVO.setEmail("admin@example.co.kr");
		userVO.setRoleId("2");
		userVO.setInitPassYn("N");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson= ow.writeValueAsString(userVO);
	    return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비밀번호 변경 정보 설정
	 * @title               : setResetPasswordInfo
	 * @return            : String
	***************************************************/
	private String setResetPasswordInfo() throws JsonProcessingException{
		UserDTO.SavePassword dto = new UserDTO.SavePassword();
		dto.setUserId("tester");
		dto.setPassword("123456");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson= ow.writeValueAsString(dto);
	    return requestJson;
	}
}
