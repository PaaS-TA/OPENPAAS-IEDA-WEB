/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.information.deploy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openpaas.ieda.api.DeploymentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class DeploymentsController {
	
	@Autowired
	private DeploymentService deploymentService;

	@RequestMapping(value="/information/listDeployment", method=RequestMethod.GET)
	public String List() {
		return "/information/listDeployment";
	}
	
	@RequestMapping(value="/deployments", method=RequestMethod.GET)
	public ResponseEntity listDeployment(){
		
		List<DeploymentInfo> contents = deploymentService.listDeployment();
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		if ( contents != null ) {
			result.put("total", contents.size());
			result.put("records", contents);
		} else
			result.put("total", 0);
		
		return new ResponseEntity( result, HttpStatus.OK);
	}
}
