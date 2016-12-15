package org.openpaas.ieda.web.config.systemRelease;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.File;
import java.io.FileInputStream;
import java.security.Principal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.BaseTestController;
import org.openpaas.ieda.web.config.systemRelease.dto.ReleaseManagementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class})
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
public class ReleaseManagementControllerTest extends BaseTestController{

	@Autowired WebApplicationContext wac;
	@Autowired ReleaseManagementDownloadServiceTest service;
	
	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	private Principal principal = null;
	
	final private static String SEPARATOR = System.getProperty("file.separator");
	final private static String FILE_PATH = System.getProperty("user.dir") + SEPARATOR + "src/test/java/org/openpaas/ieda/web/assets/dummy-release.tgz";
	final private static String RELEASE_PATH = LocalDirectoryConfiguration.getReleaseDir() + SEPARATOR + "dummy-release.tgz";
	final private static String RELEASE_REAL_PATH = LocalDirectoryConfiguration.getReleaseDir() + SEPARATOR + "bosh-openstack-cpi-release-20.tgz";
	final private static String RELEASE_LOCK_PATH = LocalDirectoryConfiguration.getLockDir()+System.getProperty("file.separator")+"bosh-openstack-cpi-release-20-download.lock";
	private final static Logger LOGGER = LoggerFactory.getLogger(ReleaseManagementControllerTest.class);
	
	/*************************************** URL *******************************************/
	final static String VIEW_URL = "/config/systemRelease"; //시스템 릴리즈 관리 화면 요청
	final static String SYSTEM_RELEASE_LIST_URL = "/config/systemRelease/list"; //시스템 릴리즈 목록 정보 조회
	final static String SYSTEM_RELEASE_REGIST_URL = "/config/systemRelease/regist/Y"; // 시스템 릴리즈 입력 정보 저장
	final static String SYSTEM_RELEASE_UPLOAD_URL = "/config/systemRelease/regist/upload"; //릴리즈 파일 업로드
	final static String SYSTEM_RELEASE_DELETE_URL = "/config/systemRelease/delete"; //시스템 릴리즈 삭제
	
	
	
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
		
		principal = getLoggined();
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 시스템 릴리즈 관리 화면 요청
	 * @title               : testGoReleaseManagement
	 * @return            : void
	***************************************************/
	@Test
	public void testGoReleaseManagement() throws Exception {
		
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  시스템 릴리즈 관리 화면 TEST START  ================="); }
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));

		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  시스템 릴리즈 관리 화면 이동 TEST END  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 시스템 릴리즈 목록 정보 조회
	 * @title               : testGetSystemReleases
	 * @return            : void
	***************************************************/
	@Test
	public void testGetSystemReleases() throws Exception {
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  시스템 릴리즈 목록 정보 조회 TEST START  ================="); }
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(SYSTEM_RELEASE_LIST_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  시스템 릴리즈 목록 정보 조회 TEST END  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : URL을 통한 시스템 릴리즈 등록 
	 * @title               : testSaveSystemReleaseUrl
	 * @return            : void
	***************************************************/
	@Test
	@Rollback(value=true)
	public void testSaveSystemReleaseUrl() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  시스템 릴리즈 등록 TEST START  ================="); }
		
		for(int i=0; i<2; i++ ){
			switch(i){
			// 시스템 릴리즈 입력 정보 저장 (url)
			case 0 : testSystemReleaseRegist("url");
					 break;
			case 1 : testSystemReleaseDownloading();
					 break;
			default : break;
			}
			File lockFile = new File(RELEASE_LOCK_PATH);
			if(lockFile.exists()){
				lockFile.delete();
			}
		}
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  시스템 릴리즈 등록 TEST END  ================="); }
	}
	
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로컬 파일을 통해 시스템 릴리즈 등록
	 * @title               : testSaveSystemReleaseFile
	 * @return            : void
	***************************************************/
	@Test
	@Rollback(value=true)
	public void testSaveSystemReleaseFile() throws Exception{
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  로컬 파일을 통해 시스템 릴리즈 등록 TEST START  ================="); }
		testSystemReleaseRegist("file");
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  로컬 파일을 통해 시스템 릴리즈 등록 TEST  END  ================="); }
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 시스템 릴리즈 입력 정보 저장 
	 * @title               : testSystemReleaseRegist
	 * @return            : void
	***************************************************/
	public void testSystemReleaseRegist(String fileType) throws Exception {
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  시스템 릴리즈 입력 정보 저장 TEST START  ================="); }
		
		String requestJson = "";
		if("url".equals(fileType)){
			requestJson = setReleaseRegistInfoUrl(fileType);
		}else{
			requestJson = setReleaseRegistInfoFile(fileType);
		}

	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(SYSTEM_RELEASE_REGIST_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  시스템 릴리즈 입력 정보 저장 TEST END  ================="); }
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : URL을 통한 릴리즈 다운로드
	 * @title               : testSystemReleaseDownloading
	 * @return            : void
	***************************************************/
	public void testSystemReleaseDownloading() throws Exception {
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  URL을 통한 릴리즈 다운로드 TEST START  ================="); }
		ReleaseManagementDTO.Regist dto = setReleaseDownload();
		Principal principalTest = principal; 
		service.testReleaseDownloadAsync(dto, principalTest);
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  URL을 통한 릴리즈 다운로드 TEST END  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로컬 File을 통한 릴리즈 다운로드
	 * @title               : testDoSystemReleaseUpload
	 * @return            : void
	***************************************************/
	@Test
	@Rollback(value=true)
	public void testDoSystemReleaseUpload() throws Exception {
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  로컬 File을 통한 릴리즈 다운로드 TEST START  ================="); }
		
		FileInputStream inputFile = new FileInputStream(FILE_PATH);
		MockMultipartFile firstFile = new MockMultipartFile("file", "bosh-openstack-cpi-release-20.tgz", "multipart/form-data", inputFile);
		
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.fileUpload(SYSTEM_RELEASE_UPLOAD_URL)
        						.file(firstFile)
        						.param("overlay", "true")
        						.param("id", "1"));
        		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  로컬 File을 통한 릴리즈 다운로드 TEST END  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 시스템 릴리즈 삭제
	 * @title               : testSystemReleaseDelete
	 * @return            : void
	***************************************************/
	@Test
	@Rollback(value=true)
	public void testSystemReleaseDelete() throws Exception {
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  시스템 릴리즈 삭제 TEST START  ================="); }
		
		testSystemReleaseRegist("url");
		testSystemReleaseDownloading();
		
		String requestJson = setReleaseDelete();

	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(SYSTEM_RELEASE_DELETE_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		File releaseFile = new File(RELEASE_PATH);
		if(releaseFile.exists()){
			releaseFile.delete();
		}
		
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  시스템 릴리즈 삭제 TEST END  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : URL을 통한 릴리즈 정보 설정 
	 * @title               : setReleaseRegistInfoUrl
	 * @return            : String
	***************************************************/
	public String setReleaseRegistInfoUrl(String fileType) throws Exception{
		ReleaseManagementDTO.Regist dto = new ReleaseManagementDTO.Regist();
		dto.setId(1);
		dto.setReleaseFileName("https://bosh.io/d/github.com/cloudfoundry-incubator/bosh-openstack-cpi-release?v=20");
		dto.setReleaseName("bosh-test-cpi-release/20");
		dto.setReleaseSize("123456789");
		dto.setFileType(fileType);
		dto.setOverlayCheck("true");
		dto.setReleaseType("cpi");
		dto.setCreateUserId("tester");
		dto.setUpdateUserId("tester");
		
		//JSON 형태로 변환
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson= ow.writeValueAsString(dto);
		
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로컬 File을 통한 릴리즈 등록 정보 설정
	 * @title               : setReleaseRegistInfoFile
	 * @return            : String
	***************************************************/
	public String setReleaseRegistInfoFile(String fileType) throws Exception{
		ReleaseManagementDTO.Regist dto = new ReleaseManagementDTO.Regist();
		dto.setId(1);
		dto.setReleaseFileName("bosh-openstack-cpi-release-20.tgz");
		dto.setReleaseName("bosh-test-cpi-release");
		dto.setReleaseSize("123456789");
		dto.setFileType(fileType);
		dto.setOverlayCheck("true");
		dto.setReleaseType("cpi");
		dto.setCreateUserId("admin");
		dto.setUpdateUserId("admin");
		
		//JSON 형태로 변환
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson= ow.writeValueAsString(dto);
		
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 시스템 릴리즈 다운로드 설정
	 * @title               : setReleaseDownload
	 * @return            : ReleaseManagementDTO.Regist
	***************************************************/
	public ReleaseManagementDTO.Regist setReleaseDownload() {
		
		ReleaseManagementDTO.Regist dto = new ReleaseManagementDTO.Regist();
		dto.setId(1);
		dto.setReleaseName("bosh-test-cpi-release/20");
		dto.setReleaseSize("123456789");
		dto.setFileType("url");
		dto.setReleaseFileName("https://bosh.io/d/github.com/cloudfoundry-incubator/bosh-openstack-cpi-release?v=20");
		dto.setOverlayCheck("true");
		dto.setReleaseType("cpi");
		
		return dto;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 시스템 릴리즈 삭제 입력 설정
	 * @title               : setReleaseDelete
	 * @return            : String
	***************************************************/
	public String setReleaseDelete() throws Exception {
		ReleaseManagementDTO.Delete dto = new ReleaseManagementDTO.Delete();
		dto.setId("1");
		dto.setReleaseFileName("bosh-openstack-cpi-release-20.tgz");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson= ow.writeValueAsString(dto);
		
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 하나의 메소드가 동작한 직후 실행
	 * @title               : tearDown
	 * @return            : void
	***************************************************/
	@After
	public void tearDown(){
		//delete Release File
		File file = new File(RELEASE_REAL_PATH);
		if(file.exists()){
			file.delete();
		}
		//delte Release lock File
		File lockFile = new File(RELEASE_LOCK_PATH);
		if(lockFile.exists()){
			lockFile.delete();
		}
	}
}
