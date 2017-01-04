package org.openpaas.ieda.web.management.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.TestBeansConfiguration;
import org.openpaas.ieda.web.common.BaseTestController;
import org.openpaas.ieda.web.management.user.dao.UserManagementVO;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class, TestBeansConfiguration.class})
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
public class UserManagementControllerTest extends BaseTestController{
	
	@Autowired WebApplicationContext wac;
	
	private MockMvc mockMvc;
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), 
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	private final static Logger USER_LOGGER = LoggerFactory.getLogger(UserManagementControllerTest.class);
	
	/************************* URL ************************************/
	//사용자 관리 화면 이동
	final static String VIEW_URL = "/admin/user";
	//사용자 목록 정보 요청
	final static String USER_LIST = "/admin/user/list";
	//사용자 등록 팝업 화면 실행 시 권한 목록 요청
	final static String ROLE_LIST = "/admin/role/group/list";
	//사용자 정보 등록
	final static String USER_ADD = "/admin/user/add";
	//사용자 정보 수정
	final static String USER_UPDATE = "/admin/user/update/tester";
	//사용자 정보 삭제
	final static String USER_DELETE = "/admin/user/delete/tester";
	/************************* URL ************************************/
	
	/***************************************************
	 * @project 	: OpenPaas 플랫폼 설치 자동
	 * @description : 하나의 메소드가 동작하기 전 실행
	 * @title 		: setUp
	 * @return 		: void
	***************************************************/
	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
		
		getLoggined();
	}
	
	/***************************************************
	 * @project 	: OpenPaas 플랫폼 설치 자동
	 * @description : 사용자 관리 화면 이동 테스트
	 * @title 		: TestGoUserManagement
	 * @return 		: void
	***************************************************/
	@Test
	public void testGoUserManagement() throws Exception{
		if( USER_LOGGER.isInfoEnabled() ){ 
			USER_LOGGER.info("=================  사용자 관리 화면 이동 요청  ================="); 
		}
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if( USER_LOGGER.isInfoEnabled() ){ 
			USER_LOGGER.info("=================  사용자 관리 화면 이동 끝  ================="); 
		}
	}
	
   	/***************************************************
   	 * @project 	: OpenPaas 플랫폼 설치 자동
   	 * @description : 사용자 목록 정보 조회 테스트
   	 * @title 		: testGetUserInfoList
   	 * @return 		: void
   	***************************************************/
   	@Test
   	public void testGetUserInfoList() throws Exception{
   		if(USER_LOGGER.isInfoEnabled()){  USER_LOGGER.info("=================  사용자 목록 정보 조회 요청"); }
   		ResultActions result = mockMvc.perform(get(USER_LIST));
   		result.andDo(MockMvcResultHandlers.print()) //PrintingResultHandler 결과값 출력
   		.andReturn();
   		if(USER_LOGGER.isInfoEnabled()){  USER_LOGGER.info("=================  사용자 목록 정보 조회 끝"); }
   	}
   	
   	/***************************************************
   	 * @project 	: OpenPaas 플랫폼 설치 자동
   	 * @description : 권한 목록 조회 테스트
   	 * @title 		: testGetRoleList
   	 * @return 		: void
   	***************************************************/
   	@Test
   	public void testGetRoleList() throws Exception{
   		if(USER_LOGGER.isInfoEnabled()){  USER_LOGGER.info("=================  권한 목록 정보 조회 요청"); }
   		ResultActions result = mockMvc.perform(get(ROLE_LIST));
   		result.andDo(MockMvcResultHandlers.print()) //PrintingResultHandler 결과값 출력
   		.andReturn();
   		if(USER_LOGGER.isInfoEnabled()){  USER_LOGGER.info("=================  권한 목록 정보 조회 끝"); }
   	}
   	
   	/***************************************************
   	 * @project 	: OpenPaas 플랫폼 설치 자동
   	 * @description : 사용자 정보 등록 테스트
   	 * @title 		: testSaveUserInfo
   	 * @return 		: void
   	***************************************************/
   	@Rollback(true)
   	@Test
   	public void testSaveUserInfo() throws Exception{
   		if(USER_LOGGER.isInfoEnabled()){  USER_LOGGER.info("=================  사용자 정보 등록 테스트 요청"); }
   		String requestJson = setUserRegistInfo("add");
		 ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(USER_ADD)
		    		.contentType(APPLICATION_JSON_UTF8)
					.content(requestJson));
			
			result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andReturn();
   		if(USER_LOGGER.isInfoEnabled()){  USER_LOGGER.info("=================  사용자 정보 등록 테스트 끝"); }
   	}
   	
	/***************************************************
	 * @project 	: OpenPaas 플랫폼 설치 자동
	 * @description : 사용자 정보 수정 테스트
	 * @title 		: testUpdateUserInfo
	 * @return 		: void
	***************************************************/
	@Rollback(true)
   	@Test
   	public void testUpdateUserInfo() throws Exception{
   		if(USER_LOGGER.isInfoEnabled()){  USER_LOGGER.info("=================  사용자 정보 수정 테스트 요청"); }
   		testSaveUserInfo();
   		String requestJson = setUserRegistInfo("update");
		 ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(USER_UPDATE)
				 	.param("userId", "tester")
		    		.contentType(APPLICATION_JSON_UTF8)
					.content(requestJson));
			
			result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andReturn();
   		if(USER_LOGGER.isInfoEnabled()){  USER_LOGGER.info("=================  사용자 정보 수정 테스트 끝"); }
   	}
   	
	/***************************************************
	 * @project 	: OpenPaas 플랫폼 설치 자동
	 * @description : 사용자 정보 삭제 테스트
	 * @title 		: testDeleteUserInfo
	 * @return 		: void
	***************************************************/
	@Rollback(true)
   	@Test
   	public void testDeleteUserInfo() throws Exception{
   		if(USER_LOGGER.isInfoEnabled()){  USER_LOGGER.info("=================  사용자 정보 삭제 테스트 요청"); }
   		testSaveUserInfo();
		 ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(USER_DELETE)
				 	.param("userId", "tester"));
			
			result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isNoContent())
			.andReturn();
   		if(USER_LOGGER.isInfoEnabled()){  USER_LOGGER.info("=================  사용자 정보 삭제 테스트 끝"); }
   	}
	
   	/***************************************************
	 * @project 	: OpenPaas 플랫폼 설치 자동
	 * @description : 각 TEST 실행 시 필요한 JSON 데이터 모음
	 * @title 		: setUserRegistInfo
	 * @return 		: String
	***************************************************/
	private String setUserRegistInfo(String func) throws Exception{
		UserManagementVO userVO = new UserManagementVO();
		if(func.equals("add")){
			userVO.setUserId("tester");
			userVO.setUserName("tester");
			userVO.setUserPassword("1234");
			userVO.setEmail("admin@cloud4u.co.kr");
			userVO.setRoleId("2");
			userVO.setInitPassYn("N");
		}else if(func.equals("update")){
			userVO.setUserName("Testadmin_수정");
			userVO.setUserPassword("1234");
			userVO.setEmail("admin@cloud4u.co.kr");
			userVO.setUpdateUserId("tester");
			userVO.setRoleId("2");
			userVO.setInitPassYn("N");
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson= ow.writeValueAsString(userVO);
	    return requestJson;
	}
	
}
