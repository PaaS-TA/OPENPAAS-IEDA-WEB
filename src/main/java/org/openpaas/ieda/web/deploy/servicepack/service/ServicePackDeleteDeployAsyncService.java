package org.openpaas.ieda.web.deploy.servicepack.service;

import java.security.Principal;
import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.web.deploy.servicepack.dao.ServicePackDAO;
import org.openpaas.ieda.web.deploy.servicepack.dao.ServicePackVO;
import org.openpaas.ieda.web.deploy.servicepack.dto.ServicePackParamDTO;
import org.openpaas.ieda.web.information.manifest.dao.ManifestDAO;
import org.openpaas.ieda.web.information.manifest.dao.ManifestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ServicePackDeleteDeployAsyncService {
	@Autowired ServicePackDAO dao;
	@Autowired private SimpMessagingTemplate messagingTemplate;
	@Autowired private DirectorConfigService directorConfigService;
	@Autowired ManifestDAO manifestDao;
	
	final private static String MESSAGE_ENDPOINT = "/deploy/servicePack/delete/logs"; 
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 플랫폼 삭제
	 * @title               : deleteDeploy
	 * @return            : void
	***************************************************/
	private void deleteDeploy(ServicePackParamDTO dto, Principal principal) {
		ServicePackVO vo = null;
		ManifestVO manifestVo = null;
		String deploymentName = null;
		vo = dao.selectServicePackDetailInfo(dto.getId());
		if ( vo != null ) {
			manifestVo = manifestDao.selectManifestInfoByDeployName(vo.getDeploymentName());
			deploymentName = vo.getDeploymentName();
		}
		if ( StringUtils.isEmpty(deploymentName) ) {
			throw new CommonException("notfound.diegodelete.exception",
					"배포정보가 존재하지 않습니다..", HttpStatus.NOT_FOUND);
		}
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		
		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			
			DeleteMethod deleteMethod = new DeleteMethod(DirectorRestHelper.getDeleteDeploymentURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), deploymentName));
			deleteMethod = (DeleteMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)deleteMethod);
			
			int statusCode = httpClient.executeMethod(deleteMethod);
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				
				Header location = deleteMethod.getResponseHeader("Location");
				String taskId = DirectorRestHelper.getTaskId(location.getValue());
				
				DirectorRestHelper.trackToTask(defaultDirector, messagingTemplate, MESSAGE_ENDPOINT, httpClient, taskId, "event", principal.getName());
				
			}else{
				DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포삭제 중 Exception이 발생하였습니다."));
			}
			if ( vo != null ) {
				dao.deleteServicePackInfoRecord(vo.getId());
				manifestVo.setDeployStatus(null);
				manifestDao.updateManifestInfo(manifestVo);
			}
		} catch(RuntimeException e){
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포삭제 중 Exception이 발생하였습니다."));
		} catch ( Exception e) {
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포삭제 중 Exception이 발생하였습니다."));
		}
	}
	

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기로  deleteDeploy 메소드 호출
	 * @title               : deleteDeployAsync
	 * @return            : void
	***************************************************/
	@Async
	public void deleteDeployAsync(ServicePackParamDTO dto, Principal principal) {
		deleteDeploy(dto, principal);
	}
}
