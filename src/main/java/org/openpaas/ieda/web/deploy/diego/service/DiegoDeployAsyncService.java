package org.openpaas.ieda.web.deploy.diego.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.web.deploy.diego.dao.DiegoDAO;
import org.openpaas.ieda.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.web.deploy.diego.dto.DiegoParamDTO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeDAO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DiegoDeployAsyncService {

	@Autowired private DiegoDAO diegoDao;
	@Autowired private SimpMessagingTemplate messagingTemplate;
	@Autowired private DirectorConfigService directorConfigService;
	@Autowired private CommonCodeDAO commonCodeDao;
	
	final private static String PARENT_CODE="1000";
	final private static String SUB_GROUP_CODE="1200";
	private final static Logger LOGGER = LoggerFactory.getLogger(DiegoDeployAsyncService.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 플랫폼 설치를 요청
	 * @title               : deploy
	 * @return            : void
	***************************************************/
	public void deploy(DiegoParamDTO.Install dto, Principal principal, String deploy) {
		
		DiegoVO vo = null;
		String deploymentFileName = null;
		CommonCodeVO commonCode = null;
		SessionInfoDTO sessionInfo = new SessionInfoDTO(principal);
		String messageEndpoint = "";
		if( deploy.toLowerCase().equals("diego") ){
			messageEndpoint = "/deploy/diego/install/logs"; 
		}else{
			messageEndpoint = "/deploy/cfDiego/install/diegoLogs";
		}
		
		
		vo = diegoDao.selectDiegoInfo(Integer.parseInt(dto.getId()));
		if ( vo != null ) deploymentFileName = vo.getDeploymentFile();
			
		if (  StringUtils.isEmpty(deploymentFileName) ) {
			throw new CommonException("notfound.diegodelete.exception",
					"배포파일 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		if ( vo != null ) {
			commonCode = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, "DEPLOY_STATUS_PROCESSING");
			vo.setDeployStatus(commonCode.getCodeName());
			vo.setUpdateUserId(sessionInfo.getUserId());
			diegoDao.updateDiegoDefaultInfo(vo);
		}
		
		String status = "";
		StringBuffer content = new StringBuffer();
		String temp = "";
		String taskId = "";
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		
		InputStreamReader isr = null;
		FileInputStream fis = null;
		BufferedReader br = null;
		try {
			
			//Instantiate an HttpClient
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			PostMethod postMethod = new PostMethod(DirectorRestHelper.getDeployURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			postMethod = (PostMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)postMethod);
			postMethod.setRequestHeader("Content-Type", "text/yaml"); //header 정의
			
			String deployFile = LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFileName;
			
			fis = new FileInputStream(deployFile);
			isr = new InputStreamReader(fis, "UTF-8");
			br = new BufferedReader(isr);
			
			while ( (temp=br.readLine()) != null) {
				content.append(temp + "\n");
			}
			
			postMethod.setRequestEntity(new StringRequestEntity(content.toString(), "text/yaml", "UTF-8"));
			
			//HTTP 요청 및 요청 결과
			int statusCode = httpClient.executeMethod(postMethod);
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
			
				Header location = postMethod.getResponseHeader("Location");
				taskId = DirectorRestHelper.getTaskId(location.getValue());
				
				status = DirectorRestHelper.trackToTask(defaultDirector, messagingTemplate, messageEndpoint, httpClient, taskId, "event", principal.getName());
				
			} else {
				DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("배포 중 오류가 발생하였습니다."));
			}

		} catch ( IOException e) {
			status = "error";
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
		} catch (RuntimeException e) {
			status = "error";
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
		} catch ( Exception e) {
			status = "error";
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
		} finally {
			try {
				if ( br != null ) br.close();
				if ( fis != null ) fis.close();
				if ( isr != null ) isr.close();
			} catch ( IOException e ) {
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error( e.getMessage() );
				}
			}
			if ( vo != null ) {
				String deployStatus = "";
				if( status.toLowerCase().equals("done") ){
					deployStatus = "DEPLOY_STATUS_DONE";
				} else if( status.toLowerCase().equals("error") ){
					deployStatus = "DEPLOY_STATUS_FAILED";
				} else if( status.toLowerCase().equals("cancelled") ){
					deployStatus = "DEPLOY_STATUS_CANCELLED";
				}
				commonCode = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, deployStatus);
				vo.setDeployStatus(commonCode.getCodeName());
				if( !StringUtils.isEmpty(taskId) ) vo.setTaskId(Integer.parseInt(taskId));
				vo.setUpdateUserId(sessionInfo.getUserId());
				diegoDao.updateDiegoDefaultInfo(vo);
			}
			
		}
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기로  deploy 메소드를 호출
	 * @title               : deployAsync
	 * @return            : void
	***************************************************/
	@Async
	public void deployAsync(DiegoParamDTO.Install dto, Principal principal, String install) {
		deploy(dto, principal, install);
	}

}
