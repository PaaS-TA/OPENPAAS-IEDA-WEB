/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.dashboard;

import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.api.DeploymentInfo;
import org.openpaas.ieda.api.ReleaseInfo;
import org.openpaas.ieda.api.StemcellInfo;
import org.openpaas.ieda.web.common.BaseController;
import org.openpaas.ieda.web.information.deploy.DeploymentService;
import org.openpaas.ieda.web.information.release.ReleaseService;
import org.openpaas.ieda.web.information.stemcell.StemcellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */
@Slf4j
@Controller
public class DashboardController extends BaseController {

	@Autowired
	private DeploymentService deploymentService;
	@Autowired
	private ReleaseService releaseService;
	@Autowired
	private StemcellService stemcellService;

	@RequestMapping(value="/dashboard", method=RequestMethod.GET)
	public String main(ModelAndView model) {
		return "/dashboard/dashboard";
	}

	@RequestMapping(value="/dashboard/deployments", method=RequestMethod.GET)
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

	@RequestMapping( value="/dashboard/releases", method =RequestMethod.GET)
	public ResponseEntity listRelease(){
		List<ReleaseInfo> contents = releaseService.listRelease();
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		if ( contents != null ) {
			result.put("total", contents.size());
			result.put("records", contents);
		} else
			result.put("total", 0);
		
		return new ResponseEntity( result, HttpStatus.OK);
	}

	// 스템셀 목록조회
	@RequestMapping(value="/dashboard/stemcells", method=RequestMethod.GET)
	public ResponseEntity listStemcell(){
		List<StemcellInfo> contents = stemcellService.listStemcell();

		HashMap<String, Object> result = new HashMap<String, Object>();
		if ( contents != null ) {
			result.put("total", contents.size());
			result.put("records", contents);
		} else
			result.put("total", 0);
		
		return new ResponseEntity(result, HttpStatus.OK);
	}

}
