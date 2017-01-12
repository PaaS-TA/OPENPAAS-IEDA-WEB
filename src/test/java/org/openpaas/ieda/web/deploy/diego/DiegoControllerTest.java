package org.openpaas.ieda.web.deploy.diego;

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
public class DiegoControllerTest extends BaseTestController{
	
	@Autowired WebApplicationContext wac;
	@Autowired DiegoServiceTest service;
	private MockMvc mockMvc;
	private Principal principal = null;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DiegoControllerTest.class);
	final private static String SEPARATOR = System.getProperty("file.separator");
	final private static String KEY_DIR = LocalDirectoryConfiguration.getKeyDir() + SEPARATOR + "openstack-diego-key-1.yml";
	
	/*************************************** URL *******************************************/
	final static String VIEW_URL = "/deploy/diego"; //Diego 화면 이동
	final static String DIEGO_LIST_URL = "/deploy/diego/list/OPENSTACK"; //목록 정보 조회
	final static String DIEGO_DETAIL_URL = "/deploy/diego/install/detail/1"; //Diego 상세 조회
	final static String CREAT_EKEY_INFO_URL ="/common/deploy/key/createKey";//Key 생성
	final static String DIEGO_DEFAULT_URL = "/deploy/diego/install/saveDefaultInfo/Y"; //기본 정보 저장
	final static String DIEGO_NETWORK_URL = "/deploy/diego/install/saveNetworkInfo"; //네트워크 정보 저장
	final static String DIEGO_RESOURCE_URL = "/deploy/diego/install/saveResourceInfo/Y"; //리소스 정보 저장
	final static String DIEGO_RECORD_DELETE_URL = "/deploy/diego/delete/data"; //단순 Diego 레코드 삭제
	final static String CREATE_SETTING_FILE_URL = "/deploy/diego/install/createSettingFile/Y"; //배포 파일 생성

	
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
	 * @description   : Diego 설치 화면 이동
	 * @title               : testGoDiego
	 * @return            : void
	***************************************************/
	@Test
	public void testGoDiego() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 설치 화면 이동 START  ================="); 
		}
	    ResultActions result = mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON));
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 설치 화면 이동 END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 목록 정보 조회
	 * @title               : testGetDiegoList
	 * @return            : void
	***************************************************/
	@Test
	public void testGetDiegoList() throws Exception{
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 목록 정보 조회 START  ================="); 
		}
		ResultActions result = 
				mockMvc.perform(MockMvcRequestBuilders.get(DIEGO_LIST_URL)
						.param("iaas", "OPENSTACK")
						.contentType(MediaType.APPLICATION_JSON));
					
		result.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 목록 정보 조회 END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : DIEGO 상세 조회 
	 * @title               : testGetDiegoInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testGetDiegoInfo() throws Exception{
		testSaveDefaultInfo();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 상세 조회 START  ================="); 
		}
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(DIEGO_DETAIL_URL)
				.param("id", "1")
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 상세 조회 END  ================="); 
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
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(CREAT_EKEY_INFO_URL)
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
	 * @description   : Diego 기본정보 저장
	 * @title               : testSaveDefaultInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveDefaultInfo() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 기본정보 저장  START  ================="); 
		}
		String requestJson = setDiegoDefaultInfo();
	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(DIEGO_DEFAULT_URL)
	    		.param("test", "Y")
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 기본정보 저장  END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 네트워크 저장
	 * @title               : testNetworkInfoSave
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testNetworkInfoSave() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 네트워크 저장  START  ================="); 
		}
		testSaveDefaultInfo();
		String requestJson = setDiegoNetworkInfo();
		
	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(DIEGO_NETWORK_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 네트워크 저장  END  ================="); 
		}
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 리소스 정보 저장
	 * @title               : testResourceInfoSave
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testResourceInfoSave() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  리소스 정보 저장  START  ================="); 
		}
		testSaveDefaultInfo();
		String requestJson = setDiegoResourceInfo();
	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(DIEGO_RESOURCE_URL)
	    		.param("test", "Y")
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  리소스 정보 저장  END  ================="); 
		}
	}
	
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : DIEGO 단순 레코드 삭제
	 * @title               : testJustDiegoInfoDelete
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testJustDiegoInfoDelete() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 단순 레코드 삭제  START  ================="); 
		}
		testSaveDefaultInfo();
		String requestJson = setDiegoDeleteInfo();
	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(DIEGO_RECORD_DELETE_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 단순 레코드 삭제  END  ================="); 
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
	 * @description   : DIEGO 배포 파일 생성 및 정보 저장
	 * @title               : testMakeDeploymentFile
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testMakeDeploymentFile() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 배포 파일 생성 및 정보 저장  START  ================="); 
		}
		service.insertDiegoInfo();
		String requestJson = setMakeManifestInfo();
	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(CREATE_SETTING_FILE_URL)
	    		.content(requestJson)
	    		.contentType(APPLICATION_JSON_UTF8)
	    		.param("testFlag", "Y"));
	    
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isNoContent())
		.andReturn();
		
		//delete manifest file
		File file = new File(LocalDirectoryConfiguration.getDeploymentDir() + "/openstack-diego-test-1.yml");
		if( file.exists() ) file.delete();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 배포 파일 생성 및 정보 저장  END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : DIEGO 설치
	 * @title               : testDoInstallDiego
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDoInstallDiego() throws Exception{
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 설치  START  ================="); 
		}
		DiegoParamDTO.Install dto = new DiegoParamDTO.Install();
		dto.setIaas("OPENSTACK");
		dto.setId("1");
		service.testDeploy(dto, principal, "diego");
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 설치  END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : DIEGO 삭제
	 * @title               : testDoDeleteDiego
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDoDeleteDiego() throws Exception{
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 삭제  START  ================="); 
		}
		DiegoParamDTO.Delete dto = new DiegoParamDTO.Delete();
		dto.setIaas("OPENSTACK");
		dto.setId("1");
		service.testdeleteDeploy(dto, principal);
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 삭제  END  ================="); 
		}
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
		dto.setPlatform("diego");
		
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
		DiegoParamDTO.Default dto = new DiegoParamDTO.Default();
		dto.setId("1");
		dto.setIaas("OPENSTACK");
		dto.setDeploymentName("cf-openstack-diego-test");
		dto.setDirectorUuid("86299086-1710-4022-b48f-c5ef6a507924");
		dto.setDiegoReleaseName("diego");
		dto.setDiegoReleaseVersion("0.1481.0");
		dto.setCflinuxfs2rootfsreleaseName("cflinuxfs2-rootfs");
		dto.setCflinuxfs2rootfsreleaseVersion("1.21.0");
		dto.setGardenReleaseName("garden-linux");
		dto.setGardenReleaseVersion("0.339.0");
		dto.setEtcdReleaseName("etcd");
		dto.setEtcdReleaseVersion("63");
		dto.setPaastaMonitoringUse("true");
		dto.setCadvisorDriverIp("10.10.10.10");
		dto.setCadvisorDriverPort("7777");
		dto.setCfDeploymentFile("openstack-cf-test-1.yml");
		dto.setCfDeploymentName("cf-openstack-diego");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
	    return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 네트워크 정보 설정
	 * @title               : setDiegoNetworkInfo
	 * @return            : String
	***************************************************/
	public String setDiegoNetworkInfo() throws Exception{
		List<NetworkDTO> networkList = new ArrayList<NetworkDTO>();
		NetworkDTO dto = new NetworkDTO();
		dto.setId("1");
		dto.setDiegoId("1");
		dto.setIaas("OPENSTACK");
		dto.setSeq("1");
		dto.setDeployType("1400");
		dto.setNet("Internal");
		dto.setSubnetRange("10.10.10.0/24");
		dto.setSubnetGateway("10.10.40.1");
		dto.setSubnetDns("8.8.8.8");
		dto.setSubnetReservedFrom("10.10.10.2");
		dto.setSubnetReservedTo("10.10.10.100");
		dto.setSubnetStaticFrom("10.10.10.101");
		dto.setSubnetStaticTo("10.10.10.127");
		dto.setSubnetId("83d71002-d8f7-4b52-af9");
		dto.setCloudSecurityGroups("bosh-security");
		networkList.add(dto);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(networkList);
	    return requestJson;
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 리소스 정보 설정
	 * @title               : setDiegoResourceInfo
	 * @return            : String
	***************************************************/
	public String setDiegoResourceInfo() throws Exception{
		ResourceDTO dto = new ResourceDTO();
		dto.setId("1");
		dto.setIaas("OPENSTACK");
		dto.setPlatform("diego");
		dto.setStemcellName("bosh-openstack-kvm-ubuntu-trusty-go_agent");
		dto.setStemcellVersion("3262");
		dto.setBoshPassword("$6$4gDD3aV0rdqlrKC$2axHCxGKIObs6tAmMTqYCspcdvQXh3JJcvWOY2WGb4SrdXtnCyNaWlrf3WEqvYR2MYizEGp3kMmbpwBC6jsHt0");
		dto.setSmallFlavor("m1.small");
		dto.setMediumFlavor("m1.medium");
		dto.setLargeFlavor("m1.large");
		dto.setRunnerFlavor("m1.xlarge");
		dto.setKeyFile("test-diego-key-1.yml");
		
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
	 * @description   : Diego 삭제 정보
	 * @title               : setDiegoDeleteInfo
	 * @return            : String
	***************************************************/
	public String setDiegoDeleteInfo() throws Exception{
		DiegoParamDTO.Delete dto = new DiegoParamDTO.Delete();
		dto.setId("1");
		dto.setIaas("OPENSTACK");
		dto.setPlatform("diego");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
	    return requestJson;
	}
	
}
