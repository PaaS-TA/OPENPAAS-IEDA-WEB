package org.openpaas.ieda.web.information.deploy.service;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.api.deployment.DeploymentDTO;
import org.openpaas.ieda.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DeploymentService {
	
	@Autowired private DirectorConfigService directroConfigService;
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : HTTP에 요청하여 읽어온 설치 목록 결과 정보 응답
	 * @title               : listDeployment
	 * @return            : List<DeploymentInfoDTO>
	***************************************************/
	public List<DeploymentInfoDTO> listDeployment(){
		DirectorConfigVO defaultDirector = directroConfigService.getDefaultDirector();
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception", "기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		List<DeploymentInfoDTO> deploymentInfoList = null;
		
		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			GetMethod get = new GetMethod(DirectorRestHelper.getDeploymentListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);
			httpClient.executeMethod(get);
			if ( !StringUtils.isEmpty(get.getResponseBodyAsString()) ) {
				
				ObjectMapper mapper = new ObjectMapper();
				DeploymentDTO[] deploymentList = mapper.readValue(get.getResponseBodyAsString(), DeploymentDTO[].class);
				
				int idx = 0;
				for ( DeploymentDTO deployment : deploymentList ) {
					if ( deploymentInfoList == null ) 
						deploymentInfoList = new ArrayList<DeploymentInfoDTO>();
					
					DeploymentInfoDTO deploymentInfo = new DeploymentInfoDTO();
					
					deploymentInfo.setRecid(idx++);
					deploymentInfo.setName(deployment.getName());
					
					StringBuffer releaseInfo = new StringBuffer();
					for ( HashMap<String, String> release : deployment.getReleases()) {
						releaseInfo.append(release.get("name") + " (" + release.get("version") + ")<br>");
					}
					deploymentInfo.setReleaseInfo(releaseInfo.toString());
					
					StringBuffer stemcellInfo = new StringBuffer();
					for ( HashMap<String, String> stemcell : deployment.getStemcells()) {
						stemcellInfo.append(stemcell.get("name") + " (" + stemcell.get("version") + ")<br>");
					}
					deploymentInfo.setStemcellInfo(stemcellInfo.toString());
					
					deploymentInfoList.add(deploymentInfo);
				}
			}
		} catch (NoRouteToHostException e){
			throw new CommonException("noRouteToHost.deployment.exception", "네트워크 연결에 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			throw new CommonException("io.deployment.exception", " 배포 정보 조회중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return deploymentInfoList;
	}
}
