package org.openpaas.ieda.web.dashboard.controller;

import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.api.release.ReleaseInfoDTO;
import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.web.information.deploy.service.DeploymentService;
import org.openpaas.ieda.web.information.release.service.ReleaseService;
import org.openpaas.ieda.web.information.stemcell.service.StemcellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController extends BaseController {

	@Autowired private DeploymentService deploymentService;
	@Autowired private ReleaseService releaseService;
	@Autowired private StemcellService stemcellService;
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : DASHBOARD 화면 호출
	 * @title               : goDashboard
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/main/dashboard", method=RequestMethod.GET)
	public String goDashboard(ModelAndView model) {
		return "/dashboard/dashboard";
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치 정보 목록 조회
	 * @title               : listDeployment
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value="/main/dashboard/deployments", method=RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> listDeployment(){

		List<DeploymentInfoDTO> contents = deploymentService.listDeployment();
		HashMap<String, Object> result = new HashMap<String, Object>();
		HttpStatus status = null;
		if( contents != null ){
			result.put("records", contents);
			result.put("total", contents.size());
			status = HttpStatus.OK;
		}else{
			status = HttpStatus.NO_CONTENT;
		}
		return new ResponseEntity<HashMap<String, Object>>( result, status);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 업로드 릴리즈 정보 목록 조회
	 * @title               : listRelease
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping( value="/main/dashboard/releases", method =RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> listRelease(){
		List<ReleaseInfoDTO> contents = releaseService.listRelease();
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		if ( contents != null ) {
			result.put("total", contents.size());
			result.put("records", contents);
		} else
			result.put("total", 0);
			
		return new ResponseEntity<HashMap<String, Object>>( result, HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 업로드 스템셀 정보 목록 조회
	 * @title               : listStemcell
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value="/main/dashboard/stemcells", method=RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> listStemcell(){
		List<StemcellManagementVO> contents = stemcellService.listStemcell("");
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("total", contents != null ? contents.size() : 0 );
		result.put("records", contents);
		return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
	}

}
