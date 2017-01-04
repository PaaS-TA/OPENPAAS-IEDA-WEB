package org.openpaas.ieda.web.information.vms.controller;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.TestBeansConfiguration;
import org.openpaas.ieda.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.web.common.BaseTestController;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigDAO;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.deploy.bootstrap.dao.BootstrapDAO;
import org.openpaas.ieda.web.deploy.bootstrap.dao.BootstrapVO;
import org.openpaas.ieda.web.information.vms.dto.VmsListDTO;
import org.openpaas.ieda.web.information.vms.service.VmsAsyncServiceTest;
import org.openpaas.ieda.web.information.vms.service.VmsServiceTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
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

/**
 * @author cheolho
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class, TestBeansConfiguration.class})
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
public class VmsControllerTest extends BaseTestController{

	@Autowired WebApplicationContext wac;
	@Autowired VmsServiceTest vmsService;
	@Autowired VmsAsyncServiceTest vmsAsyncService;
	@Autowired DirectorConfigDAO dao;
	@Autowired private BootstrapDAO bootstrapDao;
	
	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(VmsControllerTest.class);

	/*************************************** URL *******************************************/
	final static String VIEW_URL = "/info/vms"; //VM 정보 화면 이동 URL
	final static String SNAPSHOT_INFO_URL = "/info/vms/list/snapshot";//스냅샷 조회

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 하나의 메소드가 동작하기 전 실행
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
	 * @description   : VM 정보 화면 호출
	 * @title               : testGoLisVm
	 * @return            : void
	***************************************************/
	@Test
	public void testGoLisVm() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  VM 정보 화면 요청 START  ================="); 
		}
		ResultActions result = mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON));
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  VM 정보 화면 요청 END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포명 정보 목록 조회
	 * @title               : testGetDeploymentList
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testGetDeploymentList() throws Exception{
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 배포명 조회 요청");  }
		List<DeploymentInfoDTO> deploymentInfo = vmsService.listDeployment();
		if( LOGGER.isDebugEnabled()) {
			LOGGER.debug(deploymentInfo.size() + "");
			for(int i=0; i<deploymentInfo.size(); i++){
				LOGGER.debug("deployment name : " +  deploymentInfo.get(i).getName() );
			}
		}
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 배포명 조회 요청 성공");  }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 조회
	 * @title               : testGetSnapshotInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testGetSnapshotInfo() throws Exception {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> 스냅샷 사용 정보 조회 요청"); }
		//setting default director config info 
		testSaveDefaultDirector();
		//setting bootstrap install info
		testSaveOpenstack();
		ResultActions result = 
				mockMvc.perform(MockMvcRequestBuilders.get(SNAPSHOT_INFO_URL)
						.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> 스냅샷 사용 정보 조회 성공"); } 
	}
	
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : VM 정보 목록 조회
	 * @title               : testGetVmList
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testGetVmList() throws Exception {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> VM 조회 요청");  }
		String deploymentName = "cf-openstack";
		List<VmsListDTO> vmList = vmsService.getVmList(deploymentName);
		if(LOGGER.isDebugEnabled()){
			for(int i=0; i < vmList.size(); i++){
				LOGGER.debug("VM : " + vmList.get(i).getJobName()  + "/"  + vmList.get(i).getIndex() );
				LOGGER.debug("State : " + vmList.get(i).getState() );
				LOGGER.debug("AZ : " + vmList.get(i).getAz() );
				LOGGER.debug("VM Type : " + vmList.get(i).getVmType() );
				LOGGER.debug("IPs : " + vmList.get(i).getIps() );
				LOGGER.debug("Load(avg01, avg05, agv15 :" + vmList.get(i).getLoad() );
				LOGGER.debug("Cpu User : " + vmList.get(i).getCpuUser() );
				LOGGER.debug("Cpu Sys : " + vmList.get(i).getCpuSys() );
				LOGGER.debug("Cpu Wait : " + vmList.get(i).getCpuWait() );
				LOGGER.debug("Memory Usage : " + vmList.get(i).getMemoryUsage() );
				LOGGER.debug("Swap Usage : " + vmList.get(i).getSwapUsage() );
				LOGGER.debug("System Disk Usage : " + vmList.get(i).getDiskSystem() );
				LOGGER.debug("Ephemeral Disk Usage : " + vmList.get(i).getDiskEphemeral() );
				LOGGER.debug("Persistent Disk Usage : " + vmList.get(i).getDiskPersistent() );
			}
		}
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> VM 조회 요청 성공");  }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Agent/Job 로그 다운로드
	 * @title               : testDoDoenwloadLog
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDoDoenwloadLog() throws Exception {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Agent/Job 로그 다운로드 요청");  }
		MockHttpServletRequest request = new MockHttpServletRequest();       
		MockHttpServletResponse response = new MockHttpServletResponse();
		String jobName = "consul_z1";
		String index = "0";
		String deploymentName = "cf-openstack-test";
		String type = "job";
		
		assertTrue(vmsService.doDownloadLogTest(jobName, index, deploymentName, type, request, response)==200);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Agent/Job 로그 다운로드 요청 성공");  }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Job 상태 관리
	 * @title               : testChangeJobState
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testChangeJobState() throws Exception {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Job 재시작 요청");  }
		VmsListDTO dto = vmJobInfo();
		assertTrue(vmsAsyncService.doGetJobLogAsync(dto).equals("done"));
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Job 재시작 요청 성공");  }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 설치 관리자 설정
	 * @title               : testSaveDefaultDirector
	 * @return            : DirectorConfigVO
	***************************************************/
	public DirectorConfigVO testSaveDefaultDirector(){
		//director 설정
		DirectorConfigVO defaultDirector = dao.selectDirectorConfigByDefaultYn("Y");
		if( defaultDirector != null ){
			dao.deleteDirecotr(defaultDirector.getIedaDirectorConfigSeq());
		}
		DirectorConfigVO director = settingDefaultDirector();
		assertTrue(dao.insertDirector( director) == 1);
		return director;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 설치 관리자 설정
	 * @title               : settingDefaultDirector
	 * @return            : DirectorConfigVO
	***************************************************/
	public DirectorConfigVO settingDefaultDirector(){
		DirectorConfigVO vo = new DirectorConfigVO();
		vo.setIedaDirectorConfigSeq(1);
		vo.setDefaultYn("Y");
		vo.setDirectorCpi("openstack-cpi");
		vo.setDirectorName("bosh");
		vo.setDirectorPort(25555);
		vo.setDirectorUrl("10.10.10.10");
		vo.setDirectorUuid("3b623d50-10c1-450a-8aad-test");
		vo.setDirectorVersion("1.3252.0");
		vo.setCreateUserId("tester");
		vo.setUpdateUserId("tester");
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Openstack 정보 저장 
	 * @title               : testSaveOpenstack
	 * @return            : void
	***************************************************/
	@Rollback(true)
	public void testSaveOpenstack() throws Exception{
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP OPENSTACK 정보 저장 START  ================="); 
		}
		// bootstrap openstack Info
		BootstrapVO openstackInfo = setBootStrapOpenstack();
		bootstrapDao.insertBootStrapInfo(openstackInfo);
		// bootstrap default Info
		BootstrapVO defaultInfo = setBootStrapDefault();
		bootstrapDao.updateBootStrapInfo(defaultInfo);
		
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  BOOTSTRAP OPENSTACK 정보 저장 END  ================="); 
		}
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bootstrap Openstack 정보 설정
	 * @title               : setBootStrapOpenstack
	 * @return            : BootstrapVO
	***************************************************/
	public BootstrapVO setBootStrapOpenstack() throws Exception{
		
		BootstrapVO vo = new BootstrapVO();
		
		vo.setId(1);
		vo.setIaasType("OPENSTACK");
		vo.setOpenstackAuthUrl("bootstrap-openstack-authUrl");
		vo.setOpenstackTenant("bosh");
		vo.setOpenstackUserName("bosh");
		vo.setOpenstackApiKey("1234");
		vo.setDefaultSecurityGroups("test-security");
		vo.setPrivateKeyName("test-key");
		vo.setPrivateKeyPath("test-key.pem");
		vo.setCreateUserId("test");
		vo.setUpdateUserId("test");
		
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bootstrap 기본정보 설정 및 수정
	 * @title               : setBootStrapDefault
	 * @return            : BootstrapVO
	***************************************************/
	public BootstrapVO setBootStrapDefault() throws Exception{
		
		BootstrapVO vo = new BootstrapVO();
		vo.setId(1);
		vo.setDeploymentName("bosh");
		vo.setDirectorName("test-bosh");
		vo.setNtp("1.kr.pool.ntp.org, 0.asia.pool.ntp.org");
		vo.setBoshRelease("bosh-233.tgz");
		vo.setBoshCpiRelease("bosh-openstack-cpi-release-14.tgz");
		vo.setEnableSnapshots("true");
		vo.setSnapshotSchedule("0 0 7 * * *schedule");
		vo.setCreateUserId("test");
		vo.setUpdateUserId("test");
		
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   :  Job 재시작 정보 설정
	 * @title               : vmJobInfo
	 * @return            : VmsListDTO
	***************************************************/
	public VmsListDTO vmJobInfo() {
		VmsListDTO vmsDto = new VmsListDTO();
		vmsDto.setDeploymentName("cf-openstack-test");
		vmsDto.setJobName("consul_z1");
		vmsDto.setIndex("0");
		vmsDto.setState("restart");
		
		return vmsDto;
	}

}
