package org.openpaas.ieda.web.deploy.diego.controller;

import java.security.Principal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;

import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.deploy.cf.dto.CfListDTO;
import org.openpaas.ieda.web.deploy.cf.service.CfService;
import org.openpaas.ieda.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.web.deploy.common.dto.resource.ResourceDTO;
import org.openpaas.ieda.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.web.deploy.diego.dto.DiegoListDTO;
import org.openpaas.ieda.web.deploy.diego.dto.DiegoParamDTO;
import org.openpaas.ieda.web.deploy.diego.service.DiegoDeleteDeployAsyncService;
import org.openpaas.ieda.web.deploy.diego.service.DiegoDeployAsyncService;
import org.openpaas.ieda.web.deploy.diego.service.DiegoSaveService;
import org.openpaas.ieda.web.deploy.diego.service.DiegoService;
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
public class DiegoController extends BaseController{

	@Autowired private DiegoService diegoService; 
	@Autowired private DiegoSaveService diegoSaveService;
	@Autowired private DiegoDeployAsyncService diegoDeployAsyncService;
	@Autowired private DiegoDeleteDeployAsyncService diegoDeleteDeployAsyncService;
	@Autowired private CfService cfService;
	private final static Logger LOGGER = LoggerFactory.getLogger(DiegoController.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 플랫폼 설치 화면으로 이동
	 * @title               : goDiego
	 * @return            : String
	***************************************************/
	@RequestMapping(value = "/deploy/diego", method=RequestMethod.GET)
	public String goDiego() {
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> DIEGO 플랫폼 설치 화면 요청"); }
		return "/deploy/diego/diego";
	}	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 설치 팝업 화면 호출
	 * @title               : goDiegoPopup
	 * @return            : String
	***************************************************/
	@RequestMapping(value = "/deploy/diego/install/diegoPopup", method=RequestMethod.GET)
	public String goDiegoPopup() {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> DIEGO 설치 팝업 화면 요청"); }
		return "/deploy/diego/diegoPopup";
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 목록 정보 조회
	 * @title               : getDiegoInfoList
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/diego/list/{iaasType}", method=RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getDiegoInfoList(@PathVariable String iaasType) {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> DIEGO 목록 정보 조회 요청"); }
		List<DiegoListDTO> content = diegoService.getDiegoInfoList(iaasType);

		Map<String, Object> result = new HashMap<>();

		result.put("total", (content == null) ? 0:content.size());
		result.put("records", content);
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> DIEGO 목록 정보 조회 성공"); }

		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 상세 조회 
	 * @title               : getDiegoDetailInfo
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/diego/install/detail/{id}", method=RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getDiegoDetailInfo(@PathVariable int id){

		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> DIEGO 정보 상세 조회 요청"); }
		DiegoVO vo = diegoService.getDiegoDetailInfo(id);
		Map<String, Object> result =  new HashMap<>();
		result.put("content", vo);
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> DIEGO 정보 상세 조회 성공"); }
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 정보 목록 조회 
	 * @title               : getCfLIst
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/diego/list/cf/{iaas}", method=RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getCfLIst(@PathVariable String iaas) {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 정보 목록 조회 요청"); }
		List<CfListDTO> content = cfService.getCfLIst(iaas.toLowerCase(), "diego");
		Map<String, Object> result = new HashMap<>();
		
		result.put("total", (content == null) ? 0:content.size());
		result.put("records", content);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 정보 목록 조회 성공"); }
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 기본정보 저장
	 * @title               : saveDefaultInfo
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/diego/install/saveDefaultInfo/{test}", method=RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> saveDefaultInfo(@RequestBody @Valid DiegoParamDTO.Default dto, @PathVariable String test){

		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> DIEGO 기본정보 저장 요청"); }
		DiegoVO vo = diegoSaveService.saveDefaultInfo(dto, test);
		Map<String, Object> result  = new HashMap<>();
		result.put("content", vo);
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> DIEGO 기본정보 저장 성공"); }

		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.CREATED);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 정보 저장 
	 * @title               : saveDiegoInfo
	 * @return            : ResponseEntity<DiegoVO>
	***************************************************/
	@RequestMapping(value="/deploy/diego/install/saveDiegoInfo", method=RequestMethod.PUT)
	public ResponseEntity<DiegoVO> saveDiegoInfo(@RequestBody @Valid DiegoParamDTO.Diego dto){

		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> DIEGO 정보 저장 요청"); }
		DiegoVO vo = diegoSaveService.saveDiegoInfo(dto);
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> DIEGO 정보 저장 성공"); }

		return new ResponseEntity<DiegoVO>(vo, HttpStatus.CREATED);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego ETCD 정보 저장  
	 * @title               : saveEtcdInfo
	 * @return            : ResponseEntity<DiegoVO>
	***************************************************/
	@RequestMapping(value="/deploy/diego/install/saveEtcdInfo", method=RequestMethod.PUT)
	public ResponseEntity<DiegoVO> saveEtcdInfo(@RequestBody @Valid DiegoParamDTO.Etcd dto){

		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> ETCD 정보 저장 요청"); }
		DiegoVO vo = diegoSaveService.saveEtcdInfo(dto);
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> ETCD 정보 저장 성공"); }

		return new ResponseEntity<DiegoVO>(vo, HttpStatus.CREATED);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 네트워크 정보 저장  
	 * @title               : saveNetworkInfo
	 * @return            : ResponseEntity<DiegoVO>
	***************************************************/
	@RequestMapping(value="/deploy/diego/install/saveNetworkInfo", method=RequestMethod.PUT)
	public ResponseEntity<DiegoVO> saveNetworkInfo(@RequestBody @Valid List<NetworkDTO> dto){

		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> 네트워크 정보 저장 요청"); }
		DiegoVO vo = diegoSaveService.saveNetworkInfo(dto);
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> 네트워크 정보 저장 성공"); }

		return new ResponseEntity<DiegoVO>(vo, HttpStatus.CREATED);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 리소스 정보 저장 
	 * @title               : saveResourceInfo
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/diego/install/saveResourceInfo/{test}", method=RequestMethod.PUT)
	public ResponseEntity<Map<String, Object> > saveResourceInfo(@RequestBody @Valid ResourceDTO dto, @PathVariable String test){

		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> 리소스 정보 저장 요청"); }
		Map<String, Object> map = diegoSaveService.saveResourceInfo(dto,test);
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> 리소스 정보 저장 성공"); }

		return new ResponseEntity<Map<String, Object> >(map, HttpStatus.CREATED);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 배포 파일 생성
	 * @title               : makeDeploymentFile
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/deploy/diego/install/createSettingFile/{testFlag}", method=RequestMethod.POST)
	public ResponseEntity<?> makeDeploymentFile(@RequestBody DiegoParamDTO.Install dto, @PathVariable String testFlag){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> Diego 배포 파일 생성 및 정보 저장 요청"); }
		//Manifest file Create
		DiegoVO vo = diegoService.getDiegoDetailInfo( Integer.parseInt(dto.getId()) );
		diegoService.createSettingFile(vo, testFlag);
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> Diego 배포 파일 생성 및 정보 저장 성공"); }
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 설치
	 * @title               : diegoInstall
	 * @return            : ResponseEntity<Object>
	***************************************************/
	@MessageMapping("/deploy/diego/install/diegoInstall")
	@SendTo("/deploy/diego/install/logs")
	public ResponseEntity<Object> diegoInstall(@RequestBody @Valid DiegoParamDTO.Install dto, Principal principal ){

		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> DIEGO 설치 요청"); }
		diegoDeployAsyncService.deployAsync(dto, principal, "diego");
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> DIEGO 설치 성공"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 단순 레코드 삭제 
	 * @title               : deleteJustOnlyDiegoRecord
	 * @return            : ResponseEntity<Object>
	***************************************************/
	@RequestMapping( value="/deploy/diego/delete/data", method=RequestMethod.DELETE)
	public ResponseEntity<Object> deleteJustOnlyDiegoRecord(@RequestBody @Valid DiegoParamDTO.Delete dto){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> DIEGO 단순 레코드 삭제  요청"); }
		try {
			diegoService.deleteDiegoInfoRecord(dto);
		} catch (SQLException e) {
			throw new CommonException("sqlException.Diegodelete.exception",
					"삭제중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> DIEGO 단순 레코드 삭제  성공"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 플랫폼 삭제 요청
	 * @title               : deleteDiego
	 * @return            : ResponseEntity<Object>
	***************************************************/
	@MessageMapping("/deploy/diego/delete/instance")
	@SendTo("/deploy/diego/delete/logs")
	public ResponseEntity<Object> deleteDiego(@RequestBody @Valid DiegoParamDTO.Delete dto, Principal principal ){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> DIEGO 플랫폼 삭제 요청"); }
		diegoDeleteDeployAsyncService.deleteDeployAsync(dto, "diego", principal);
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> DIEGO 플랫폼 삭제 요청 성공"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
