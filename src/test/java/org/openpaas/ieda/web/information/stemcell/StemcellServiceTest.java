package org.openpaas.ieda.web.information.stemcell;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.api.stemcell.StemcellListDTO;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementVO;
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
public class StemcellServiceTest {
	
	@Autowired private SimpMessagingTemplate messagingTemplate;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(StemcellServiceTest.class);
	final private static String SEPARATOR = System.getProperty("file.separator");
	private final static String stemcellDir = System.getProperty("user.dir") + SEPARATOR + "src/test/java/org/openpaas/ieda/web/assets/dummy-stemcell.tgz";
	
	/***************************************************
	 * @return 
	 * @project       : OpenPaas 플랫폼 설치 자동화
	 * @description   : 업로드 된 스템셀 목록
	 * @title         : uploadedStemcellList
	 * @return        : void
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
			throw new CommonException("serverError.stemcell.exception", " 스템셀 정보 조회중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}catch (Exception e) {
			throw new CommonException("notfound.stemcell.exception", " 스템셀 정보 조회중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}

		return stemcellInfoList;
	}
	
	/***************************************************
	 * @project       : OpenPaas 플랫폼 설치 자동화
	 * @description   : 스템셀 업로드
	 * @title         : uploadStemcell
	 * @return        : void
	***************************************************/
	@Rollback(true)
	private void uploadStemcell(String userId) {
		DirectorConfigVO defaultDirector = settingDefaultDirector();
		
		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			
			PostMethod postMethod = new PostMethod(DirectorRestHelper.getUploadStemcellURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			postMethod = (PostMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)postMethod);
			postMethod.setRequestHeader("Content-Type", "application/x-compressed");
			
  			String uploadFile = stemcellDir;
			
			//파일업로드를 요청할 파일 생성 (파일, 컨텐츠, messagingTemplate, MESSAGE_ENDPOINT)
			postMethod.setRequestEntity(new FileUploadRequestDTO(new File(uploadFile), "application/x-compressed", messagingTemplate, "messageEndPoint", userId));
			int statusCode = HttpStatus.MOVED_PERMANENTLY.value();
			
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				
				String taskId = "1";
				LOGGER.debug("taskID : "+ taskId+  "     ================= 스템셀 업로드 성공 =================");
				
			} else {
				LOGGER.debug("스템셀 업로드 중 에러가 발생하였습니다.");
			}
			
		} catch ( Exception e) {
			LOGGER.debug("스템셀 업로드 중 에러가 발생하였습니다.");
		}
	}
	
	/***************************************************
	 * @project       : OpenPaas 플랫폼 설치 자동화
	 * @description   : 스템셀 업로드 비동기 호출 부분
	 * @title         : uploadStemcellAsync
	 * @return        : void
	***************************************************/
	public void uploadStemcellAsync(String userId) {
		uploadStemcell(userId);
	}
	
	
	/***************************************************
	 * @project : OpenPaas 플랫폼 설치 자동화
	 * @description : HTTP에 요청하여 해당 스템셀 정보 삭제를 요청하고 요청 이력 결과를 subscribe에 전달
	 * @title :  deleteStemcell
	 * @return : void
	 ***************************************************/
	public void deleteStemcell() {
		String stemcellName = "bosh-stemcell-2641-openstack-kvm-ubuntu-lucid-go_agent.tgz";
		String stemcellVersion = "2641";
		//기본 설치 관리자 정보 조회
		DirectorConfigVO defaultDirector = settingDefaultDirector();
		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());

			DeleteMethod deleteMethod = new DeleteMethod(DirectorRestHelper.getDeleteStemcellURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), stemcellName, stemcellVersion));
			deleteMethod = (DeleteMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)deleteMethod);
			
			//Request에 대한 응답
			int statusCode = HttpStatus.MOVED_PERMANENTLY.value();
			
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				
				String taskId = "1";
				LOGGER.debug("taskID : "+ taskId+  "     ================= 업로드 된 스템셀 삭제 성공 =================");
				
			} else {
				LOGGER.debug("스템셀 삭제 중 에러가 발생하였습니다."+statusCode);
			}
			
		} catch ( RuntimeException e) {
			LOGGER.debug("스템셀 삭제 중 에러가 발생하였습니다.");
		} catch ( Exception e) {
			LOGGER.debug("스템셀 삭제 중 에러가 발생하였습니다.");
		}

	}
	
	/***************************************************
	 * @project       : OpenPaas 플랫폼 설치 자동화
	 * @description   : 업로드된 스템셀 삭제 비동기 호출 부분
	 * @title         : deleteStemcellAsync
	 * @return        : void
	***************************************************/
	public void deleteStemcellAsync() {
		deleteStemcell();
	}
	/***************************************************
	 * @project 			: OpenPaas 플랫폼 설치 자동화
	 * @description 		: 기본 설치 관리자 설정
	 * @title 				: settingDefaultDirector
	 * @return 			: DirectorConfigVO
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
	
	private String setUploadedStecmcellInfo() {
		String info = "[{\"name\":\"bosh-aws-xen-ubuntu-trusty-go_agent\",";
		info += "\"operating_system\":\"ubuntu-trusty\","; 
		info += "\"version\":\"3262\","; 
		info += "\"cid\":\"7115e1ba-84a7-4964-9b0d-213d2279f63b\","; 
		info += "\"deployments\":[]}]"; 
		return info;
	}

}
