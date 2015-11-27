package org.openpaas.ieda.web.deploy.release;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.openpaas.ieda.api.DirectorClient;
import org.openpaas.ieda.api.DirectorClientBuilder;
import org.openpaas.ieda.api.Release;
import org.openpaas.ieda.api.ReleaseVersion;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.IEDAConfiguration;
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
	private IEDAConfiguration iedaConfiguration;
	
	@Autowired
	private IEDADirectorConfigRepository directorConfigRepository;
	
	public List<ReleaseConfig> listRelease() {
		IEDADirectorConfig defaultDirector = new IEDADirectorConfig();

		Release[] releases = null;
		List<ReleaseConfig> releaseConfigs = new ArrayList<ReleaseConfig>();
		try {
			defaultDirector = directorConfigRepository.findOneByDefaultYn("Y");
			if (defaultDirector == null) {

				throw new IEDACommonException("notfound.releases.exception", " 기본관리자 정보가 존재하지 않습니다.",
						HttpStatus.NOT_FOUND);
			}
			log.info("DirectorUrl : " + defaultDirector.getDirectorUrl() + "/", defaultDirector.getDirectorPort());
			log.info("UserId      : " + defaultDirector.getUserId() + "/" + defaultDirector.getUserPassword());

			DirectorClient client = new DirectorClientBuilder()
					.withHost(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort())
					.withCredentials(defaultDirector.getUserId(), defaultDirector.getUserPassword()).build();

			URI releasesUri = UriComponentsBuilder.fromUri(client.getRoot()).pathSegment("releases").build().toUri();

			releases = client.getRestTemplate().getForObject(releasesUri, Release[].class);
			if (releases != null) {
				for (Release release : releases) {
					if (release.getReleaseVersions().size() > 0) {
						log.info("### ReleaseVersions :::" + release.getReleaseVersions().size());
						for (ReleaseVersion version : release.getReleaseVersions()) {
							ReleaseConfig config = new ReleaseConfig();
							config.setName(release.getName());
							config.setVersion(version.getVersion());
							config.setCommitHash(version.getCommitHash());
							config.setCurrentlyDeployed(version.getCurrentlyDeployed() ? "Y" : "N");
							config.setUncommittedChanges(version.getUncommittedChanges() ? "Y" : "N");
							releaseConfigs.add(config);
						}
					}
				}
			}
		} catch (ResourceAccessException e) {
			e.printStackTrace();
			log.info("getMessage : " + e.getMessage());
			log.info("getLocalizedMessage : " + e.getLocalizedMessage());
			throw new IEDACommonException("notfound.releases.exception", " Release정보 조회중 오류가 발생하였습니다.",
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			throw new IEDACommonException("notfound.releases.exception", "요청정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		return releaseConfigs;
	}
	
	/**
	 * Get Local Release File List 
	 * @return List<ReleaseConfig>
	 */
	public List<ReleaseConfig> listLocalRelease() {
		File file = new File(iedaConfiguration.getReleaseDir());
		File[] localFiles = file.listFiles();
		
		List<ReleaseConfig> returnReleases = new ArrayList<>();
		
		for (File fileInfo : localFiles) {
			ReleaseConfig config = new ReleaseConfig();
			if(fileInfo.getName().endsWith(".tgz")){
				config.setFileName(fileInfo.getName());
				returnReleases.add(config);
			}
		}
		return returnReleases;
	}
	
}
