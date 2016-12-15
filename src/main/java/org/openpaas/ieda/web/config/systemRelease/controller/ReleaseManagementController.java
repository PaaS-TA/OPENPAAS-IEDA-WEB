package org.openpaas.ieda.web.config.systemRelease.controller;

import java.security.Principal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.config.stemcell.controller.StemcellManagementController;
import org.openpaas.ieda.web.config.systemRelease.dao.ReleaseManagementVO;
import org.openpaas.ieda.web.config.systemRelease.dto.ReleaseManagementDTO;
import org.openpaas.ieda.web.config.systemRelease.service.ReleaseManagementDownloadService;
import org.openpaas.ieda.web.config.systemRelease.service.ReleaseManagementService;
import org.openpaas.ieda.web.config.systemRelease.service.ReleaseManagementUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
public class ReleaseManagementController extends BaseController{

	@Autowired private ReleaseManagementService service;
	@Autowired private ReleaseManagementDownloadService downloadService;
	@Autowired private ReleaseManagementUploadService uploadService;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(StemcellManagementController.class);

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 시스템 릴리즈 관리 화면 요청
	 * @title               : goReleaseManagement
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/config/systemRelease", method=RequestMethod.GET)
	public String goReleaseManagement() {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 릴리즈 관리 화면 요청"); }
		return "/config/releaseManagement";
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 시스템 릴리즈 정보 목록 조회
	 * @title               : getSystemReleaseList
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value="/config/systemRelease/list",  method=RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> getSystemReleaseList(){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 시스템 릴리즈 목록 조회 요청"); }
		List<ReleaseManagementVO> systemReleases = service.getSystemReleaseList();
		
		HashMap<String, Object> list = new HashMap<String, Object>();
		
		list.put("total", systemReleases.size());
		list.put("records", systemReleases);
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 시스템 릴리즈 목록 조회 성공"); }
		return new ResponseEntity<HashMap<String, Object> >(list, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 릴리즈 유형 조회
	 * @title               : getSystemReleaseTypeList
	 * @return            : ResponseEntity<List<String>>
	***************************************************/
	@RequestMapping(value="/config/systemRelease/list/releaseType",  method=RequestMethod.GET)
	public ResponseEntity<List<String>> getSystemReleaseTypeList(){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 시스템 릴리즈 유형 목록 조회 요청"); }
		List<String> releaseTypes = service.getSystemReleaseTypeList();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 시스템 릴리즈 유형 목록 조회 성공"); }
		return new ResponseEntity<List<String>>(releaseTypes, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 시스템 릴리즈 입력 정보 저장
	 * @title               : systemReleaseRegist
	 * @return            : ResponseEntity<ReleaseManagementVO>
	***************************************************/
	@RequestMapping(value="/config/systemRelease/regist/{testFlag}",  method=RequestMethod.POST)
	public ResponseEntity<ReleaseManagementVO> systemReleaseRegist(@RequestBody ReleaseManagementDTO.Regist dto, @PathVariable String testFlag ){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 시스템 릴리즈 등록 요청"); }
		if(LOGGER.isDebugEnabled()){ 
			LOGGER.debug("# 시스템 릴리즈 명 : " + dto.getReleaseName());
			LOGGER.debug("# 시스템 릴리즈 유형"  + dto.getReleaseType());
			LOGGER.debug("# 시스템 릴리즈 파일 " + dto.getReleaseFileName());
		}
		ReleaseManagementVO result = null;
		if("url".equals(dto.getFileType())){
			 result = service.registSystemReleaseDownloadInfo(dto, testFlag);
		}else{
			 result = service.registSystemReleaseUploadInfo(dto, testFlag);
		}
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 시스템 릴리즈 등록 성공"); }
		
		return new ResponseEntity<ReleaseManagementVO>(result, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 원격지에 있는 릴리즈 다운로드
	 * @title               : doSystemReleaseDonwload
	 * @return            : ResponseEntity<?>
	***************************************************/
	@MessageMapping("/config/systemRelease/regist/download/releaseDownloading")
	@SendTo("/config/systemRelease/regist/socket/logs")
	public ResponseEntity<?> doSystemReleaseDonwload(@RequestBody @Valid ReleaseManagementDTO.Regist dto, Principal principal){
	
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 시스템 릴리즈 다운로드 요청"); }
			downloadService.releaseDownloadAsync(dto, principal);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 시스템 릴리즈 다운로드 성공"); }
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 릴리즈 파일 업로드
	 * @title               : doSystemReleaseUpload
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/config/systemRelease/regist/upload",  method=RequestMethod.POST)
	public ResponseEntity<?> doSystemReleaseUpload( MultipartHttpServletRequest request ){

		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 시스템 릴리즈 파일 업로드 요청"); }
		uploadService.uploadReleaseFile(request);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 시스템 릴리즈 파일 업로드 성공"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
		
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 시스템 릴리즈 삭제
	 * @title               : systemRelaseDelete
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/config/systemRelease/delete",  method=RequestMethod.DELETE)
	public ResponseEntity<?> systemRelaseDelete(@RequestBody ReleaseManagementDTO.Delete dto ){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 시스템 릴리즈 삭제 요청"); }
		try{
			service.deleteSystemRelease(dto);
		}catch(SQLException e){
			throw new CommonException("sql.systemRelease.exception",
					"시스템 릴리즈 정보를 삭제하는데 실패하였습니다. ", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 시스템 릴리즈 삭제 성공"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
