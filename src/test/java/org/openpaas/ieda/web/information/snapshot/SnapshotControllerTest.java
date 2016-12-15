package org.openpaas.ieda.web.information.snapshot;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.web.common.BaseTestController;
import org.openpaas.ieda.web.information.snapshot.dto.SnapshotListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class})
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
public class SnapshotControllerTest extends BaseTestController{

	@Autowired WebApplicationContext wac;
	@Autowired SnapshotServiceTest snapshotService;
	
	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SnapshotControllerTest.class);
	final static String VIEW_URL = "/info/snapshot"; //VM 정보 화면 이동 URL
	
	
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
	 * @description   : 스냅샷 화면 호출
	 * @title               : testGoLisSnapshot
	 * @return            : void
	***************************************************/
	@Test
	public void testGoLisSnapshot() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 스냅샷 조회 화면 테스트 요청"); }
		}
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){ 
			if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 스냅샷 조회 화면 테스트 요청 성공"); }
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   :  스냅샷 정보 목록 조회
	 * @title               : testGetSnapshotList
	 * @return            : void
	***************************************************/
	@Test
	public void testGetSnapshotList() throws Exception{
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 스냅샷 조회 테스트 요청");  }
		String deploymentName = "cf-openstack";
		assertTrue(snapshotService.getSnapshotList(deploymentName).size() == 1);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 스냅샷 조회 테스트 요청 성공");  }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 삭제
	 * @title               : testDeleteSnapshot
	 * @return            : void
	***************************************************/
	@Test
	public void testDeleteSnapshot() throws Exception {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 스냅샷 삭제 테스트 요청");  }
		String type = "part";
		SnapshotListDTO dto  = settingDeleteSnapshotInfo();
		assertTrue( "done".equals(snapshotService.deleteSnapshots(type, dto)) );
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 스냅샷 삭제 테스트 요청 성공");  }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 삭제 입력 데이터 설정
	 * @title               : settingDeleteSnapshotInfo
	 * @return            : SnapshotListDTO
	***************************************************/
	public SnapshotListDTO settingDeleteSnapshotInfo(){
		SnapshotListDTO dto = new SnapshotListDTO();
		dto.setDeploymentName("cf-openstack");
		dto.setSnapshotCid("test-cid");
		
		return dto;
	}
}
