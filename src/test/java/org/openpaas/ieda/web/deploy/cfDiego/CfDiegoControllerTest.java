package org.openpaas.ieda.web.deploy.cfDiego;

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
import org.openpaas.ieda.web.deploy.cf.CfServiceTest;
import org.openpaas.ieda.web.deploy.cf.dto.CfParamDTO;
import org.openpaas.ieda.web.deploy.cfDiego.dto.CfDiegoParamDTO;
import org.openpaas.ieda.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.web.deploy.common.dto.resource.ResourceDTO;
import org.openpaas.ieda.web.deploy.diego.DiegoServiceTest;
import org.openpaas.ieda.web.deploy.diego.dto.DiegoParamDTO;
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
public class CfDiegoControllerTest extends BaseTestController {
	
	@Autowired WebApplicationContext wac;
	@Autowired CfDiegoServiceTest cfDiegoService;
	@Autowired CfServiceTest cfService;
	@Autowired DiegoServiceTest diegoService;

	private MockMvc mockMvc;
	private Principal principal = null;
	private final static Logger LOGGER = LoggerFactory.getLogger(CfDiegoControllerTest.class);
	final private static String SEPARATOR = System.getProperty("file.separator");
	final private static String CF_KEY_DIR = LocalDirectoryConfiguration.getKeyDir() + SEPARATOR + "openstack-cf-key-1.yml";
	final private static String DIEGO_KEY_DIR = LocalDirectoryConfiguration.getKeyDir() + SEPARATOR + "openstack-diego-key-1.yml";
	
	/*************************************** URL *******************************************/
	final static String VIEW_URL = "/deploy/cfDiego"; //Cf & Diego menu 화면
	final static String CF_POPUP_URL = "deploy/cfDiego/install/cfPopup";//cf 팝업 화면
	final static String DIEGO_POPUP_URL = "deploy/cfDiego/install/diegoPopup";//diego 팝업 화면
	final static String CF_DIEGO_LIST_URL = "/deploy/cfDiego/list/openstack"; //CF & Dieg 목록 조회
	final static String CF_DIEGO_DETAIL_URL = "/deploy/cfDiego/install/detail/1"; //CF & Diego 정보 상세 조회
	final static String CF_LIST_URL = "/deploy/cfDiego/list/cf/openstack";//CF 정보 목록 조회
	final static String SAVE_DEFAULT_INFO_URL = "/deploy/cfDiego/install/saveDefaultInfo/Y";//기본 정보 저장 
	final static String GET_COUNTRY_CODELIST_URL = "/common/deploy/codes/countryCode/20000";//국가 코드 조회
	final static String CREAT_KEY_INFO_URL ="/common/deploy/key/createKey";//Key 생성
	final static String SAVE_KEY_INFO_URL ="/deploy/cfDiego/install/saveKeyInfo";//키 정보 저장
	final static String SAVE_NETWORK_INFO_URL="/deploy/cfDiego/install/saveNetworkInfo";//네트워크 정보 저장 
	final static String SAVE_RESOURCE_INFO_URL = "/deploy/cfDiego/install/saveResourceInfo/Y";//리소스 정보 저장
	final static String MAKE_DEPLOYMENTFILE_URL = "/deploy/cfDiego/install/createSettingFile/Y";//배포 파일 생성
	final static String DELETE_CF_DIEGO_RECORD_URL = "/deploy/cfDiego/delete/data";//CF&Diego 단순 레코드 삭제

	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 하나의 메소드가 실행되기 전 호출
	 * @title               : setUp
	 * @return            : void
	***************************************************/
	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
		//login
		principal = getLoggined();
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF & Diego 통합 설치 화면 이동
	 * @title               : testGoCfDiego
	 * @return            : void
	***************************************************/
	@Test
	public void testGoCfDiego() throws Exception {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego 통합 설치 화면 요청"); }
			ResultActions result = mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON));
			result.andDo(MockMvcResultHandlers.print())
			.andReturn();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego 통합 설치 화면 요청 성공"); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 설치 팝업 화면 호출
	 * @title               : testGoCfPopup
	 * @return            : void
	***************************************************/
	@Test
	public void testGoCfPopup() throws Exception {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 설치 팝업 화면 요청"); }
			ResultActions result = mockMvc.perform(get(CF_POPUP_URL).contentType(MediaType.APPLICATION_JSON));
			result.andDo(MockMvcResultHandlers.print())
			.andReturn();
			if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 설치 팝업 화면 요청 성공"); }
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 설치 팝업 화면 호출
	 * @title               : tesGoDiegoPopup
	 * @return            : void
	***************************************************/
	@Test
	public void tesGoDiegoPopup() throws Exception {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> Diego  설치 팝업 화면 요청"); }
			ResultActions result = mockMvc.perform(get(DIEGO_POPUP_URL).contentType(MediaType.APPLICATION_JSON));
			result.andDo(MockMvcResultHandlers.print())
			.andReturn();
			if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> Diego  설치 팝업 화면 요청 성공"); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF & Dieg 목록 조회
	 * @title               : testGetCfDiegoLIst
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testGetCfDiegoLIst() throws Exception {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego 정보 목록 조회 요청"); }
			//save the cf&diego info
			cfDiegoService.saveCfDiegoInfo();
			//request the cf&diego list 
			ResultActions result =
					mockMvc.perform(MockMvcRequestBuilders.get(CF_DIEGO_LIST_URL)
							.param("iaas", "openstack")
							.contentType(MediaType.APPLICATION_JSON));
		
			result.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego 정보 목록 조회 성공"); }
	}
	
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF & Diego 정보 상세 조회
	 * @title               : testGetCfDiegoInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testGetCfDiegoInfo() throws Exception {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego  정보 상세 조회 요청"); }
		//save the default info
		cfDiegoService.saveCfDiegoInfo();
		//request the cf & diego detail info
		ResultActions result =
				mockMvc.perform(MockMvcRequestBuilders.get(CF_DIEGO_DETAIL_URL)
						.param("id", "1")
						.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego  정보 상세 조회 성공"); }
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : IaaS에 따른 CF 정보 목록 조회 
	 * @title               : testGetCfDeploymentLst
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testGetCfDeploymentLst() throws Exception {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 정보 목록 조회 요청"); }
			ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get( CF_LIST_URL )
					.param("iaas", "openstack")
					.contentType(MediaType.APPLICATION_JSON));
			
			result.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 정보 목록 조회 성공"); }
	}
	
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 정보 저장 호출
	 * @title               : testSaveDefaultInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveDefaultInfo() throws Exception {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego 기본 정보 저장 요청"); }
		saveDefaultInfo("cf");
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego 기본 정보 저장 성공!!"); }
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 정보 저장
	 * @title               : saveDefaultInfo
	 * @return            : void
	***************************************************/
	public void saveDefaultInfo(String platform) throws Exception{
		//set the default info
		String requestJson  = "";
		if( "cf".equals(platform) ) requestJson = setCfDefaultInfo();
		else {
			requestJson = setDiegoDefaultInfo();
		}
			
		//request for storing the default info 
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put( SAVE_DEFAULT_INFO_URL )
					.content(requestJson)
					.param("test", "Y")
					.contentType(MediaType.APPLICATION_JSON));
			
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
	 * @description   : 키 생성 정보 저장
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
	public void testSaveNetworkCfInfo() throws Exception {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego 네트워크 정보 저장 요청"); }
		//set the default info
		saveDefaultInfo("cf");
		//request for storing the network info 
		String requestJson = setNetworkInfo();
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put( SAVE_NETWORK_INFO_URL )
				.content(requestJson)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo( MockMvcResultHandlers.print() )
			.andExpect( MockMvcResultMatchers.status().isNoContent() )
			.andReturn();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego 네트워크 정보 저장 성공!!"); }
		
	}
	
	

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF & Diego 리소스 정보 저장
	 * @title               : testSaveResourceInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveResourceInfo() throws Exception {
		if( LOGGER.isInfoEnabled() ){ 
			LOGGER.info("=================  RESOURCE 정보 저장 START  ================="); 
		}
		saveDefaultInfo("cf");
		String requestJson = setResourceInfo();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(SAVE_RESOURCE_INFO_URL)
				.content(requestJson)
				.param("test", "Y")
				.contentType(APPLICATION_JSON_UTF8));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if( LOGGER.isInfoEnabled() ){ 
			LOGGER.info("=================  RESOURCE  저장 END  ================="); 
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
			LOGGER.info("=================  배포 파일 생성 START  ================="); 
		}
		//save data
		saveDefaultInfo("cf");
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
			LOGGER.info("=================  배포 파일 생성 END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치
	 * @title               : testInstallCfDiego
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testInstallCfDiego() throws Exception {
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF & DIEGO 설치  START  ================="); 
		}
		// install cf platform
		CfParamDTO.Install cfDto = setCfInstallInfo();
		cfService.deployAsync(cfDto, principal, "cf");
		
		//install diego platform
		DiegoParamDTO.Install diegoDto = setDiegoInstallInfo();
		diegoService.testDeploy(diegoDto,principal, "diego");
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================   CF & DIEGO 설치  END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 플랫폼 삭제
	 * @title               : testDeleteCfDiego
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDeleteCfDiego() throws Exception {
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF & DIEGO 삭제  START  ================="); 
		}
		// delete cf platform
		CfParamDTO.Delete cfDto = setCfDeleteInfo();
		cfService.deleteDeployAsync(cfDto);
		
		//delete diego platform
		DiegoParamDTO.Delete diegoDto = setDiegoDeleteInfo();
		diegoService.testdeleteDeploy(diegoDto, principal);
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================   CF & DIEGO 삭제  END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF & Diego 단순 레코드 삭제 
	 * @title               : testDeleteJustOnlyCfDiegoRecord
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDeleteJustOnlyCfDiegoRecord() throws Exception {
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF & Diego 단순 레코드 삭제 START  ================="); 
		}
		cfDiegoService.saveCfDiegoInfo();
		String requestJson = setCfDeleteRecordInfo();
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_CF_DIEGO_RECORD_URL)
				.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF & Diego 단순 레코드 삭제 END  ================="); 
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
		File cfKeyFile = new File( CF_KEY_DIR );
		File diegoKeyFile = new File( DIEGO_KEY_DIR );
		if( cfKeyFile.exists() ){
			cfKeyFile.delete();
		}else if( diegoKeyFile.exists() ){
			diegoKeyFile.delete();
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 기본 정보 설정
	 * @title               : setCfDefaultInfo
	 * @return            : String
	***************************************************/
	public String setCfDefaultInfo() throws Exception {
		CfDiegoParamDTO.Default dto = new CfDiegoParamDTO.Default();
		dto.setId("1");
		dto.setIaas("openstack");
		dto.setPlatform("cf");
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
		dto.setPaastaMonitoringUse("true");
		dto.setIngestorIp("10.1.10.10");
		dto.setIngestorPort("7777");
		
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
	 * @description   : Diego 기본 정보 설정
	 * @title               : setDiegoDefaultInfo
	 * @return            : String
	***************************************************/
	public String setDiegoDefaultInfo() throws Exception{
		CfDiegoParamDTO.Default dto = new CfDiegoParamDTO.Default();
		dto.setId("1");
		dto.setIaas("OPENSTACK");
		dto.setCfId(1);
		dto.setPlatform("diego");
		dto.setDeploymentName("cf-openstack-diego-Test");
		dto.setDirectorUuid("86299086-1710-4022-b48f-c5ef6a50-test");
		dto.setDiegoReleaseName("diego");
		dto.setDiegoReleaseVersion("0.1481.0");
		dto.setCflinuxfs2rootfsreleaseName("cflinuxfs2-rootfs");
		dto.setCflinuxfs2rootfsreleaseVersion("1.21.0");
		dto.setGardenReleaseName("garden-linux");
		dto.setGardenReleaseVersion("0.339.0");
		dto.setEtcdReleaseName("etcd");
		dto.setEtcdReleaseVersion("63");
		dto.setCfDeployment("cf-openstack-test");
		dto.setCfDeploymentName("cf-openstack-test");
		dto.setKeyFile("vsphere-diego-key-1.yml");
		dto.setPaastaMonitoringUse("true");
		dto.setCadvisorDriverIp("10.10.10.10");
		dto.setCadvisorDriverPort("7777");
		
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
			dto.setDeployType("1300");
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
	 * @description   : CF 설치 정보 설정
	 * @title               : setCfInstallInfo
	 * @return            : CfParamDTO.Install
	***************************************************/
	public CfParamDTO.Install setCfInstallInfo(){
		CfParamDTO.Install  dto = new CfParamDTO.Install();
		
		dto.setId("1");
		dto.setIaas("openstack");
		dto.setPlatform("cf");
		return dto;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : DIEGO 설치 정보 설정
	 * @title               : setDiegoInstallInfo
	 * @return            : DiegoParamDTO.Install
	***************************************************/
	public DiegoParamDTO.Install setDiegoInstallInfo(){
		DiegoParamDTO.Install  dto = new DiegoParamDTO.Install();
		dto.setId("1");
		dto.setIaas("openstack");
		dto.setPlatform("diego");
		return dto;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 삭제 정보 설정
	 * @title               : setCfDeleteRecordInfo
	 * @return            : String
	***************************************************/
	public String setCfDeleteRecordInfo() throws Exception{
		
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
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 삭제 정보 설정
	 * @title               : setCfDeleteInfo
	 * @return            : CfParamDTO.Delete
	***************************************************/
	public CfParamDTO.Delete setCfDeleteInfo() throws Exception{
		
		CfParamDTO.Delete dto = new CfParamDTO.Delete();
		dto.setId("1");
		dto.setIaas("openstack");
		dto.setPlatform("cf");
	    
	    return dto;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 삭제 정보 설정
	 * @title               : setDiegoDeleteInfo
	 * @return            : DiegoParamDTO.Delete
	***************************************************/
	public DiegoParamDTO.Delete setDiegoDeleteInfo() throws Exception{
		
		DiegoParamDTO.Delete dto = new DiegoParamDTO.Delete();
		dto.setId("1");
		dto.setIaas("openstack");
		dto.setPlatform("diego");
		
	    return dto;
	}
}
