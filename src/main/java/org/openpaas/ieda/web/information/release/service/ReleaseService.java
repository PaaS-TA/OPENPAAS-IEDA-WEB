package org.openpaas.ieda.web.information.release.service;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.api.release.ReleaseDTO;
import org.openpaas.ieda.api.release.ReleaseInfoDTO;
import org.openpaas.ieda.api.release.ReleaseVersionDTO;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ReleaseService {
	
	@Autowired private DirectorConfigService directorConfigService;
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 업로드된 릴리즈 목록 조회 요청
	 * @title               : listRelease
	 * @return            : List<ReleaseInfoDTO>
	***************************************************/
	public List<ReleaseInfoDTO> listRelease() {
		
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		List<ReleaseInfoDTO> releaseInfoList = null;
		try {
			
			HttpClient client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			GetMethod get = new GetMethod(DirectorRestHelper.getReleaseListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);
			client.executeMethod(get);
			
			if ( !StringUtils.isEmpty(get.getResponseBodyAsString())) {
				
				ObjectMapper mapper = new ObjectMapper();
				ReleaseDTO[] releases = mapper.readValue(get.getResponseBodyAsString(), ReleaseDTO[].class);
				int idx = 0;
				List<ReleaseDTO> releaseList = Arrays.asList(releases);
				for ( ReleaseDTO release : releaseList ) {
					
					List<ReleaseVersionDTO> versionList = release.getReleaseVersions();
					for (ReleaseVersionDTO releaseVersion : versionList) {
						
						ReleaseInfoDTO releaseInfo = new ReleaseInfoDTO();
						releaseInfo.setRecid(idx++);
						releaseInfo.setName(release.getName());
						releaseInfo.setVersion(releaseVersion.getVersion());
						releaseInfo.setCurrentDeployed(releaseVersion.getCurrentlyDeployed().toString());
						releaseInfo.setJobNames(releaseVersion.getJobNames().toString());
						
						if ( releaseInfoList == null ) 
							releaseInfoList = new ArrayList<ReleaseInfoDTO>();
						
						releaseInfoList.add(releaseInfo);
					}
				}
				if ( releaseInfoList != null ) {
					// 릴리즈 버전 역순으로 정렬
					Comparator<ReleaseInfoDTO> byReleaseVersion = Collections.reverseOrder(Comparator.comparing(ReleaseInfoDTO::getVersion));
					releaseInfoList = releaseInfoList.stream()
							.sorted(byReleaseVersion).collect(Collectors.toList());
				}
			}
		}catch(RuntimeException e){
			throw new CommonException("runtime.releases.exception", "업로드된 릴리즈 정보 조회 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}catch (NoRouteToHostException e){
			throw new CommonException("noRouteToHost.releases.exception", "네트워크 연결에 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (JsonParseException e) {
			throw new CommonException("josnParse.releases.exception", "업로드된 릴리즈 정보 조회 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (JsonMappingException e) {
			throw new CommonException("jsonMapping.releases.exception", "업로드된 릴리즈 정보 조회 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			throw new CommonException("ioFIleRead.releases.exception", "업로드된 릴리즈 정보 조회 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} 
		
		return releaseInfoList; 
	}
	

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF, DIEGO, Garden-Linux, ETCD 등의 공통 릴리즈 정보를 읽고 응답
	 * @title               : getReleasesFilter
	 * @return            : List<ReleaseInfoDTO>
	***************************************************/
	public List<ReleaseInfoDTO> getReleasesFilter(String type) {

		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		List<ReleaseInfoDTO> releaseInfoList = null;
		try {
			
			HttpClient client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			GetMethod get = new GetMethod(DirectorRestHelper.getReleaseListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);

			client.executeMethod(get);
			
			if ( !StringUtils.isEmpty(get.getResponseBodyAsString()) ) {
				releaseInfoList = new ArrayList<ReleaseInfoDTO>();
				
				ObjectMapper mapper = new ObjectMapper();
				ReleaseDTO[] releases = mapper.readValue(get.getResponseBodyAsString(), ReleaseDTO[].class);
				
				int idx = 0;
				List<ReleaseDTO> releaseList = Arrays.asList(releases);
				for ( ReleaseDTO release : releaseList ) {
					String releaseName = release.getName();
					if( release.getName().indexOf("paasta-") > -1 ){
						releaseName = release.getName().split("paasta-")[1];
						if("controller".equals(releaseName)) releaseName = "cf";
						else if("container".equals(releaseName)) releaseName="diego";
						else if("garden-runc".equals(releaseName)) releaseName="garden-linux";
					}
					if( type.equals(releaseName)){
						
						List<ReleaseVersionDTO> versionList = release.getReleaseVersions();
						for (ReleaseVersionDTO releaseVersion : versionList) {
							ReleaseInfoDTO releaseInfo = new ReleaseInfoDTO();
							
							releaseInfo.setRecid(idx++);
							releaseInfo.setName(release.getName());
							releaseInfo.setVersion(releaseVersion.getVersion());
							
							releaseInfoList.add(releaseInfo);
								
						}
					}
				}
				// 릴리즈 버전 역순으로 정렬
				Comparator<ReleaseInfoDTO> byReleaseVersion = Collections.reverseOrder(Comparator.comparing(ReleaseInfoDTO::getVersion));
				releaseInfoList = releaseInfoList.stream()
						.sorted(byReleaseVersion)
						.collect(Collectors.toList());
			}
			
		}catch(RuntimeException e){
			throw new CommonException("runtime.releases.exception", "릴리즈 정보 조회 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (JsonParseException e) {
				throw new CommonException("jsonParse.releases.exception", "릴리즈 정보 조회 중 오류가 발생하였습니다.", HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			throw new CommonException("jsonMapping.releases.exception", "릴리즈 정보 조회 중 오류가 발생하였습니다.", HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			throw new CommonException("ioFileRead.releases.exception", "릴리즈 정보 조회 중 오류가 발생하였습니다.", HttpStatus.BAD_REQUEST);
		}
		
		return releaseInfoList; 
	}

}