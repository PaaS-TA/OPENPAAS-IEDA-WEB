package org.openpaas.ieda.web.management.code.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.charset.Charset;

import org.hornetq.utils.json.JSONException;
import org.hornetq.utils.json.JSONObject;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class, TestBeansConfiguration.class})
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
public class CommonCodeControllerTest extends BaseTestController{
@Autowired WebApplicationContext wac;
	
	private MockMvc mockMvc;
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), 
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	private final static Logger LOGGER = LoggerFactory.getLogger(CommonCodeControllerTest.class);
	
	/*************************************** URL *******************************************/
	final static String VIEW_URL = "/admin/code";//
	final static String CODE_GROUPLIST_URL = "/admin/code/groupList";
	final static String CODE_CODELIST_URL = "/admin/code/codeList/10";
	final static String CODE_ADD_URL = "/admin/code/add";
	final static String CODE_UPDATE_URL = "/admin/code/update/10";
	final static String CODE_DELETE_URL = "/admin/code/delete/10";
	
	
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
	 * @description   : 코드 관리 화면 테스트 요청
	 * @title               : testGoCodeManagement
	 * @return            : void
	***************************************************/
	@Test
	public void testGoCodeManagement() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  코드 관리 화면 이동 테스트 요청  ================="); }
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON)); //실행 결과 값이 리턴이 된다
		
		result.andDo(MockMvcResultHandlers.print()) //PrintingResultHandler 결과값 출력
		.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  코드 관리 화면 이동 테스트 성공  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 그룹 목록 정보 조회
	 * @title               : testGetCodeGroups
	 * @return            : void
	***************************************************/
	@Test
	public void testGetCodeGroups() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  코드 그룹 조회 테스트 요청"); }
		ResultActions result = mockMvc.perform(get(CODE_GROUPLIST_URL));
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  코드 그룹 조회 테스트 성공"); }
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 그룹 등록
	 * @title               : testCreateCode
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testCreateCode() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  코드 그룹 등록 테스트 요청"); }
			String requestJson = setCodeGroupRegistInfo();
			 ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(CODE_ADD_URL)
			    		.contentType(APPLICATION_JSON_UTF8)
						.content(requestJson));
				
				result.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  코드 그룹 등록 테스트 성공"); }
	}
	
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 그룹 수정
	 * @title               : testUpdateCode
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testUpdateCode() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  코드 그룹 수정 테스트 요청"); }
		testCreateCode();
		String requestJson = setCodeGroupUpdateInfo();
			 ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(CODE_UPDATE_URL)
			    		.contentType(APPLICATION_JSON_UTF8)
						.content(requestJson)
						.param("codeIdx", "10"));
				result.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  코드 그룹 수정 테스트 성공"); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 그룹 삭제
	 * @title               : testDeleteCode
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDeleteCode() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  코드 그룹 삭제 테스트 요청"); }
		testCreateCode();
		String requestJson = setCodeGroupUpdateInfo();
			 ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(CODE_UPDATE_URL)
			    		.contentType(APPLICATION_JSON_UTF8)
						.content(requestJson)
						.param("codeIdx", "10"));
				
				result.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  코드 그룹 삭제 테스트 성공"); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 그룹 목록 정보 조회
	 * @title               : testGetCodeList
	 * @return            : void
	***************************************************/
	@Test
	public void testGetCodeList() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  코드 조회 테스트 요청"); }
		testCreateCode();
		ResultActions result = mockMvc.perform(get(CODE_CODELIST_URL)
				.param("parentCode", "10"));
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  코드 조회 테스트 성공"); }
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 그룹 추가 정보 설정
	 * @title               : setCodeGroupRegistInfo
	 * @return            : String
	***************************************************/
	private String setCodeGroupRegistInfo() throws JSONException {
		JSONObject jobj = new JSONObject();
		jobj.put("codeIdx", 10);
		jobj.put("codeName", "CodeNameTest");
		jobj.put("codeValue", "CodeValueTest");
		jobj.put("codeDescription", "CodeDescriptionTest");
		jobj.put("sortOrder", 0);
		jobj.put("createUserId", "tester");
		jobj.put("updateUserId", "tester");
		return jobj.toString();
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 그룹 수정 정보 설정
	 * @title               : setCodeGroupUpdateInfo
	 * @return            : String
	***************************************************/
	private String setCodeGroupUpdateInfo() throws JSONException {
		
		JSONObject jobj = new JSONObject();
		jobj.put("codeName", "CodeNameTest");
		jobj.put("codeValue", "CodeValueTest");
		jobj.put("codeDescription", "codeDescriptionTest");
		jobj.put("codeNameKR", "제이유닛 테스트");
		jobj.put("parentCode", "11111");
		jobj.put("createUserId", "tester");
		jobj.put("updateUserId", "tester");
		
		return jobj.toString();
	}
}
