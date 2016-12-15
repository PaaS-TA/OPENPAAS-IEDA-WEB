package org.openpaas.ieda.web.deploy.bosh;

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
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.BaseTestController;
import org.openpaas.ieda.web.deploy.bosh.dao.BoshVO;
import org.openpaas.ieda.web.deploy.bosh.dto.BoshParamDTO;
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
public class BoshControllerTest extends BaseTestController{
	
	@Autowired WebApplicationContext wac;
	@Autowired BoshServiceTest boshService;
	
	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	private Principal principal = null;
	
	private final static Logger BOSH_LOGGER = LoggerFactory.getLogger(BoshControllerTest.class);

	/*************************************** URL *******************************************/
	final static String VIEW_URL = "/deploy/bosh"; //BOSH 화면 이동
	final static String BOSH_LIST_URL = "/deploy/bosh/list/OPENSTACK"; //BOSH 정보 목록 조회
	final static String BOSH_DETAIL_URL = "/deploy/bosh/install/detail/1";  //BOSH 상세 정보 조회
	final static String SAVE_AWS_INFO_URL = "/deploy/bosh/install/saveAwsInfo/Y";//BOSH AWS 정보 저장
	final static String SAVE_OPENSTACK_INFO_URL = "/deploy/bosh/install/saveOpenstackInfo/Y"; //BOSH OPENSTACK 정보 저장
	final static String SAVE_VSPHERE_INFO_URL = "/deploy/bosh/install/saveVSphereInfo/Y"; //BOSH VSPHERE 정보 저장
	final static String SAVE_DEFAULT_INFO_URL = "/deploy/bosh/install/saveDefaultInfo"; //BOSH 기본 정보 저장
	final static String SAVE_NETWORK_INFO_URL="/deploy/bosh/install/saveNetworkInfo"; //BOSH 네트워크 정보 저장
	final static String SAVE_RESOURCE_INFO_URL = "/deploy/bosh/install/saveResourceInfo"; //BOSH 리소스 정보 저장
	final static String MAKE_DEPLOYMENTFILE_URL = "/deploy/bosh/install/createSettingFile/1/Y"; //BOSH MANIFEST 파일 생성
	final static String DELETE_BOSH_RECORD_URL = "/deploy/bosh/delete/data"; //BOSH 단순 레코드 삭제
	
	
	
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
	 * @description   : Bosh 설치 화면 이동
	 * @title               : testGoBosh
	 * @return            : void
	***************************************************/
	@Test
	public void testGoBosh() throws Exception{
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH 설치 화면 이동 TEST START  ================="); 
		}
		ResultActions result = mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON));
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH 설치 화면 이동 TEST END  ================="); 
		}
	}
	

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bosh 전체 목록 조회
	 * @title               : testGetBoshList
	 * @return            : void
	***************************************************/
	@Test
	public void testGetBoshList() throws Exception{
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH 정보 목록 조회 TEST START  ================="); 
		}
		ResultActions result = 
				mockMvc.perform(MockMvcRequestBuilders.get(BOSH_LIST_URL)
						.param("iaas", "OPENSTACK")
						.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH 정보 목록 조회 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : AWS 정보 저장
	 * @title               : testSaveAwsInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveAwsInfo() throws Exception{
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH AWS 정보 저장 TEST START  ================="); 
		}
		String requestJson = setBoshAwsInfo();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(SAVE_AWS_INFO_URL)
				.param("test", "Y")
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andReturn();
		
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH AWS 저장 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Openstack 정보 저장
	 * @title               : testSaveOpenstackInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveOpenstackInfo() throws Exception{
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH OPENSTACK 정보 저장 TEST START  ================="); 
		}
		String requestJson = setBoshOpenstackInfo();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(SAVE_OPENSTACK_INFO_URL)
				.param("test", "Y")
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andReturn();
		
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH OPENSTACK 저장 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH VSphere 정보 저장
	 * @title               : testSaveVspherekInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveVspherekInfo() throws Exception{
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH VSPHERE 정보 저장 TEST START  ================="); 
		}
		String requestJson = setVsphereInfo();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(SAVE_VSPHERE_INFO_URL)
				.param("test", "Y")
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andReturn();
		
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH VSPHERE 저장 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 기본 정보 저장
	 * @title               : testSaveDefalutInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveDefalutInfo() throws Exception{
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH 기본 정보 저장 TEST START  ================="); 
		}
		testSaveOpenstackInfo();
		String requestJson = setBoshDefalutInfo();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(SAVE_DEFAULT_INFO_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andReturn();
		
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH 기본 저장 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 네트워크 저장
	 * @title               : testSaveNetworkInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveNetworkInfo() throws Exception{
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH 기본 네트워크 저장 TEST START  ================="); 
		}
		testSaveOpenstackInfo();
		String requestJson = setNetworkList();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(SAVE_NETWORK_INFO_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andReturn();
		
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH 네트워크 저장 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 리소스 정보 저장
	 * @title               : testSaveResourceInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testSaveResourceInfo() throws Exception{
		if( BOSH_LOGGER.isInfoEnabled() ){ BOSH_LOGGER.info("=================  BOSH 기본 리소스 저장 TEST START  ================="); }
		testSaveOpenstackInfo();
		String requestJson = setResourceInfo();
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(SAVE_RESOURCE_INFO_URL)
	    		.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andReturn();
		
		if( BOSH_LOGGER.isInfoEnabled() ){  BOSH_LOGGER.info("=================  BOSH 리소스 저장 TEST END  ================="); }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bosh 상세 조회
	 * @title               : testBoshDetailInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testBoshDetailInfo() throws Exception{
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH 상세 목록 조회 TEST START  ================="); 
		}
		boshService.saveBoshInfo();
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(BOSH_DETAIL_URL)
				.param("id", "1")
	    		.contentType(APPLICATION_JSON_UTF8));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH 상세 목록 조회 TEST END  ================="); 
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
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  배포 파일 생성 TEST START  ================="); 
		}
		
		boshService.saveBoshInfo();
		String requestJson = setDepoymentInfo();
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(MAKE_DEPLOYMENTFILE_URL)
				.param("id", "1")
				.param("test", "Y")
				.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isNoContent())
		.andReturn();
		
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  배포 파일 생성 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bosh 설치
	 * @title               : testDoInstallBosh
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDoInstallBosh() throws Exception {
		if( BOSH_LOGGER.isInfoEnabled() ){ 
			BOSH_LOGGER.info("=================  BOSH 설치  TEST START  ================="); 
		}
		
		BoshParamDTO.Install dto = setInstallInfo();
		boshService.deployAsync(dto, principal);
		
		if( BOSH_LOGGER.isInfoEnabled() ){ 
			BOSH_LOGGER.info("=================  BOSH 설치  TEST END  ================="); 
		}
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 단순 레코드 삭제 
	 * @title               : testDeleteJustOnlyBoshRecord
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDeleteJustOnlyBoshRecord() throws Exception {
		
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH 단순 레코드 삭제 TEST START  ================="); 
		}
		boshService.saveBoshInfo();
		String requestJson = setDeleteInfo();
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_BOSH_RECORD_URL)
				.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		result.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isNoContent())
		.andReturn();
		
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH 단순 레코드 삭제 TEST END  ================="); 
		}
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 플랫폼 삭제 요청
	 * @title               : testDeleteBosh
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDeleteBosh() throws Exception {
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH 플랫폼 삭제 TEST START  ================="); 
		}
		
		BoshParamDTO.Delete dto = new BoshParamDTO.Delete();
		dto.setId("1");
		dto.setIaas("openstack");
		
		boshService.deleteDeployAsync(dto);
		
		if(BOSH_LOGGER.isInfoEnabled()){ 
			BOSH_LOGGER.info("=================  BOSH 플랫폼 삭제 TEST END  ================="); 
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
		boshService.deleteBoshInfo();
		File file = new File(LocalDirectoryConfiguration.getDeploymentDir()+System.getProperty("file.separator")+"bosh-openstack-test-1.yml");
		if(file.exists()){
			file.delete();
		}
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : AWS 정보 설정
	 * @title               : setBoshAwsInfo
	 * @return            : String
	***************************************************/
	public String setBoshAwsInfo() throws Exception{
		BoshParamDTO.AWS dto = new BoshParamDTO.AWS();
		dto.setId("1");
		dto.setIaas("AWS");
		dto.setAccessKeyId("AWSAccessKey");
		dto.setSecretAccessKey("AWSScretAccessKey");
		dto.setPrivateKeyName("AWSKeyName");
		dto.setRegion("Region");
		dto.setDefaultSecurityGroups("SecurityGroups");
		dto.setAvailabilityZone("AvailablityZone");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : OPENSTACK 정보 설정
	 * @title               : setBoshOpenstackInfo
	 * @return            : String
	***************************************************/
	public String setBoshOpenstackInfo() throws Exception{
		BoshParamDTO.Openstack dto = new BoshParamDTO.Openstack();
		dto.setId("1");
		dto.setIaas("OPENSTACK");
		dto.setAuthUrl("111.1111.11111");
		dto.setTenant("bosh");
		dto.setUserName("bosh");
		dto.setApiKey("bosh-key");
		dto.setDefaultSecurityGroups("securityGroups");
		dto.setPrivateKeyName("keyName");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
		return requestJson;
	}
	

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH VSPHERE 정보 설정
	 * @title               : setVsphereInfo
	 * @return            : String
	***************************************************/
	public String setVsphereInfo()throws Exception{
		BoshParamDTO.VSphere dto = new BoshParamDTO.VSphere();
		dto.setIaas("VSPHERE");
		dto.setId("1");
		dto.setvCenterAddress("10.10.10.10");
		dto.setvCenterUser("bosh");
		dto.setvCenterPassword("1234");
		dto.setvCenterName("centerName");
		dto.setvCenterVMFolder("vm");
		dto.setvCenterDatastore("datastore");
		dto.setvCenterPersistentDatastore("datastore2");
		dto.setvCenterDiskPath("diskPath");
		dto.setvCenterCluster("cluster");
		dto.setvCenterTemplateFolder("TemplateFolder");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 기본 정보 설정
	 * @title               : setBoshDefalutInfo
	 * @return            : String
	***************************************************/
	public String setBoshDefalutInfo() throws Exception{
		BoshParamDTO.DefaultInfo dto = new BoshParamDTO.DefaultInfo();
		dto.setId("1");
		dto.setDeploymentName("openstack-bosh-test");
		dto.setDirectorUuid("3b623d50-10c1-450a-8aad-test");
		dto.setReleaseVersion("bosh/256");
		dto.setDirectorName("test-bosh");
		dto.setSnapshotSchedule("");
		dto.setEnableSnapshots("false");
		dto.setNtp("ntp");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH Network 정보 설정
	 * @title               : setNetworkList
	 * @return            : String
	***************************************************/
	public String setNetworkList() throws Exception{
		List<NetworkDTO> networkList = new ArrayList<NetworkDTO>();
		for(int i=0; i <2; i++){
			NetworkDTO dto = new NetworkDTO();
			dto.setBoshId("1");
			dto.setId(i+"");
			dto.setIaas("openstack");
			dto.setDeployType("1500");
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
			}
			networkList.add(dto);
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(networkList);
		return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bosh Resource 정보 설정
	 * @title               : setResourceInfo
	 * @return            : String
	***************************************************/
	public String setResourceInfo() throws Exception{
		ResourceDTO dto = new ResourceDTO();
		dto.setId("1");
		dto.setIaas("openstack");
		dto.setStemcellName("bosh-openstack-kvm-ubuntu-trusty-go_agent");
		dto.setStemcellVersion("3262");
		dto.setBoshPassword("test-password");
		dto.setSmallFlavor("m1.small");
		dto.setMediumFlavor("m1.medium");
		dto.setPlatform("Bosh");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
		return requestJson;
	}
	

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 배포 정보 설정
	 * @title               : setDepoymentInfo
	 * @return            : String
	***************************************************/
	private String setDepoymentInfo() throws Exception{
		
		BoshVO vo = new BoshVO();
		vo.setId(1);
		vo.setDeploymentFile("bosh-openstack-test-1.yml");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(vo);
		return requestJson;
	}

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 단순 레코드 삭제 정보 설정
	 * @title               : setDeleteInfo
	 * @return            : String
	***************************************************/
	private String setDeleteInfo() throws Exception{
		BoshParamDTO.Delete dto = new BoshParamDTO.Delete();
		dto.setId("1");
		dto.setIaas("OPENSTACK");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto);
	    return requestJson;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 설치 정보 설정
	 * @title               : setInstallInfo
	 * @return            : BoshParamDTO.Install
	***************************************************/
	private BoshParamDTO.Install setInstallInfo() {
		BoshParamDTO.Install  dto = new BoshParamDTO.Install ();
		dto.setId("1");
		dto.setIaas("openstack");
		return dto;
	}
	
	
}
