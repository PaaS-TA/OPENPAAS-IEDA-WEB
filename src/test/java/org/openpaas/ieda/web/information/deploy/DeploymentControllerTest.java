package org.openpaas.ieda.web.information.deploy;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

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
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class, TestBeansConfiguration.class})

@WebAppConfiguration
@TestPropertySource(locations="classpath:application_test.properties")
public class DeploymentControllerTest extends BaseTestController{
	
	@Autowired WebApplicationContext wac;
	@Autowired DeploymentServiceTest service;
	
	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	private final static Logger LOGGER = LoggerFactory.getLogger(DeploymentControllerTest.class);	
	
	final static String VIEW_URL = "/info/deployment"; //배포 정보 메뉴 이동 URL
	
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
	 * @description   : 배포 정보 화면 요청
	 * @title               : testGoListDeployment
	 * @return            : void
	***************************************************/
	@Test
	public void testGoListDeployment() throws Exception {
		
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================   배포 정보 화면 이동 TEST START  ================="); }
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================   배포 정보 화면 이동 TEST END  ================="); }
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포 정보 요청
	 * @title               : testGetListDeployment
	 * @return            : void
	***************************************************/
	@Test
	public void testGetListDeployment() throws Exception {
		
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================   배포 정보 목록 조회 TEST START  ================="); }
		List<DeploymentInfoDTO> list = service.listDeployment();
		for(int i=0;i<list.size();i++){
			LOGGER.debug(list.get(i).getName());
			LOGGER.debug(list.get(i).getReleaseInfo());
			LOGGER.debug(list.get(i).getStemcellInfo());
		}
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================   배포 정보 목록 조회 TEST END  ================="); }
	}
	
	
}
