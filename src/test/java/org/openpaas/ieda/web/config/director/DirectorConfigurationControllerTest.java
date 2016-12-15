package org.openpaas.ieda.web.config.director;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.web.common.BaseTestController;
import org.openpaas.ieda.web.config.setting.dto.DirectorConfigDTO;
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
public class DirectorConfigurationControllerTest extends BaseTestController{
	@Autowired WebApplicationContext wac;
	@Autowired DirectorConfigurationServiceTest service;
	
	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	private final static Logger LOGGER = LoggerFactory.getLogger(DirectorConfigurationControllerTest.class);	
	final private static String SEPARATOR = System.getProperty("file.separator");
	final private static String BOSH_CONFIG_PATH = System.getProperty("user.home") + SEPARATOR + ".bosh_config_test";
	
	/************************* URL ************************************/
	final static String VIEW_URL = "/config/director"; //설치 관리자 설정 메뉴 이동
	final static String DIRECTOR_LIST_URL = "/config/director/list"; //설치 관리자 목록 정보 조회
	/************************* URL **********************************/
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 하나의 메소드가 실행되기전 호출
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
	 * @description   : 설치 관리자 설정 화면 요청
	 * @title               : testGoListDirector
	 * @return            : void
	***************************************************/
	@Test
	public void testGoListDirector() throws Exception {
		
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  설치 관리자 설정 화면 이동 TEST START  ================="); }
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================   설치 관리자 설정 화면 TEST END  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치 관리자 정보 목록 조회
	 * @title               : testGetListDirector
	 * @return            : void
	***************************************************/
	@Test
	public void testGetListDirector() throws Exception {
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================   설치 관리자 목록 조회 TEST START  ================="); }
		testCreateDirector();
		ResultActions result = mockMvc.perform(get(DIRECTOR_LIST_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================   설치 관리자 목록 조회 TEST END  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치 관리자 설정 추가
	 * @title               : testCreateDirector
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testCreateDirector() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  설치 관리자 설정 추가 TEST START  ================="); }
		DirectorConfigDTO.Create dto= setCreateDirectorInfo();
		service.createDirector(dto);
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  설치 관리자 설정 추가 TEST END  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치 관리자 설정 수정
	 * @title               : testUpdateDirector
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testUpdateDirector() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  설치 관리자 설정 수정 TEST  START  ================="); }
		testCreateDirector();
		DirectorConfigDTO.Update dto= setUpdateDirectorInfo();
		service.updateDirector(dto);
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  설치 관리자 설정 수정 TEST  END  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치 관리자 설정 삭제
	 * @title               : testDeleteDirector
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDeleteDirector() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  설치 관리자 설정 삭제 TEST START  ================="); }
		testCreateDirector();
		service.deleteDirector(1);
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  설치 관리자 설정 삭제 TEST END  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 설치 관리자 설정
	 * @title               : testsetDefaultDirector
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testsetDefaultDirector() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 기본 설치 관리자 설정 TEST START  ================="); }
		testCreateDirector();
		service.setDefaultDirector(1);
		if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 기본 설치 관리자 설정 TEST END  ================="); }
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치 관리자 추가 정보 설정
	 * @title               : setCreateDirectorInfo
	 * @return            : DirectorConfigDTO.Create
	***************************************************/
	public DirectorConfigDTO.Create setCreateDirectorInfo(){
		DirectorConfigDTO.Create dto = new DirectorConfigDTO.Create();
		dto.setIedaDirectorConfigSeq(1);
		dto.setDirectorPort(25555);
		dto.setDirectorUrl("10.10.10.10");
		dto.setUserId("test");
		dto.setUserPassword("test");
		return dto;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치 관리자 수정 정보 설정
	 * @title               : setUpdateDirectorInfo
	 * @return            : DirectorConfigDTO.Update
	***************************************************/
	public DirectorConfigDTO.Update setUpdateDirectorInfo(){
		DirectorConfigDTO.Update dto = new DirectorConfigDTO.Update();
		dto.setIedaDirectorConfigSeq(1);
		dto.setUserId("test");
		dto.setUserPassword("test");
		return dto;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 하나의 메소드가 실행된 후 호출
	 * @title               : tearDown
	 * @return            : void
	***************************************************/
	@After
	public void tearDown(){
		//delete .bosh_config_test File
		File file = new File(BOSH_CONFIG_PATH);
		if(file.exists()){
			file.delete();
		}
	}
}
