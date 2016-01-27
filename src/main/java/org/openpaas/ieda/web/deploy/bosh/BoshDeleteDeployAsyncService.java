package org.openpaas.ieda.web.deploy.bosh;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.openpaas.ieda.api.director.DirectorRestHelper;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class BoshDeleteDeployAsyncService {
	
	@Autowired
	private IEDABoshAwsRepository awsRepository;

	@Autowired
	private IEDABoshOpenstackRepository openstackRepository;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private IEDADirectorConfigService directorConfigService;
	
	final private String messageEndpoint = "/bosh/boshDelete"; 
	
	public void deleteDeploy(BoshParam.Delete dto) {
		
		IEDABoshAwsConfig aws = null;
		IEDABoshOpenstackConfig openstack = null;
		String deploymentName = null;
		
		if( "AWS".equals(dto.getIaas())) { 
			aws = awsRepository.findOne(Integer.parseInt(dto.getId()));
			if ( aws != null ) deploymentName = aws.getDeploymentName();

		} else {
			openstack = openstackRepository.findOne(Integer.parseInt(dto.getId()));
			if ( openstack != null ) deploymentName = openstack.getDeploymentName();
		}
			
		if ( StringUtils.isEmpty(deploymentName) ) {
			throw new IEDACommonException("illigalArgument.boshdelete.exception",
					"배포정보가 존재하지 않습니다..", HttpStatus.NOT_FOUND);
		}
		
		IEDADirectorConfig defaultDirector = directorConfigService.getDefaultDirector();
		
		BufferedReader br = null;
		InputStreamReader isr = null;
		FileInputStream fis = null;

		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			
			DeleteMethod deleteMethod = new DeleteMethod(DirectorRestHelper.getDeleteDeploymentURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), deploymentName));
			deleteMethod = (DeleteMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)deleteMethod);
		
			int statusCode = httpClient.executeMethod(deleteMethod);
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				
				Header location = deleteMethod.getResponseHeader("Location");
				String taskId = DirectorRestHelper.getTaskId(location.getValue());
				
				DirectorRestHelper.trackToTask(defaultDirector, messagingTemplate, messageEndpoint, httpClient, taskId, "event");
				
				if ( aws != null ) awsRepository.delete(aws);
				if ( openstack != null ) openstackRepository.delete(openstack);
				
			} else {
				DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "error", Arrays.asList("배포삭제 중 오류가 발생하였습니다.[" + statusCode + "]"));
			}

		} catch ( Exception e) {
			DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "error", Arrays.asList("배포삭제 중 Exception이 발생하였습니다."));
		} finally {
			try {
				if ( fis != null ) fis.close();
				if ( isr != null ) isr.close();
				if ( br != null ) br.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}

	}

	@Async
	public void deleteDeployAsync(BoshParam.Delete dto) {
		deleteDeploy(dto);
	}	
}
