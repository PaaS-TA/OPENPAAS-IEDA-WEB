package org.openpaas.ieda.web.information.task.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.api.task.TaskListDTO;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TaskService {
	
	@Autowired private DirectorConfigService directorConfigService;
	final private static Logger LOGGER = LoggerFactory.getLogger(TaskService.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : bosh에 Task 실행 이력 정보 요청
	 * @title               : listTask
	 * @return            : List<TaskListDTO>
	***************************************************/
	@JsonIgnoreProperties(ignoreUnknown = true)
	public List<TaskListDTO> listTask() {
		//기본설치자 조회
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();

		TaskListDTO[] tasks = null;
		
		try {
			HttpClient client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			GetMethod get = new GetMethod(DirectorRestHelper.getTaskListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);
			client.executeMethod(get);
			ObjectMapper mapper = new ObjectMapper();
			tasks = mapper.readValue(get.getResponseBodyAsString(), TaskListDTO[].class);

		} catch (ResourceAccessException e) {
			throw new CommonException("notfound.tasks.exception", " Task정보 조회중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		} catch (JsonParseException e) {
			throw new CommonException("jsonParse.tasks.exception", " Task정보 조회중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (JsonMappingException e) {
			throw new CommonException("jsonMapping.tasks.exception", " Task정보 조회중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			throw new CommonException("internalServer.tasks.exception", " Task정보 조회중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return Arrays.asList(tasks);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 디버그 로그 다운로드 요청
	 * @title               : getDownloadDebugLogFile
	 * @return            : void
	***************************************************/
	public void getDownloadDebugLogFile(String taskId, HttpServletRequest request, HttpServletResponse response) {
		String content = "";
		String fileName = "";
		int statusCode = 0;
		
		try {
			//설치 관리자 정보 조회
			DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
			
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			GetMethod getTaskOutputMethod = new GetMethod(DirectorRestHelper.getTaskOutputURI(
					defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId, "debug"));
			getTaskOutputMethod = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(),
					defaultDirector.getUserPassword(), (HttpMethodBase) getTaskOutputMethod);
			String range = "bytes=" + 0 + "-";
			getTaskOutputMethod.setRequestHeader("Range", range);
			//요청
			statusCode = httpClient.executeMethod(getTaskOutputMethod);
			if( LOGGER.isDebugEnabled() ) { 
				LOGGER.debug( "status Code : " + statusCode);
			}
			//요청 결과 정보
			content = getTaskOutputMethod.getResponseBodyAsString().replace("\n", "\r\n");
			Date now = new Date();
			SimpleDateFormat dataformat = new SimpleDateFormat("yyyymmdd_HHmmss", Locale.KOREA);
			fileName = dataformat.format(now)+"_task_debug_"+ taskId + ".log"; //로그 파일명
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
}
