package org.openpaas.ieda.web.information.release.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.api.release.ReleaseInfoDTO;
import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.config.systemRelease.dao.ReleaseManagementVO;
import org.openpaas.ieda.web.config.systemRelease.service.ReleaseManagementService;
import org.openpaas.ieda.web.information.release.dto.ReleaseContentDTO;
import org.openpaas.ieda.web.information.release.service.ReleaseDeleteAsyncService;
import org.openpaas.ieda.web.information.release.service.ReleaseService;
import org.openpaas.ieda.web.information.release.service.ReleaseUploadAsyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ReleaseController extends BaseController {
	
	@Autowired private ReleaseService releaseService;
	@Autowired private ReleaseUploadAsyncService releaseUploadService;
	@Autowired private ReleaseDeleteAsyncService releaseDeleteService;
	@Autowired private ReleaseManagementService systemReleaseService;
	
	final private static Logger LOGGER = LoggerFactory.getLogger(ReleaseController.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 릴리즈 업로드 화면을 호출하여 이동한다.
	 * @title               : goListRelease
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/info/release", method=RequestMethod.GET)
	public String goListRelease() {
		return "/information/listRelease";
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 업로드된 릴리즈 정보 목록 조회
	 * @title               : listRelease
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping( value="/info/release/list/upload", method =RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> listRelease(){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================> 업로드된 릴리즈 정보 목록 조회 요청!!"); }
		
		List<ReleaseInfoDTO> contents = releaseService.listRelease();
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		if ( contents != null ) {
			result.put("total", contents.size());
			result.put("records", contents);
		} else
			result.put("total", 0);
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================> 업로드된 릴리즈 정보 목록 조회 성공!!"); }
		
		return new ResponseEntity<HashMap<String, Object>>( result, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로컬에 다운로드된 릴리즈 정보 목록 조회
	 * @title               : listLocalRelease
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping( value="/info/release/list/local", method =RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> listLocalRelease(){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================> 다운로드 된 릴리즈 정보 목록 조회 요청!!"); }
		List<ReleaseManagementVO> contents = systemReleaseService.getSystemReleaseList();
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		if ( contents != null ) {
			result.put("total", contents.size());
			result.put("records", contents);
		} else
			result.put("total", 0);
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================> 다운로드 된 릴리즈 정보 목록 조회 성공!!"); }
		
		return new ResponseEntity<HashMap<String, Object>>( result, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 릴리즈 업로드
	 * @title               : doUploadRelease
	 * @return            : ResponseEntity<Object>
	***************************************************/
	@MessageMapping("/info/release/upload/releaseUploading")
	@SendToUser("/info/release/upload/socket/logs")
	public ResponseEntity<Object> doUploadRelease(SimpMessageHeaderAccessor headerAccessor,  Principal p, @RequestBody @Valid ReleaseContentDTO.Upload dto) {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================> 릴리즈 업로드 요청!!"); }
		releaseUploadService.uploadReleaseAsync(dto.getFileName(), p.getName() );
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================> 릴리즈 업로드 성공!!"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 업로드된 릴리즈 삭제
	 * @title               : doDeleteRelease
	 * @return            : ResponseEntity<Object>
	***************************************************/
	@MessageMapping("/info/release/delete/releaseDelete")
	@SendTo("/info/release/delete/socket/logs")
	public ResponseEntity<Object> doDeleteRelease(@RequestBody @Valid ReleaseContentDTO.Delete dto, Principal principal) {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================> 업로드 된 릴리즈 삭제 요청!!"); }
		releaseDeleteService.deleteReleaseAsync(dto.getFileName(), dto.getVersion(), principal);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================> 업로드 된 릴리즈 삭제 성공!!"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
