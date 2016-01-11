package org.openpaas.ieda.web.information.task;

import java.util.Arrays;

import org.apache.commons.httpclient.HttpClient;
import org.openpaas.ieda.api.director.DirectorRestHelper;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	private void doGetTaskLog(String logType, String taskId) {
		IEDADirectorConfig defaultDirector = directorConfigService.getDefaultDirector();

		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			DirectorRestHelper.trackToTask(defaultDirector, messagingTemplate, messageEndpoint, httpClient, taskId, logType);
			
		} catch ( Exception e) {
			DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "error", Arrays.asList("Task 이벤트 로그 조회 중 Exception이 발생하였습니다."));
		}
	}
	
	@Async
	public void doGetTaskLogAsync(String logType, String taskId) {
		doGetTaskLog(logType, taskId);
	}
}
