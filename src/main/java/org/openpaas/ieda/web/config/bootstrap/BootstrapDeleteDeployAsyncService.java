package org.openpaas.ieda.web.config.bootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.openpaas.ieda.api.director.DirectorRestHelper;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BootstrapDeleteDeployAsyncService {
	@Autowired
	private IEDABootstrapAwsRepository awsRepository;

	@Autowired
	private IEDABootstrapOpenstackRepository openstackRepository;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	final private String messageEndpoint = "/bootstrap/bootstrapDelete"; 
	
	public void deleteDeploy(BootStrapDto.Delete dto) {
		
		IEDABootstrapAwsConfig aws = null;
		IEDABootstrapOpenstackConfig openstack = null;
		
		String deploymentName = "";
		String status = "";
		
		File deploymentFile = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		
		Runtime r = Runtime.getRuntime();
		
		if( "AWS".equals(dto.getIaas())) { 
			aws = awsRepository.findOne(Integer.parseInt(dto.getId()));
			if ( aws != null ) deploymentName = aws.getDeploymentName();

		} else {
			openstack = openstackRepository.findOne(Integer.parseInt(dto.getId()));
			if ( openstack != null ) deploymentName = openstack.getDeploymentName();
		}
			
		if ( deploymentName == null || deploymentName.isEmpty() ) {
			throw new IEDACommonException("illigalArgument.bootstrap.delete.exception",
					"배포정보가 존재하지 않습니다..", HttpStatus.NOT_FOUND);
		}
		
		try {
			String deployFile = LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentName;
			String command = "";
			deploymentFile = new File(deployFile);
			
			if( deploymentFile.exists() ){
				command += "bosh-init delete " + deployFile;
				Process process = r.exec(command);
				
				inputStream = process.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String info = null;
				String deleteContent = "";
				while ((info = bufferedReader.readLine()) != null){
					deleteContent += info + "\n";
					
					log.info("=== Deployment File Merge \n"+ info );
					DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "started", Arrays.asList(info));
				}
				// TODO: do check if task success or fail
			}
		} catch ( Exception e) {
			status = "error";
			DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
		} finally {
			
			
		}
		
		log.info("### Deploy Status = " + status);
		if ( aws != null ) {
			aws.setDeployStatus(status);
			aws.setDeploymentFile("");
			awsRepository.save(aws);
		}
		if ( openstack != null ) {
			openstack.setDeployStatus(status);
			openstack.setDeploymentFile("");
			openstackRepository.save(openstack);
		}

	}

	@Async
	public void deleteDeployAsync(BootStrapDto.Delete dto) {
		deleteDeploy(dto);
	}	

}
