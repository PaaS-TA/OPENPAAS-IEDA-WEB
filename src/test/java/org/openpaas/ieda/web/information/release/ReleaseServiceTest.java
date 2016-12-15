package org.openpaas.ieda.web.information.release;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.api.release.ReleaseDTO;
import org.openpaas.ieda.api.release.ReleaseInfoDTO;
import org.openpaas.ieda.api.release.ReleaseVersionDTO;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.information.stemcell.dto.FileUploadRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
@TestPropertySource(locations="classpath:application_test.properties")
public class ReleaseServiceTest {
	
	@Autowired private SimpMessagingTemplate messagingTemplate;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ReleaseServiceTest.class);
	private final static String releaseFileName = "dummy-release.tgz";
	final private static String SEPARATOR = System.getProperty("file.separator");
	private final static String releaseDir = System.getProperty("user.dir") + SEPARATOR + "src/test/java/org/openpaas/ieda/web/assets/dummy-release.tgz";
	
	
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
					// 릴리즈 버전 역순으로 정렬
					Comparator<ReleaseInfoDTO> byReleaseVersion = Collections.reverseOrder(Comparator.comparing(ReleaseInfoDTO::getVersion));
					releaseInfoList = releaseInfoList.stream()
							.sorted(byReleaseVersion)
							.collect(Collectors.toList());
				}
		}catch(RuntimeException e){
			throw new CommonException("runtime.releases.exception", "릴리즈 정보 조회 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}catch (Exception e) {
			throw new CommonException("badRequest.releases.exception", "릴리즈 정보 조회 중 오류가 발생하였습니다.", HttpStatus.BAD_REQUEST);
		}
		
		return releaseInfoList; 
	}

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 릴리즈 업로드
	 * @title               : uploadRelease
	 * @return            : void
	***************************************************/
	@Rollback(true)
	private void uploadRelease(String userId) {
		DirectorConfigVO defaultDirector = settingDefaultDirector();

		try {
			//1. http 객체 생성
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			
			//2. 서버에 리소스 데이터 생성
			PostMethod postMethod = new PostMethod(DirectorRestHelper.getUploadReleaseURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			postMethod = (PostMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)postMethod);
			postMethod.setRequestHeader("Content-Type", "application/x-compressed");
			
			//3. 업로드할 파일저장 위치를 설정
  			String uploadFile = releaseDir + System.getProperty("file.separator") + releaseFileName;
			
			//요청할 파일내용을 생성
			postMethod.setRequestEntity(new FileUploadRequestDTO(new File(uploadFile), "application/x-compressed", messagingTemplate, "messageEndPoint", userId));
			
			//업로드 시작
			int statusCode = HttpStatus.MOVED_PERMANENTLY.value();
			if(LOGGER.isDebugEnabled()){  
				LOGGER.debug("#################### 업로드 요청 상태코드 : " +  statusCode);
			}
			
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				Header location = postMethod.getResponseHeader("Location");
				String taskId = "1";
				LOGGER.debug("################ 릴리즈 업로드 성공");
			} else {
				if(LOGGER.isDebugEnabled()){  
					LOGGER.debug("릴리즈 업로드 중 에러가 발생하였습니다.");
					LOGGER.debug("################ 업로드 요청 오류 상태코드 : " +  statusCode);
				}
			}
			
		} catch(RuntimeException e){
			LOGGER.debug("릴리즈 업로드 중 에러가 발생하였습니다.");
		} catch ( Exception e) {
			LOGGER.debug("릴리즈 업로드 중 에러가 발생하였습니다.");
		}
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기 메소드 호출
	 * @title               : uploadReleaseAsync
	 * @return            : void
	***************************************************/
	public void uploadReleaseAsync(String userId) {
		uploadRelease(userId);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 릴리즈 삭제
	 * @title               : deleteRelease
	 * @return            : void
	***************************************************/
	private void deleteRelease() {
		DirectorConfigVO defaultDirector = settingDefaultDirector();
		String releaseName = "bosh-test-cpi-release";
		String releaseVersion = "20";
		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			DeleteMethod deleteMethod = new DeleteMethod(DirectorRestHelper.getDeleteReleaseURI(
					defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), releaseName, releaseVersion));
			deleteMethod = (DeleteMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)deleteMethod);
			
			//실행
			int statusCode = HttpStatus.MOVED_PERMANENTLY.value();
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()	) {
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("릴리즈 삭제 성공"+statusCode);
				}
			} else {
				LOGGER.debug("업로드된 릴리즈 삭제 중 에러가 발생하였습니다."+statusCode);
			}
			
		}catch(RuntimeException e){
			LOGGER.debug("업로드된 릴리즈 삭제 중 에러가 발생하였습니다.");
		}catch ( Exception e) {
			LOGGER.debug("업로드된 릴리즈 삭제 중 에러가 발생하였습니다.");
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기로 메소드 호출
	 * @title               : deleteReleaseAsync
	 * @return            : void
	***************************************************/
	public void deleteReleaseAsync() {
		deleteRelease();
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

}
