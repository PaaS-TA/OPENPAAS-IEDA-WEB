package org.openpaas.ieda.web.deploy.bootstrap.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.Arrays;

import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
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
public class BootstrapDeleteDeployAsyncService{

	@Autowired private SimpMessagingTemplate messagingTemplate;
	@Autowired private BootstrapDAO bootstrapDao;
	@Autowired private CommonCodeDAO commonCodeDao;
	
	final private static String MESSAGE_ENDPOINT = "/deploy/bootstrap/delete/logs"; 
	final private static String PARENT_CODE="1000";
	final private static String SUB_GROUP_CODE="1200";
	private final static Logger LOGGER = LoggerFactory.getLogger(BootstrapDeleteDeployAsyncService.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : bosh-init을 실행하여 해당 플랫폼 삭제 요청
	 * @title               : deleteBootstrapDeploy
	 * @return            : void
	***************************************************/
	public void deleteBootstrapDeploy(BootStrapParamDTO.Delete dto, Principal principal) {
		
		BootstrapVO vo = null;
		CommonCodeVO commonCode = null;
		String userId = principal.getName();
		String deploymentFile = "";
		String accumulatedLog = "";
		vo = bootstrapDao.selectBootstrapInfo(Integer.parseInt(dto.getId()));
		if ( vo != null ) deploymentFile = vo.getDeploymentFile();
		
		String status = "";
		String resultMessage = "";
		
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;

		try {
			String deloyStateFile = LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") +deploymentFile.split(".yml")[0] + "-state.json";
			File stateFile = new File(deloyStateFile);
			if ( !stateFile.exists() ) {
				status = "done";
				resultMessage = "BOOTSTRAP 삭제가 완료되었습니다.";
				if ( vo != null ) {
					bootstrapDao.deleteBootstrapInfo(vo.getId());
				}
				DirectorRestHelper.sendTaskOutput(userId, messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList("BootStrap을 삭제했습니다."));
				
			}else{
				String deployFile = LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFile;
				File file = new File(deployFile);
				if( file.exists() ){
					
					ProcessBuilder builder = new ProcessBuilder("bosh-init", "delete", deployFile);
					builder.redirectErrorStream(true);
					Process process = builder.start();
					
					//배포 상태
					commonCode = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, "DEPLOY_STATUS_DELETING");
					vo.setDeployStatus(commonCode.getCodeName());
					saveDeployStatus(vo, principal);
					
					//Delete log...
					inputStream = process.getInputStream();
					bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
					StringBuffer accumulatedBuffer = new StringBuffer();
					String info = null;
					while ((info = bufferedReader.readLine()) != null){
						accumulatedBuffer.append(info + "\n");
						DirectorRestHelper.sendTaskOutput(userId, messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList(info));
					}
					accumulatedLog = accumulatedBuffer.toString();
				} else {
					status = "error";
					resultMessage = "배포 파일(" + deployFile + ")이 존재하지 않습니다.";
					commonCode = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, "DEPLOY_STATUS_FAILED");
					vo.setDeployStatus(commonCode.getCodeName());
					saveDeployStatus(vo, principal);
				}
				
				if ( "error".equals(status) || accumulatedLog.contains("fail") || accumulatedLog.contains("error") || accumulatedLog.contains("No deployment")) {
					status = "error";
					commonCode = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, "DEPLOY_STATUS_FAILED");
					vo.setDeployStatus(commonCode.getCodeName());
					saveDeployStatus(vo, principal);
					if ( resultMessage.isEmpty() ) resultMessage = "BOOTSTRAP 삭제 중 오류가 발생하였습니다.";
				} else {
					status = "done";
					resultMessage = "BOOTSTRAP 삭제가 완료되었습니다.";
					bootstrapDao.deleteBootstrapInfo(vo.getId());
				}
				DirectorRestHelper.sendTaskOutput(userId, messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList(resultMessage));
			}
		}catch(RuntimeException e){
			status = "error";
			DirectorRestHelper.sendTaskOutput(userId, messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList("BootStrap 삭제 중 Exception이 발생하였습니다."));
		} catch ( Exception e) {
			status = "error";
			DirectorRestHelper.sendTaskOutput(userId, messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList("BootStrap 삭제 중 Exception이 발생하였습니다."));
		}finally {
			if(status.toLowerCase().equals("error")){
				commonCode = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, "DEPLOY_STATUS_FAILED");
				vo.setDeployStatus(commonCode.getCodeName());
				saveDeployStatus(vo, principal);
			}
			if(bufferedReader!=null){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					if( LOGGER.isErrorEnabled() ){
						LOGGER.error( e.getMessage() );
					}
				}
			}
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
	 * @description   : 설치 상태 저장
	 * @title               : saveDeployStatus
	 * @return            : BootstrapVO
	***************************************************/
	public BootstrapVO saveDeployStatus(BootstrapVO bootstrapVo, Principal principal) {
		if ( bootstrapVo == null ) return null;
		SessionInfoDTO sessionInfo = new SessionInfoDTO(principal);
		bootstrapVo.setUpdateUserId(sessionInfo.getUserId());
		bootstrapDao.updateBootStrapInfo(bootstrapVo);
		return bootstrapVo;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기로  deleteDeploy 호출
	 * @title               : deleteDeployAsync
	 * @return            : void
	***************************************************/
	@Async
	public void deleteDeployAsync(BootStrapParamDTO.Delete dto, Principal principal) {
			deleteBootstrapDeploy(dto, principal);
	}	

}
