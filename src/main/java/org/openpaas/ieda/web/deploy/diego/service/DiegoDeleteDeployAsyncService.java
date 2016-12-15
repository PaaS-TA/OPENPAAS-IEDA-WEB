package org.openpaas.ieda.web.deploy.diego.service;

import java.security.Principal;
import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.web.deploy.common.dao.key.KeyDAO;
import org.openpaas.ieda.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.web.deploy.diego.dao.DiegoDAO;
import org.openpaas.ieda.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.web.deploy.diego.dto.DiegoParamDTO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeDAO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class DiegoDeleteDeployAsyncService {
	
	@Autowired private DiegoDAO diegoDao;
	@Autowired private SimpMessagingTemplate messagingTemplate;
	@Autowired private DirectorConfigService directorConfigService;
	@Autowired private NetworkDAO networkDao;
	@Autowired private KeyDAO keyDao;
	@Autowired private ResourceDAO resourceDao;
	@Autowired private CommonCodeDAO commonCodeDao;
	
	final private static String PARENT_CODE="1000"; //배포 코드
	final private static String SUB_GROUP_CODE="1100"; //배포 유형 코드
	final private static String STATUS_SUB_GROUP_CODE="1200"; //배포 상태 코드
	final private static String CODE_NAME="DEPLOY_TYPE_DIEGO"; //배포 할 플랫폼명
		
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 플랫폼 삭제 요청
	 * @title               : deleteDeploy
	 * @return            : void
	***************************************************/
	public void deleteDeploy(DiegoParamDTO.Delete dto, String platform, Principal principal) {
		String messageEndpoint = "/deploy/"+platform+"/delete/logs"; 
		DiegoVO vo = null;
		String deploymentName = null;
		CommonCodeVO commonCode = null;
		SessionInfoDTO sessionInfo = new SessionInfoDTO(principal);
		
		vo = diegoDao.selectDiegoInfo(Integer.parseInt(dto.getId()));
		if ( vo != null ) deploymentName = vo.getDeploymentName();
			
		if ( StringUtils.isEmpty(deploymentName) ) {
			throw new CommonException("notfound.diegodelete.exception",
					"배포정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		if ( vo != null ) {
			commonCode = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, STATUS_SUB_GROUP_CODE, "DEPLOY_STATUS_DELETING");
			if( commonCode != null ){
				vo.setDeployStatus(commonCode.getCodeValue());
				vo.setUpdateUserId(sessionInfo.getUserId());
				saveDeployStatus(vo);
			}
		}
		
		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			
			DeleteMethod deleteMethod = new DeleteMethod(DirectorRestHelper.getDeleteDeploymentURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), deploymentName));
			deleteMethod = (DeleteMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)deleteMethod);
		
			int statusCode = httpClient.executeMethod(deleteMethod);
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				
				Header location = deleteMethod.getResponseHeader("Location");
				String taskId = DirectorRestHelper.getTaskId(location.getValue());
				
				DirectorRestHelper.trackToTask(defaultDirector, messagingTemplate, messageEndpoint, httpClient, taskId, "event", principal.getName());
				deleteDiegoInfo(vo);
				
			} else {
				deleteDiegoInfo(vo);
			}
		}catch(RuntimeException e){
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("배포삭제 중 Exception이 발생하였습니다."));
		}catch ( Exception e) {
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("배포삭제 중 Exception이 발생하였습니다."));
		}

	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 정보 삭제
	 * @title               : deleteDiegoInfo
	 * @return            : void
	***************************************************/
	@Transactional
	public void deleteDiegoInfo( DiegoVO vo ){
		if ( vo != null ) {
			diegoDao.deleteDiegoInfoRecord(vo.getId());
			CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
			networkDao.deleteNetworkInfoRecord( vo.getId(), codeVo.getCodeName() );
			resourceDao.deleteResourceInfo( vo.getId(), codeVo.getCodeName() );	
			keyDao.deleteKeyInfo(vo.getId(), codeVo.getCodeName());
		}
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 배포 상태 저장
	 * @title               : saveDeployStatus
	 * @return            : DiegoVO
	***************************************************/
	public DiegoVO saveDeployStatus(DiegoVO diegoVo) {
		if ( diegoVo == null ) return null;
		diegoDao.updateDiegoDefaultInfo(diegoVo);
		return diegoVo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기로  deleteDeploy 메소드 호출
	 * @title               : deleteDeployAsync
	 * @return            : void
	***************************************************/
	@Async
	public void deleteDeployAsync(DiegoParamDTO.Delete dto, String platform, Principal principal) {
		deleteDeploy(dto, platform, principal);
	}	

}
