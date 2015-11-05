/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.dashboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openpaas.ieda.api.Stemcell;
import org.openpaas.ieda.web.config.stemcell.StemcellContent;
import org.openpaas.ieda.web.config.stemcell.StemcellManagementService;
import org.openpaas.ieda.web.deploy.release.IEDAReleaseService;
import org.openpaas.ieda.web.deploy.release.ReleaseConfig;
import org.openpaas.ieda.web.deploy.stemcell.IEDAStemcellService;
import org.openpaas.ieda.web.information.deploy.DeploymentsConfig;
import org.openpaas.ieda.web.information.deploy.IEDADeploymentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */
@Slf4j
@Controller
public class DashboardController {

	@Autowired
	private IEDADeploymentsService deploymentsService;
	@Autowired
	private IEDAReleaseService releaseService;
	@Autowired
	private IEDAStemcellService stemcellService;

	@RequestMapping(value="/dashboard", method=RequestMethod.GET)
	public String main(ModelAndView model) {
		return "/dashboard/dashboard";
	}

	@RequestMapping(value="/dashboard/deployments", method=RequestMethod.GET)
	public ResponseEntity listDeployment(){

		List<DeploymentsConfig> contents = deploymentsService.listDeployment();
		log.info("*****************");
		log.info("SIZE : " + contents.size());
		log.info("*****************");
		if(contents != null && contents.size() > 0){
			int recid = 0;
			for(DeploymentsConfig deploymentConfig : contents){
				deploymentConfig.setRecid(recid++);
			}
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", contents.size());
		
		if(contents.size() > 5)	contents.subList(0, 4);
		result.put("records", contents);

		return new ResponseEntity(result, HttpStatus.OK);
	}

	@RequestMapping( value="/dashboard/releases", method =RequestMethod.GET)
	public ResponseEntity listRelease(){
		List<ReleaseConfig> contents = releaseService.listRelease();

		int recid = 0;
		if(contents != null){
			for( ReleaseConfig config : contents ){
				config.setRecid(recid++);
			}
		}

		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("total", contents.size());
		
		if(contents.size() > 5)	contents.subList(0, 4);
		result.put("records", contents);
		return new ResponseEntity( result, HttpStatus.OK);
	}

	// 스템셀 목록조회
	@RequestMapping(value="/dashboard/stemcells", method=RequestMethod.GET)
	public ResponseEntity listStemcell(){
		List<Stemcell> contents = stemcellService.listStemcell();
		int recid = 0;
		if(contents.size() > 0){
			for( Stemcell stemcell : contents ){
				stemcell.setRecid(recid++);
				log.info("### OS : " + stemcell.getOperatingSystem());
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", contents.size());
		result.put("records", contents);
		return new ResponseEntity( result, HttpStatus.OK);
	}

}
