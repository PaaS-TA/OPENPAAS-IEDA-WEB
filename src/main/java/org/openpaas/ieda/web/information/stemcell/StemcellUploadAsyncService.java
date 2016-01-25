package org.openpaas.ieda.web.information.stemcell;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.PostMethod;
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
public class StemcellUploadAsyncService {
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private IEDADirectorConfigService directorConfigService;
	
	final private String messageEndpoint = "/socket/uploadStemcell"; 

	
	public void uploadStemcell(String stemcellDir, String stemcellFileName) {

		IEDADirectorConfig defaultDirector = directorConfigService.getDefaultDirector();

		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			
			PostMethod postMethod = new PostMethod(DirectorRestHelper.getUploadStemcellURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			postMethod = (PostMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)postMethod);
			postMethod.setRequestHeader("Content-Type", "application/x-compressed");
			
  			String uploadFile = stemcellDir + System.getProperty("file.separator") + stemcellFileName;
			File fileToUpload = new File(uploadFile);
			
			postMethod.setRequestEntity(new FileUploadRequestEntity(new File(uploadFile), "application/x-compressed", messagingTemplate, messageEndpoint));

			DirectorRestHelper.sendTaskOutputWithTag(messagingTemplate, messageEndpoint, "Started", stemcellFileName, Arrays.asList("Uploading Stemcell ...", ""));
			
			int statusCode = httpClient.executeMethod(postMethod);
			
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				
				Header location = postMethod.getResponseHeader("Location");
				String taskId = DirectorRestHelper.getTaskId(location.getValue());
				
				DirectorRestHelper.trackToTaskWithTag(defaultDirector, messagingTemplate, messageEndpoint, stemcellFileName , httpClient, taskId, "event");
				
			}
			else {
				DirectorRestHelper.sendTaskOutputWithTag(messagingTemplate, messageEndpoint, "error", stemcellFileName, Arrays.asList("스템셀 업로드 중 오류가 발생하였습니다.[" + statusCode + "]"));
			}
			
		}
		catch ( Exception e) {
			e.printStackTrace();
			DirectorRestHelper.sendTaskOutputWithTag(messagingTemplate, messageEndpoint, "error", stemcellFileName, Arrays.asList("스템셀 업로드 중 Exception이 발생하였습니다."));
		}
	}

	@Async
	public void uploadStemcellAsync(String stemcellDir, String stemcellFileName) {
		uploadStemcell(stemcellDir, stemcellFileName);
	}
}
