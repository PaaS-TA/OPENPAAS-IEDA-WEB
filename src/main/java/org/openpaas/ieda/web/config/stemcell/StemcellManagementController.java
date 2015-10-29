/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.config.stemcell;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

	@RequestMapping(value="/config/listPublicStemcell", method=RequestMethod.GET)
	public String List() {
		
		return "/config/listPublicStemcell";
	}
	
	@RequestMapping(value="/publicStemcells", method=RequestMethod.GET)
	public ResponseEntity getPublicStemcells(@RequestBody StemcellContentDto.query queryDto) {
		
		log.info("os        = " + queryDto.getOs());
		log.info("osVersion = " + queryDto.getOsVersion());
		log.info("iaas      = " + queryDto.getIaas());
		
		List<StemcellContent> stemcellList = service.getPublicStemcell();
		
		log.info("Stemcell List = " + stemcellList.size());
		
		HashMap<String, Object> d = new HashMap<String, Object>();
		d.put("total", stemcellList.size());
		d.put("records", stemcellList);
		
		return new ResponseEntity<>(d, HttpStatus.OK);
	}
}
