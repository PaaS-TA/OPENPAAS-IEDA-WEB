package org.openpaas.ieda.web.information.task;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.api.task.TaskListDTO;
import org.openpaas.ieda.web.common.BaseTestController;
import org.openpaas.ieda.web.information.task.dto.TaskDTO;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class})
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
public class TaskControllerTest extends BaseTestController{
	
	@Autowired WebApplicationContext wac;
	@Autowired TaskServiceTest service;
	
	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	private final static Logger LOGGER = LoggerFactory.getLogger(TaskControllerTest.class);
	
	/************************* URL ************************************/
	final static String VIEW_URL = "/info/task"; //Task 정보 화면 요청
	/************************* URL **********************************/
	
	/***************************************************
	 * @project 	: OpenPaas 플랫폼 설치 자동
	 * @description : 하나의 메소드가 동작한 직후 실행
	 * @title 		: setUp
	 * @return 		: void
	***************************************************/
	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
		getLoggined();
	}
	
	/***************************************************
	 * @project 	: OpenPaas 플랫폼 설치 자동
	 * @description : 스템셀 관리 화면 요청
	 * @title 		: testGoListTaskHistory
	 * @return 		: void
	***************************************************/
	@Test
	public void testGoTaskHistory() throws Exception {
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================   Task 화면 이동 START  ================="); }
		ResultActions result = mockMvc.perform(get(VIEW_URL)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================   Task 화면 화면 이동 END  ================="); }
	}
	
	/***************************************************
	 * @project 	: OpenPaas 플랫폼 설치 자동
	 * @description : 스템셀 목록 정보 조회
	 * @title 		: testGetListTaskHistory
	 * @return 		: void
	***************************************************/
	@Test
	public void testGetListTaskHistory() throws Exception {
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  Task 목록 정보 조회 START  ================="); }
		List<TaskListDTO> list = service.listTask();
		for(int i=0;i<list.size();i++){
			LOGGER.info(list.get(i).getDeployment());
			LOGGER.info(list.get(i).getId());
			LOGGER.info(list.get(i).getState());
			LOGGER.info(list.get(i).getDescription());
			LOGGER.info(list.get(i).getResult());
		}
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  Task 목록 정보 조회 END  ================="); }
	}
	
	/***************************************************
	 * @project       : OpenPaas 플랫폼 설치 자동화
	 * @description   : 디버그 로그 다운로드
	 * @title         : testDoDownloadTaskLog
	 * @return        : void
	***************************************************/
	@Test
	public void testDoDownloadTaskLog() throws Exception {
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  Task 디버그 로그 다운로드 START  ================="); }
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = sra.getRequest();
		HttpServletResponse response = sra.getResponse();
		service.getDownloadDebugLogFile(request,response);
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  Task 디버그 로그 다운로드 END  ================="); }
	}
	
	/***************************************************
	 * @project       : OpenPaas 플랫폼 설치 자동화
	 * @description   : 이벤트 로그 조회
	 * @title         : testDoGetTaskLog
	 * @return        : void
	***************************************************/
	@Test
	public void testDoGetTaskLog(){
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  Task 이벤트 로그 조회 START  ================="); }
		TaskDTO.GetLog dto = settingEvnetLogInfo();
		service.doGetTaskLogAsync(dto);
		if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  Task 이벤트 로그 조회 END  ================="); }
	}
	
	/***************************************************
	 * @project       : OpenPaas 플랫폼 설치 자동화
	 * @description   : 이벤트 로그 정보 설정
	 * @title         : settingEvnetLogInfo
	 * @return        : TaskDTO.GetLog
	***************************************************/
	public TaskDTO.GetLog settingEvnetLogInfo() {
		TaskDTO.GetLog dto = new TaskDTO.GetLog();
		dto.setLineOneYn("false");
		dto.setLogType("event");
		dto.setTaskId("1");
		return dto;
	}
}
