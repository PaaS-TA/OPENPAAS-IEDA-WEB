package org.openpaas.ieda.web.deploy.servicePack;

import java.io.File;
import java.io.FileInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.BaseTestController;
import org.openpaas.ieda.web.deploy.servicepack.dto.ServicePackParamDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
public class ServicePackControllerTest extends BaseTestController{
	@Autowired WebApplicationContext wac;
	@Autowired ServicePackServiceTest service;
	
	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	final private static String SEPARATOR = System.getProperty("file.separator");
	final private static String FILE_PATH = System.getProperty("user.dir") + SEPARATOR + "src/test/java/org/openpaas/ieda/web/assets/servicepack-test.yml";
	final private static String MANIFEST_DIRECTORY	= LocalDirectoryConfiguration.getManifastDir() + SEPARATOR+"servicepack-test.yml";
	final private static String DEPLOYMENT_DIRECTORY	= LocalDirectoryConfiguration.getDeploymentDir() + SEPARATOR+"servicepack-test.yml";
	private final static Logger LOGGER = LoggerFactory.getLogger(ServicePackControllerTest.class);
	
	
	/*************************************** URL *******************************************/
	final static String VIEW_URL = "/deploy/servicePack"; // 서비스팩 화면 이동
	final static String SERVICEPACK_LIST_URL = "/deploy/servicePack/list/OPENSTACK"; // 서비스팩 정보 목록 조회
	final static String DEPLOYMENT_MANIFESTFILE_LIST_URL = "/deploy/servicePack/list/deploymentFileName"; // 업로드된 메니패스트 파일 조회
	final static String SAVE_SERVICEPACKINFO_URL = "/deploy/servicePack/install/saveServicePackinfo/Y";// 서비스팩 정보 저장
	final static String CREATE_SETTINGFILE_URL = "/deploy/servicePack/install/createSettingFile/1/Y";// 서비스팩 배포 파일 생성
	final static String MANIFEST_UPLOAD_URL = "/info/manifest/upload/Y"; //메니페스트 파일 업로드
	final static String DELETE_SERVICEPACK_RECORD_URL = "/deploy/servicePack/delete/data"; //서비스팩 단순 레코드 삭제
	final static String SERACH_SERVICEPACK_URL = "/deploy/servicePack/list/manifest/search/OPENSTACK"; //서비스팩 검색
	
	
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
	 * @description   : Servicepack 설치 화면 이동
	 * @title               : testGoServicePack
	 * @return            : void
	***************************************************/
	@Test
	public void testGoServicePack() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================   서비스팩 설치 화면 이동 TEST START  ================="); 
		}
	    ResultActions result = mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON));
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  서비스팩 설치 화면 이동 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Servicepack 전체 목록 조회
	 * @title               : testServicePackList
	 * @return            : void
	***************************************************/
	@Test
	public void testServicePackList() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================   서비스팩 전체 목록 조회  TEST START  ================="); 
		}
	    ResultActions result = mockMvc.perform(get(DEPLOYMENT_MANIFESTFILE_LIST_URL).contentType(MediaType.APPLICATION_JSON)
	    		.param("iaas", "OPENSTACK"));
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  서비스팩 전체 목록 조회 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 업로드된 Manifest 목록 요청
	 * @title               : testgetDeployManifestList
	 * @return            : void
	***************************************************/
	@Test
	public void testGetDeployManifestList() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================   업로드된 Manifest 목록 요청 TEST START  ================="); 
		}
	    ResultActions result = mockMvc.perform(get(DEPLOYMENT_MANIFESTFILE_LIST_URL).contentType(MediaType.APPLICATION_JSON));
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  업로드된 Manifest 목록 요청 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 업로드
	 * @title               : testUploadManifest
	 * @return            : void
	***************************************************/
	public void testUploadManifest() throws Exception {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 파일 업로드 요청"); }
		FileInputStream inputFile = new FileInputStream(FILE_PATH);
		MockMultipartFile firstFile = new MockMultipartFile("file", "servicepack-test.yml", "multipart/form-data", inputFile);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.fileUpload(MANIFEST_UPLOAD_URL)
        						.file(firstFile)
        						.param("description", "servicepack manifest test yml file")
        						.param("iaas", "OPENSTACK")
        						.param("test", "Y"));
        		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andReturn();

		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 파일 업로드 성공"); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 정보 저장
	 * @title               : testSaveServicePackInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveServicePackInfo() throws Exception{
		testUploadManifest();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  서비스팩 정보 저장  TEST START  ================="); 
		}
		String requestJson = setServicePackInfo();
	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(SAVE_SERVICEPACKINFO_URL)
	    		.param("test", "Y")
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  서비스팩 정보 저장  TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포파일 생성
	 * @title               : testMakeDeploymentFile
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testMakeDeploymentFile() throws Exception{
		testUploadManifest();
		service.saveServicePackInfo();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  배포 파일 생성 TEST START  ================="); 
		}
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(CREATE_SETTINGFILE_URL)
				.param("id", "1")
				.param("test", "Y")
				.contentType(APPLICATION_JSON_UTF8));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isNoContent())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  배포 파일 생성 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치
	 * @title               : testDoInstallServicePack
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDoInstallServicePack() throws Exception {
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  서비스팩 설치  TEST START  ================="); 
		}
		testUploadManifest();
		ServicePackParamDTO dto = setInstallInfo();
		service.deployAsync(dto);
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  서비스팩 설치  TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 단순 레코드 삭제
	 * @title               : testDeleteJustOnlyServicePackRecord
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDeleteJustOnlyServicePackRecord() throws Exception{
		testUploadManifest();
		service.saveServicePackInfo();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  서비스팩 단순 레코드 삭제 TEST START  ================="); 
		}
		String requestJson = setDeleteInfo();
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_SERVICEPACK_RECORD_URL)
				.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isNoContent())
		.andReturn();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  서비스팩 단순 레코드 삭제 TEST END  ================="); 
		}
	}
	

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스 플랫폼 삭제 요청
	 * @title               : testDeleteService
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDeleteService() throws Exception {
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  서비스팩 플랫폼 삭제 TEST START  ================="); 
		}
		
		ServicePackParamDTO dto = new ServicePackParamDTO();
		dto.setId(1);
		dto.setIaas("openstack");
		
		service.deleteDeployAsync(dto);
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  서비스팩 플랫폼 삭제 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 정보 검색
	 * @title               : testSearchManifestList
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSearchManifestList() throws Exception{
		testUploadManifest();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  서비스팩 정보 검색  TEST START  ================="); 
		}
	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(SERACH_SERVICEPACK_URL)
	    		.param("searchVal", "OPENSTACK")
	    		.contentType(APPLICATION_JSON_UTF8));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  서비스팩 정보 검색  TEST END  ================="); 
		}
	}
	
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 정보 설정
	 * @title               : setServicePackInfo
	 * @return            : String
	***************************************************/
	private String setServicePackInfo() throws JsonProcessingException {
		ServicePackParamDTO dto = new ServicePackParamDTO();
		dto.setId(1);
		dto.setDeploymentFile("servicepack-test.yml");
		dto.setDeploymentName("openstack-servicepack");
		dto.setIaas("OPENSTACK");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 설치 DTO 설정
	 * @title               : setInstallInfo
	 * @return            : ServicePackParamDTO
	***************************************************/
	private ServicePackParamDTO setInstallInfo() {
		ServicePackParamDTO dto = new ServicePackParamDTO();
		dto.setId(1);
		dto.setIaas("OPENSTACK");
		return dto;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 삭제 정보 설정
	 * @title               : setDeleteInfo
	 * @return            : String
	***************************************************/
	private String setDeleteInfo() throws Exception {
		ServicePackParamDTO dto = new ServicePackParamDTO();
		dto.setId(1);
		dto.setIaas("OPENSTACK");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
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
		//delete Manfest File
		File file = new File(MANIFEST_DIRECTORY);
		if(file.exists()){
			file.delete();
		}
		//delete deployment ServicePack File
		File file2 = new File(DEPLOYMENT_DIRECTORY);
		if(file2.exists()){
			file2.delete();
		}
	}
	
}
