package org.openpaas.ieda.web.deploy.cf;

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
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class})
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

	/*************************************** URL *******************************************/
	final static String VIEW_URL = "/deploy/cf"; //Cf menu 화면 이동
	final static String POPUP_URL = "deploy/cf/install/cfPopup";//cf 팝업 화면
	final static String CF_LIST_URL = "/deploy/cf/list/openstack"; //Cf 정보 목록 조회
	final static String CF_DETAIL_URL = "/deploy/cf/install/detail/1"; //cf 상세 조회
	final static String SAVE_DEFAULT_INFO_URL = "/deploy/cf/install/saveDefaultInfo/Y";//기본 정보 저장
	final static String SAVE_NETWORK_INFO_URL="/deploy/cf/install/saveNetworkInfo";//네트워크 정보 저장
	final static String SAVE_UAA_INFO_URL = "/deploy/cf/install/saveUaaInfo";//Uaa 정보 저장
	final static String SAVE_CONSUL_INFO_URL = "/deploy/cf/install/saveConsulInfo";//consul 정보 저장
	final static String SAVE_BLOBSTORE_INFO_URL = "/deploy/cf/install/saveBlobstoreInfo";//Blobstore 정보 저장
	final static String SAVE_HM9000_INFO_URL = "/deploy/cf/install/saveHm9000Info";//Hm9000 정보 저장
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
	 * @description   : Uaa 정보 저장 
	 * @title               : testSaveUaaCfInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveUaaCfInfo() throws Exception {
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF UAA 정보 저장 TEST START  ================="); 
		}
		testSaveDefaultInfo();
		String requestJson = setUaaInfo();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(SAVE_UAA_INFO_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF UAA  저장 TEST END  ================="); 
		}
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CONSUL 정보 저장 
	 * @title               : testSaveConsulCfInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveConsulCfInfo() throws Exception {
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF CONSUL 정보 저장 TEST START  ================="); 
		}
		testSaveDefaultInfo();
		String requestJson = setConsulInfo();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(SAVE_CONSUL_INFO_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF CONSUL  저장 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Blobstore 정보 저장 
	 * @title               : testSaveBlobstoreInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveBlobstoreInfo() throws Exception {
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF Blobstore 정보 저장 TEST START  ================="); 
		}
		
		testSaveDefaultInfo();
		String requestJson = setBlobstoreInfo();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(SAVE_BLOBSTORE_INFO_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF Blobstore  저장 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Hm9000 정보 저장
	 * @title               : testSaveHm9000Info
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveHm9000Info() throws Exception {
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF Hm9000 정보 저장 요청  ================="); 
		}
		testSaveDefaultInfo();
		String requestJson = setHm9000Info();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(SAVE_HM9000_INFO_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isNoContent())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  CF Hm9000 저장 요청 성공  ================="); 
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
	 * @description   : Hm9000 정보 설정
	 * @title               : setHm9000Info
	 * @return            : String
	***************************************************/
	public String setHm9000Info() throws Exception{
		CfParamDTO.Hm9000 dto = new CfParamDTO.Hm9000();
		dto.setId("1");
		
		String serverKey = "-----BEGIN RSA PRIVATE KEY-----" + "\n";
		serverKey += "MIIEogIBAAKCAQEA5G7RjVZ6uN5Or4y5z6cNWQICBo0dXQjuC1o9/pzqI79BlXTa" + "\n";
		serverKey += "FHCcP8q70n8VxFDow58zpIZ9BisstBWZqfWtp/2l8+ltaSoKPEH8kObyYhz2lYTv" + "\n";
		serverKey += "nqIru/3CAGu80guI+ZS+1yJb3zpuKJqeI9nYMkUPx6/WIrdHuRgpu24XN63Emr8Y" + "\n";
		serverKey += "...testing" + "\n";
		dto.setHm9000ServerKey(serverKey);
		
		
		String serverCert = "-----BEGIN CERTIFICATE-----";
		serverCert += "MIIEljCCAn6gAwIBAgIRANEjwc4F1YGiFVeEB60FW3QwDQYJKoZIhvcNAQELBQAw" + "\n";
		serverCert += "EzERMA8GA1UEAxMIaG05MDAwQ0EwHhcNMTYwNjMwMDEwOTEwWhcNMTgwNjMwMDEw" + "\n";
		serverCert += "OTEwWjAuMSwwKgYDVQQDEyNsaXN0ZW5lci1obTkwMDAuc2VydmljZS5jZi5pbnRl" + "\n";
		serverCert += "...testing" + "\n";
		dto.setHm9000ServerCert(serverCert);
		
		String clientKey = "-----BEGIN RSA PRIVATE KEY-----" + "\n";
		clientKey += "MIIEowIBAAKCAQEArKLPdvV0x/p4VmNvsSch7QV3l6q5YQRE+y9aqr6jA9EA8Kf+" + "\n";
		clientKey += "HAIz7OdOaeptFMYWK4KxdtvwyVre6jb7Bi5P3oKas++n70/4t0oxO3zXrms7nb2w" + "\n";
		clientKey += "cnbwp9pE39TsqXbK8k9BDjilyaacSiiWbfG+xGIwOaivea3xirg9RAyAxaMYtfPx" + "\n";
		clientKey += "...testing" + "\n";
		dto.setHm9000ClientKey(clientKey);
		
		String clientCert = "-----BEGIN CERTIFICATE-----" + "\n";
		clientCert += "MIIEJzCCAg+gAwIBAgIRAO7BArISW16/EbYzDT0vczgwDQYJKoZIhvcNAQELBQAw" + "\n";
		clientCert += "EzERMA8GA1UEAxMIaG05MDAwQ0EwHhcNMTYwNjMwMDEwOTExWhcNMTgwNjMwMDEw" + "\n";
		clientCert += "OTExWjAYMRYwFAYDVQQDDA1obTkwMDBfY2xpZW50MIIBIjANBgkqhkiG9w0BAQEF" + "\n";
		clientCert += "...testing" + "\n";
		dto.setHm9000ClientCert(clientCert);
		
		String caCert = "-----BEGIN CERTIFICATE-----" + "\n";
		caCert += "MIIFBzCCAu+gAwIBAgIBATANBgkqhkiG9w0BAQsFADATMREwDwYDVQQDEwhobTkw" + "\n";
		caCert += "MDBDQTAeFw0xNjA2MzAwMTA5MDhaFw0yNjA2MzAwMTA5MTBaMBMxETAPBgNVBAMT" + "\n";
		caCert += "CGhtOTAwMENBMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAvoXJWyDF" + "\n";
		caCert += "...testing" + "\n";
		dto.setHm9000CaCert(caCert);
		
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
