package org.openpaas.ieda.web.information.vms.service;

import java.security.Principal;
import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
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
public class VmsSnapshotAsyncService {

	@Autowired private SimpMessagingTemplate messagingTemplate;
	@Autowired private DirectorConfigService directorConfigService;
	
	final private static String MESSAGE_ENDPOINT = "/info/vms/snapshotLog/socket"; 
	final private static int THREAD_SLEEP_TIME = 2 * 1000;
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 생성 요청 로그를 웹소켓을 통해 전달
	 * @title               : doGetSnapshotLog
	 * @return            : String
	***************************************************/
	public String doGetSnapshotLog(VmsListDTO dto, Principal principal ){
		
		//1.1 director Info
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		String content = "";
		String taskId = "";
		String status = "";
		HttpClient client = null;
		GetMethod getMethod = null;
		PostMethod postMethod  = null;
		try{
			//1.1 get manifest content by deployment
			client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			getMethod  = new GetMethod(DirectorRestHelper.getManifestURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), dto.getDeploymentName()));
			getMethod = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)getMethod);
			int statusCode = client.executeMethod(getMethod);
			
			if (HttpStatus.valueOf(statusCode) != HttpStatus.OK) {
				throw new CommonException("notfound.vm.exception",  "스냅 정보가 존재 하지 않습니다.", HttpStatus.NOT_FOUND);
			}
			JSONObject obj = new JSONObject(getMethod.getResponseBodyAsString());
			content = obj.get("manifest").toString();
			
			Thread.sleep(THREAD_SLEEP_TIME);
			
			//1.2 POST take_snapshot
			postMethod = new PostMethod(DirectorRestHelper.getTakeSnapshotURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), dto.getDeploymentName(), dto.getJobName(), dto.getIndex()));
			postMethod = (PostMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)postMethod);
			postMethod.setRequestHeader("Content-Type", "text/yaml");
			
			postMethod.setRequestEntity(new StringRequestEntity(content, "text/yaml", "UTF-8"));
			statusCode = client.executeMethod(postMethod);
			
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
					  || statusCode == HttpStatus.MOVED_TEMPORARILY.value() ) {
				
				Header location = postMethod.getResponseHeader("Location");
				taskId = DirectorRestHelper.getTaskId(location.getValue());
				
				Thread.sleep(THREAD_SLEEP_TIME);
				
				status = DirectorRestHelper.trackToTaskLineOne(defaultDirector, messagingTemplate, MESSAGE_ENDPOINT, client, taskId, "event", principal.getName());
			}else {
				DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("스냅샷 생성 중 오류가 발생하였습니다.[" + statusCode + "]"));
			}
		}catch(RuntimeException e){
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("스냅샷 생성 중 Exception이 발생하였습니다."));
		}catch(Exception e){
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("스냅샷 생성 중 Exception이 발생하였습니다."));
		}finally{
			if( getMethod != null ){
				getMethod.releaseConnection();
			}
			if( postMethod != null ){
				postMethod.releaseConnection();
			}
		}
		return status;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기 처리로 doGetSnapshotLog 호출
	 * @title               : doGetSnapshotLogAsync
	 * @return            : void
	***************************************************/
	@Async
	public void doGetSnapshotLogAsync(VmsListDTO dto, Principal principal) {
		doGetSnapshotLog(dto, principal);
	}
}
