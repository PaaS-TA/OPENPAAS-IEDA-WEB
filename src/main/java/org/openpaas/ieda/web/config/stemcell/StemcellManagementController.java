/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.config.stemcell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

	@RequestMapping(value="/config/listPublicStemcell", method=RequestMethod.GET)
	public String List() {
		
		return "/config/listPublicStemcell";
	}
	
	@RequestMapping(value="/publicStemcells", method=RequestMethod.GET)
	public ResponseEntity getPublicStemcells(@RequestParam  HashMap<String, String> requestMap) {
		
		List<StemcellContent> stemcellList = service.getPublicStemcell();

		stemcellList = stemcellList.stream()
				.filter(t ->  t.getOs().toUpperCase().equals(requestMap.get("os").toUpperCase()))
				.filter(t ->  t.getOsVersion().toUpperCase().equals(requestMap.get("osVersion").toUpperCase()))
				.filter(t ->  t.getIaas().toUpperCase().equals(requestMap.get("iaas").toUpperCase()))
				.collect(Collectors.toList()); 
		
	
		HashMap<String, Object> d = new HashMap<String, Object>();
		d.put("total", stemcellList.size());
		d.put("records", stemcellList);
		
		return new ResponseEntity<>(d, HttpStatus.OK);
	}
}
