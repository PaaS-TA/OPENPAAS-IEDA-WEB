package org.openpaas.ieda.web.config.stemcell.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.common.ErrorResponse;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.web.config.stemcell.dto.StemcellManagementDTO;
import org.openpaas.ieda.web.config.stemcell.service.StemcellManagementDownloadAsyncService;
import org.openpaas.ieda.web.config.stemcell.service.StemcellManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class StemcellManagementController extends BaseController {
	
	@Autowired private StemcellManagementService service;
	@Autowired private StemcellManagementDownloadAsyncService donwonloadService;
	
	private final static Logger LOG = LoggerFactory.getLogger(StemcellManagementController.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 관리 화면 이동
	 * @title               : goStemcellManagement
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/config/stemcell", method=RequestMethod.GET)
	public String goStemcellManagement() {
		if(LOG.isInfoEnabled()){ LOG.info("================================> 스템셀 관리 화면 요청"); }
		return "/config/stemcellManagement";
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Public 스템셀 목록 조회
	 * @title               : getPublicStemcells
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value="/config/stemcell/publicStemcells", method=RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> getPublicStemcells(
			@RequestParam  HashMap<String, String> requestMap) {
		
		if(LOG.isInfoEnabled()){ LOG.info("================================> public 스템셀 목록 조회 요청"); }
		List<StemcellManagementVO> stemcellList = service.getStemcellList(requestMap.get("os").toUpperCase(),
		requestMap.get("osVersion").toUpperCase(),
		requestMap.get("iaas").toUpperCase());
		
		HashMap<String, Object> list = new HashMap<String, Object>();
		list.put("total", stemcellList.size());
		list.put("records", stemcellList);
		
		if(LOG.isInfoEnabled()){ LOG.info("================================> public 스템셀 목록 조회 성공!"); }
		
		return new ResponseEntity<HashMap<String, Object> >(list, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 다운로드
	 * @title               : doDownloadStemcell
	 * @return            : ResponseEntity<Object>
	***************************************************/
	@MessageMapping("/config/stemcell/download/stemcellDownloading")
	@SendTo("/config/stemcell/download/socket/downloadStemcell")
	public ResponseEntity<Object> doDownloadStemcell(@RequestBody @Valid StemcellManagementDTO.Download dto, Principal principal) {
		
		if(LOG.isInfoEnabled()){ LOG.info("================================> public 스템셀 다운로드 요청!!"); }
		if(LOG.isDebugEnabled()){
			LOG.debug("stemcell dir : " + LocalDirectoryConfiguration.getStemcellDir());
			LOG.debug("doDownload key      : " + dto.getSublink());
			LOG.debug("doDownload fileName : " + dto.getFileName());
			LOG.debug("doDownload fileSize : " + new BigDecimal(dto.getFileSize()));
		}
		
		donwonloadService.doDownload(dto, principal);
		if(LOG.isInfoEnabled()){ LOG.info("================================> public 스템셀 다운로드 성공!!"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 삭제
	 * @title               : doDeleteStemcell
	 * @return            : ResponseEntity<Object>
	***************************************************/
	@RequestMapping(value="/config/stemcell/deletePublicStemcell", method=RequestMethod.DELETE)
	public ResponseEntity<Object> doDeleteStemcell(@RequestBody HashMap<String, String> requestMap, BindingResult result) throws SQLException {
		
		if(LOG.isInfoEnabled()){ LOG.info("================================> public 스템셀 삭제 요청!!"); }
		if ( result.hasErrors() ) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		String stemcellFileName = requestMap.get("stemcellFileName");
		String id = requestMap.get("id");
		if ( StringUtils.isEmpty(stemcellFileName) || StringUtils.isEmpty(id)) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setMessage("잘못된 요청입니다.");
			errorResponse.setCode("bad.request");
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		service.doDeleteStemcell(stemcellFileName, id);
		if(LOG.isInfoEnabled()){ LOG.info("================================> public 스템셀 삭제 성공!!"); }
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 목록 동기화
	 * @title               : doSyncPublicStemcell
	 * @return            : ResponseEntity<Object>
	***************************************************/
	@RequestMapping(value="/config/stemcell/syncPublicStemcell", method=RequestMethod.PUT)
	public ResponseEntity<Object> doSyncPublicStemcell() {
		
		if(LOG.isInfoEnabled()){ LOG.info("============================> 스템셀 목록 동기화 요청"); }
		service.syncPublicStemcell();
		if(LOG.isInfoEnabled()){ LOG.info("============================> 스템셀 목록 동기화 성공"); }
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
}
