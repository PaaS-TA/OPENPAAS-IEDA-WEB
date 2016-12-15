package org.openpaas.ieda.web.information.task;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.hornetq.utils.json.JSONException;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.api.task.TaskListDTO;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.information.task.dto.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@TestPropertySource(locations="classpath:application_test.properties")
public class TaskServiceTest {
	final private static Logger LOGGER = LoggerFactory.getLogger(TaskServiceTest.class);
	
	/***************************************************
	 * @project       : OpenPaas 플랫폼 설치 자동화
	 * @description   : Task 정보 목록 조회
	 * @title         : listTask
	 * @return        : List<TaskListDTO>
	***************************************************/
	@JsonIgnoreProperties(ignoreUnknown = true)
	public List<TaskListDTO> listTask() {
		//기본설치자 조회
				DirectorConfigVO defaultDirector = settingDefaultDirector();

				TaskListDTO[] tasks = null;
				
				try {
					HttpClient client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
					GetMethod get = new GetMethod(DirectorRestHelper.getTaskListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
					get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);
					ObjectMapper mapper = new ObjectMapper();
					tasks = mapper.readValue(setTasksInfo(), TaskListDTO[].class);
					
				} catch (ResourceAccessException e) {
					throw new CommonException("notfound.tasks.exception", " Task정보 조회중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
				} catch (Exception e) {
					throw new CommonException("notfound.tasks.exception", "요청정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
				}
				//List로 반환
				return Arrays.asList(tasks);
	}
	
	/***************************************************
	 * @param response 
	 * @param request 
	 * @project       : OpenPaas 플랫폼 설치 자동화
	 * @description   : Task 디버그 다운로드
	 * @title         : getDownloadDebugLogFile
	 * @return        : void
	***************************************************/
	public void getDownloadDebugLogFile(HttpServletRequest request, HttpServletResponse response) {
		String content = "";
		String fileName = "";
		int statusCode = 0;
		
		try {
			//설치 관리자 정보 조회
			DirectorConfigVO defaultDirector = settingDefaultDirector();
			
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			GetMethod getTaskOutputMethod = new GetMethod(DirectorRestHelper.getTaskOutputURI(
					defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), "1", "debug"));
			getTaskOutputMethod = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(),
					defaultDirector.getUserPassword(), (HttpMethodBase) getTaskOutputMethod);
			String range = "bytes=" + 0 + "-";
			getTaskOutputMethod.setRequestHeader("Range", range);
			//4. 요청
			statusCode = 206;
			if( LOGGER.isDebugEnabled() ) { 
				LOGGER.debug( "status Code : " + statusCode);
			}
			//5. 요청 결과 정보
			content = setDebugInfo();
			fileName = "Test_task_debug_"+ "1" + ".log"; //로그 파일명
			response.setContentType("application/octet-stream");
			//Content-Disposition : 브라우저에서 다운로드 창을 띄우는 역할
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			//문자열 데이터를 파일에 쓴다.
			IOUtils.write(content, response.getOutputStream());
			
		} catch (IOException e) {
			if( LOGGER.isErrorEnabled() ){
				LOGGER.error( e.getMessage() );  
			}
		}
		
	}
	
	/***************************************************
	 * @param dto 
	 * @project       : OpenPaas 플랫폼 설치 자동화
	 * @description   : 이벤트 로그 조회
	 * @title         : doGetTaskLog
	 * @return        : void
	***************************************************/
	private void doGetTaskLog(TaskDTO.GetLog dto) {
		String lineOneYn = dto.getLineOneYn();
		String taskId = dto.getTaskId();
		String logType = dto.getLogType();
		DirectorConfigVO defaultDirector = settingDefaultDirector();
		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			if( "true".equals(lineOneYn) ){
				LOGGER.debug( "taskId : " + taskId);
				LOGGER.debug( "logType: " + logType);
				LOGGER.debug( "################ 이벤트로그 조회 성공");
			}else{
				LOGGER.debug( "taskId: " + taskId);
				LOGGER.debug( "logType: " + logType);
				LOGGER.debug( "################ 이벤트로그 조회 성공");
			}
		} catch ( Exception e) {
			throw new CommonException("INTERNAL_SERVER_ERROR.tasks.exception", " Task 이벤트로그 조회중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/***************************************************
	 * @project 			: OpenPaas 플랫폼 설치 자동화
	 * @description 		: 기본 설치 관리자 설정
	 * @title 				: settingDefaultDirector
	 * @return 			: DirectorConfigVO
	***************************************************/
	public DirectorConfigVO settingDefaultDirector(){
		DirectorConfigVO vo = new DirectorConfigVO();
		vo.setIedaDirectorConfigSeq(1);
		vo.setDefaultYn("Y");
		vo.setDirectorCpi("openstack-cpi");
		vo.setDirectorName("bosh");
		vo.setDirectorPort(25555);
		vo.setDirectorUrl("172.16.XXX.XXX");
		vo.setDirectorUuid("3b623d50-10c1-450a-8aad-test");
		vo.setDirectorVersion("1.3252.0");
		return vo;
	}
	
	/***************************************************
	 * @project       : OpenPaas 플랫폼 설치 자동화
	 * @description   : Task 정보 설정
	 * @title         : setTasksInfo
	 * @return        : String
	***************************************************/
	public String setTasksInfo() throws JSONException{
		String info = "[{\"id\":\"6782\",";
			info += "\"state\":\"done\",";
			info += "\"description\":\"create release\",";
			info += "\"timestamp\":\"1476404012\",";
			info += "\"result\":\"Created release 'cf/241'\",";
			info += "\"user\":\"admin\",";
			info += "\"deployment\":\"null\"}]";
		return info;
	}
	
	/***************************************************
	 * @project       : OpenPaas 플랫폼 설치 자동화
	 * @description   : 디버그 정보 설정
	 * @title         : setDebugInfo
	 * @return        : String
	***************************************************/
	private String setDebugInfo() {
		String info = "D, [2016-10-14 00:13:32 #24882] [task:6782] DEBUG -- DirectorJobRunner: (0.000497s) UPDATE \"tasks\" SET \"state\" = 'done', \"timestamp\"";
		info+= "='2016-10-14 00:13:32.635895+0000', \"description\" = 'create release',";
		info+= "\"result\" = 'Created release ''cf/241''', \"output\" = '/var/vcap/store/director/tasks/6782', \"checkpoint_time\" = '2016-10-14 00:08:54.748103+0000',";
		info+= " \"type\" = 'update_release', \"username\" = 'admin', \"deployment_name\" = NULL, \"started_at\" = '2016-10-14 00:08:54.748035+0000' WHERE (\"id\" = 6782) \n";
		info += "D, [2016-10-14 00:13:32 #24882] [task:6782] DEBUG -- DirectorJobRunner: (0.014744s) COMMIT \n";
		info += "I, [2016-10-14 00:13:32 #24882] []  INFO -- DirectorJobRunner: Task took 4 minutes 37.91153457799999 seconds to process.";
		return info;
	}
	
	/***************************************************
	 * @param dto 
	 * @project       : OpenPaas 플랫폼 설치 자동화
	 * @description   : 비동기식 호출
	 * @title         : doGetTaskLogAsync
	 * @return        : void
	***************************************************/
	public void doGetTaskLogAsync(TaskDTO.GetLog dto) {
		doGetTaskLog(dto);
	}

}
