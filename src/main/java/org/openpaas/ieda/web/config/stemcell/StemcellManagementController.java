/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.config.stemcell;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.common.IEDAConfiguration;
import org.openpaas.ieda.common.IEDAErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Slf4j
@Controller
public class StemcellManagementController {
	
	@Autowired
	private StemcellManagementService service;
	
	@Autowired
	private IEDAConfiguration iedaConfiguration;

	@Autowired
	private IEDAStemcellDownload stemcellDownloadService;
	
	@RequestMapping(value="/config/stemcellManagement", method=RequestMethod.GET)
	public String List() {
		
		return "/config/stemcellManagement";
	}
	
	// 스템셀 목록조회
	@RequestMapping(value="/publicStemcells", method=RequestMethod.GET)
	public ResponseEntity getPublicStemcells(@RequestParam  HashMap<String, String> requestMap) {
		
		List<StemcellContent> stemcellList = service.getStemcellList(requestMap.get("os").toUpperCase(),
				requestMap.get("osVersion").toUpperCase(),
				requestMap.get("iaas").toUpperCase());
		
		HashMap<String, Object> d = new HashMap<String, Object>();
		d.put("total", stemcellList.size());
		d.put("records", stemcellList);
		
		return new ResponseEntity<>(d, HttpStatus.OK);
	}
	
	//  스템셀 다운로드
	//@RequestMapping(value="/downloadPublicStemcell", method=RequestMethod.POST)
	@MessageMapping("/stemcellDownloading")
	@SendTo("/stemcell/downloadStemcell")
	public ResponseEntity doDownloadStemcell(@RequestBody @Valid StemcellContentDto.Download dto) {
		log.info("stemcell dir : " + iedaConfiguration.getStemcellDir());
		log.info("doDownload key      : " + dto.getKey());
		log.info("doDownload fileName : " + dto.getFileName());
		log.info("doDownload fileSize : " + new BigDecimal(dto.getFileSize()));
		
		stemcellDownloadService.doDownload(dto);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/deletePublicStemcell", method=RequestMethod.DELETE)
	public ResponseEntity doDeleteStemcell(@RequestBody HashMap<String, String> requestMap, BindingResult result) {
		if ( result.hasErrors() ) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		log.info("doDelete stemecllFileName : " + requestMap.get("stemcellFileName"));
		
		String stemcellFileName = requestMap.get("stemcellFileName");
		if ( stemcellFileName == null && stemcellFileName.isEmpty() ) {
			IEDAErrorResponse errorResponse = new IEDAErrorResponse();
			errorResponse.setMessage("잘못된 요청입니다.");
			errorResponse.setCode("bad.request");
			
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		
		service.doDeleteStemcell(stemcellFileName);
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
}
