package org.openpaas.ieda.web.deploy.cf.controller;

import java.security.Principal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.web.deploy.cf.dto.CfListDTO;
import org.openpaas.ieda.web.deploy.cf.dto.CfParamDTO;
import org.openpaas.ieda.web.deploy.cf.service.CfDeleteDeployAsyncService;
import org.openpaas.ieda.web.deploy.cf.service.CfDeployAsyncService;
import org.openpaas.ieda.web.deploy.cf.service.CfSaveService;
import org.openpaas.ieda.web.deploy.cf.service.CfService;
import org.openpaas.ieda.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.web.deploy.common.dto.resource.ResourceDTO;
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
public class CfController extends BaseController{
	
	@Autowired private CfService cfService;
	@Autowired private CfSaveService cfSaveService;
	@Autowired private CfDeployAsyncService cfDeployAsyncService;
	@Autowired private CfDeleteDeployAsyncService cfDeleteDeployAsyncService;
	
	final private static Logger LOGGER = LoggerFactory.getLogger(CfController.class);
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 설치 화면 이동
	 * @title               : goCf
	 * @return            : String
	***************************************************/
	@RequestMapping(value = "/deploy/cf", method=RequestMethod.GET)
	public String goCf() {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 설치 그리드 화면 요청"); }
		return "/deploy/cf/cf";
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 설치 팝업 화면 호출
	 * @title               : goCfPopup
	 * @return            : String
	***************************************************/
	@RequestMapping(value = "/deploy/cf/install/cfPopup", method=RequestMethod.GET)
	public String goCfPopup() {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 설치 팝업 화면 요청"); }
		return "/deploy/cf/cfPopup";
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 정보 목록 조회 
	 * @title               : getCfLIst
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/cf/list/{iaas}", method=RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getCfLIst(@PathVariable String iaas) {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 정보 목록 조회 요청"); }
		List<CfListDTO> content = cfService.getCfLIst(iaas.toLowerCase(),"cf");
		Map<String, Object> result = new HashMap<>();
		
		result.put("total", (content == null) ? 0:content.size());
		result.put("records", content);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 정보 목록 조회 성공"); }
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 정보 상세 조회
	 * @title               : getCfInfo
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/cf/install/detail/{id}", method=RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getCfInfo(@PathVariable int id){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 정보 상세 조회 요청"); }
		CfVO vo = cfService.getCfInfo(id);
		Map<String, Object> result =  new HashMap<>();
		result.put("content", vo);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 정보 상세 조회 성공!!"); }
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 정보 저장 
	 * @title               : saveDefaultInfo
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/cf/install/saveDefaultInfo/{test}", method=RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> saveDefaultInfo(@RequestBody @Valid CfParamDTO.Default dto, @PathVariable String test){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 기본 정보 저장 요청"); }
		CfVO vo = cfSaveService.saveDefaultInfo(dto, test);
		Map<String, Object> result  = new HashMap<>();
		result.put("content", vo);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 기본 정보 저장 성공!!"); }
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 네트워크 정보 저장 
	 * @title               : saveNetworkCfInfo
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/deploy/cf/install/saveNetworkInfo", method=RequestMethod.PUT)
	public ResponseEntity<?> saveNetworkCfInfo(@RequestBody @Valid List<NetworkDTO> dto){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 네트워크 정보 저장 요청"); }
		cfSaveService.saveNetworkInfo(dto);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 네트워크 정보 저장 성공!!"); }
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : UAA 정보 저장
	 * @title               : saveUaaCfInfo
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/cf/install/saveUaaInfo", method=RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> saveUaaCfInfo(@RequestBody @Valid CfParamDTO.Uaa dto){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF UAA 정보 저장 요청"); }
		Map<String, Object> result  = new HashMap<>();
		
		CfVO vo = cfSaveService.saveUaaCfInfo(dto);
		result.put("content", vo);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF UAA 정보 저장 성공!!"); }
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CONSUL 정보 저장 
	 * @title               : saveConsulCfInfo
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/cf/install/saveConsulInfo", method=RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> saveConsulCfInfo(@RequestBody @Valid CfParamDTO.Consul dto){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF CONSUL 정보 저장 요청"); }
		Map<String, Object> result  = new HashMap<>();
		CfVO vo = cfSaveService.saveConsulCfInfo(dto);
		result.put("content", vo);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF CONSUL 정보 저장 성공!!"); }
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BlobStore 정보 저장
	 * @title               : saveBlobstoreInfo
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/cf/install/saveBlobstoreInfo", method=RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> saveBlobstoreInfo(@RequestBody @Valid CfParamDTO.Blobstore dto){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF BlobStore 정보 저장 요청"); }
		Map<String, Object> result  = new HashMap<>();
		CfVO vo = cfSaveService.saveBlobstoreInfo(dto);
		result.put("content", vo);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF BlobStore 정보 저장 성공!!"); }
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Hm9000 정보 저장
	 * @title               : saveHm9000Info
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/deploy/cf/install/saveHm9000Info", method=RequestMethod.PUT)
	public ResponseEntity<?> saveHm9000Info(@RequestBody @Valid CfParamDTO.Hm9000 dto){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF Hm9000 정보 저장 요청"); }
		cfSaveService.saveHm9000Info(dto);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF Hm9000 정보 저장 요청"); }
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 리소스 정보 저장
	 * @title               : saveResourceCfInfo
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/cf/install/saveResourceInfo/{test}", method=RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> saveResourceCfInfo(@RequestBody @Valid ResourceDTO dto, @PathVariable String test){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 리소스 정보 저장 요청"); }
		Map<String, Object> map = cfSaveService.saveResourceInfo(dto,test);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 리소스 정보 저장 성공!!"); }
		
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포 파일 생성
	 * @title               : makeDeploymentFile
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/deploy/cf/install/createSettingFile/{test}", method=RequestMethod.POST)
	public ResponseEntity<?> makeDeploymentFile(@RequestBody CfParamDTO.Install dto, @PathVariable String test){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> CF 배포 파일 생성 및 정보 저장 요청"); }
		//Manifest file Create
		CfVO vo = cfService.getCfInfo( Integer.parseInt(dto.getId()) );
		cfService.createSettingFile(vo, test);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> CF 배포 파일 생성 및 정보 저장 성공"); }
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 플랫폼 설치
	 * @title               : installCf
	 * @return            : ResponseEntity<?>
	***************************************************/
	@MessageMapping("/deploy/cf/install/cfInstall")
	@SendTo("/deploy/cf/install/logs")
	public ResponseEntity<?> installCf(@RequestBody @Valid CfParamDTO.Install dto, Principal principal){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 플랫폼 설치 요청"); }
		cfDeployAsyncService.deployAsync(dto, principal, "cf");
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 플랫폼 설치 성공!!"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 단순 레코드 삭제 
	 * @title               : deleteJustOnlyCfRecord
	 * @return            : ResponseEntity<Object>
	***************************************************/
	@RequestMapping( value="/deploy/cf/delete/data", method=RequestMethod.DELETE)
	public ResponseEntity<Object> deleteJustOnlyCfRecord(@RequestBody @Valid  CfParamDTO.Delete dto) { 
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 단순 레코드 삭제 요청"); }
		try {
			cfService.deleteCfInfoRecord(dto);
		} catch (SQLException e) {
			throw new CommonException("sqlException.cfdelete.exception",
					"삭제중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 단순 레코드 삭제 성공!!"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 플랫폼 삭제 요청
	 * @title               : deleteCf
	 * @return            : ResponseEntity<?>
	***************************************************/
	@MessageMapping("/deploy/cf/delete/instance")
	@SendTo("/deploy/cf/delete/logs")
	public ResponseEntity<?> deleteCf(@RequestBody @Valid CfParamDTO.Delete dto, Principal principal){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 플랫폼 삭제 요청 요청"); }
		cfDeleteDeployAsyncService.deleteDeployAsync(dto, "cf", principal);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 플랫폼 삭제 요청 성공!!"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	
}