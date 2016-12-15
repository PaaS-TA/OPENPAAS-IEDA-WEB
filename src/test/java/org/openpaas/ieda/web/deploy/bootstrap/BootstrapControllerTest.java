package org.openpaas.ieda.web.deploy.bootstrap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import java.io.File;
import java.security.Principal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.BaseTestController;
import org.openpaas.ieda.web.deploy.bootstrap.dao.BootstrapVO;
import org.openpaas.ieda.web.deploy.bootstrap.dto.BootStrapParamDTO;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class})
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
public class BootstrapControllerTest extends BaseTestController {
	
	@Autowired WebApplicationContext wac;
	@Autowired BootstrapServiceTest bootstrapServiceTest;
	
	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	private Principal principal = null;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(BootstrapControllerTest.class);
	private final static String DEPLOYMENT_FILE = LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + "openstack-microbosh-test-1.yml";
	
	/*************************************** URL *******************************************/
	final static String VIEW_URL = "/deploy/bootstrap"; //Bootstrap 화면 이동
	final static String BOOTSTRAP_LIST_URL = "/deploy/bootstrap/list"; //목록 정보 조회
	final static String BOOTSTRAP_DETAIL_URL = "/deploy/bootstrap/install/detail/1"; //Bootstrap  상세 조회
	final static String AWS_INFO_SAVE_URL = "/deploy/bootstrap/install/saveAwsInfo/Y"; //AWS 정보 저장
	final static String OPENSTACK_INFO_SAVE_URL = "/deploy/bootstrap/install/setOpenstackInfo/Y"; //Openstack 정보 저장
	final static String VSPHERE_INFO_SAVE_URL = "/deploy/bootstrap/install/saveVSphereInfo/Y"; //vSphere 정보 저장 
	final static String BOOTSTRAP_DEFAULT_URL = "/deploy/bootstrap/install/setDefaultInfo"; //기본 정보 저장
	final static String BOOTSTRAP_NETWORK_URL = "/deploy/bootstrap/install/setNetworkInfo"; //네트워크 정보 저장
	final static String BOOTSTRAP_RESOURCE_URL = "/deploy/bootstrap/install/setResourceInfo"; //리소스 정보 저장
	final static String BOOTSTRAP_RECORD_DELETE_URL = "/deploy/bootstrap/delete/data"; //단순 Bootstrap 레코드 삭제
	final static String CREATE_SETTING_FILE_URL = "/deploy/bootstrap/install/createSettingFile/1/test"; //배포 파일 생성
	

	
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
	 * @description   : Bootstrap 설치 화면 이동
	 * @title               : testGoBootstrap
	 * @return            : void
	***************************************************/
	@Test
	public void testGoBootstrap() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 설치 화면 이동 TEST START  ================="); 
		}
	    ResultActions result = mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON));
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 설치 화면 이동 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : bootstrap 목록 정보 조회
	 * @title               : testGetBootstrapList
	 * @return            : void
	***************************************************/
	@Test
	public void testGetBootstrapList() throws Exception{
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 목록 정보 조회 TEST START  ================="); 
		}
		ResultActions result = 
				mockMvc.perform(MockMvcRequestBuilders.get(BOOTSTRAP_LIST_URL)
						.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 목록 정보 조회 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Openstack 정보 저장 호출
	 * @title               : testSaveOpenstackInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveOpenstackInfo() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 설치 TEST TEST START  ================="); 
		}
		testSaveOpenstack();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 설치 TEST TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : AWS 정보 저장 호출
	 * @title               : testSaveAwsInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveAwsInfo() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 설치 TEST TEST START  ================="); 
		}
		testSaveAws();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 설치 TEST TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : vSphere 정보 저장 호출
	 * @title               : testSaveVSphereInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveVSphereInfo() throws Exception{
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 설치 TEST TEST START  ================="); 
		}
		testSaveVSphere();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 설치 TEST TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Openstack 정보 저장 
	 * @title               : testSaveOpenstack
	 * @return            : void
	***************************************************/
	public void testSaveOpenstack() throws Exception{
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP OPENSTACK 정보 저장 TEST START  ================="); 
		}
		String requestJson = setBootStrapOpenstack();
		
	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(OPENSTACK_INFO_SAVE_URL)
				.param("testFlag", "Y")
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP OPENSTACK 정보 저장 TEST END  ================="); 
		}
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : AWS 정보 저장 
	 * @title               : testSaveAws
	 * @return            : void
	***************************************************/
	public void testSaveAws() throws Exception{
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP AWS 정보 저장  TEST START  ================="); 
		}
		String requestJson = setBootStrapAws();
	    
	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(AWS_INFO_SAVE_URL)
				.param("testFlag", "Y")
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP AWS 정보 저장  TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : vSphere 정보 저장 
	 * @title               : testSaveVSphere
	 * @return            : void
	***************************************************/
	public void testSaveVSphere() throws Exception {
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  Bootstrap vSphere 정보 저장  TEST START  ================="); 
		}

		String requestJson = setBootStrapVSphere();
	    
	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(VSPHERE_INFO_SAVE_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson)
				.param("testFlag", "Y"));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  Bootstrap vSphere 정보 저장  TEST END  ================="); 
		}
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bootstrap 기본정보 저장 및 수정
	 * @title               : testSaveDefaultInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveDefaultInfo() throws Exception{
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 기본정보 저장 및 수정  TEST START  ================="); 
		}
		
		testSaveOpenstackInfo();

		String requestJson = setBootStrapDefault();
		
	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(BOOTSTRAP_DEFAULT_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 기본정보 저장 및 수정  TEST END  ================="); 
		}
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bootstrap 네트워크 정보 저장 
	 * @title               : testNetworkInfoSave
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testNetworkInfoSave() throws Exception{
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 네트워크 정보 저장  TEST START  ================="); 
		}

		testSaveOpenstackInfo();
		
		String requestJson = setBootStrapNetwork();
	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(BOOTSTRAP_NETWORK_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 네트워크 정보 저장  TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bootstrap 리소스 정보 저장 
	 * @title               : testResourcesInfoSave
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testResourcesInfoSave() throws Exception{
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 리소스 정보 저장  TEST START  ================="); 
		}

		testSaveOpenstackInfo();
		
		String requestJson = setBootStrapResource();
	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(BOOTSTRAP_RESOURCE_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 리소스 정보 저장  TEST END  ================="); 
		}
		
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포 파일 생성 및 정보 저장
	 * @title               : testMakeDeploymentFile
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testMakeDeploymentFile() throws Exception{
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 리소스 정보 저장  TEST START  ================="); 
		}

		testSaveOpenstackInfo();
		
		String requestJson = setBootStrapInfo();
	    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(CREATE_SETTING_FILE_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson)
	    		.param("id", "1")
	    		.param("testFlag", "Y"));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isNoContent())
		.andReturn();
		
		File deploymentFile = new File(DEPLOYMENT_FILE);
		if(deploymentFile.exists()){
			deploymentFile.delete();
		}
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 리소스 정보 저장  TEST END  ================="); 
		}
		
	}
	

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bootstrap 설치
	 * @title               : testDoInstallBootstrap
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDoInstallBootstrap() throws Exception{
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 설치  TEST START  ================="); 
		}
		
		testSaveOpenstackInfo();
		BootStrapParamDTO.Install dto = setBootstrapInstall();
		bootstrapServiceTest.testDeployAsync(dto, principal);
		
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 설치  TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   :  Bootstrap 상세 조회 
	 * @title               : testGetBootstrapInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testGetBootstrapInfo() throws Exception{
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 상세 조회 TEST START  ================="); 
		}
		
		testSaveOpenstackInfo();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(BOOTSTRAP_DETAIL_URL)
				.param("id", "1")
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 상세 조회 TEST END  ================="); 
		}
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 단순 Bootstrap 레코드 삭제
	 * @title               : testDeleteJustOnlyBootstrapRecord
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDeleteJustOnlyBootstrapRecord()throws Exception{
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 단순 BOOTSTRAP 레코드 삭제 TEST START  ================="); 
		}
		
		testSaveOpenstackInfo();
		
		String requestJson = setBootstrapDelete();
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(BOOTSTRAP_RECORD_DELETE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP 단순 BOOTSTRAP 레코드 삭제 TEST END  ================="); 
		}
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Openstack 정보 설정
	 * @title               : setBootStrapOpenstack
	 * @return            : String
	***************************************************/
	public String setBootStrapOpenstack() throws Exception{
		
		BootStrapParamDTO.Openstack dto = new BootStrapParamDTO.Openstack();
		
		dto.setId("1");
		dto.setIaas("OPENSTACK");
		dto.setAuthUrl("bootstrap-openstack-authUrl");
		dto.setTenant("bosh");
		dto.setUserName("bosh");
		dto.setApiKey("1234");
		dto.setDefaultSecurityGroups("test-security");
		dto.setPrivateKeyName("test-key");
		dto.setPrivateKeyPath("test-key.pem");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
	
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : vSphere 정보 설정
	 * @title               : setBootStrapVSphere
	 * @return            : String
	***************************************************/
	public String setBootStrapVSphere() throws Exception{
		
		BootStrapParamDTO.VSphere dto = new BootStrapParamDTO.VSphere();
		
		dto.setId("1");
		dto.setIaas("VSPHERE");
		
		dto.setvCenterAddress("10.10.10.10");
		dto.setvCenterUser("user");
		dto.setvCenterPassword("1q2w3e4r");
		dto.setvCenterName("datacenter");
		dto.setvCenterVMFolder("vmFolder");
		dto.setvCenterTemplateFolder("template_stemcell");
		dto.setvCenterDatastore("datastore");
		dto.setvCenterPersistentDatastore("datastore");
		dto.setvCenterDiskPath("bosh_disk");
		dto.setvCenterCluster("cluster");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
	
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : AWS 정보 설정
	 * @title               : setBootStrapAws
	 * @return            : String
	***************************************************/
	public String setBootStrapAws() throws Exception{
		
		BootStrapParamDTO.Aws awsDto = new BootStrapParamDTO.Aws();
		
		awsDto.setId("1");
		awsDto.setIaas("AWS");
		awsDto.setAccessKeyId("bootstrap-aws");
		awsDto.setSecretAccessId("boostrap-aws-secret");
		awsDto.setRegion("m.east");
		awsDto.setAvailabilityZone("m.east_1");
		awsDto.setDefaultSecurityGroups("test-secrity");
		awsDto.setPrivateKeyName("test-key");
		awsDto.setPrivateKeyPath("test-key.pem");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(awsDto);
	    
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본정보 설정 및 수정
	 * @title               : setBootStrapDefault
	 * @return            : String
	***************************************************/
	public String setBootStrapDefault() throws Exception{
		
		BootStrapParamDTO.Default dto = new BootStrapParamDTO.Default();
		dto.setId("1");
		dto.setDeploymentName("bosh");
		dto.setDirectorName("test-bosh");
		dto.setNtp("1.kr.pool.ntp.org, 0.asia.pool.ntp.org");
		dto.setBoshRelease("bosh-233.tgz");
		dto.setBoshCpiRelease("bosh-openstack-cpi-release-14.tgz");
		dto.setEnableSnapshots("true");
		dto.setSnapshotSchedule("0 0 7 * * * schedule");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
		
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 네트워크 정보 설정
	 * @title               : setBootStrapNetwork
	 * @return            : String
	***************************************************/
	public String setBootStrapNetwork() throws Exception{
		
		BootStrapParamDTO.Network dto = new BootStrapParamDTO.Network();
		
		dto.setId("1");
		dto.setSubnetId("text-subnetId-12345");
		dto.setPrivateStaticIp("10.0.100.11");
		dto.setPublicStaticIp("10.0.20.6");
		dto.setSubnetRange("10.0.20.0/24");
		dto.setSubnetGateway("10.0.20.1");
		dto.setSubnetDns("8.8.8.8");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
		
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 리소스 정보 설정
	 * @title               : setBootStrapResource
	 * @return            : String
	***************************************************/
	public String setBootStrapResource() throws Exception{
		
		BootStrapParamDTO.Resource dto = new BootStrapParamDTO.Resource();
		
		dto.setId("1");
		dto.setStemcell("light-bosh-stemcell-3147-aws-xen-ubuntu-trusty-go_agent.tgz");
		dto.setCloudInstanceType("m1.large");
		dto.setBoshPassword("1234");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
		
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bootstrap 설치 정보 설정
	 * @title               : setBootStrapInfo
	 * @return            : String
	***************************************************/
	public String setBootStrapInfo() throws Exception{
		
		BootstrapVO vo = new BootstrapVO();
		//openstack
		vo.setIaasType("openstack");
		
		vo.setOpenstackAuthUrl("bootstrap-openstack-authUrl");
		vo.setOpenstackTenant("bosh");
		vo.setOpenstackUserName("bosh");
		vo.setOpenstackApiKey("1234");
		vo.setDefaultSecurityGroups("test-security");
		vo.setPrivateKeyName("test-key");
		vo.setPrivateKeyPath("test-key.pem");
		
		//기본정보
		vo.setDeploymentName("bosh");
		vo.setDirectorName("test-bosh");
		vo.setBoshRelease("bosh-257.tgz");
		vo.setBoshCpiRelease("bosh-openstack-cpi-release-14.tgz");
		vo.setEnableSnapshots("true");
		vo.setSnapshotSchedule("0 0 7 * * * schedule");
		
		//네트워크
		vo.setSubnetId("text-subnetId-12345");
		vo.setPrivateStaticIp("10.0.100.11");
		vo.setPublicStaticIp("10.0.20.6");
		vo.setSubnetRange("10.0.20.0/24");
		vo.setSubnetGateway("10.0.20.1");
		vo.setSubnetDns("8.8.8.8");
		vo.setNtp("1.kr.pool.ntp.org, 0.asia.pool.ntp.org");
		
		//리소스
		vo.setStemcell("light-bosh-stemcell-3147-aws-xen-ubuntu-trusty-go_agent.tgz");
		vo.setCloudInstanceType("m1.large");
		vo.setBoshPassword("1234");
		vo.setDeploymentFile("openstack-microbosh-test-1.yml");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(vo);
		
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bootstrap 설치 설정
	 * @title               : setBootstrapInstall
	 * @return            : BootStrapParamDTO.Install
	***************************************************/
	public BootStrapParamDTO.Install setBootstrapInstall() throws Exception{
		
		BootStrapParamDTO.Install dto = new BootStrapParamDTO.Install();
		dto.setId("1");
		dto.setIaas("OPENSTACK");
		
		return dto;
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bootstrap 삭제 정보 설정
	 * @title               : setBootstrapDelete
	 * @return            : String
	***************************************************/
	public String setBootstrapDelete() throws Exception{
		
		BootStrapParamDTO.Delete dto = new BootStrapParamDTO.Delete();
		dto.setId("1");
		dto.setIaas("OPENSTACK");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
		
	    return requestJson;
	}
	
}
