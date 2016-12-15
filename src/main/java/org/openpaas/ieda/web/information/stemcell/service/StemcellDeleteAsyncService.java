package org.openpaas.ieda.web.information.stemcell.service;

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
public class StemcellDeleteAsyncService {
	
	@Autowired private SimpMessagingTemplate messagingTemplate;
	@Autowired private DirectorConfigService directorConfigService;
	
	final private static String MESSAGE_ENDPOINT  = "/info/stemcell/delete/logs"; 
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 업로드된 스템셀 삭제 요청
	 * @title               : deleteStemcell
	 * @return            : void
	***************************************************/
	public void deleteStemcell(String stemcellName, String stemcellVersion, Principal principal) {
		
		//기본 설치 관리자 정보 조회
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());

			DeleteMethod deleteMethod = new DeleteMethod(DirectorRestHelper.getDeleteStemcellURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), stemcellName, stemcellVersion));
			deleteMethod = (DeleteMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)deleteMethod);
			
			//Request에 대한 응답
			int statusCode = httpClient.executeMethod(deleteMethod);
			
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				
				Header location = deleteMethod.getResponseHeader("Location");
				String taskId = DirectorRestHelper.getTaskId(location.getValue());
				
				DirectorRestHelper.trackToTask(defaultDirector, messagingTemplate, MESSAGE_ENDPOINT, httpClient, taskId, "event", principal.getName());
				
			} else {
				DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("스템셀 삭제 중 오류가 발생하였습니다.[" + statusCode + "]"));
			}
			
		} catch ( RuntimeException e) {
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("스템셀 삭제 중 Exception이 발생하였습니다."));
		} catch ( Exception e) {
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("스템셀 삭제 중 Exception이 발생하였습니다."));
		}

	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기 처리방식으로 deleteStemcell 메소드 호출
	 * @title               : deleteStemcellAsync
	 * @return            : void
	***************************************************/
	@Async
	public void deleteStemcellAsync(String stemcellName, String stemcellVersion, Principal principal) {
		deleteStemcell(stemcellName, stemcellVersion, principal);
	}	
}
