package org.openpaas.ieda.web.management.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.TestBeansConfiguration;
import org.openpaas.ieda.web.common.BaseTestController;
import org.openpaas.ieda.web.management.auth.dao.AuthManagementVO;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class, TestBeansConfiguration.class})
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
public class AuthManagementControllerTest extends BaseTestController{
	
	@Autowired WebApplicationContext wac;
	
	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	
	public static final MediaType APPLICATION_JSON_UTF8 = 
			new MediaType(MediaType.APPLICATION_JSON.getType(),  MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	private final static Logger LOGGER = LoggerFactory.getLogger(AuthManagementControllerTest.class);
	
	
	/*************************************** URL *******************************************/
	//권한 관리 화면 이동
	final static String VIEW_URL = "/admin/role";
	//권한 그룹 목록 정보 조회
	final static String ROLE_ROLE_LIST = "/admin/role/group/list";
	//상세 권한 목록 정보 조회
	final static String ROLE_ROLE_DETAIL_LIST = "/admin/role/group/10";
	//공통 코드 목록 정보 조회
	final static String COMMON_CODE_LIST = "/admin/role/commonCodeList";
	//권한 그룹 등록
	final static String ROLE_ADD ="/admin/role/group/add";
	//권한 그룹 수정
	final static String ROLE_UPDATE="/admin/role/group/update/10";
	//권한 그룹 삭제
	final static String ROLE_DELETE="/admin/role/group/delete/10";
	//상세 권한 수정
	final static String ROLE_DETAIL_UPDATE = "/admin/role/detail/update/10";

	
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
	 * @description   : 권한 관리 화면 요청 테스트
	 * @title               : testGoAuthManagement
	 * @return            : void
	***************************************************/
	@Test
	public void testGoAuthManagement() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  권한 관리 화면 이동 요청  ================="); }
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON)); //실행 결과 값이 리턴이 된다
		
		result.andDo(MockMvcResultHandlers.print()) //PrintingResultHandler 결과값 출력
		.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  권한 관리 화면 이동 성공  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 그룹 목록 정보 조회
	 * @title               : testGetRoleGroupList
	 * @return            : void
	***************************************************/
	@Test
	public void testGetRoleGroupList() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  권한 그룹 리스트 조회 요청"); }
		ResultActions result = mockMvc.perform(get(ROLE_ROLE_LIST));
		result.andDo(MockMvcResultHandlers.print()) //PrintingResultHandler 결과값 출력
		.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  권한 그룹 리스트 조회 성공"); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 상세 권한 목록 정보 조회 테스트
	 * @title               : testGetRoleDetailList
	 * @return            : void
	***************************************************/
	@Test
	public void testGetRoleDetailList() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  권한 그룹 하위 코드 리스트 조회 요청"); }
		ResultActions result = mockMvc.perform(get(ROLE_ROLE_DETAIL_LIST)
				.param("id", "10")
				.contentType(MediaType.APPLICATION_JSON)); 
		
		result.andDo(MockMvcResultHandlers.print()) 
		.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  권한 그룹 하위 코드 리스트 조회 성공"); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 공통 코드 목록 정보 조회 
	 * @title               : testGetCommonCodeList
	 * @return            : void
	***************************************************/
	@Test
	public void testGetCommonCodeList() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  권한 그룹 등록 요청 시 권한 코드 리스트 조회 요청"); }
		ResultActions result = mockMvc.perform(get(COMMON_CODE_LIST)
				.contentType(MediaType.APPLICATION_JSON)); //실행 결과 값이 리턴이 된다
		
		result.andDo(MockMvcResultHandlers.print()) //PrintingResultHandler 결과값 출력
		.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  권한 그룹 등록 요청 시 권한 코드 리스트 조회 성공"); }
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 그룹 등록
	 * @title               : testSaveRoleInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveRoleInfo() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  권한 관리 등록 테스트 요청"); }
			String requestJson = setroleRegistInfo("add");
			 ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(ROLE_ADD)
			    		.contentType(APPLICATION_JSON_UTF8)
						.content(requestJson));
				
				result.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  권한 관리 등록 테스트 성공"); }
	}
	

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 그룹 수정
	 * @title               : testUpdateRole
	 * @return            : void
	***************************************************/
	@Rollback(value=true)
	@Test
	public void testUpdateRole() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  권한 관리 수정 테스트 요청"); }
		testSaveRoleInfo();
		String requestJson = setroleRegistInfo("update");
		 ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(ROLE_UPDATE)
				 	.param("id", "10")
		    		.contentType(APPLICATION_JSON_UTF8)
					.content(requestJson));			
			result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andReturn();
				
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  권한 관리 수정 테스트 성공"); }
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 그룹 삭제 
	 * @title               : testDeleteRole
	 * @return            : void
	***************************************************/
	@Rollback(value=true)
	@Test
	public void testDeleteRole() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  권한 관리 삭제 테스트 요청"); }
		testSaveRoleInfo();
		String requestJson = setroleRegistInfo("delete");
		 ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(ROLE_DELETE)
				 	.param("id", "10")
		    		.contentType(APPLICATION_JSON_UTF8)
					.content(requestJson));
			
			result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isNoContent())
			.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  권한 관리 삭제 테스트 성공"); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 상세 권한 수정
	 * @title               : testUpdateRoleDetailInfo
	 * @return            : void
	***************************************************/
	@Rollback(value=true)
	@Test
	public void testUpdateRoleDetailInfo() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  권한 상세 업데이트 테스트 요청"); }
		testSaveRoleInfo();
		String requestJson = setroleRegistInfo("detail_update");
		 ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(ROLE_DETAIL_UPDATE)
				 	.param("id", "10")
		    		.contentType(APPLICATION_JSON_UTF8)
					.content(requestJson));
			
			result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  권한 상세 업데이트 테스트 성공"); }
	}
	
	/***************************************************
	 * @project 	: OpenPaas 플랫폼 설치 자동
	 * @description : 각 TEST 실행 시 필요한 JSON 데이터 모음
	 * @title 		: setroleRegistInfo
	 * @return 		: String
	***************************************************/
	private String setroleRegistInfo(String func) throws Exception{
		AuthManagementVO authVO = new AuthManagementVO();
		if(func.equals("add")){
			authVO.setRoleId(10);
			authVO.setRoleName("JunitTESTmyRole");
			authVO.setRoleDescription("JunitTESTmyRole");
		}else if(func.equals("update")){
			authVO.setRoleId(10);
			authVO.setRoleName("JunitTESTmyRole1");
			authVO.setRoleDescription("JunitTESTmyRole1");
			authVO.setUpdateUserId("TEST");
		}else if(func.equals("delete")){
			authVO.setRoleId(10);
		}else if(func.equals("detail_update")){
			authVO.setRoleId(10);
			List<String> list = new ArrayList<String>();	
			list.add(0, "111112");	
			authVO.setActiveYn(list);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson= ow.writeValueAsString(authVO);
	    return requestJson;
	}
}
