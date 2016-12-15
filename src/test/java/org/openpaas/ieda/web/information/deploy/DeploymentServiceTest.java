package org.openpaas.ieda.web.information.deploy;

import java.io.IOException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@TestPropertySource(locations="classpath:application_test.properties")
public class DeploymentServiceTest {
	
	final private static Logger LOGGER = LoggerFactory.getLogger(DeploymentServiceTest.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : HTTP에 요청하여 읽어온 설치 목록 결과 정보 응답
	 * @title               : listDeployment
	 * @return            : List<DeploymentInfoDTO>
	***************************************************/
	public List<DeploymentInfoDTO> listDeployment(){
		DirectorConfigVO defaultDirector = settingDefaultDirector();
		
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception",
					"기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		List<DeploymentInfoDTO> deploymentInfoList = null;
		try {
			
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			GetMethod get = new GetMethod(DirectorRestHelper.getDeploymentListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);
			
			if(LOGGER.isDebugEnabled()){
				LOGGER.info("httpClient:" + httpClient);
			}
				
				String responseBody = setResponseBody();
				ObjectMapper mapper = new ObjectMapper();
				
				DeploymentDTO[] deploymentList = mapper.readValue(responseBody, DeploymentDTO[].class);
				
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
		} catch (IOException e) {
			throw new CommonException("notfound.deployment.exception", " 배포 정보 조회중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
		
		return deploymentInfoList;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 설치 관리자 설정
	 * @title               : settingDefaultDirector
	 * @return            : DirectorConfigVO
	***************************************************/
	public DirectorConfigVO settingDefaultDirector(){
		DirectorConfigVO vo = new DirectorConfigVO();
		vo.setIedaDirectorConfigSeq(1);
		vo.setDefaultYn("Y");
		vo.setDirectorCpi("openstack-cpi");
		vo.setDirectorName("bosh");
		vo.setDirectorPort(25555);
		vo.setDirectorUrl("10.10.10.10");
		vo.setDirectorUuid("3b623d50-10c1-450a-8aad-test");
		vo.setDirectorVersion("1.3252.0");
		
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포 정보 설정
	 * @title               : setResponseBody
	 * @return            : String
	***************************************************/
	public String setResponseBody(){
		String info = "[{\"name\":\"cf-vsphere\",";
		info += "\"releases\":[{\"name\":\"cf\",\"version\":\"240\"}],";
		info += "\"stemcells\":[{\"name\":\"bosh-vsphere-esxi-ubuntu-trusty-go_agent\",\"version\":\"3232.4\"}],";
		info += "\"cloud_config\":\"none\"}]";
		return info;
	}
}
