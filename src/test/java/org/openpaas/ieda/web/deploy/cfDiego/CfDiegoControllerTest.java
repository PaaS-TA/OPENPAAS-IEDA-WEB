package org.openpaas.ieda.web.deploy.cfDiego;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.BaseTestController;
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
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class})
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
	
	/*************************************** URL *******************************************/
	final static String VIEW_URL = "/deploy/cfDiego"; //Cf & Diego menu 화면
	final static String CF_POPUP_URL = "deploy/cfDiego/install/cfPopup";//cf 팝업 화면
	final static String DIEGO_POPUP_URL = "deploy/cfDiego/install/diegoPopup";//diego 팝업 화면
	final static String CF_DIEGO_LIST_URL = "/deploy/cfDiego/list/openstack"; //CF & Dieg 목록 조회
	final static String CF_DIEGO_DETAIL_URL = "/deploy/cfDiego/install/detail/1"; //CF & Diego 정보 상세 조회
	final static String CF_LIST_URL = "/deploy/cfDiego/list/cf/openstack";//CF 정보 목록 조회
	final static String SAVE_DEFAULT_INFO_URL = "/deploy/cfDiego/install/saveDefaultInfo/Y";//기본 정보 저장 
	final static String SAVE_NETWORK_INFO_URL="/deploy/cfDiego/install/saveNetworkInfo";//네트워크 정보 저장 
	final static String SAVE_UAA_INFO_URL = "/deploy/cfDiego/install/saveUaaInfo";//CF UAA 정보 저장
	final static String SAVE_CONSUL_INFO_URL = "/deploy/cfDiego/install/saveConsulInfo";//CF CONSUL 정보 저장 
	final static String SAVE_BLOBSTORE_INFO_URL = "/deploy/cfDiego/install/saveBlobstoreInfo";//CF BlobStore 정보 저장
	final static String SAVE_DIEGO_INFO_URL = "/deploy/cfDiego/install/saveDiegoInfo";//Diego 정보 저장  
	final static String SAVE_ETCD_INFO_URL = "/deploy/cfDiego/install/saveEtcdInfo";//ETCD 정보 저장  
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
	 * @description   : CF UAA 정보 저장
	 * @title               : testSaveUaaCfInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveUaaCfInfo() throws Exception{
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego UAA 정보 저장 요청"); }
			//set the default info
			saveDefaultInfo("cf");
			//request for storing the Uaa info 
			String requestJson = setUaaInfo();
			ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put( SAVE_UAA_INFO_URL )
					.content(requestJson)
					.contentType(MediaType.APPLICATION_JSON));
			
			result.andDo( MockMvcResultHandlers.print() )
				.andExpect( MockMvcResultMatchers.status().isOk() )
				.andReturn();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego UAA 정보 저장 성공!!"); }
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF CONSUL 정보 저장 
	 * @title               : testSaveConsulCfInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveConsulCfInfo() throws Exception{
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego Consul 정보 저장 요청"); }
			//set the default info
			saveDefaultInfo("cf");
			//request for storing the Consul info 
			String requestJson = setConsulInfo();
			ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put( SAVE_CONSUL_INFO_URL )
					.content(requestJson)
					.contentType(MediaType.APPLICATION_JSON));
			
			result.andDo( MockMvcResultHandlers.print() )
				.andExpect( MockMvcResultMatchers.status().isOk() )
				.andReturn();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego Consul 정보 저장 성공!!"); }
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF BlobStore 정보 저장
	 * @title               : testSaveBlobstoreInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveBlobstoreInfo() throws Exception{
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego Blobstore 정보 저장 요청"); }
			//set the default info
			saveDefaultInfo("cf");
			//request for storing the Blobstore info 
			String requestJson = setBlobstoreInfo();
			ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put( SAVE_BLOBSTORE_INFO_URL )
					.content(requestJson)
					.contentType(MediaType.APPLICATION_JSON));
			
			result.andDo( MockMvcResultHandlers.print() )
				.andExpect( MockMvcResultMatchers.status().isOk() )
				.andReturn();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego Blobstore 정보 저장 성공!!"); }
		
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 정보 저장  
	 * @title               : testSaveDiegoInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveDiegoInfo() throws Exception{
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego DIEGO 정보 저장 요청"); }
			//set the cf default info
			saveDefaultInfo("cf");
			//set the Diego default info
			saveDefaultInfo("diego");
			//request for storing the Diego info 
			String requestJson = setDiegoInfo();
			ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put( SAVE_DIEGO_INFO_URL )
					.content(requestJson)
					.contentType(MediaType.APPLICATION_JSON));
			
			result.andDo( MockMvcResultHandlers.print() )
				.andExpect( MockMvcResultMatchers.status().isCreated() )
				.andReturn();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego DIEGO 정보 저장 성공!!"); }
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego ETCD 정보 저장
	 * @title               : testEtcdInfoSave
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testEtcdInfoSave() throws Exception{
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego ETCD 정보 저장 요청"); }
			//set the cf default info
			saveDefaultInfo("cf");
			//set the Diego default info
			saveDefaultInfo("diego");
			//request for storing the Diego info 
			String requestJson = setDiegoEtcdInfo();
			ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put( SAVE_ETCD_INFO_URL )
					.content(requestJson)
					.contentType(MediaType.APPLICATION_JSON));
			
			result.andDo( MockMvcResultHandlers.print() )
				.andExpect( MockMvcResultMatchers.status().isCreated() )
				.andReturn();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego ETCD 정보 저장 성공!!"); }
		
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
	 * @description   : UAA 정보 설정 
	 * @title               : setUaaInfo
	 * @return            : String
	***************************************************/
	public String setUaaInfo() throws Exception {
		CfParamDTO.Uaa dto = new CfParamDTO.Uaa();
		
		dto.setId("1");
		dto.setIaas("openstack");
		dto.setLoginSecret("test-login-security");
		//공개키
		String verificationKey = "-----BEGIN PUBLIC KEY-----" +"\n";
		verificationKey += "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1kp7Wg/cyq12DWTin7Tu"+"\n";
		verificationKey += "HKZjUolmOxj93iMj4PePxrvHgTkLs4xA5smR9w6BhCMJ/B0fpJvca8TqXgvVtDfx" + "\n";
		verificationKey += "2ui9NuDQKB477mOfg/SHrB2h9G9JZdsJdbIqSEiXW0XugJU/vm3qiV/RTisZYhX4" + "\n";
		verificationKey += "...testing";
		dto.setVerificationKey(verificationKey);
		
		//개인키
		String signingKey= "-----BEGIN RSA PRIVATE KEY-----" + "\n";
		signingKey += "MIIEowIBAAKCAQEA1kp7Wg/cyq12DWTin7TuHKZjUolmOxj93iMj4PePxrvHgTkL" + "\n";
		signingKey += "s4xA5smR9w6BhCMJ/B0fpJvca8TqXgvVtDfx2ui9NuDQKB477mOfg/SHrB2h9G9J" + "\n";
		signingKey += "ZdsJdbIqSEiXW0XugJU/vm3qiV/RTisZYhX4P8kXcbQZJBKdqrHaAjJijrsUqp78" + "\n";
		signingKey += "...testing";
		dto.setSigningKey(signingKey);
		
		//프록시 정보 - HAProxy 공인 IP
		dto.setProxyStaticIps("172.12.34.100");
		
		//프록시 정보 - HAProxy 인증서
		String sslPemPub = "-----BEGIN CERTIFICATE-----" + "\n";
		sslPemPub += "MIICnzCCAggCCQCKDfbzvFEfUTANBgkqhkiG9w0BAQsFADCBkzELMAkGA1UEBhMC" + "\n";
		sslPemPub += "S1IxDjAMBgNVBAgMBVNlb3VsMQ4wDAYDVQQHDAVTZW91bDEQMA4GA1UECgwHY2xv" + "\n";
		sslPemPub += "dWQ0dTEMMAoGA1UECwwDT0NQMSAwHgYDVQQDDBcqLjE3Mi4xNi4xMDAuMTA5Lnhp" + "\n";
		sslPemPub += "...testing";
		dto.setSslPemPub(sslPemPub);
		
		//프록시 정보 - HAProxy 개인키
		String sslPemRsa = "-----BEGIN RSA PRIVATE KEY-----" + "\n";
		sslPemRsa += "MIICXQIBAAKBgQDpfkbjspe++72gufsWV7kfT9wjMTxeWp4LmML7qt2NSSuTQ05E" + "\n";
		sslPemRsa += "choQei0FMj1AV2A2nHbnEahyPNNoUpV7Oc2DlJYREZVzfok+6qYSGbHZBKzp2kiO" + "\n";
		sslPemRsa += "E07E75mLAs5vHAWv3CBKFsxfJ2GZf+3FfLChVsKpLImywHrwwq27SODnhQIDAQAB" + "\n";
		sslPemRsa += "...testing";
		dto.setSslPemRsa(sslPemRsa);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
	
		return requestJson;
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Consul 정보 설정 
	 * @title               : setConsulInfo
	 * @return            : String
	***************************************************/
	public String setConsulInfo() throws Exception {
		CfParamDTO.Consul dto = new CfParamDTO.Consul();
		
		dto.setId("1");
		dto.setIaas("openstack");
		//에이전트 인증서
		String agentCert = "-----BEGIN CERTIFICATE-----" + "\n";
		agentCert += "MIIEJjCCAg6gAwIBAgIRAJFxJohnE9e10yrz0P9QET0wDQYJKoZIhvcNAQELBQAw" + "\n";
		agentCert += "EzERMA8GA1UEAxMIY29uc3VsQ0EwHhcNMTYwNjMwMDEwOTA4WhcNMTgwNjMwMDEw" + "\n";
		agentCert += "OTA4WjAXMRUwEwYDVQQDEwxjb25zdWwgYWdlbnQwggEiMA0GCSqGSIb3DQEBAQUA" + "\n";
		agentCert += "...testing" + "\n";
		dto.setAgentCert(agentCert);
		
		//에이전트 개인키
		String agentKey = "-----BEGIN RSA PRIVATE KEY-----" + "\n";
		agentKey += "MIIEogIBAAKCAQEAwAG6admvDNWfWgmH2PKAcqXPGiayFTcQZLQLxjFgjEmjyv8r" + "\n";
		agentKey += "5A2mg58fLOG59VuGMLTjAsEuqR2rkBjsSEoVwiRkC108bGoGQi2eHj2UtImYAfw1" + "\n";
		agentKey += "x2YAbpocIyAc70Rb90CF/R05BuLlLRZ+fVQOn0OoGd3Cba3PwMJ2Nz0HonrEBFcE" + "\n";
		agentKey += "...testing" + "\n";
		dto.setAgentKey(agentKey);
		
		//서버 CA 인증서
		String caCert = "-----BEGIN CERTIFICATE-----" + "\n";
		caCert += "MIIFBzCCAu+gAwIBAgIBATANBgkqhkiG9w0BAQsFADATMREwDwYDVQQDEwhjb25z" + "\n";
		caCert += "dWxDQTAeFw0xNjA2MzAwMTA4NTlaFw0yNjA2MzAwMTA5MDZaMBMxETAPBgNVBAMT" + "\n";
		caCert += "CGNvbnN1bENBMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA21gc29p5" + "\n";
		caCert += "...testing" + "\n";
		dto.setCaCert(caCert);
		
		//암호화 키 
		dto.setEncryptKeys("test-encryptKeys");
		
		//서버 인증서
		String serverCert = "-----BEGIN CERTIFICATE-----" + "\n";
		serverCert += "MIIELzCCAhegAwIBAgIQC/1znIT58wJhzfjeFU9EbzANBgkqhkiG9w0BAQsFADAT" + "\n";
		serverCert += "MREwDwYDVQQDEwhjb25zdWxDQTAeFw0xNjA2MzAwMTA5MDdaFw0xODA2MzAwMTA5" + "\n";
		serverCert += "MDdaMCExHzAdBgNVBAMTFnNlcnZlci5kYzEuY2YuaW50ZXJuYWwwggEiMA0GCSqG" + "\n";
		serverCert += "...testing" + "\n";
		dto.setServerCert(serverCert);
		
		//서버 개인키
		String serverKey = "-----BEGIN RSA PRIVATE KEY-----" + "\n";
		serverKey += "MIIEpAIBAAKCAQEA1V2Q0MwP2ucCvCDuXgVrShUH9g+uXDkyUQh1lXuylGW2tbQw" + "\n";
		serverKey += "v8bijtVvGYJaWNFSPOoPBbU03nw7e+jPrHbNt1PcrmHTOLqvZZwJ1nGs93LefpMv" + "\n";
		serverKey += "lUeg7omYDTi8BU3Y+zmZH3yik9QIcxRStTWJtFrg45H2DhP2DT1v+dIg2AjLgYtC" + "\n";
		serverKey += "...testing" + "\n";
		dto.setServerKey(serverKey);
	
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
	
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Blobstore 정보 설정 
	 * @title               : setBlobstoreInfo
	 * @return            : String
	***************************************************/
	public String setBlobstoreInfo() throws Exception {
		
		CfParamDTO.Blobstore dto = new CfParamDTO.Blobstore();
		dto.setId("1");
	
		//1.1 Blobstore Tls Cert Info
		String blobstoreTlsCert = "-----BEGIN CERTIFICATE-----" + "\n";
		blobstoreTlsCert += "MIIENDCCAhygAwIBAgIQfZsdWwO8eOvEphuoeM3qsTANBgkqhkiG9w0BAQsFADAR" + "\n";
		blobstoreTlsCert += "MQ8wDQYDVQQDEwZibG9iQ0EwHhcNMTYwNjMwMDEwOTQ1WhcNMTgwNjMwMDEwOTQ1" + "\n";
		blobstoreTlsCert += "WjAoMSYwJAYDVQQDEx1ibG9ic3RvcmUuc2VydmljZS5jZi5pbnRlcm5hbDCCASIw" + "\n";
		blobstoreTlsCert += "...testing" + "\n";
		dto.setBlobstoreTlsCert(blobstoreTlsCert);
		
		//1,2 Blobstore Private Key Info
		String blobstorePrivateKey = "-----BEGIN RSA PRIVATE KEY-----" + "\n";
		blobstorePrivateKey += "MIIEowIBAAKCAQEAxKqzlMyLyFRnw31br+nVbBI6SV+RRAnOaLq66MM37w/mRUoh" + "\n";
		blobstorePrivateKey += "nk4EQVMLgHTnV3Rb7ZGpD2fS+ARd6HEmIl0RwLEgBu/TGD91PCzBsibipxxD8M/u" + "\n";
		blobstorePrivateKey += "adYmJvQFGCpnXg9bJi42cUCWOy8QRTx4HGuqZAWBfbbFLKDZDFAcXu4/aNML+ZSu" + "\n";
		blobstorePrivateKey += "...testing" + "\n";
		dto.setBlobstorePrivateKey(blobstorePrivateKey);
		
		//1.3 blobstore Ca Cert Info
		String blobstoreCaCert = "-----BEGIN CERTIFICATE-----" + "\n";
		blobstoreCaCert += "MIIFAzCCAuugAwIBAgIBATANBgkqhkiG9w0BAQsFADARMQ8wDQYDVQQDEwZibG9i" + "\n";
		blobstoreCaCert += "Q0EwHhcNMTYwNjMwMDEwOTQ0WhcNMjYwNjMwMDEwOTQ1WjARMQ8wDQYDVQQDEwZi" + "\n";
		blobstoreCaCert += "bG9iQ0EwggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQCpiIZ3VyNfK9CV" + "\n";
		blobstoreCaCert += "...testing" + "\n";
		dto.setBlobstoreCaCert(blobstoreCaCert);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
	
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 정보 설정
	 * @title               : setDiegoInfo
	 * @return            : String
	***************************************************/
	public String setDiegoInfo() throws Exception{
		DiegoParamDTO.Diego dto = new DiegoParamDTO.Diego();
		//Diego 정보 저장
		dto.setId("1");
		dto.setIaas("OPENSTACK");
		dto.setDiegoEncryptionKeys("CGIGDy0h4tH2EFbVBbp9yw==");
		
		String diegoCaCert = "-----BEGIN CERTIFICATE-----" + "\n";
		diegoCaCert += "MIIFBTCCAu2gAwIBAgIBATANBgkqhkiG9w0BAQsFADASMRAwDgYDVQQDEwdkaWVn" + "\n";
		diegoCaCert += "b0NBMB4XDTE2MDcxMzAzNTgyNFoXDTI2MDcxMzAzNTgyN1owEjEQMA4GA1UEAxMH" +"\n";
		diegoCaCert += "ZGllZ29DQTCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBAOe2lsw3rRaG"+"\n";
		diegoCaCert += "...testing";
		dto.setDiegoCaCert(diegoCaCert);
		
		String diegoHostKey = "-----BEGIN RSA PRIVATE KEY-----"+"\n";
		diegoHostKey += "MIIEpAIBAAKCAQEA0k+NS+z0n7w1caSMswmHpbl1ECGLkV+zZZrNXPpProi5FEDt"+"\n";
		diegoHostKey += "oQuGPLVGwM9S78pFrzHECCyF0HfRSt/gMptzeeQY82Cz0Z+SUF64IfiFggYjWw6e"+"\n";
		diegoHostKey += "oGxsrU6LLXuO2gcbEn37T8RQTW7A1QWDG6im1B//uPf/X3BoOSvDYKwpwsvI0NDM"+"\n";
		diegoHostKey += "...testing";
		dto.setDiegoHostKey(diegoHostKey);
		
		String diegoClientCert ="-----BEGIN CERTIFICATE-----"+"\n";
		diegoClientCert += "MIIEIjCCAgqgAwIBAgIQSggNzWsVu2zV+hl9UcI4jTANBgkqhkiG9w0BAQsFADAS"+"\n";
		diegoClientCert += 	"MRAwDgYDVQQDEwdkaWVnb0NBMB4XDTE2MDcxMzAzNTgzMFoXDTE4MDcxMzAzNTgz"+"\n";
		diegoClientCert += "MFowFTETMBEGA1UEAxMKYmJzIGNsaWVudDCCASIwDQYJKoZIhvcNAQEBBQADggEP"+"\n";
		diegoClientCert += "...testing";
		dto.setDiegoClientCert(diegoClientCert);
		
		String diegoClientKey ="-----BEGIN RSA PRIVATE KEY-----"+"\n";
		diegoClientKey += "MIIEogIBAAKCAQEAwqvB4Ec1otV0W+vYsiQxR/bd0+HOiKB8MDYOJErbBit2xh3y"+"\n";
		diegoClientKey += "4imQ8KgZcIbsVzR+esWih6CfkWFd3Xf5/RsR0H7kHnaqLWhkafdPeR5GIXWzet/w"+"\n";
		diegoClientKey += "+v04+HXQ2vXxRyRsjb7xzwgilDchtt40Mer/6g5Dw2nkoKx+ZNUPdt/J1tyhBLjX"+"\n";
		diegoClientKey += "...testing";
		dto.setDiegoClientKey(diegoClientKey);
		
		String diegoServerCert = "-----BEGIN CERTIFICATE-----"+"\n";
		diegoServerCert += "MIIEcTCCAlmgAwIBAgIRAJjV9zqnfRSpbi4gCs0ya+EwDQYJKoZIhvcNAQELBQAw"+"\n";
		diegoServerCert += "EjEQMA4GA1UEAxMHZGllZ29DQTAeFw0xNjA3MTMwMzU4MjlaFw0xODA3MTMwMzU4"+"\n";
		diegoServerCert += "MjlaMCIxIDAeBgNVBAMTF2Jicy5zZXJ2aWNlLmNmLmludGVybmFsMIIBIjANBgkq"+"\n";
		diegoServerCert += "...testing";
		dto.setDiegoServerCert(diegoServerCert);
		
		String diegoServerKey = "-----BEGIN RSA PRIVATE KEY-----"+"\n";
		diegoServerKey += "MIIEpAIBAAKCAQEAwPN+KUBYftQZ6f4ycY/R4dAr/n7shFBdTzEJNrO4F+VVHKwO"+"\n";
		diegoServerKey += "nqoZuFcJxL5DnCNLaan4tCXTKt4UxabXSoFUIJm4HhcygT/3+CoAG53+7lrwNCGu"+"\n";
		diegoServerKey += "JWH0cVYKw1tbpO3j6xjLjiYinliFiQMpM89lDtElZ8dsdf8KbSYHAJlBDDq7my6Y"+"\n";
		diegoServerKey += "...testing";
		dto.setDiegoServerKey(diegoServerKey);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
	    return requestJson;
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego Etcd 정보 설정
	 * @title               : setDiegoEtcdInfo
	 * @return            : String
	***************************************************/
	public String setDiegoEtcdInfo() throws Exception{
		DiegoParamDTO.Etcd dto = new DiegoParamDTO.Etcd();
		//2.2 ETCD 정보
		dto.setId("1");
		dto.setIaas("OPENSTACK");
		
		String etcdClientCert = "-----BEGIN CERTIFICATE-----"+"\n";
		etcdClientCert += "MIIEKTCCAhGgAwIBAgIQP2jEsUKg8Qoi8gMWji9T3jANBgkqhkiG9w0BAQsFADAS"+"\n";
		etcdClientCert += "MRAwDgYDVQQDEwdkaWVnb0NBMB4XDTE2MDcxMzAzNTgyOFoXDTE4MDcxMzAzNTgy"+"\n";
		etcdClientCert += "OFowHDEaMBgGA1UEAxMRZGllZ28gZXRjZCBjbGllbnQwggEiMA0GCSqGSIb3DQEB"+"\n";
		etcdClientCert += "...testing";
		dto.setEtcdClientCert(etcdClientCert);

		String etcdClientKey = "-----BEGIN RSA PRIVATE KEY-----"+"\n";
		etcdClientKey += "MIIEpQIBAAKCAQEAp4VuVixP56r+4FMQ+wbceKBHHbAYF9yVr8C6zjXVfO3L/UQi"+"\n";
		etcdClientKey += "KsnB7Bp4FC1UGcph5TPWE8G4rgeGNxykBIlkj0yIA8f5swTaa9zAt3C5fDyZKCEx"+"\n";
		etcdClientKey += "/SLEQqgeekjAt9vvswOdVGQ8nPsGaiLJ66kiEzvdoQ3rKCGQeEjvIaGFBRSovsYP"+"\n";
		etcdClientKey += "...testing";
		dto.setEtcdClientKey(etcdClientKey);
		
		String etcdServerCert =  "-----BEGIN CERTIFICATE-----"+"\n";
		etcdServerCert += "MIIEdDCCAlygAwIBAgIRAOK5xOGEOpaTykT5575SeKEwDQYJKoZIhvcNAQELBQAw"+"\n";
		etcdServerCert += "EjEQMA4GA1UEAxMHZGllZ29DQTAeFw0xNjA3MTMwMzU4MjhaFw0xODA3MTMwMzU4"+"\n";
		etcdServerCert += "MjhaMCMxITAfBgNVBAMTGGV0Y2Quc2VydmljZS5jZi5pbnRlcm5hbDCCASIwDQYJ"+"\n";
		etcdServerCert += "...testing";
		dto.setEtcdServerCert(etcdServerCert);
		
		String etcdServerKey = "-----BEGIN RSA PRIVATE KEY-----"+"\n";
		etcdServerKey += "MIIEowIBAAKCAQEA6OfNM/avsWoO5oCZ/5jsa8SQQ+vFBajGf2Iyfvab75KgYcK0"+"\n";
		etcdServerKey += "ou45C1PmHQpP8t1Nfzs65zfawUYnvbN1j8L4f3vZZpJyP88YODXlbq0+vdYGy7KF"+"\n";
		etcdServerKey += "I5wXnw1bTSUVBXObFutYHMOHnP3gMQjjiZnebihaJyzgJ/MW3coWUzUqXRA7evqD"+"\n";
		etcdServerKey += "...testing";
		dto.setEtcdServerKey(etcdServerKey);
		
		String etcdPeerCaCert ="-----BEGIN CERTIFICATE-----"+"\n";
		etcdPeerCaCert += "MIIFCzCCAvOgAwIBAgIBATANBgkqhkiG9w0BAQsFADAVMRMwEQYDVQQDEwpldGNk"+"\n";
		etcdPeerCaCert += "UGVlckNBMB4XDTE2MDcxMzAzNTgyN1oXDTI2MDcxMzAzNTgyOFowFTETMBEGA1UE"+"\n";
		etcdPeerCaCert += "AxMKZXRjZFBlZXJDQTCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBAKNW"+"\n";
		etcdPeerCaCert += "...testing";
		dto.setEtcdPeerCaCert(etcdPeerCaCert);


		String etcdPeerCert ="-----BEGIN RSA PRIVATE KEY-----"+"\n";
		etcdPeerCert += "MIIEowIBAAKCAQEAqAEf782+SZrIaR74gZAiXnhHvM9Pzg2YK2WvqwHj0bdH4IIR"+"\n";
		etcdPeerCert += "NCXj20hjxNPK0Lyci6a0eClll3IFzeUaMfqI9285CY1p/7HbbiRVnNnTG3/In4Pt"+"\n";
		etcdPeerCert += "xzCOCGTRLpI6Z1bbPhRdHA92b9aLA2g2IRgZNWzdErY0sAdG5ry41YoaujuKuvUP"+"\n";
		etcdPeerCert +=  "...testing";
		dto.setEtcdPeerCert(etcdPeerCert);
		
		String etcdPeerKey = "-----BEGIN CERTIFICATE-----"+"\n";
		etcdPeerKey += "MIIEdjCCAl6gAwIBAgIQM97ap+d6hRI8DqCtMhDHKDANBgkqhkiG9w0BAQsFADAV"+"\n";
		etcdPeerKey += "MRMwEQYDVQQDEwpldGNkUGVlckNBMB4XDTE2MDcxMzAzNTgyOVoXDTE4MDcxMzAz"+"\n";
		etcdPeerKey += "NTgyOVowIzEhMB8GA1UEAxMYZXRjZC5zZXJ2aWNlLmNmLmludGVybmFsMIIBIjAN"+"\n";
		etcdPeerKey += "...testing";
		dto.setEtcdPeerKey(etcdPeerKey);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
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
