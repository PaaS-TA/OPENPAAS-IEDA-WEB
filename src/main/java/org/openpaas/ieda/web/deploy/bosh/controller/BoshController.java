package org.openpaas.ieda.web.deploy.bosh.controller;

import java.security.Principal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.deploy.bosh.dao.BoshVO;
import org.openpaas.ieda.web.deploy.bosh.dto.BoshListDTO;
import org.openpaas.ieda.web.deploy.bosh.dto.BoshParamDTO;
import org.openpaas.ieda.web.deploy.bosh.service.BoshDeleteDeployAsyncService;
import org.openpaas.ieda.web.deploy.bosh.service.BoshDeployAsyncService;
import org.openpaas.ieda.web.deploy.bosh.service.BoshSaveService;
import org.openpaas.ieda.web.deploy.bosh.service.BoshService;
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
public class BoshController extends BaseController {
	
	@Autowired private BoshSaveService saveService;
	@Autowired private BoshService boshService;
	@Autowired private BoshDeployAsyncService boshDeployAsyncService;
	@Autowired private BoshDeleteDeployAsyncService boshDeleteDeployAsyncService;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(BoshController.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 설치 화면 이동
	 * @title               : goBosh
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/deploy/bosh")
	public String goBosh(){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> BOSH 설치 화면 요청"); }
		return "/deploy/bosh/bosh";
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 설치 팝업 화면 호출
	 * @title               : goBoshPopup
	 * @return            : String
	***************************************************/
	@RequestMapping(value = "/deploy/bosh/install/boshPopup", method=RequestMethod.GET)
	public String goBoshPopup() {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> DIEGO 설치 팝업 화면 요청"); }
		return "/deploy/bosh/boshPopup";
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 정보 목록 조회
	 * @title               : getBoshList
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/bosh/list/{iaas}", method=RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> getBoshList(@PathVariable String iaas) {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> BOSH 정보 목록 조회 요청"); }
		List<BoshListDTO> content = boshService.getBoshList(iaas);
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		if(content.size() != 0){
			result.put("total",content.size());
			result.put("records", content);
		}
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> BOSH 정보 목록 조회 성공"); }
		
		return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 정보 상세 조회
	 * @title               : getBoshDetailInfo
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/bosh/install/detail/{id}", method=RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getBoshDetailInfo(@PathVariable int id){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> BOSH 정보 상세 요청"); }
		BoshVO vo = boshService.getBoshDetailInfo(id);
		
		Map<String, Object> result =  new HashMap<>();
		result.put("contents", vo);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> BOSH 정보 상세 성공"); }
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH AWS 정보 저장
	 * @title               : saveAwsInfo
	 * @return            : ResponseEntity<Integer>
	***************************************************/
	@RequestMapping(value="/deploy/bosh/install/saveAwsInfo/{test}", method=RequestMethod.PUT)
	public ResponseEntity<Integer> saveAwsInfo(@RequestBody @Valid BoshParamDTO.AWS dto, @PathVariable String test){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> BOSH AWS 정보 저장 요청"); }
		BoshVO awsInfo =  saveService.saveBoshAwsInfo(dto, test);	
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> BOSH AWS 정보 저장 성공"); }
		
		return new ResponseEntity<Integer>(awsInfo.getId(), HttpStatus.CREATED);
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : OPENSTACK 정보 저장
	 * @title               : saveOpenstackInfo
	 * @return            : ResponseEntity<BoshVO>
	***************************************************/
	@RequestMapping(value="/deploy/bosh/install/saveOpenstackInfo/{test}", method=RequestMethod.PUT)
	public ResponseEntity<BoshVO> saveOpenstackInfo(@RequestBody @Valid BoshParamDTO.Openstack dto, @PathVariable String test){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("BOSH 설치 ====================================> OPENSTACK 정보 저장 요청 !!"); }
		BoshVO config = saveService.saveOpenstackInfo(dto, test);	
		if(LOGGER.isInfoEnabled()){ LOGGER.info("BOSH 설치 ====================================> OPENSTACK 정보 저장 성공 !!"); }
		
		return new ResponseEntity<BoshVO>(config, HttpStatus.CREATED);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : VSPHERE 정보 저장
	 * @title               : saveVSphereInfo
	 * @return            : ResponseEntity<BoshVO>
	***************************************************/
	@RequestMapping(value="/deploy/bosh/install/saveVSphereInfo/{test}", method=RequestMethod.PUT)
	public ResponseEntity<BoshVO> saveVSphereInfo(@RequestBody @Valid BoshParamDTO.VSphere dto, @PathVariable String test){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("BOSH 설치 ====================================> VSPHERE 정보 저장 요청 !!"); }
		BoshVO config = saveService.saveVsphereInfo(dto, test);	
		if(LOGGER.isInfoEnabled()){ LOGGER.info("BOSH 설치 ====================================> VSPHERE 정보 저장 성공 !!"); }
		
		return new ResponseEntity<BoshVO>(config, HttpStatus.CREATED);
	}

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 기본 정보 저장
	 * @title               : saveDefaultInfo
	 * @return            : ResponseEntity<BoshVO>
	***************************************************/
	@RequestMapping(value="deploy/bosh/install/saveDefaultInfo", method=RequestMethod.PUT)
	public ResponseEntity<BoshVO> saveDefaultInfo(@RequestBody @Valid BoshParamDTO.DefaultInfo dto){

		if(LOGGER.isInfoEnabled()){ LOGGER.info("BOSH 설치 ====================================> BOSH 기본정보 저장 요청 !!"); }
		BoshVO config = saveService.saveDefaultInfo(dto);	
		if(LOGGER.isInfoEnabled()){ LOGGER.info("BOSH 설치 ====================================> BOSH 기본정보 저장 성공 !!"); }
		
		return new ResponseEntity<BoshVO>(config, HttpStatus.CREATED);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 네트워크 정보 저장
	 * @title               : saveNetworkInfo
	 * @return            : ResponseEntity<BoshVO>
	***************************************************/
	@RequestMapping(value="/deploy/bosh/install/saveNetworkInfo", method=RequestMethod.PUT)
	public ResponseEntity<BoshVO> saveNetworkInfo(@RequestBody @Valid List<NetworkDTO> dto){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("BOSH 설치 ====================================> BOSH 네크트워정보 저장 요청!!"); }
		BoshVO config = saveService.saveNetworkInfo(dto);	
		if(LOGGER.isInfoEnabled()){ LOGGER.info("BOSH 설치 ====================================> BOSH 네크트워정보 저장 성공 !!"); }
		
		return new ResponseEntity<BoshVO>(config, HttpStatus.CREATED);
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 리소스 및 배포파일 정보 저장
	 * @title               : saveResourceInfo
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value="/deploy/bosh/install/saveResourceInfo", method=RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> saveResourceInfo(@RequestBody @Valid ResourceDTO dto){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> BOSH 리소스 정보 저장 요청"); }
		
		HttpStatus status = HttpStatus.CREATED;
		BoshVO content = saveService.saveResourceInfo(dto);
		Map<String, Object> result = new HashMap<>();
		result.put("content",content );			
			
		if( result.get("content") == null) status = HttpStatus.NO_CONTENT;
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> BOSH 리소스 정보 저장 성공"); }
		
		return new ResponseEntity<Map<String, Object>>(result, status);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포 파일 생성
	 * @title               : makeDeploymentFile
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/deploy/bosh/install/createSettingFile/{id}/{test}", method=RequestMethod.POST)
	public ResponseEntity<?> makeDeploymentFile(@RequestBody BoshVO vo, @PathVariable int id, @PathVariable String test){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> BOSH 배포 파일 생성 및 정보 저장 요청"); }
		//Manifest file Create
		vo.setId(id);
		 boshService.createSettingFile(vo, test);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> BOSH 배포 파일 생성 및 정보 저장 성공"); }
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 설치
	 * @title               : doBoshInstall
	 * @return            : ResponseEntity<Object>
	***************************************************/
	@MessageMapping("/deploy/bosh/install/boshInstall")
	@SendTo("/deploy/bosh/install/logs")
	public ResponseEntity<Object> doBoshInstall(@RequestBody @Valid BoshParamDTO.Install dto, Principal principal){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> BOSH 설치 요청"); }
		boshDeployAsyncService.deployAsync(dto, principal);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> BOSH 설치 성공"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 레코드 단순 삭제
	 * @title               : deleteJustOnlyBoshRecord
	 * @return            : ResponseEntity<Object>
	***************************************************/
	@RequestMapping( value="/deploy/bosh/delete/data", method=RequestMethod.DELETE)
	public ResponseEntity<Object> deleteJustOnlyBoshRecord(@RequestBody @Valid BoshParamDTO.Delete dto){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> BOSH 레코드 단순 삭제 요청"); }
		try {
			boshService.deleteBoshInfoRecord(dto);
		} catch (SQLException e) {
			throw new CommonException("illigalArgument.boshdelete.exception",
					"삭제중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> BOSH 레코드 단순 삭제 성공"); }
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 플랫폼 삭제 요청
	 * @title               : deleteBosh
	 * @return            : ResponseEntity<Object>
	***************************************************/
	@MessageMapping("/deploy/bosh/delete/instance")
	@SendTo("/deploy/bosh/delete/logs")
	public ResponseEntity<Object> deleteBosh(@RequestBody @Valid BoshParamDTO.Delete dto, Principal principal){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> BOSH 플랫폼 삭제 요청 요청"); }
		boshDeleteDeployAsyncService.deleteDeployAsync(dto, principal);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> BOSH 플랫폼 삭제 요청 성공"); }
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
}