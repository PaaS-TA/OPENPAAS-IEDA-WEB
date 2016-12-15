package org.openpaas.ieda.web.deploy.diego;

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
public class DiegoControllerTest extends BaseTestController{
	
	@Autowired WebApplicationContext wac;
	@Autowired DiegoServiceTest service;
	private MockMvc mockMvc;
	private Principal principal = null;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DiegoControllerTest.class);
	
	/*************************************** URL *******************************************/
	final static String VIEW_URL = "/deploy/diego"; //Diego 화면 이동
	final static String DIEGO_LIST_URL = "/deploy/diego/list/OPENSTACK"; //목록 정보 조회
	final static String DIEGO_DETAIL_URL = "/deploy/diego/install/detail/1"; //Diego 상세 조회
	final static String DIEGO_DEFAULT_URL = "/deploy/diego/install/saveDefaultInfo/Y"; //기본 정보 저장
	final static String DIEGO_NETWORK_URL = "/deploy/diego/install/saveNetworkInfo"; //네트워크 정보 저장
	final static String DIEGO_DIEGO_URL = "/deploy/diego/install/saveDiegoInfo"; //디에고 정보 저장
	final static String DIEGO_ETCD_URL = "/deploy/diego/install/saveEtcdInfo"; //ETCD 정보 저장
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
	 * @description   : Diego 정보 저장
	 * @title               : testDeigoInfoSave
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDeigoInfoSave() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 정보 저장  START  ================="); 
		}
		testSaveDefaultInfo();
		String requestJson = setDiegoInfo();
		
	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(DIEGO_DIEGO_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  DIEGO 정보 저장  END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : ETCD 정보 저장
	 * @title               : testEtcdInfoSave
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testEtcdInfoSave() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  ETCD 정보 저장  START  ================="); 
		}
		testSaveDefaultInfo();
		String requestJson = setDiegoEtcdInfo();
		
	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(DIEGO_ETCD_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  ETCD 정보 저장  END  ================="); 
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
		dto.setCfDeployment("openstack-cf-test-1.yml");
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
	 * @description   : Diego 키 정보 설정
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
