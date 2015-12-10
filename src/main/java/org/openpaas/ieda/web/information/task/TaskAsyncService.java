package org.openpaas.ieda.web.information.task;

import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.api.director.DirectorRestHelper;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskAsyncService {
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private IEDADirectorConfigService directorConfigService;
	
	final private String messageEndpoint = "/socket/task"; 
	
	private void getDebugLog(String taskId) {
		IEDADirectorConfig defaultDirector = directorConfigService.getDefaultDirector();

		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			GetMethod getMethod = new GetMethod(DirectorRestHelper.getTaskOutputURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId, "debug"));
			getMethod = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)getMethod);

			httpClient.executeMethod(getMethod);
			
			DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "done", Arrays.asList(getMethod.getResponseBodyAsString()));
			
		} catch ( Exception e) {
			DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "error", Arrays.asList("Task 디버그 로그 조회 중 Exception이 발생하였습니다."));
		}
	}
	
	private void getEventLog(String taskId) {
		IEDADirectorConfig defaultDirector = directorConfigService.getDefaultDirector();

		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			DirectorRestHelper.trackToTask(defaultDirector, messagingTemplate, messageEndpoint, httpClient, taskId);
			
		} catch ( Exception e) {
			DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "error", Arrays.asList("Task 이벤트 로그 조회 중 Exception이 발생하였습니다."));
		}
	}

	private void doGetTaskLog(String logType, String taskId) {
		if ( logType.equals("debug") )
			getDebugLog(taskId);
		else if ( logType.equals("event") )
			getEventLog(taskId);
	}
	
	@Async
	public void doGetTaskLogAsync(String logType, String taskId) {
		doGetTaskLog(logType, taskId);
	}
}
