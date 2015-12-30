package org.openpaas.ieda.web.deploy.cf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.openpaas.ieda.api.director.DirectorRestHelper;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CfDeleteDeployAsyncService {
	
	@Autowired
	private IEDACfAwsRepository awsRepository;

	@Autowired
	private IEDACfOpenstackRepository openstackRepository;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private IEDADirectorConfigService directorConfigService;
	
	final private String messageEndpoint = "/cf/cfDelete"; 
	
	public void deleteDeploy(CfParam.Delete dto) {
		
		IEDACfAwsConfig aws = null;
		IEDACfOpenstackConfig openstack = null;
		String deploymentName = null;
		
		if( "AWS".equals(dto.getIaas())) { 
			aws = awsRepository.findOne(Integer.parseInt(dto.getId()));
			if ( aws != null ) deploymentName = aws.getDeploymentName();

		} else {
			openstack = openstackRepository.findOne(Integer.parseInt(dto.getId()));
			if ( openstack != null ) deploymentName = openstack.getDeploymentName();
		}
			
		if ( deploymentName == null || deploymentName.isEmpty() ) {
			throw new IEDACommonException("illigalArgument.cfdelete.exception",
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
				
				DirectorRestHelper.trackToTask(defaultDirector, messagingTemplate, messageEndpoint, httpClient, taskId);
				
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
	public void deleteDeployAsync(CfParam.Delete dto) {
		deleteDeploy(dto);
	}	
}
