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
		
		String deploymentFile = "";

		File file = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		
		Runtime r = Runtime.getRuntime();
		
		if( "AWS".equals(dto.getIaas())) { 
			aws = awsRepository.findOne(Integer.parseInt(dto.getId()));
			if ( aws != null ) deploymentFile = aws.getDeploymentFile();

		} else {
			openstack = openstackRepository.findOne(Integer.parseInt(dto.getId()));
			if ( openstack != null ) deploymentFile = openstack.getDeploymentFile();
		}
			
		if ( deploymentFile == null || deploymentFile.isEmpty() ) {
			throw new IEDACommonException("illigalArgument.bootstrap.delete.exception",
					"배포정보가 존재하지 않습니다..", HttpStatus.NOT_FOUND);
		}

		String status = "";
		String accumulatedLog = "";
		String resultMessage = "";
		
		try {
			String deployedFilePath = LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFile;
			String command = "";
			file = new File(deployedFilePath);
			
			log.info("# deployedFilePath :" +  deployedFilePath);
			
			if( file.exists() ){
				command += "bosh-init delete " + deployedFilePath;
				Process process = r.exec(command);
				
				status = "Deleting";
				saveAWSDeployStatus(aws, status);
				saveOpenstackDeployStatus(openstack, status);
				
				inputStream = process.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String info = null;
				while ((info = bufferedReader.readLine()) != null){
					accumulatedLog += info + "\n";
					DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "started", Arrays.asList(info));
				}
			}
			else {
				status = "error";
				resultMessage = "배포 파일(" + deployedFilePath + ")이 존재하지 않습니다.";
			}
			
			if ( status.equals("error") || accumulatedLog.contains("fail") || accumulatedLog.contains("error") || accumulatedLog.contains("No deployment")) {
				status = "error";
				if ( resultMessage.isEmpty() ) resultMessage = "BOOTSTRAP 삭제 중 오류가 발생하였습니다.";
			} else {
				status = "done";
				resultMessage = "BOOTSTRAP 삭제가 완료되었습니다.";
			}
			
			DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, status, Arrays.asList(resultMessage));
			
		} catch ( Exception e) {
			status = "error";
			DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, status, Arrays.asList("배포 중 Exception이 발생하였습니다."));
		}
		
		// 오류가 발생한 경우라도 레코드는 삭제하자.
		if ( aws != null ) awsRepository.delete(aws);
		if ( openstack != null ) openstackRepository.delete(openstack);
	}
	
	public IEDABootstrapAwsConfig saveAWSDeployStatus(IEDABootstrapAwsConfig aws, String status) {
		if ( aws == null ) return null;
		aws.setDeployStatus(status);
		return awsRepository.save(aws);
	}
	
	public IEDABootstrapOpenstackConfig saveOpenstackDeployStatus(IEDABootstrapOpenstackConfig openstack, String status) {
		if ( openstack == null ) return null;
		openstack.setDeployStatus(status);
		return openstackRepository.save(openstack);
	}

	@Async
	public void deleteDeployAsync(BootStrapDto.Delete dto) {
		deleteDeploy(dto);
	}	

}
