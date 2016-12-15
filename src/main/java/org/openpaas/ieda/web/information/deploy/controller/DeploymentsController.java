package org.openpaas.ieda.web.information.deploy.controller;

import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.information.deploy.service.DeploymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DeploymentsController extends BaseController { 
	
	@Autowired private DeploymentService deploymentService;
	
	final private static Logger LOGGER = LoggerFactory.getLogger(DeploymentsController.class);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포 정보 화면 이동
	 * @title               : goListDeployment
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/info/deployment", method=RequestMethod.GET)
	public String goListDeployment() {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 배포 정보 화면 요청"); }
		return "/information/listDeployment";
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치 정보 목록을 조회 
	 * @title               : listDeployment
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value="/info/deployment/list", method=RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> listDeployment(){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 설치 정보 목록을 조회 요청"); }
		List<DeploymentInfoDTO> contents = deploymentService.listDeployment();
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		if(contents != null){
		result.put("total", contents.size()); //이 부분 null 포인트 에러 바꿀 것.
		result.put("records", contents);
		}
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 설치 정보 목록을 조회 성공"); }
		return new ResponseEntity<HashMap<String, Object>>( result, HttpStatus.OK);
	}
}
