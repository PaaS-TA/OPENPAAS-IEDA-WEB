/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.information.deploy;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
public class DeploymentsController {
	
	@Autowired
	private IEDADeploymentsService service;

	@RequestMapping(value="/information/listDeployment", method=RequestMethod.GET)
	public String List() {
		return "/information/listDeployment";
	}
	
	@RequestMapping(value="/deployments", method=RequestMethod.GET)
	public ResponseEntity listDeployment(){
		
		List<Deployment> contents = service.listDeployment();
		if(contents != null && contents.size() > 0){
			int recid = 0;
			for(Deployment deploymentConfig : contents){
				deploymentConfig.setRecid(recid++);
			}
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", contents.size());
		result.put("records", contents);
		
		return new ResponseEntity(result, HttpStatus.OK);
	}
}
