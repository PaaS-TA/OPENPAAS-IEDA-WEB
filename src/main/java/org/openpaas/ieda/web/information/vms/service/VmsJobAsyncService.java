package org.openpaas.ieda.web.information.vms.service;

import java.security.Principal;
import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONObject;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.web.information.vms.dto.VmsListDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class VmsJobAsyncService {

	@Autowired private SimpMessagingTemplate messagingTemplate;
	@Autowired private DirectorConfigService directorConfigService;
	
	final private static String MESSAGE_ENDPOINT = "/info/vms/vmLogs/socket"; 
	final private static int THREAD_SLEEP_TIME = 1 * 1000;
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Job 상태 요청
	 * @title               : doGetJobLog
	 * @return            : void
	***************************************************/
	public void doGetJobLog( VmsListDTO dto, Principal principal ){
		//1.1 git director Info
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		String content = "";
		String taskId = "";
		HttpClient httpClient  = null;
		GetMethod getMethod = null;
		PutMethod putMehotd  = null;
		try{
			//1.1 get manifest content by deployment
			httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			getMethod  = new GetMethod(DirectorRestHelper.getManifestURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), dto.getDeploymentName()));
			getMethod = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)getMethod);
			
			int statusCode = httpClient.executeMethod(getMethod);
			if (HttpStatus.valueOf(statusCode) != HttpStatus.OK) {
				throw new CommonException("notfound.vm.exception", "Job 정보가 존재 하지 않습니다.", HttpStatus.NOT_FOUND);
			}
			JSONObject obj = new JSONObject(getMethod.getResponseBodyAsString());
			content = obj.get("manifest").toString();
			
			Thread.sleep(THREAD_SLEEP_TIME);
			
			//1.2 put job state
			putMehotd  = new PutMethod(DirectorRestHelper
					.getJobStateURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), dto.getDeploymentName(), dto.getJobName(), dto.getIndex(),  dto.getState()));
			putMehotd = (PutMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)putMehotd);
			putMehotd.setRequestHeader("Content-Type", "text/yaml");
			
			putMehotd.setRequestEntity(new StringRequestEntity(content, "text/yaml", "UTF-8"));
			
			statusCode = httpClient.executeMethod(putMehotd);
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
					  || statusCode == HttpStatus.MOVED_TEMPORARILY.value() ) {
				
				Header location = putMehotd.getResponseHeader("Location");
				taskId = DirectorRestHelper.getTaskId(location.getValue());
				DirectorRestHelper.trackToTask(defaultDirector, messagingTemplate, MESSAGE_ENDPOINT, httpClient, taskId, "event", principal.getName());
			}else {
				DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("Job " + dto.getState() + " 중 오류가 발생하였습니다."));
			}
		}catch(RuntimeException e){
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("Job " + dto.getState() + " 중 Exception이 발생하였습니다."));
		}catch(Exception e){
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("Job " + dto.getState() + " 중 Exception이 발생하였습니다."));
		}finally{
			if ( getMethod != null ){
				getMethod.releaseConnection();
			}
			if ( putMehotd != null ){
				putMehotd.releaseConnection();
			}
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기로 doGetJobLog 호출
	 * @title               : doGetJobLogAsync
	 * @return            : void
	***************************************************/
	@Async
	public void doGetJobLogAsync(VmsListDTO dto, Principal principal) {
		doGetJobLog(dto, principal);
	}
}
