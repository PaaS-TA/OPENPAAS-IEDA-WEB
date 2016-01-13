package org.openpaas.ieda.web.deploy.diego;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.openpaas.ieda.api.director.DirectorRestHelper;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigService;
import org.openpaas.ieda.web.deploy.diego.DiegoParam;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoAwsConfig;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoAwsRepository;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoOpenstackConfig;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoOpenstackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DiegoDeployAsyncService {

	@Autowired
	private IEDADiegoAwsRepository awsRepository;

	@Autowired
	private IEDADiegoOpenstackRepository openstackRepository;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private IEDADirectorConfigService directorConfigService;
	
	final private String messageEndpoint = "/diego/diegoInstall"; 
	
	public void deploy(DiegoParam.Install dto) {
		
		IEDADiegoAwsConfig aws = null;
		IEDADiegoOpenstackConfig openstack = null;
		String deploymentFileName = null;
		
		if( "AWS".equals(dto.getIaas().toUpperCase())) { 
			aws = awsRepository.findOne(Integer.parseInt(dto.getId()));
			if ( aws != null ) deploymentFileName = aws.getDeploymentFile();

		} 
		else {
			openstack = openstackRepository.findOne(Integer.parseInt(dto.getId()));
			if ( openstack != null ) deploymentFileName = openstack.getDeploymentFile();
		}
			
		if (  StringUtils.isEmpty(deploymentFileName) ) {
			throw new IEDACommonException("illigalArgument.diegodelete.exception",
					"배포파일 정보가 존재하지 않습니다..", HttpStatus.NOT_FOUND);
		}
		
		if ( aws != null ) {
			aws.setDeployStatus("deploying");
			awsRepository.save(aws);
		}
		
		if ( openstack != null ) {
			openstack.setDeployStatus("deploying");
			openstackRepository.save(openstack);
		}
		
		String status = "";
		IEDADirectorConfig defaultDirector = directorConfigService.getDefaultDirector();
		
		BufferedReader br = null;
		InputStreamReader isr = null;
		FileInputStream fis = null;

		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			
			PostMethod postMethod = new PostMethod(DirectorRestHelper.getDeployURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			postMethod = (PostMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)postMethod);
			postMethod.setRequestHeader("Content-Type", "text/yaml");
			
			String deployFile = LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFileName;
			
			String content = "", temp = "";
			
			fis = new FileInputStream(deployFile);
			isr = new InputStreamReader(fis, "UTF-8");
			br = new BufferedReader(isr);
			
			while ( (temp=br.readLine()) != null) {
				content += temp + "\n";
			}
			
			postMethod.setRequestEntity(new StringRequestEntity(content, "text/yaml", "UTF-8"));
			
		
			int statusCode = httpClient.executeMethod(postMethod);
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				
				Header location = postMethod.getResponseHeader("Location");
				String taskId = DirectorRestHelper.getTaskId(location.getValue());
				
				status = DirectorRestHelper.trackToTask(defaultDirector, messagingTemplate, messageEndpoint, httpClient, taskId, "event");
				
			} else {
				DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "error", Arrays.asList("배포 중 오류가 발생하였습니다.[" + statusCode + "]"));
			}

		} catch ( Exception e) {
			status = "error";
			DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
		} finally {
			try {
				if ( fis != null ) fis.close();
				if ( isr != null ) isr.close();
				if ( br != null ) br.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		
		if ( aws != null ) {
			aws.setDeployStatus(status);
			awsRepository.save(aws);
		}
		if ( openstack != null ) {
			openstack.setDeployStatus(status);
			openstackRepository.save(openstack);
		}

	}

	@Async
	public void deployAsync(DiegoParam.Install dto) {
		deploy(dto);
	}

}
