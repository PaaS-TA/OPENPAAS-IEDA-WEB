package org.openpaas.ieda.web.config.stemcell.controller;

import java.security.Principal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.web.config.stemcell.dto.StemcellManagementDTO;
import org.openpaas.ieda.web.config.stemcell.service.StemcellManagementDownloadAsyncService;
import org.openpaas.ieda.web.config.stemcell.service.StemcellManagementService;
import org.openpaas.ieda.web.config.stemcell.service.StemcellManagementUploadService;
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
public class StemcellManagementController extends BaseController {
	
	@Autowired private StemcellManagementService service;
	@Autowired private StemcellManagementUploadService uploadService;
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
	public ResponseEntity<HashMap<String, Object>> getPublicStemcells() {
		
		if(LOG.isInfoEnabled()){ LOG.info("================================> public 스템셀 목록 조회 요청"); }
		List<StemcellManagementVO> stemcellList = service.getStemcellList();
		
		HashMap<String, Object> list = new HashMap<String, Object>();
		if(stemcellList != null){
			list.put("total", stemcellList.size());
			list.put("records", stemcellList);
		}
		if(LOG.isInfoEnabled()){ LOG.info("================================> public 스템셀 목록 조회 성공!"); }
		
		return new ResponseEntity<HashMap<String, Object> >(list, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 입력 정보 저장
	 * @title               : systemStemcellRegist
	 * @return            : ResponseEntity<ReleaseManagementVO>
	***************************************************/
	@RequestMapping(value="/config/stemcell/regist/savestemcell/{testFlag}",  method=RequestMethod.POST)
	public ResponseEntity<StemcellManagementVO> systemStemcellRegist(@RequestBody StemcellManagementDTO.Regist dto, @PathVariable String testFlag ){
		
		if(LOG.isInfoEnabled()){ LOG.info("================================> 스템셀 등록 요청"); }
		StemcellManagementVO result = null;
		if("url".equals(dto.getFileType())){
			result = service.registPublicStemcellDownLoadInfo(dto, testFlag);
		}else if("version".equals(dto.getFileType())){
			result = service.registPublicStemcellDownLoadInfo(dto, testFlag);
		}else if("file".equals(dto.getFileType())){
			result = service.registPublicStemcellUploadInfo(dto, testFlag);
		}
		
		if(LOG.isInfoEnabled()){ LOG.info("================================> 스템셀 등록 성공"); }
		
		return new ResponseEntity<StemcellManagementVO>(result, HttpStatus.CREATED);
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 파일 업로드
	 * @title               : doPublicStemcellUpload
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/config/stemcell/regist/upload",  method=RequestMethod.POST)
	public ResponseEntity<?> doPublicStemcellUpload( MultipartHttpServletRequest request ){
		if(LOG.isInfoEnabled()){ LOG.info("================================> 스템셀 파일 업로드 요청"); }
		uploadService.uploadStemcellFile(request);
		if(LOG.isInfoEnabled()){ LOG.info("================================> 스템셀 파일 업로드 성공"); }
		return new ResponseEntity<>(HttpStatus.OK);
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 원격지에 있는 스템셀 다운로드
	 * @title               : doPublicStemcellDonwload
	 * @return            : ResponseEntity<?>
	***************************************************/
	@MessageMapping("/config/stemcell/regist/stemcellDownloading")
	@SendTo("/config/stemcell/regist/socket/logs")
	public ResponseEntity<?> doPublicStemcellDonwload(@RequestBody @Valid StemcellManagementDTO.Regist dto, Principal principal){
	
		if(LOG.isInfoEnabled()){ LOG.info("================================>  스템셀 다운로드 요청"); }
		donwonloadService.stemcellDownloadAsync(dto, principal);
		if(LOG.isInfoEnabled()){ LOG.info("================================>  스템셀 다운로드 성공"); }
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   :  스템셀 삭제
	 * @title               : systemRelaseDelete
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/config/stemcell/deletePublicStemcell",  method=RequestMethod.DELETE)
	public ResponseEntity<?> publicStemcellDelete(@RequestBody StemcellManagementDTO.Delete dto ){
		
		if(LOG.isInfoEnabled()){ LOG.info("================================>  스템셀 삭제 요청"); }
		try{
			service.deletePublicStemcell(dto);
		}catch(SQLException e){
			throw new CommonException("sql.systemRelease.exception",
					"스템셀 정보를 삭제하는데 실패하였습니다. ", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(LOG.isInfoEnabled()){ LOG.info("================================>  스템셀 삭제 성공"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
}
