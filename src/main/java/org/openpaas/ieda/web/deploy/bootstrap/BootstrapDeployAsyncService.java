package org.openpaas.ieda.web.deploy.bootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.openpaas.ieda.api.Info;
import org.openpaas.ieda.api.director.DirectorRestHelper;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BootstrapDeployAsyncService {

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
		String publicIp = "";
		if( "AWS".equals(dto.getIaas())) { 
			aws = awsRepository.findOne(Integer.parseInt(dto.getId()));
			if ( aws != null ) {
				publicIp = aws.getPublicStaticIp();
				deploymentFileName = aws.getDeploymentFile();
			}

		} else {
			openstack = openstackRepository.findOne(Integer.parseInt(dto.getId()));
			if ( openstack != null ) {
				publicIp = openstack.getPublicStaticIp();
				deploymentFileName = openstack.getDeploymentFile();
			}
		}
			
		if ( StringUtils.isEmpty(deploymentFileName)) {
			throw new IEDACommonException("illigalArgument.bootstrap.delete.exception",
					"배포파일 정보가 존재하지 않습니다..", HttpStatus.NOT_FOUND);
		}
		
		String status = "started";
		String accumulatedLog = "";
		String resultMessage = "";
		File deploymentFile = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		
		try {
			
			String deployFile = LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFileName;
			deploymentFile = new File(deployFile);
			
			if( deploymentFile.exists() ) {
				
				status = "deploying";
				saveAWSDeployStatus(aws, status);
				saveOpenstackDeployStatus(openstack, status);

				ProcessBuilder builder = new ProcessBuilder("bosh-init", "deploy", deployFile);
				builder.redirectErrorStream(true);
				Process process = builder.start();
				
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
				resultMessage = "설치할 배포 파일(" + deployFile + ")이 존재하지 않습니다.";
			}
			
			if ( aws != null ) aws.setDeployLog(accumulatedLog);
			if ( openstack != null ) openstack.setDeployLog(accumulatedLog);
			
			if ( status.equals("error") ) {
				saveAWSDeployStatus(aws, status);
				saveOpenstackDeployStatus(openstack, status);
				DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "error", Arrays.asList(resultMessage));
			} else {
				
				if ( accumulatedLog.contains("Failed deploying")) {
					status = "error";
					saveAWSDeployStatus(aws, status);
					saveOpenstackDeployStatus(openstack, status);
					DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "error", Arrays.asList("", "BOOTSTRAP 설치 중 오류가 발생하였습니다."));
				}
				else {
					// 타겟 테스트
					DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "started", Arrays.asList("","BOOTSTRAP 디렉터 정보 : https://" + publicIp + ":25555"));
					
					DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "started", Arrays.asList("BOOTSTRAP 디렉터 타겟 접속 테스트..."));
					
					Info info = directorConfigService.getDirectorInfo(publicIp, 25555, "admin", "admin");
					if ( info == null ) {
						status = "error";
						saveAWSDeployStatus(aws, status);
						saveOpenstackDeployStatus(openstack, status);
						DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "error", Arrays.asList("BOOTSTRAP 디렉터 타겟 접속 테스트 실패"));
					} else {
						DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "started", Arrays.asList("BOOTSTRAP 디렉터 타겟 접속 테스트 성공"));
						status = "done";
						saveAWSDeployStatus(aws, status);
						saveOpenstackDeployStatus(openstack, status);
						DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "done", Arrays.asList("", "BOOTSTRAP 설치가 완료되었습니다."));
					}
					
				}
			}
			
		} catch ( Exception e) {
			e.printStackTrace();
			status = "error";
			DirectorRestHelper.sendTaskOutput(messagingTemplate, messageEndpoint, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
			if ( aws != null ) aws.setDeployLog(accumulatedLog);
			if ( openstack != null ) openstack.setDeployLog(accumulatedLog);
			saveAWSDeployStatus(aws, status);
			saveOpenstackDeployStatus(openstack, status);
		}

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
	public void deployAsync(BootStrapDto.Install dto) {
		deploy(dto);
	}	
}
