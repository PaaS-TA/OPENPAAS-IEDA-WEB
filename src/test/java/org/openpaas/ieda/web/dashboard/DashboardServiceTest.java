package org.openpaas.ieda.web.dashboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.api.deployment.DeploymentDTO;
import org.openpaas.ieda.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.api.release.ReleaseDTO;
import org.openpaas.ieda.api.release.ReleaseInfoDTO;
import org.openpaas.ieda.api.release.ReleaseVersionDTO;
import org.openpaas.ieda.api.stemcell.StemcellListDTO;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementVO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@TestPropertySource(locations="classpath:application_test.properties")
public class DashboardServiceTest {
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   :  HTTP에 요청하여 읽어온 설치 목록 결과 정보 응답
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
	 * @description   : 업로드 된 릴리즈 목록 조회
	 * @title               : uploadedReleaseList
	 * @return            : List<ReleaseInfoDTO>
	***************************************************/
	@Rollback(true)
	public List<ReleaseInfoDTO> uploadedReleaseList() {
		DirectorConfigVO defaultDirector = settingDefaultDirector();
		List<ReleaseInfoDTO> releaseInfoList = null;
		try {
			
			HttpClient client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			GetMethod get = new GetMethod(DirectorRestHelper.getReleaseListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);
			
			String responseBody = setUploadReleaseList();
			ObjectMapper mapper = new ObjectMapper();
			ReleaseDTO[] releases = mapper.readValue(responseBody, ReleaseDTO[].class);
			
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
				// 스템셀 버전 역순으로 정렬
				Comparator<ReleaseInfoDTO> byReleaseVersion = Collections.reverseOrder(Comparator.comparing(ReleaseInfoDTO::getVersion));
				releaseInfoList = releaseInfoList.stream()
						.sorted(byReleaseVersion)
						.collect(Collectors.toList());
			}
		}catch(RuntimeException e){
			throw new CommonException("runtime.dashboard.release.exception", "릴리즈 정보 조회 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}catch (Exception e) {
			throw new CommonException("badRequest.dashboard.release.exception", "릴리즈 정보 조회 중 오류가 발생하였습니다.", HttpStatus.BAD_REQUEST);
		}
		
		return releaseInfoList; 
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 업로드 된 스템셀 목록
	 * @title               : uploadedStemcellList
	 * @return            : List<StemcellManagementVO>
	***************************************************/
	@Rollback
	public List<StemcellManagementVO> uploadedStemcellList() {
		DirectorConfigVO defaultDirector = settingDefaultDirector();

		List<StemcellManagementVO> stemcellInfoList = null;
		try {
			HttpClient client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());

			GetMethod get = new GetMethod(DirectorRestHelper.getStemcellsURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);
			String JsonResult = setUploadedStecmcellInfo();
			ObjectMapper mapper = new ObjectMapper();
			StemcellListDTO[] stemcells = mapper.readValue(JsonResult, StemcellListDTO[].class);

			int idx = 0;
			for ( StemcellListDTO stemcell : stemcells ) {
				if ( stemcellInfoList == null ) 
					stemcellInfoList = new ArrayList<StemcellManagementVO>();

				StemcellManagementVO stemcellInfo = new StemcellManagementVO();

				stemcellInfo.setRecid(idx++);
				stemcellInfo.setStemcellFileName(stemcell.getName());
				stemcellInfo.setOs(stemcell.getOperatingSystem());
				stemcellInfo.setStemcellVersion(stemcell.getVersion());
				stemcellInfoList.add(stemcellInfo);
			}

			// 스템셀 버전 역순으로 정렬
			if ( stemcellInfoList != null && !stemcellInfoList.isEmpty() ) {
				Comparator<StemcellManagementVO> byStemcellVersion = Collections.reverseOrder(Comparator.comparing(StemcellManagementVO::getStemcellVersion));
				stemcellInfoList = stemcellInfoList.stream()
						.sorted(byStemcellVersion)
						.collect(Collectors.toList());
			}
		}catch(RuntimeException e){
			throw new CommonException("runtime.dashboard.stemcell.exception", " 스템셀 정보 조회중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}catch (Exception e) {
			throw new CommonException("notfound.dashboard.stemcell.exception", " 스템셀 정보 조회중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}

		return stemcellInfoList;
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
		vo.setDirectorUrl("172.16.XXX.XXX");
		vo.setDirectorUuid("3b623d50-10c1-450a-8aad-test");
		vo.setDirectorVersion("1.3252.0");
		
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 업로드 된 릴리즈 정보 설정
	 * @title               : setUploadReleaseList
	 * @return            : String
	***************************************************/
	private String setUploadReleaseList() {
		String info = "[{\"name\":\"bosh\",";
		info += "\"release_versions\":[{\"version\":\"256\",";
		info += "\"commit_hash\":\"71adadbc\","; 
		info += "\"uncommitted_changes\":true,"; 
		info += "\"currently_deployed\":true,"; 
		info += "\"job_names\":[\"71adadbc\"]}]}]"; 
		return info;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 업로드된 스템셀 정보 설정
	 * @title               : setUploadedStecmcellInfo
	 * @return            : String
	***************************************************/
	private String setUploadedStecmcellInfo() {
		String info = "[{\"name\":\"bosh-aws-xen-ubuntu-trusty-go_agent\",";
		info += "\"operating_system\":\"ubuntu-trusty\","; 
		info += "\"version\":\"3262\","; 
		info += "\"cid\":\"7115e1ba-84a7-4964-9b0d-213d2279f63b\","; 
		info += "\"deployments\":[]}]"; 
		return info;
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
