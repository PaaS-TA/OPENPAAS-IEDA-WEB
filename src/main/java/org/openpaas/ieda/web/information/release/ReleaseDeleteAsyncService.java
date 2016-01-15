package org.openpaas.ieda.web.information.release;

import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.openpaas.ieda.api.director.DirectorRestHelper;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ReleaseDeleteAsyncService {
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private IEDADirectorConfigService directorConfigService;
	
	final private String messageEndpoint = "/socket/deleteRelease"; 

	public void deleteRelease(String releaseName, String releaseVersion) {
		
		IEDADirectorConfig defaultDirector = directorConfigService.getDefaultDirector();

		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());

			DeleteMethod deleteMethod = new DeleteMethod(DirectorRestHelper.getDeleteReleaseURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), releaseName, releaseVersion));
			deleteMethod = (DeleteMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)deleteMethod);
			
			int statusCode = httpClient.executeMethod(deleteMethod);
			
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				
				Header location = deleteMethod.getResponseHeader("Location");
				String taskId = DirectorRestHelper.getTaskId(location.getValue());
				
				DirectorRestHelper.trackToTask(defaultDirector, messagingTemplate, messageEndpoint, httpClient, taskId, "event");
				
			} else {
				DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "error", Arrays.asList("릴리즈 삭제 중 오류가 발생하였습니다.[" + statusCode + "]"));
			}
			
		} catch ( Exception e) {
			DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "error", Arrays.asList("릴리즈 삭제 중 Exception이 발생하였습니다."));
		}
	}

	@Async
	public void deleteReleaseAsync(String releaseName, String releaseVersion) {
		deleteRelease(releaseName, releaseVersion);
	}
}
