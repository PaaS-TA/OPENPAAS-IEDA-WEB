package org.openpaas.ieda.web.information.stemcell.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.web.config.stemcell.service.StemcellManagementService;
import org.openpaas.ieda.web.information.stemcell.dto.StemcellDTO;
import org.openpaas.ieda.web.information.stemcell.service.StemcellDeleteAsyncService;
import org.openpaas.ieda.web.information.stemcell.service.StemcellService;
import org.openpaas.ieda.web.information.stemcell.service.StemcellUploadAsyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class StemcellController extends BaseController {

	@Autowired private StemcellService service;
	@Autowired private StemcellManagementService stemcellManagementService;
	@Autowired private StemcellDeleteAsyncService stemcellDeleteService;
	@Autowired private StemcellUploadAsyncService stemcellUploadService;
	
	final private static Logger LOG = LoggerFactory.getLogger(StemcellController.class);

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 업로드 화면을 호출하여 이동
	 * @title               : goListStemcell
	 * @return            : String
	***************************************************/
	@RequestMapping(value = "/info/stemcell", method = RequestMethod.GET)
	public String goListStemcell() {
		return "/information/listStemcell";
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 업로드된 스템셀 목록 조회 
	 * @title               : getUploadStemcellLIst
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value = "/info/stemcell/list/upload", method = RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> getUploadStemcellLIst() {
		
		if(LOG.isInfoEnabled()){ LOG.info("=======================> 업로드 된 스템셀 목록 조회 요청!"); }
		List<StemcellManagementVO> contents = service.listStemcell("");

		HashMap<String, Object> result = new HashMap<String, Object>();
		if ( contents != null ) {
			result.put("total", contents.size());
			result.put("records", contents);
		}
		
		if(LOG.isInfoEnabled()){ LOG.info("=======================> 업로드 된 스템셀 목록 조회 성공!!"); }
		
		return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로컬에 다운로드된 스템셀 목록 조회
	 * @title               : getLocalStemcellList
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value = "/info/stemcell/list/local", method = RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> getLocalStemcellList() {
		
		if(LOG.isInfoEnabled()){ LOG.info("=======================> 다운로드 된 스템셀 목록 조회 요청!"); }
		String iaas = "";
		List<StemcellManagementVO> contents = stemcellManagementService.listLocalStemcells(iaas);
		
		HashMap<String, Object> result = null;
		if ( contents != null ) {
			result = new HashMap<String, Object>();
			result.put("total", contents.size());
			result.put("records", contents);
		} 
		if(LOG.isInfoEnabled()){ LOG.info("=======================> 다운로드 된  스템셀 목록 조회 요청!"); }
		
		return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 업로드
	 * @title               : doUploadStemcell
	 * @return            : ResponseEntity<Object>
	***************************************************/
	@MessageMapping("/info/stemcell/upload/stemcellUploading")
    @SendTo("/info/stemcell/upload/logs")
	public ResponseEntity<Object> doUploadStemcell(@RequestBody @Valid StemcellDTO.Upload dto, Principal principal) {
		
		if(LOG.isInfoEnabled()){ LOG.info("=======================> 스템셀 업로드 요청!"); }
		stemcellUploadService.uploadStemcellAsync(LocalDirectoryConfiguration.getStemcellDir(), dto.getFileName(), principal.getName());
		if(LOG.isInfoEnabled()){ LOG.info("=======================> 스템셀 업로드 성공!!"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 업로드 된 스템셀 삭제
	 * @title               : doDeleteStemcell
	 * @return            : ResponseEntity<Object>
	***************************************************/
	@MessageMapping("/info/stemcell/delete/stemcellDelete")
    @SendTo("/info/stemcell/delete/logs")
	public ResponseEntity<Object> doDeleteStemcell(@RequestBody @Valid StemcellDTO.Delete dto, Principal principal) {
		stemcellDeleteService.deleteStemcellAsync(dto.getStemcellName(), dto.getVersion(), principal);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
