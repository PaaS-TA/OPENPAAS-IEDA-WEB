package org.openpaas.ieda.web.config.systemStemcell;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.File;
import java.util.List;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.BaseTestController;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.web.config.stemcell.dto.StemcellManagementDTO;
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
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class})
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
public class StemcellManagementControllerTest extends BaseTestController{
	
	@Autowired WebApplicationContext wac;
	@Autowired StemcellManagementServiceTest service;
	
	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(StemcellManagementControllerTest.class);
	final private static String SEPARATOR = System.getProperty("file.separator");
	final private static String STEMCELL_PATH = LocalDirectoryConfiguration.getStemcellDir() + SEPARATOR + "dummy-stemcell.tgz";
	
	/*************************************** URL *******************************************/
	final static String VIEW_URL = "/config/stemcell"; // 스템셀 관리 화면 요청
	final static String PUBLIC_STEMCELL_LIST_URL = "/config/stemcell/publicStemcells?os=Ubuntu&osVersion=Lucid&iaas=OpenStack"; //시스템 스템셀 목록 정보 조회
	final static String PUBLIC_STEMCELL_DELETE_URL = "/config/stemcell/deletePublicStemcell"; // 스템셀 삭제
	final static String PUBLIC_STEMCELL_SYNCHRONIZATION_URL = "/config/stemcell/syncPublicStemcell"; // 스템셀 삭제
	
	
	
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
	 * @description   :  스템셀 관리 화면 요청
	 * @title               : testGoStemcellManagement
	 * @return            : void
	***************************************************/
	@Test
	public void testGoStemcellManagement() throws Exception {
		
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================   스템셀 관리 화면 이동 TEST TEST START  ================="); }
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================   스템셀 관리 화면 이동 TEST TEST END  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 목록 정보 조회
	 * @title               : testGetPublicStemcells
	 * @return            : void
	***************************************************/
	@Test
	public void testGetPublicStemcells() throws Exception {
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  스템셀 목록 정보 조회 TEST START  ================="); }
		testDoSyncPublicStemcell();
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(PUBLIC_STEMCELL_LIST_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  스템셀 목록 정보 조회 TEST END  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 목록 동기화
	 * @title               : testDoSyncPublicStemcell
	 * @return            : void
	***************************************************/
	@Test
	public void testDoSyncPublicStemcell() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  스템셀 목록 동기화 TEST START  ================="); }
		List<StemcellManagementVO> list = service.syncPublicStemcell();
		for(int i=0;i<list.size();i++){
			LOGGER.debug(list.get(i).getStemcellVersion());
			LOGGER.debug(list.get(i).getStemcellFileName());
			LOGGER.debug(list.get(i).getSize());
			LOGGER.debug(list.get(i).getSublink());
		}
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  스템셀 목록 동기회 TEST END  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 다운로드
	 * @title               : testDoDownloadStemcell
	 * @return            : void
	***************************************************/
	@Test
	public void testDoDownloadStemcell() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  스템셀 다운로드 TEST START  ================="); }
		StemcellManagementDTO.Download dto = setDownloadStemcellData();
		service.testDoDownload(dto);
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  스템셀 다운로드 TEST END  ================="); }
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 다운로드된 스템셀 삭제
	 * @title               : testDoDeleteStemcell
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDoDeleteStemcell() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  스템셀 삭제 TEST START  ================="); }
        testDoDownloadStemcell();
        String requestJson = setDeleteStemcellInfo();
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(PUBLIC_STEMCELL_DELETE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isNoContent())
				.andReturn();
        
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  스템셀 삭제 TEST END  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 공개 스템셀 다운로드 정보 설정
	 * @title               : setDownloadStemcellData
	 * @return            : StemcellManagementDTO.Download
	***************************************************/
	public StemcellManagementDTO.Download setDownloadStemcellData(){
		StemcellManagementDTO.Download dto = new StemcellManagementDTO.Download();
		dto.setId(1);
		dto.setFileName("dummy-stemcell.tgz");
		dto.setSublink("bosh-stemcell/aws/light-bosh-stemcell-2624-aws-xen-ubuntu-lucid-go_agent.tgz");
		dto.setFileSize("5067");
		return dto;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 다운로드 된 스템셀 삭제 정보 설정
	 * @title               : setDeleteStemcellInfo
	 * @return            : String
	***************************************************/
	public String setDeleteStemcellInfo(){
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("id", 1);
		jsonObj.put("stemcellFileName", "dummy-stemcell.tgz");
		return jsonObj.toString();
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 하나의 메소드가 동작한 직후 실행
	 * @title               : tearDown
	 * @return            : void
	***************************************************/
	@After
	public void tearDown(){
		//delete deployment stemcell File
		File file = new File(STEMCELL_PATH);
		if(file.exists()){
			file.delete();
		}
	}
}
