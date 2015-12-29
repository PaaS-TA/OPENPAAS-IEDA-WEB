package org.openpaas.ieda.web.information.deploy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.api.Deployment;
import org.openpaas.ieda.api.DeploymentInfo;
import org.openpaas.ieda.api.director.DirectorRestHelper;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DeploymentService {
	
	@Autowired
	private IEDADirectorConfigService directroConfigService;
	
	public List<DeploymentInfo> listDeployment(){
		IEDADirectorConfig defaultDirector = directroConfigService.getDefaultDirector();
		
		if ( defaultDirector == null ) {
			throw new IEDACommonException("notfound.director.exception",
					"기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		List<DeploymentInfo> deploymentInfoList = null;
		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			GetMethod get = new GetMethod(DirectorRestHelper.getDeploymentListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);
			
			httpClient.executeMethod(get);

			if ( get.getResponseBodyAsString() != null && !get.getResponseBodyAsString().isEmpty()) {
				ObjectMapper mapper = new ObjectMapper();
				
				Deployment[] deploymentList = mapper.readValue(get.getResponseBodyAsString(), Deployment[].class);
				
				int idx = 0;
				for ( Deployment deployment : deploymentList ) {
					if ( deploymentInfoList == null ) 
						deploymentInfoList = new ArrayList<DeploymentInfo>();
					
					DeploymentInfo deploymentInfo = new DeploymentInfo();
					
					deploymentInfo.setRecid(idx++);
					deploymentInfo.setName(deployment.getName());
					
					String releaseInfo = "";
					for ( HashMap<String, String> release : deployment.getReleases()) {
						releaseInfo = releaseInfo + release.get("name") + " (" + release.get("version") + ")<br>";
					}
					deploymentInfo.setReleaseInfo(releaseInfo);
					
					String stemcellInfo = "";
					for ( HashMap<String, String> stemcell : deployment.getStemcells()) {
						stemcellInfo = stemcellInfo + stemcell.get("name") + " (" + stemcell.get("version") + ")<br>";
					}
					deploymentInfo.setStemcellInfo(stemcellInfo);
					
					deploymentInfoList.add(deploymentInfo);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new IEDACommonException("notfound.deployment.exception", " 배포정보 조회중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
		
		return deploymentInfoList;
	}
}
