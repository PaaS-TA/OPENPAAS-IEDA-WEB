package org.openpaas.ieda.web.deploy.cf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.TestBeansConfiguration;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.BaseTestController;
import org.openpaas.ieda.web.common.dto.KeyInfoDTO;
import org.openpaas.ieda.web.deploy.cf.dto.CfParamDTO;
import org.openpaas.ieda.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.web.deploy.common.dto.resource.ResourceDTO;
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
public class CfControllerTest extends BaseTestController {

	@Autowired WebApplicationContext wac;
	@Autowired CfServiceTest cfService;
	
	private MockMvc mockMvc;
	private Principal principal = null;
	private final static Logger LOGGER = LoggerFactory.getLogger(CfControllerTest.class);
	final private static String SEPARATOR = System.getProperty("file.separator");
	final private static String KEY_DIR = LocalDirectoryConfiguration.getKeyDir() + SEPARATOR + "openstack-cf-key-1.yml";

	/*************************************** URL *******************************************/
	final static String VIEW_URL = "/deploy/cf"; //Cf menu 화면 이동
	final static String POPUP_URL = "deploy/cf/install/cfPopup";//cf 팝업 화면
	final static String CF_LIST_URL = "/deploy/cf/list/openstack"; //Cf 정보 목록 조회
	final static String CF_DETAIL_URL = "/deploy/cf/install/detail/1"; //cf 상세 조회
	final static String SAVE_DEFAULT_INFO_URL = "/deploy/cf/install/saveDefaultInfo/Y";//기본 정보 저장
	final static String GET_COUNTRY_CODELIST_URL = "/common/deploy/codes/countryCode/20000";//국가 코드 조회
	final static String CREAT_KEY_INFO_URL ="/common/deploy/key/createKey";//Key 생성
	final static String SAVE_KEY_INFO_URL ="/deploy/cf/install/saveKeyInfo";//키 정보 저장
	final static String SAVE_NETWORK_INFO_URL="/deploy/cf/install/saveNetworkInfo";//네트워크 정보 저장
	final static String SAVE_RESOURCE_INFO_URL = "/deploy/cf/install/saveResourceInfo/Y";//리소스 정보 저장
	final static String MAKE_DEPLOYMENTFILE_URL = "/deploy/cf/install/createSettingFile/Y";//배포 파일 생성
	final static String DELETE_CF_RECORD_URL = "/deploy/cf/delete/data";//cf 정보 삭제
	
	
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
	 * @description   : CF 설치 화면 이동
	 * @title               : testGoCf
	 * @return            : void
	***************************************************/
	@Test
	public void testGoCf() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 설치 화면 이동 TEST START  ================="); 
		}
		ResultActions result = mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON));
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 설치 화면 이동 TEST END  ================="); 
		}
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF Popup 화면 이동
	 * @title               : testGoCfPopup
	 * @return            : void
	***************************************************/
	@Test
	public void testGoCfPopup() throws Exception{
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 설치 팝업 화면 요청"); }
			ResultActions result = mockMvc.perform(get(POPUP_URL).contentType(MediaType.APPLICATION_JSON));
			result.andDo(MockMvcResultHandlers.print())
			.andReturn();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 설치 팝업 화면 성공"); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 목록 정보 조회
	 * @title               : testGetCfLIst
	 * @return            : void
	***************************************************/
	@Test
	public void testGetCfLIst() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 정보 목록 조회 TEST START  ================="); 
		}
		ResultActions result = 
				mockMvc.perform(MockMvcRequestBuilders.get(CF_LIST_URL)
						.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 정보 목록 조회 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 정보 상세 조회
	 * @title               : testGetCfInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testGetCfInfo() throws Exception {
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 정보 상세 조회 TEST START  ================="); 
		}
		//CF 설치 정보 저장
		cfService.saveCfInfo();
		ResultActions result = 
				mockMvc.perform(MockMvcRequestBuilders.get(CF_DETAIL_URL)
						.param("id", "1")
						.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 정보 상세 조회 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 정보 저장 호출
	 * @title               : testSaveDefaultInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveDefaultInfo() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 기본 정보 저장 TEST START  ================="); 
		}
		saveDefaultInfo();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 기본  저장 TEST END  ================="); 
		}
	}
	

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 정보 저장
	 * @title               : saveDefaultInfo
	 * @return            : void
	***************************************************/
	public void saveDefaultInfo() throws Exception{
		String requestJson = setDefaultInfo();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(SAVE_DEFAULT_INFO_URL)
				.param("test", "Y")
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 국가 코드 조회
	 * @title               : testGetCountryCodeList
	 * @return            : void
	***************************************************/
	@Test
	public void testGetCountryCodeList() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  국가 코드 조회 테스트 요청  ================="); 
		}
		ResultActions result = mockMvc.perform(get(GET_COUNTRY_CODELIST_URL)
				.param("parentCode", "20000")
	    		.contentType(APPLICATION_JSON_UTF8));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  국가 코드 조회 테스트 요청 성공  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 키 생성
	 * @title               : testCreateKeyInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testCreateKeyInfo() throws Exception {
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 키 생성 TEST 요청  ================="); 
		}
		testSaveDefaultInfo(); //기본 정보 저장
		String requestJson = setKeyInfo();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(CREAT_KEY_INFO_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 키 생성 TEST 요청 성공 ================="); 
		}
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : KEY 생성 정보 저장
	 * @title               : testSaveKeyInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveKeyInfo() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF KEY 생성 정보 저장 TEST START  ================="); 
		}
		testSaveDefaultInfo(); //기본 정보 저장
		String requestJson = setKeyInfo();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(SAVE_KEY_INFO_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isNoContent())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF KEY 생성 정보 저장 TEST START  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 네트워크 정보 저장
	 * @title               : testSaveNetworkCfInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveNetworkCfInfo() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 네트워크 정보 저장 TEST START  ================="); 
		}
		testSaveDefaultInfo();
		String requestJson = setNetworkInfo();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(SAVE_NETWORK_INFO_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isNoContent())
		.andReturn();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 네트워크  저장 TEST END  ================="); 
		}
	}
	

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 리소스 정보 저장
	 * @title               : testSaveResourceCfInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveResourceCfInfo() throws Exception {
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF RESOURCE 정보 저장 TEST START  ================="); 
		}
		
		testSaveDefaultInfo();
		String requestJson = setResourceInfo();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(SAVE_RESOURCE_INFO_URL)
				.content(requestJson)
				.param("test", "Y")
				.contentType(APPLICATION_JSON_UTF8));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF RESOURCE  저장 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포 파일 생성	 
	 * @title               : testMakeDeploymentFile
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testMakeDeploymentFile() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  배포 파일 생성 TEST START  ================="); 
		}
		//save data
		cfService.saveCfInfo();
		String requestJson = setMakeManifestInfo();
		// make manifest file
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(MAKE_DEPLOYMENTFILE_URL)
				.param("test", "Y")
				.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isNoContent())
		.andReturn();
		
		//delete manifest file
		File file = new File(LocalDirectoryConfiguration.getDeploymentDir() + "/openstack-cf-1-test.yml");
		if( file.exists() ) file.delete();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  배포 파일 생성 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 설치
	 * @title               : testDoInstallCf
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDoInstallCf() throws Exception {
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 설치  TEST START  ================="); 
		}
		
		CfParamDTO.Install dto = setInstallInfo();
		cfService.deployAsync(dto, principal, "cf");
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 설치  TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 단순 레코드 삭제 
	 * @title               : testDeleteJustOnlyCfRecord
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDeleteJustOnlyCfRecord() throws Exception {
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 단순 레코드 삭제 TEST START  ================="); 
		}
		
		cfService.saveCfInfo();
		String requestJson = setDeleteInfo();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_CF_RECORD_URL)
				.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 단순 레코드 삭제 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 플랫폼 삭제 요청
	 * @title               : testDeleteCf
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDeleteCf() throws Exception {
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 플랫폼 삭제 TEST START  ================="); 
		}
		
		CfParamDTO.Delete dto = new CfParamDTO.Delete();
		dto.setId("1");
		dto.setIaas("openstack");
		dto.setPlatform("cf");
		
		cfService.deleteDeployAsync(dto);
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF 플랫폼 삭제 TEST END  ================="); 
		}
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 하나의 메소드가 동작한 직후 실행
	 * @title               : tearDown
	 * @return            : void
	***************************************************/
	@After
	public void tearDown(){
		//키 파일 삭제
		File keyFile = new File( KEY_DIR );
		if( keyFile.exists() ){
			keyFile.delete();
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 정보 설정
	 * @title               : setDefaultInfo
	 * @return            : String
	***************************************************/
	public String setDefaultInfo()  throws Exception{
		CfParamDTO.Default dto = new CfParamDTO.Default();
		
		dto.setId("1");
		dto.setIaas("openstack");
		dto.setDiegoYn("true");
		dto.setDeploymentName("test-openstack");
		dto.setDirectorUuid("test-director-Uuid");
		dto.setReleaseName("cf");
		dto.setReleaseVersion("236");
		dto.setAppSshFingerprint("test-app-ssh-Finger-print");
		dto.setDeaMemoryMB("32768");
		dto.setDeaDiskMB("8192");
		dto.setDomain("172.12.34.100.xip.io");
		dto.setDescription("test-domain");
		dto.setDomainOrganization("test-org");
		dto.setProxyStaticIps("172.12.34.100");
		dto.setLoginSecret("1234");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
	
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 키 생성 정보 설정
	 * @title               : setKeyInfo
	 * @return            : String
	***************************************************/
	public String setKeyInfo() throws Exception{
		KeyInfoDTO dto = new KeyInfoDTO();
		dto.setId("1");
		dto.setIaas("openstack");
		dto.setPlatform("cf");
		dto.setDomain("172.12.34.100.xip.io");//도메인
		dto.setCountryCode("KR");//국가코드
		dto.setStateName("Seoul");//시/도
		dto.setLocalityName("Seoul");//시/구/군
		dto.setOrganizationName("PaaS");//회사명
		dto.setUnitName("unit");//부서명
		dto.setEmail("paas@example.co.kr");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
	
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 네트워크 정보 설정 
	 * @title               : setNetworkInfo
	 * @return            : String
	***************************************************/
	public String setNetworkInfo() throws Exception {
		List<NetworkDTO> listNetwork = new ArrayList<NetworkDTO>();
		
		for(int i=0; i <2; i++){
			NetworkDTO dto = new NetworkDTO();
			dto.setCfId("1");
			dto.setId(i+"");
			dto.setIaas("openstack");
			dto.setDeployType("DEPLOY_TYPE_CF");
			if(i == 0){
				dto.setNet("External");
				dto.setSubnetStaticFrom("172.12.34.100");
			}else{
				dto.setNet("Internal");
				dto.setSubnetRange("192.0.10.0/24");
				dto.setSubnetGateway("192.0.10.1");
				dto.setSubnetReservedFrom("192.0.10.2");
				dto.setSubnetReservedTo("192.0.10.100");
				dto.setSubnetStaticFrom("192.0.10.101");
				dto.setSubnetStaticTo("192.0.10.127");
				dto.setSubnetDns("8.8.8.8");
				dto.setSubnetId("Internal");
				dto.setCloudSecurityGroups("test-security");
			}
			listNetwork.add(dto);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(listNetwork);
	
		return requestJson;
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 리소스 정보 설정
	 * @title               : setResourceInfo
	 * @return            : String
	***************************************************/
	public String setResourceInfo() throws Exception {
		ResourceDTO dto = new ResourceDTO();
		
		dto.setId("1");
		dto.setIaas("openstack");
		dto.setPlatform("cf");
		dto.setStemcellName("bosh-openstack-kvm-ubuntu-trusty-go_agent");
		dto.setStemcellVersion("3262");
		dto.setBoshPassword("test-password");
		dto.setSmallFlavor("m1.small");
		dto.setMediumFlavor("m1.medium");
		dto.setLargeFlavor("m1.large");
		dto.setRunnerFlavor("m1.large");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
	
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포 파일 생성 데이터 설정
	 * @title               : setMakeManifestInfo
	 * @return            : String
	***************************************************/
	public String setMakeManifestInfo() throws Exception{
		CfParamDTO.Install dto = new CfParamDTO.Install();
		dto.setId("1");
		dto.setIaas("openstack");
		dto.setPlatform("cf");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
	    
	
		return requestJson;
	}
	 
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치 정보 설정
	 * @title               : setInstallInfo
	 * @return            : CfParamDTO.Install
	***************************************************/
	public CfParamDTO.Install setInstallInfo(){
		CfParamDTO.Install  dto = new CfParamDTO.Install();
		
		dto.setId("1");
		dto.setIaas("openstack");
		dto.setPlatform("cf");
		return dto;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 삭제 정보 설정
	 * @title               : setDeleteInfo
	 * @return            : String
	***************************************************/
	public String setDeleteInfo() throws Exception{
		
		CfParamDTO.Delete dto = new CfParamDTO.Delete();
		dto.setId("1");
		dto.setIaas("openstack");
		dto.setPlatform("cf");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
	    
	    return requestJson;
	}
}
