package org.openpaas.ieda.web.information.task.service;

import java.security.Principal;
import java.util.Arrays;

import org.apache.commons.httpclient.HttpClient;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class TaskAsyncService {
	
	@Autowired private SimpMessagingTemplate messagingTemplate;
	@Autowired private DirectorConfigService directorConfigService;
	
	final static private String messageEndpoint = "/info/task/list/eventLog/socket"; 
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   :  Task의 이벤트 이력 정보 요청
	 * @title               : doGetTaskLog
	 * @return            : void
	***************************************************/
	private void doGetTaskLog(String logType, String taskId, String lineOneYn, Principal principal) {
		
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			if( "true".equals(lineOneYn) ){
				DirectorRestHelper.trackToTaskLineOne(defaultDirector, messagingTemplate, messageEndpoint, httpClient, taskId, logType, principal.getName());
			}else{
				DirectorRestHelper.trackToTask( defaultDirector, messagingTemplate, messageEndpoint, httpClient, taskId, logType, principal.getName());
			}
		} catch( HttpClientErrorException e){
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("Task 이벤트 로그 조회 중 Exception이 발생하였습니다."));
		} catch ( Exception e) {
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("Task 이벤트 로그 조회 중 Exception이 발생하였습니다."));
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기로 doGetTaskLog 메소드 호출
	 * @title               : doGetTaskLogAsync
	 * @return            : void
	***************************************************/
	@Async
	public void doGetTaskLogAsync(String logType, String taskId, String lineOneYn, Principal principal) {
		doGetTaskLog(logType, taskId, lineOneYn, principal);
	}
}
