package org.openpaas.ieda.web.information.stemcell.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
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
public class StemcellUploadAsyncService {
	
	@Autowired private SimpMessagingTemplate messagingTemplate;
	@Autowired private DirectorConfigService directorConfigService;
	
	final private static String MESSAGE_ENDPOINT  = "/info/stemcell/upload/logs"; 
	final private static Logger LOGGER = LoggerFactory.getLogger(StemcellUploadAsyncService.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 업로드 요청
	 * @title               : uploadStemcell
	 * @return            : void
	***************************************************/
	public void uploadStemcell(String stemcellDir, String stemcellFileName, String userId) {
		
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		PostMethod postMethod = null;
		HttpClient httpClient = null;
		try {
			httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			postMethod = new PostMethod(DirectorRestHelper.getUploadStemcellURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			postMethod = (PostMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)postMethod);
			postMethod.setRequestHeader("Content-Type", "application/x-compressed");
			
  			String uploadFile = stemcellDir + System.getProperty("file.separator") + stemcellFileName;
			
			postMethod.setRequestEntity(new FileUploadRequestDTO(new File(uploadFile), "application/x-compressed", messagingTemplate, MESSAGE_ENDPOINT, userId));

			DirectorRestHelper.sendTaskOutputWithTag(userId, messagingTemplate, MESSAGE_ENDPOINT, "Started", stemcellFileName, Arrays.asList("Uploading Stemcell ...", ""));
			
			int statusCode = httpClient.executeMethod(postMethod);
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				
				Header location = postMethod.getResponseHeader("Location");
				String taskId = DirectorRestHelper.getTaskId(location.getValue());
				
				DirectorRestHelper.trackToTaskWithTag(defaultDirector, messagingTemplate, MESSAGE_ENDPOINT, stemcellFileName , httpClient, taskId, "event", userId);
			} else {
				DirectorRestHelper.sendTaskOutputWithTag(userId, messagingTemplate, MESSAGE_ENDPOINT, "error", stemcellFileName, Arrays.asList("스템셀 업로드 중 오류가 발생하였습니다."));
			}
		} catch( HttpException e){
			DirectorRestHelper.sendTaskOutputWithTag(userId, messagingTemplate, MESSAGE_ENDPOINT, "error", stemcellFileName, Arrays.asList("스템셀 업로드 중 Exception이 발생하였습니다."));
		} catch ( IOException e) {
			DirectorRestHelper.sendTaskOutputWithTag(userId, messagingTemplate, MESSAGE_ENDPOINT, "error", stemcellFileName, Arrays.asList("스템셀 업로드 중 Exception이 발생하였습니다."));
		}finally{
			File file = new File(LocalDirectoryConfiguration.getLockDir() + System.getProperty("file.separator") + stemcellFileName.split(".tgz")[0]+"-upload.lock");
			if(file.exists()){
				Boolean check = file.delete();
				if( LOGGER.isDebugEnabled() ){
					LOGGER.debug("check delete lock File  : "  + check);
				}
			}
		}
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기 처리방식으로 uploadStemcell 메소드를 호출
	 * @title               : uploadStemcellAsync
	 * @return            : void
	***************************************************/
	@Async
	public void uploadStemcellAsync(String stemcellDir, String stemcellFileName, String userId) {
		uploadStemcell(stemcellDir, stemcellFileName, userId);
	}
}
