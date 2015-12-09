package org.openpaas.ieda.web.deploy.release;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.api.Release;
import org.openpaas.ieda.api.ReleaseFile;
import org.openpaas.ieda.api.ReleaseInfo;
import org.openpaas.ieda.api.ReleaseVersion;
import org.openpaas.ieda.api.Stemcell;
import org.openpaas.ieda.api.director.DirectorRestHelper;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.IEDAConfiguration;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReleaseService {
	@Autowired
	private IEDAConfiguration iedaConfiguration;
	
	@Autowired
	private IEDADirectorConfigService directorConfigService;
	
	public List<ReleaseInfo> listRelease() {
		
		IEDADirectorConfig defaultDirector = directorConfigService.getDefaultDirector();

		Release[] releases = null;
		List<ReleaseInfo> releaseInfoList = null;
		try {
			
			HttpClient client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			GetMethod get = new GetMethod(DirectorRestHelper.getReleaseListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);

			client.executeMethod(get);
			ObjectMapper mapper = new ObjectMapper();
			releases = mapper.readValue(get.getResponseBodyAsString(), Release[].class);
			
			int idx = 0;
			List<Release> releaseList = Arrays.asList(releases);
			for ( Release release : releaseList ) {
				
				List<ReleaseVersion> versionList = release.getReleaseVersions();
				for (ReleaseVersion releaseVersion : versionList) {
					
					ReleaseInfo releaseInfo = new ReleaseInfo();
					releaseInfo.setRecid(idx++);
					releaseInfo.setName(release.getName());
					releaseInfo.setVersion(releaseVersion.getVersion());
					releaseInfo.setCurrentDeployed(releaseVersion.getCurrentlyDeployed().toString());
					releaseInfo.setJobNames(releaseVersion.getJobNames().toString());
					
					if ( releaseInfoList == null ) 
						releaseInfoList = new ArrayList<ReleaseInfo>();
					
					releaseInfoList.add(releaseInfo);
					
					log.info("===============");
					log.info("--> " + releaseInfo.toString());
				}
			}
			
			if ( releaseInfoList != null ) {
				// 스템셀 버전 역순으로 정렬
				//Comparator<ReleaseInfo> byReleaseName = Collections.reverseOrder(Comparator.comparing(ReleaseInfo::getName));
				Comparator<ReleaseInfo> byReleaseVersion = Collections.reverseOrder(Comparator.comparing(ReleaseInfo::getVersion));

				releaseInfoList = releaseInfoList.stream()
						//.sorted(byReleaseName)
						.sorted(byReleaseVersion)
						.collect(Collectors.toList());
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new IEDACommonException("notfound.releases.exception", "릴리즈 정보 조회 중 오류가 발생하였습니다.", HttpStatus.BAD_REQUEST);
		}
		
		return releaseInfoList; 
	}
	
	/**
	 * Get Local Release File List 
	 * @return List<ReleaseConfig>
	 */
	
	public List<ReleaseFile> listLocalRelease() {
		File file = new File(iedaConfiguration.getReleaseDir());
		File[] localFiles = file.listFiles();
		
		List<ReleaseFile> localReleaseList = null; 
		
		int idx = 0;
		for (File fileInfo : localFiles) {
			if ( localReleaseList == null )
				localReleaseList = new ArrayList<ReleaseFile>();
			
			if(!fileInfo.getName().endsWith(".tgz") && !fileInfo.getName().endsWith(".TGZ")) continue;
			
			ReleaseFile releaseFile = new ReleaseFile();
			releaseFile.setRecid(idx++);
			releaseFile.setReleaseFile(fileInfo.getName());
			
			DecimalFormat numberFormatter = new DecimalFormat("###,###");
			releaseFile.setReleaseFileSize(numberFormatter.format(fileInfo.length()));
			
			localReleaseList.add(releaseFile);
			
		}
		return localReleaseList;
	}

}
