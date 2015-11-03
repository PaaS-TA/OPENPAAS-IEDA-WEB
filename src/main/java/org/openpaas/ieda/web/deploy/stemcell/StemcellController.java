/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.deploy.stemcell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openpaas.ieda.api.Stemcell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Slf4j
@Controller
public class StemcellController {
	
	@Autowired
	private IEDAStemcellService sevice;
	
	@RequestMapping(value="/deploy/listStemcell", method=RequestMethod.GET)
	public String List() {
		return "/deploy/listStemcell";
	}
	
	@RequestMapping(value="/stemcells", method=RequestMethod.GET)
	public ResponseEntity listStemcell(){
		List<Stemcell> contents = sevice.listStemcell();
		log.info("$$$$$$$$$$$$$$$$$$$");
		log.info("SIZE : " + contents.size());
		log.info("$$$$$$$$$$$$$$$$$$$");
		int recid = 0;
		if(contents.size() > 0){
			for( Stemcell stemcell : contents ){
				stemcell.setRecid(recid);
				log.info("### OS : " + stemcell.getOperatingSystem());
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", contents.size());
		result.put("records", contents);
		return new ResponseEntity( result, HttpStatus.OK);
	}
}

