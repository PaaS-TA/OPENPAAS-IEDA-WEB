package org.openpaas.ieda.web.information.release.service;


import java.io.File;
import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.PostMethod;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.web.information.stemcell.dto.FileUploadRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ReleaseUploadAsyncService {
	
	@Autowired private SimpMessagingTemplate messagingTemplate;
	@Autowired private DirectorConfigService directorService;
	
	final private static Logger LOGGER = LoggerFactory.getLogger(ReleaseUploadAsyncService.class);
	final private static String MESSAGE_ENDPOINT  = "/info/release/upload/socket/logs"; 
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 릴리즈 업로드 요청
	 * @title               : uploadRelease
	 * @return            : void
	***************************************************/
	public void uploadRelease( String releaseFileName, String userId) {
		DirectorConfigVO defaultDirector = directorService.getDefaultDirector();

		HttpClient httpClient = null;
		PostMethod postMethod = null;
		try {
			httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			postMethod = new PostMethod(DirectorRestHelper.getUploadReleaseURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			postMethod = (PostMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)postMethod);
			postMethod.setRequestHeader("Content-Type", "application/x-compressed");
			
  			String uploadFile = LocalDirectoryConfiguration.getReleaseDir() + System.getProperty("file.separator") + releaseFileName;
			postMethod.setRequestEntity(new FileUploadRequestDTO(new File(uploadFile), "application/x-compressed", messagingTemplate, MESSAGE_ENDPOINT, userId));
			DirectorRestHelper.sendTaskOutputWithTag(userId, messagingTemplate, MESSAGE_ENDPOINT, "Started", releaseFileName, Arrays.asList("Uploading Release ...", ""));
			
			int statusCode = httpClient.executeMethod(postMethod);
			
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				Header location = postMethod.getResponseHeader("Location");
				String taskId = DirectorRestHelper.getTaskId(location.getValue());
				
				DirectorRestHelper.trackToTaskWithTag(defaultDirector, messagingTemplate, MESSAGE_ENDPOINT, releaseFileName, httpClient, taskId, "event", userId);
			} else {
				if(LOGGER.isDebugEnabled()){  
					LOGGER.debug("################ 업로드 요청 오류 상태코드 : " +  statusCode);
				}
				DirectorRestHelper.sendTaskOutputWithTag(userId, messagingTemplate, MESSAGE_ENDPOINT, "error", releaseFileName, Arrays.asList("릴리즈 업로드 중 오류가 발생하였습니다.[" + statusCode + "]"));
			}
			
		} catch(RuntimeException e){
			DirectorRestHelper.sendTaskOutput(userId, messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("릴리즈 업로드 중 Exception이 발생하였습니다."));
		} catch ( Exception e) {
			DirectorRestHelper.sendTaskOutputWithTag(userId, messagingTemplate, MESSAGE_ENDPOINT, "error", releaseFileName, Arrays.asList("릴리즈 업로드 중 Exception이 발생하였습니다."));
		}finally{
			String lockFile = releaseFileName.split(".tgz")[0]+"-upload";
			File file = new File(LocalDirectoryConfiguration.getLockDir()+System.getProperty("file.separator")+lockFile+".lock");
			if(file.exists()){
				Boolean check = file.delete();
				if( LOGGER.isDebugEnabled() ){
					LOGGER.debug("check delete lock File  : "  + check);
				}
			}
			if(postMethod != null){
				postMethod.releaseConnection();
			}
			
		}
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기로 uploadRelease 메소드 호출
	 * @title               : uploadReleaseAsync
	 * @return            : void
	***************************************************/
	@Async
	public void uploadReleaseAsync(String releaseFileName, String sessionId) {
		uploadRelease(releaseFileName, sessionId);
	}
}
