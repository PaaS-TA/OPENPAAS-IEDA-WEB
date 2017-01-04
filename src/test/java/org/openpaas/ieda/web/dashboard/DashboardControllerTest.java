package org.openpaas.ieda.web.dashboard;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.TestBeansConfiguration;
import org.openpaas.ieda.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.web.common.BaseTestController;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class, TestBeansConfiguration.class})
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
public class DashboardControllerTest extends BaseTestController{
	
	@Autowired WebApplicationContext wac;
	@Autowired DashboardServiceTest service;
	
	private MockMvc mockMvc;
	final private static Logger LOGGER = LoggerFactory.getLogger(DashboardControllerTest.class);
	final static String DASHBOARD_URL = "/main/dashboard";//대시보드 화면
	
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
		getLoggined();
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : DASHBOARD 화면 호출
	 * @title               : testGoDashboard
	 * @return            : void
	***************************************************/
	@Test
	public void testGoDashboard() throws Exception {
		if(LOGGER.isInfoEnabled()) LOGGER.info("=============== 대쉬 보드 화면 이동 TEST TEST START ===============");
		ResultActions result = mockMvc.perform(get(DASHBOARD_URL).contentType(MediaType.APPLICATION_JSON));
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()) LOGGER.info("=============== 대쉬 보드 화면 이동 TEST TEST END ===============");
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치 정보 목록 조회
	 * @title               : testListDeployment
	 * @return            : void
	***************************************************/
	@Test
	public void testListDeployment() throws Exception {
		if(LOGGER.isInfoEnabled()) LOGGER.info("=============== 배포 목록 조회 TEST TEST START ===============");
		List<DeploymentInfoDTO> list = service.listDeployment();
		for(int i=0;i<list.size();i++){
			LOGGER.debug(list.get(i).getName());
			LOGGER.debug(list.get(i).getReleaseInfo());
			LOGGER.debug(list.get(i).getStemcellInfo());
		}
		if(LOGGER.isInfoEnabled()) LOGGER.info("=============== 배포 목록 조회 TEST TEST END ===============");
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 업로드 릴리즈 정보 목록 조회
	 * @title               : testListRelease
	 * @return            : void
	***************************************************/
	@Test
	public void testListRelease() throws Exception {
		LOGGER.info("=============== 업로드된 릴리즈 목록 조회 TEST START ===============");
		service.uploadedReleaseList();
		LOGGER.info("=============== 업로드된 릴리즈 목록 조회 TEST END ===============");
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 업로드 스템셀 정보 목록 조회
	 * @title               : listStemcell
	 * @return            : void
	***************************************************/
	@Test
	public void listStemcell() throws Exception {
		LOGGER.info("=============== 업로드된 스템셀 목록 조회 TEST START ===============");
		service.uploadedStemcellList();
		LOGGER.info("=============== 업로드된 스템셀 목록 조회 TEST END ===============");
	}
}
