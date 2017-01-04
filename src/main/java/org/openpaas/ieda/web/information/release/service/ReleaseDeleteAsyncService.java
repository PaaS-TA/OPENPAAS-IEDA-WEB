package org.openpaas.ieda.web.information.release.service;

import java.security.Principal;
import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ReleaseDeleteAsyncService {
	
	@Autowired private SimpMessagingTemplate messagingTemplate;
	@Autowired private DirectorConfigService directorConfigService;
	
	final private static String MESSAGEENDPOINT = "/info/release/delete/socket/logs"; 

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   :  업로드된 릴리즈 삭제 요청
	 * @title               : deleteRelease
	 * @return            : void
	***************************************************/
	public void deleteRelease(String releaseName, String releaseVersion, Principal principal) {
		
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();

		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			DeleteMethod deleteMethod = new DeleteMethod(DirectorRestHelper.getDeleteReleaseURI(
					defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), releaseName, releaseVersion));
			deleteMethod = (DeleteMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)deleteMethod);
			//실행
			int statusCode = httpClient.executeMethod(deleteMethod);
			
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value() 
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				
				Header location = deleteMethod.getResponseHeader("Location");
				String taskId = DirectorRestHelper.getTaskId(location.getValue());
				
				DirectorRestHelper.trackToTask(defaultDirector, messagingTemplate, MESSAGEENDPOINT, httpClient, taskId, "event", principal.getName());
			} else {
				DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGEENDPOINT, "error", Arrays.asList("릴리즈 삭제 중 오류가 발생하였습니다."));
			}
			
		}catch(RuntimeException e){
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGEENDPOINT, "error", Arrays.asList("릴리즈 삭제 중 Exception이 발생하였습니다."));
		}catch ( Exception e) {
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGEENDPOINT, "error", Arrays.asList("릴리즈 삭제 중 Exception이 발생하였습니다."));
		}
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기로 deleteRelease 메소드 호출
	 * @title               : deleteReleaseAsync
	 * @return            : void
	***************************************************/
	@Async
	public void deleteReleaseAsync(String releaseName, String releaseVersion, Principal principal) {
		deleteRelease(releaseName, releaseVersion, principal);
	}
}
