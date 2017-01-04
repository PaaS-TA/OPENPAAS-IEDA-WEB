package org.openpaas.ieda.web.information.stemcell;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.security.Principal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.TestBeansConfiguration;
import org.openpaas.ieda.web.common.BaseTestController;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementVO;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class, TestBeansConfiguration.class})
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
public class StemcellControllerTest extends BaseTestController{
	@Autowired WebApplicationContext wac;
	@Autowired StemcellServiceTest service;
	
	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	private Principal principal = null;
	private final static Logger LOGGER = LoggerFactory.getLogger(StemcellControllerTest.class);
	
	/************************* URL ************************************/
	final static String VIEW_URL = "/info/stemcell"; //스템셀 업로드 화면 요청
	final static String UPLOAD_STEMCELL_LIST_URL = "/info/stemcell/list/upload"; //업로드된 스템셀 목록 정보 조회
	final static String DOWNLOAD_STEMCELL_LIST_URL = "/info/stemcell/list/local/OPENSTACK"; //로컬에 다운로드 된 스템셀 목록 정보 조회
	/************************* URL **********************************/
	
	/***************************************************
	 * @project 	: OpenPaas 플랫폼 설치 자동0
	 * @description : 하나의 메소드가 동작한 직후 실행
	 * @title 		: setUp
	 * @return 		: void
	***************************************************/
	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
		principal = getLoggined();
	}
	
	/***************************************************
	 * @project 	: OpenPaas 플랫폼 설치 자동
	 * @description : 스템셀 관리 화면 요청
	 * @title 		: testGoStemcellManagement
	 * @return 		: void
	***************************************************/
	@Test
	public void testGoStemcellManagement() throws Exception {
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================   스템셀 업로드 화면 이동 START  ================="); }
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================   스템셀 업로드 화면 이동 END  ================="); }
	}
	
	/***************************************************
	 * @project 	: OpenPaas 플랫폼 설치 자동
	 * @description : 스템셀 목록 정보 조회
	 * @title 		: testGetUploadStemcellLIst
	 * @return 		: void
	***************************************************/
	@Test
	public void testGetUploadStemcellLIst() throws Exception {
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  업로드 된 스템셀 정보 조회 START  ================="); }
		List<StemcellManagementVO> list = service.uploadedStemcellList();
		for(int i=0;i<list.size();i++){
			LOGGER.debug(list.get(i).getOsVersion());
			LOGGER.debug(list.get(i).getStemcellVersion());
			LOGGER.debug(list.get(i).getStemcellFileName());
		}
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  업로드 된 스템셀 정보 조회 END  ================="); }
	}
	
	/***************************************************
	 * @project 	: OpenPaas 플랫폼 설치 자동
	 * @description : 스템셀 목록 정보 조회
	 * @title 		: testGetLocalStemcellList
	 * @return 		: void
	***************************************************/
	@Test
	public void testGetLocalStemcellList() throws Exception {
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  로컬에 다운로드 된 스템셀 정보 조회 START  ================="); }
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(DOWNLOAD_STEMCELL_LIST_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.param("iaaS", "OPENSTACK"));
		
		result.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  로컬에 다운로드 된 스템셀 정보 조회 END  ================="); }
	}
	
	/***************************************************
	 * @project 	: OpenPaas 플랫폼 설치 자동
	 * @description : 스템셀 업로드
	 * @title 		: testGetLocalStemcellList
	 * @return 		: void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDoUploadStemcell() throws Exception {
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  스템셀 업로드 START  ================="); }
		String userId = principal.getName();
		service.uploadStemcellAsync(userId);
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  스템셀 업로드 END  ================="); }
	}
	
	/***************************************************
	 * @project 	: OpenPaas 플랫폼 설치 자동
	 * @description : 스템셀 업로드
	 * @title 		: testDoDeleteStemcell
	 * @return 		: void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDoDeleteStemcell() throws Exception {
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  업로드 된 스템셀 삭제 START  ================="); }
		service.deleteStemcellAsync();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  업로드 된 스템셀 삭제 END  ================="); }
	}
	
	
}
