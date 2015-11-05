package org.openpaas.ieda.web.information.deploy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.openpaas.ieda.api.Deployment;
import org.openpaas.ieda.api.DirectorClient;
import org.openpaas.ieda.api.DirectorClientBuilder;
import org.openpaas.ieda.api.Stemcell;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigRepository;
import org.openpaas.ieda.web.deploy.release.ReleaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDADeploymentsService {


	@Autowired
	private IEDADirectorConfigRepository directorConfigRepository;
	
	public List<DeploymentsConfig> listDeployment(){
		IEDADirectorConfig defaultDirector = directorConfigRepository.findOneByDefaultYn("Y");
		
		Deployment[] deployments = null;
		List<DeploymentsConfig> deploymentConfigs = new ArrayList<DeploymentsConfig>();
		try {
			
			DirectorClient client = new DirectorClientBuilder()
					.withHost("52.23.2.85"/*defaultDirector.getDirectorUrl()*/, defaultDirector.getDirectorPort())
					.withCredentials(defaultDirector.getUserId(), defaultDirector.getUserPassword()).build();
			
			URI deploymentsUri = UriComponentsBuilder.fromUri(client.getRoot())
					.pathSegment("deployments").build().toUri();
			
			deployments = client.getRestTemplate().getForObject(deploymentsUri, Deployment[].class);
			
			if(deployments != null && deployments.length > 0){
				for( Deployment deployment : deployments){
					DeploymentsConfig config = new DeploymentsConfig();
					config.setDeployName(deployment.getName());
					
					if(deployment.getReleases() != null && deployment.getReleases().size() > 0
							&& deployment.getStemcells() != null && deployment.getStemcells().size() > 0){
						for(ReleaseConfig releaseConfig : deployment.getReleases()){
							config.setRelease(releaseConfig.getName() + "/" + releaseConfig.getVersion());
							for(Stemcell stemcell : deployment.getStemcells()){
								config.setStemcellName(stemcell.getName());
								deploymentConfigs.add(config);
							}
						}
					}
				}
			}else{
				throw new IEDACommonException("notfound.deploymentConfigs.exception", "Deployment 목록이 존재하지 않습니다.", HttpStatus.NO_CONTENT);
			}
		} catch (ResourceAccessException e) {
			e.printStackTrace();
			log.info("getMessage : " + e.getMessage());
			log.info("getLocalizedMessage : " + e.getLocalizedMessage());
			throw new IEDACommonException("notfound.deploymentConfigs.exception", " DeploymentConfig정보 조회중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			throw new IEDACommonException("notfound.deploymentConfigs.exception",
					"요청정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
		}
		return deploymentConfigs;
	}
}
