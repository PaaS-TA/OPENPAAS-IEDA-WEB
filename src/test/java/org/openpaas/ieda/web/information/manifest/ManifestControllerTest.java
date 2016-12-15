package org.openpaas.ieda.web.information.manifest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.File;
import java.io.FileInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.BaseTestController;
import org.openpaas.ieda.web.information.manifest.dto.ManifestParamDTO;
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
public class ManifestControllerTest extends BaseTestController{
	
	@Autowired WebApplicationContext wac;

	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	
	final private static String SEPARATOR = System.getProperty("file.separator");
	final private static String FILE_PATH = System.getProperty("user.dir") + SEPARATOR + "src/test/java/org/openpaas/ieda/web/assets/servicepack-test.yml";
	final private static String MANIFEST_DIRECTORY	= LocalDirectoryConfiguration.getManifastDir() + SEPARATOR+"servicepack-test.yml";
	private final static Logger LOGGER = LoggerFactory.getLogger(ManifestControllerTest.class);
	
	/*************************************** URL *******************************************/
	final static String VIEW_URL = "/info/manifest"; //Manifest 정보 화면 이동
	final static String MANIFEST_INFO_URL = "/info/manifest/list";//Manifest 정보 전체 조회
	final static String MANIFEST_DETAIL_INFO_URL = "/info/manifest/update/1";//Manifest 정보 상세 조회
	final static String MANIFEST_UPLOAD_URL = "/info/manifest/upload/Y";//Manifest 업로드
	final static String MANIFEST_UPDATE_URL = "/info/manifest/update";//Manifest 내용 수정
	final static String MANIFEST_DOWNLOAD_URL = "/info/manifest/download/1";//Manifest 다운로드
	final static String MANIFEST_DELETE_URL = "/info/manifest/delete/1";//Manifest 삭제

	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 하나의 메소드가 동작하기 전에 실행
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
	 * @description   : Manifest 정보 화면 호출
	 * @title               : testGoListManifest
	 * @return            : void
	***************************************************/
	@Test
	public void testGoListManifest() throws Exception{
		if(LOGGER.isInfoEnabled()){ LOGGER.info("=================  Manifest 정보 화면 요청  ================="); }
			ResultActions result = mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON));
			result.andDo(MockMvcResultHandlers.print())
			.andReturn();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("=================  Manifest 정보 화면 요청 성공  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 정보 목록 조회
	 * @title               : testGetManifestList
	 * @return            : void
	***************************************************/
	@Test
	public void testGetManifestList() throws Exception {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 조회 요청"); }
			ResultActions result = mockMvc.perform(get(MANIFEST_INFO_URL)
					.contentType(MediaType.APPLICATION_JSON));
			
			result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 조회 성공"); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 해당 Manifest 파일 정보 조회
	 * @title               : testGetManifestInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testGetManifestInfo() throws Exception {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 조회 요청"); }
			//1.1 upload manifest
			testUploadManifest();
			//1.2 get mafniest info
			ResultActions result = mockMvc.perform(get(MANIFEST_DETAIL_INFO_URL)
					.param("id", "1")
					.contentType(MediaType.APPLICATION_JSON));
			
			result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 조회 성공"); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 업로드
	 * @title               : testUploadManifest
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testUploadManifest() throws Exception {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 파일 업로드 요청"); }
			FileInputStream inputFile = new FileInputStream(FILE_PATH);
			MockMultipartFile firstFile = new MockMultipartFile("file", "servicepack-test.yml", "multipart/form-data", inputFile);
	
	        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.fileUpload(MANIFEST_UPLOAD_URL)
	        						.file(firstFile)
	        						.param("description", "servicepack manifest file")
	        						.param("iaas", "OPENSTACK")
	        						.param("test", "Y"));
	        		
			result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andReturn();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 파일 업로드 성공"); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 파일 다운로드
	 * @title               : testDownloadManifestFile
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDownloadManifestFile() throws Exception {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 파일 업로드 요청"); }
		//1.1 upload manifest
		testUploadManifest();
		//1.2 download manifest
		mockMvc.perform(get(MANIFEST_DOWNLOAD_URL)
				.param("id", "1")
				.contentType(MediaType.APPLICATION_JSON));
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 파일 업로드 성공"); }
	
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 내용 수정
	 * @title               : testUpdateManifest
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testUpdateManifest() throws Exception {
		//1.1 upload manifest
		testUploadManifest();
		//1.2 update manifest contnets
		String requestJson = settingManifestContents();
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(MANIFEST_UPDATE_URL)
				.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andReturn();
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 삭제
	 * @title               : testDeleteManifest
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDeleteManifest() throws Exception {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 삭제 요청"); }
		//1.1 upload manifest
		testUploadManifest();
		//1.2 delete manifest
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(MANIFEST_DELETE_URL)
				.param("id", "1")
				.contentType(APPLICATION_JSON_UTF8));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isNoContent())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 삭제 성공"); }
		
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 내용 수정
	 * @title               : settingManifestContents
	 * @return            : String
	***************************************************/
	public String settingManifestContents() throws Exception{
		ManifestParamDTO dto = new ManifestParamDTO();
		
		String info = "---\ncompilation: \n  cloud_properties: \n    instance_type: m1.medium\n";
		info += "name: servicepack-test";
		dto.setContent(info);
		dto.setFileName("servicepack-test.yml");
		dto.setId("1");
		
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
		//delete manifest file
		File file = new File(MANIFEST_DIRECTORY);
		if(file.exists()){
			file.delete();
		}
	}
}
