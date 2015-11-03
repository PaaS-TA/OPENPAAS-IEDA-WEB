/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.deploy.release;

import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.api.Release;
import org.openpaas.ieda.api.ReleaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Slf4j
@Controller
public class ReleaseController {

	@Autowired
	private ReleaseService service;
	
	@RequestMapping(value="/deploy/listRelease", method=RequestMethod.GET)
	public String List() {
		return "/deploy/listRelease";
	}
	
	@RequestMapping( value="/releases", method =RequestMethod.GET)
	public ResponseEntity listRelease(){
		List<ReleaseConfig> contents = service.listRelease();
		
		log.info("#####listRelease#####");
		log.info("contents.size  : "+ contents.size());
		log.info("#####################");
		int recid = 0;
		for( ReleaseConfig config : contents ){
			config.setRecid(recid++);
		}
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("total", contents.size());
		result.put("records", contents);
		return new ResponseEntity( result, HttpStatus.OK);
	}
	
}

