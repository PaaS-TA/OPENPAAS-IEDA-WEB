package org.openpaas.ieda.web.deploy.release;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.openpaas.ieda.api.DirectorClient;
import org.openpaas.ieda.api.DirectorClientBuilder;
import org.openpaas.ieda.api.Release;
import org.openpaas.ieda.api.ReleaseVersion;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDAReleaseService {

	@Autowired
	private IEDADirectorConfigRepository directorConfigRepository;

	public List<ReleaseConfig> listRelease(){
		IEDADirectorConfig defaultDirector = directorConfigRepository.findOneByDefaultYn("Y");
		
		Release[] releases = null;
		List<ReleaseConfig> releaseConfigs = new ArrayList<ReleaseConfig>(); 
		try{
			DirectorClient client = new DirectorClientBuilder()
					.withHost(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort())
					.withCredentials(defaultDirector.getUserId(), defaultDirector.getUserPassword()).build();
			
			URI releasesUri = UriComponentsBuilder.fromUri(client.getRoot())
					.pathSegment("releases").build().toUri();
			
			releases = client.getRestTemplate().getForObject(releasesUri, Release[].class);
			if(  releases != null ){
				for (Release release : releases) {
					ReleaseConfig config = new ReleaseConfig();
					config.setName(release.getName());
					if(release.getReleaseVersions().size() > 0){
						for(ReleaseVersion version :  release.getReleaseVersions()){
							config.setVersion(version.getVersion());
							config.setCommitHash(version.getCommitHash());
							config.setCurrentlyDeployed(version.getCurrentlyDeployed() ? "Y" :"N");
							config.setUncommittedChanges(version.getUncommittedChanges() ? "Y" :"N");
						}					
					}
					releaseConfigs.add(config);
				}
			}
		} catch (ResourceAccessException e) {
			e.printStackTrace();
			log.info("getMessage : " + e.getMessage());
			log.info("getLocalizedMessage : " + e.getLocalizedMessage());
			throw new IEDACommonException("notfound.releases.exception", " Release정보 조회중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			throw new IEDACommonException("notfound.releases.exception",
					"요청정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
		}
		
		return releaseConfigs;
	}
}
