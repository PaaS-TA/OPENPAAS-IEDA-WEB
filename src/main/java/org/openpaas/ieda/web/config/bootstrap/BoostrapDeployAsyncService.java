package org.openpaas.ieda.web.config.bootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BoostrapDeployAsyncService {


	@Autowired
	private IEDABootstrapAwsRepository awsRepository;

	@Autowired
	private IEDABootstrapOpenstackRepository openstackRepository;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private IEDADirectorConfigService directorConfigService;
	
	final private String messageEndpoint = "/bootstrap/bootstrapInstall"; 
	
	public void deploy(BootStrapDto.Install dto) {
		
		IEDABootstrapAwsConfig aws = null;
		IEDABootstrapOpenstackConfig openstack = null;
		String deploymentFileName = null;
		
		if( "AWS".equals(dto.getIaas())) { 
			aws = awsRepository.findOne(Integer.parseInt(dto.getId()));
			if ( aws != null ) deploymentFileName = aws.getDeploymentFile();

		} else {
			openstack = openstackRepository.findOne(Integer.parseInt(dto.getId()));
			if ( openstack != null ) deploymentFileName = openstack.getDeploymentFile();
		}
			
		if ( deploymentFileName == null || deploymentFileName.isEmpty() ) {
			throw new IEDACommonException("illigalArgument.boshdelete.exception",
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
		Runtime r = Runtime.getRuntime();
		File deploymentFile = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		
		try {

	
			String deployFile = LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFileName;
			String command = "";
			deploymentFile = new File(deployFile);
			
			if( deploymentFile.exists() ){
				command += "bosh-init deploy " + deployFile;
				Process process = r.exec(command);
				
				inputStream = process.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String info = null;
				String deloymentContent = "";
				while ((info = bufferedReader.readLine()) != null){
					deloymentContent += info + "\n";
					log.info("=== Deployment File Merge \n"+ info );
					messagingTemplate.convertAndSend("/bootstrap/bootstrapInstall", info);
				}
			}
		} catch ( Exception e) {
			status = "error";
			DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
		} finally {

		}
		
		log.info("### Deploy Status = " + status);
		
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
	public void deployAsync(BootStrapDto.Install dto) {
		deploy(dto);
	}	
}
