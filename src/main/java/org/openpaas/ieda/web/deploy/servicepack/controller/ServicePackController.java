package org.openpaas.ieda.web.deploy.servicepack.controller;

import java.security.Principal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.deploy.servicepack.dao.ServicePackVO;
import org.openpaas.ieda.web.deploy.servicepack.dto.ServicePackParamDTO;
import org.openpaas.ieda.web.deploy.servicepack.service.ServicePackDeleteDeployAsyncService;
import org.openpaas.ieda.web.deploy.servicepack.service.ServicePackDeployAsyncService;
import org.openpaas.ieda.web.deploy.servicepack.service.ServicePackService;
import org.openpaas.ieda.web.information.manifest.dao.ManifestVO;
import org.openpaas.ieda.web.information.manifest.service.ManifestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ServicePackController extends BaseController{
	@Autowired ServicePackService service;
	@Autowired ManifestService manifestService;
	@Autowired ServicePackDeployAsyncService servicePackDeployAsyncService;
	@Autowired ServicePackDeleteDeployAsyncService servicePackDeleteDeployAsyncService;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ServicePackController.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 화면 이동
	 * @title               : goServicePack
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/deploy/servicePack", method=RequestMethod.GET)
	public String goServicePack(){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 서비스팩 설치 화면 요청"); }
		return "/deploy/servicepack/servicePack";
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 전체 목록 조회
	 * @title               : getServicePackList
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/servicePack/list/{iaas}", method=RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> getServicePackList(@PathVariable String iaas) {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> 서비스 팩 목록 조회 요청"); }
		List<ServicePackVO> content = service.getServicePackList(iaas);
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		result.put("total", (content == null) ? 0:content.size());
		result.put("records", content);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> 서비스 팩 목록 조회 성공"); }
		
		return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 업로드된 Manifest 조회 
	 * @title               : getManifestList
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/servicePack/list/manifest", method=RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> getManifestList() {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> 업드르 된 Manifest 파일 명 조회 요청"); }
		List<ManifestVO> content = manifestService.getManifestList();
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		result.put("total", (content == null) ? 0:content.size());
		result.put("records", content);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> Manifest 파일 명 조회 성공"); }
		
		return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 정보 저장
	 * @title               : saveServicePackInfo
	 * @return            : ResponseEntity<ServicePackVO>
	***************************************************/
	@RequestMapping(value="/deploy/servicePack/install/saveServicePackinfo/{test}", method=RequestMethod.POST)
	public ResponseEntity<ServicePackVO> saveServicePackInfo(@RequestBody @Valid ServicePackParamDTO dto, @PathVariable String test){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> 서비스팩 정보 저장 요청"); }
		ServicePackVO vo = service.saveServicePackInfo(dto,test);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> 서비스팩 정보 저장 성공"); }
		return new ResponseEntity<ServicePackVO>(vo,HttpStatus.CREATED);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 배포 파일 생성
	 * @title               : makeDeploymentFile
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/deploy/servicePack/install/createSettingFile/{id}/{testFlag}", method=RequestMethod.POST)
	public ResponseEntity<?> makeDeploymentFile(@PathVariable int id, @PathVariable String testFlag){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> 서비스팩 배포 파일 생성 및 정보 저장 요청"); }
		//Manifest file Create
		service.makeDeploymentFile(id, testFlag);
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> 서비스팩 배포 파일 생성 및 정보 저장 성공"); }
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스 팩 설치
	 * @title               : servicePackInstall
	 * @return            : ResponseEntity<?>
	***************************************************/
	@MessageMapping("/deploy/servicePack/install/servicepackInstall")
	@SendTo("/deploy/servicePack/install/logs")
	public ResponseEntity<?> servicePackInstall(@RequestBody @Valid ServicePackParamDTO dto, Principal principal ){

		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> 서비스팩 설치 요청"); }
		servicePackDeployAsyncService.deployAsync(dto, principal);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> 서비스팩 설치 성공"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 단순 레코드 삭제
	 * @title               : deleteJustOnlyServicePackRecord
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping( value="/deploy/servicePack/delete/data", method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteJustOnlyServicePackRecord(@RequestBody @Valid ServicePackParamDTO dto){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> 서비스팩 단순 레코드 삭제  요청"); }
		try {
			service.deleteServicePackInfoRecord(dto);
		} catch (SQLException e) {
			throw new CommonException("sql.servicePack.exception", "해당 서비스팩 정보를 삭제할 수 없습니다.", HttpStatus.NOT_FOUND);
		}
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> 서비스팩 단순 레코드 삭제  성공"); }
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 플랫폼 삭제 요청
	 * @title               : servicePackDelete
	 * @return            : ResponseEntity<?>
	***************************************************/
	@MessageMapping("/deploy/servicePack/delete/instance")
	@SendTo("/deploy/servicePack/delete/logs")
	public ResponseEntity<?> servicePackDelete(@RequestBody @Valid ServicePackParamDTO dto, Principal principal){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> 서비스팩 플랫폼 삭제 요청"); }
		servicePackDeleteDeployAsyncService.deleteDeployAsync(dto,principal);
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> 서비스팩 플랫폼 삭제 요청 성공"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 검색
	 * @title               : searchManifestList
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/servicePack/list/manifest/search/{searchVal}", method=RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> searchManifestList(@PathVariable String searchVal) {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> 서비스 팩 검색 요청"); }
		List<ManifestVO> content = manifestService.searchManifestList(searchVal);
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		result.put("total", (content == null) ? 0:content.size());
		result.put("records", content);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> 서비스 팩 검색 성공"); }
		
		return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
	}
}
