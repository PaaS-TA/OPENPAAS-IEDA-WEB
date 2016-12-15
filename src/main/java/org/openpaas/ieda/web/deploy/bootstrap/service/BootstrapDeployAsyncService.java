package org.openpaas.ieda.web.deploy.bootstrap.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.Arrays;

import org.openpaas.ieda.api.director.dto.DirectorInfoDTO;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.web.deploy.bootstrap.dao.BootstrapDAO;
import org.openpaas.ieda.web.deploy.bootstrap.dao.BootstrapVO;
import org.openpaas.ieda.web.deploy.bootstrap.dto.BootStrapParamDTO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeDAO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BootstrapDeployAsyncService {

	@Autowired private SimpMessagingTemplate messagingTemplate;
	@Autowired private DirectorConfigService directorConfigService;
	@Autowired private BootstrapDAO bootstrapDao;
	@Autowired private CommonCodeDAO commonCodeDao;
	
	final private static String MESSAGE_ENDPOINT = "/deploy/bootstrap/install/logs"; 
	final private static String PARENT_CODE="1000";
	final private static String SUB_GROUP_CODE="1200";
	private final static Logger LOGGER = LoggerFactory.getLogger(BootstrapDeployAsyncService.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : bosh-init을 실행하여 해당 Manifest 파일과 함께 플랫폼 설치 요청
	 * @title               : deployBootstrap
	 * @return            : void
	***************************************************/
	public void deployBootstrap(BootStrapParamDTO.Install dto, Principal principal) {
		
		String deploymentFileName = null;
		BootstrapVO bootstrapInfo = null;
		CommonCodeVO commonCode = null;
		String userId = principal.getName();
		String publicIp = "";
		String status = "started";
		String accumulatedLog = "";
		String resultMessage = "";
		File deploymentFile = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		
		try {
			bootstrapInfo = bootstrapDao.selectBootstrapInfo(Integer.parseInt(dto.getId()));
			
			//User Info
			SessionInfoDTO sessionInfo = new SessionInfoDTO(principal);
			if( sessionInfo.getUserId() != null ){
				bootstrapInfo.setUpdateUserId(sessionInfo.getUserId());
			}
			
			if( bootstrapInfo != null  ){
				publicIp = bootstrapInfo.getPublicStaticIp();
				deploymentFileName = bootstrapInfo.getDeploymentFile();
			}
			
			String deployFile = LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFileName;
			deploymentFile = new File(deployFile);
			
			if( deploymentFile.exists() ) {
				//1. 배포상태 설정
				commonCode = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE,"DEPLOY_STATUS_PROCESSING");
				if( commonCode != null ){
					bootstrapInfo.setDeployStatus(commonCode.getCodeName());
					saveDeployStatus(bootstrapInfo);
				}

				//2. bosh-init 실행 
				ProcessBuilder builder = new ProcessBuilder("bosh-init", "deploy", deployFile);
				builder.redirectErrorStream(true);
				Process process = builder.start();
				//실행 출력하는 로그를 읽어온다.
				inputStream = process.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
				String info = null;
				StringBuffer accumulatedBuffer = new StringBuffer();
				while ((info = bufferedReader.readLine()) != null){
					accumulatedBuffer.append(info + "\n");
					DirectorRestHelper.sendTaskOutput(userId, messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList(info));
				}
				accumulatedLog = accumulatedBuffer.toString();
			} else {
				status = "error";
				resultMessage = "설치할 배포 파일(" + deployFile + ")이 존재하지 않습니다.";
			}
			
			if ( bootstrapInfo != null ) bootstrapInfo.setDeployLog(accumulatedLog);
			
			if ( status.equals("error") ) {
				commonCode = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE,"DEPLOY_STATUS_FAILED");
				if( commonCode != null ){
					bootstrapInfo.setDeployStatus(commonCode.getCodeName());
					saveDeployStatus(bootstrapInfo);
				}
				DirectorRestHelper.sendTaskOutput(userId, messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList(resultMessage));
			} else {
				if ( accumulatedLog.contains("Failed deploying")) {
					status = "error";
					commonCode = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE,"DEPLOY_STATUS_FAILED");
					if( commonCode != null ){
						bootstrapInfo.setDeployStatus(commonCode.getCodeName());
						saveDeployStatus(bootstrapInfo);
					}
					DirectorRestHelper.sendTaskOutput(userId, messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("", "BOOTSTRAP 설치 중 오류가 발생하였습니다."));
				}	else {
					// 타겟 테스트
					DirectorRestHelper.sendTaskOutput(userId, messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList("","BOOTSTRAP 디렉터 정보 : https://" + publicIp + ":25555"));
					DirectorRestHelper.sendTaskOutput(userId, messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList("BOOTSTRAP 디렉터 타겟 접속 테스트..."));
					DirectorInfoDTO directorInfo = directorConfigService.getDirectorInfo(publicIp, 25555, "admin", "admin");
					
					if ( directorInfo == null ) {
						status = "error";
						commonCode = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, "DEPLOY_STATUS_FAILED");
						if( commonCode != null ){
							bootstrapInfo.setDeployStatus(commonCode.getCodeName());
							saveDeployStatus(bootstrapInfo);
						}
						DirectorRestHelper.sendTaskOutput(userId, messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("BOOTSTRAP 디렉터 타겟 접속 테스트 실패"));
					} else {
						DirectorRestHelper.sendTaskOutput(userId, messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList("BOOTSTRAP 디렉터 타겟 접속 테스트 성공"));
						status = "done";
						commonCode = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, "DEPLOY_STATUS_DONE");
						if( commonCode != null ){
							bootstrapInfo.setDeployStatus(commonCode.getCodeName());
							saveDeployStatus(bootstrapInfo);
						}
						DirectorRestHelper.sendTaskOutput(userId, messagingTemplate, MESSAGE_ENDPOINT, "done", Arrays.asList("", "BOOTSTRAP 설치가 완료되었습니다."));
					}
					
				}
			}
		}catch(RuntimeException e){
			status = "error";
			DirectorRestHelper.sendTaskOutput(userId, messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
			if ( bootstrapInfo != null ) bootstrapInfo.setDeployLog(accumulatedLog);
			commonCode = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, "DEPLOY_STATUS_FAILED");
			if( commonCode != null ){
				bootstrapInfo.setDeployStatus(commonCode.getCodeName());
				saveDeployStatus(bootstrapInfo);
			}
		}catch ( Exception e) {	
			status = "error";
			DirectorRestHelper.sendTaskOutput(userId, messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
			if ( bootstrapInfo != null ) bootstrapInfo.setDeployLog(accumulatedLog);
			commonCode = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, "DEPLOY_STATUS_FAILED");
			if( commonCode != null ){
				bootstrapInfo.setDeployStatus(commonCode.getCodeName());
				saveDeployStatus(bootstrapInfo);
			}
		}finally {
			try {
				if(bufferedReader!=null) bufferedReader.close();
			} catch (IOException e) {
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error( e.getMessage() );
				}
			}
			//동시 설치 방지 lock 파일 삭제
			File lockFile = new File(LocalDirectoryConfiguration.getLockDir()+System.getProperty("file.separator")+"bootstrap.lock");	
			if(lockFile.exists()){
				Boolean check = lockFile.delete();
				if( LOGGER.isDebugEnabled() ){
					LOGGER.debug("check delete lock File  : "  + check);
				}
			}
		}

	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치 상태를 설정하여 저장
	 * @title               : saveDeployStatus
	 * @return            : BootstrapVO
	***************************************************/
	public BootstrapVO saveDeployStatus(BootstrapVO bootstrapVo) {
		if ( bootstrapVo == null ) return null;
		bootstrapDao.updateBootStrapInfo(bootstrapVo);
		
		return bootstrapVo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기로 deployAsync 메소드 호출
	 * @title               : deployAsync
	 * @return            : void
	***************************************************/
	@Async
	public void deployAsync(BootStrapParamDTO.Install dto, Principal principal) {
			deployBootstrap(dto, principal);
	}
}
