package org.openpaas.ieda.web.deploy.bootstrap.controller;

import java.security.Principal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.deploy.bootstrap.dao.BootstrapVO;
import org.openpaas.ieda.web.deploy.bootstrap.dto.BootStrapParamDTO;
import org.openpaas.ieda.web.deploy.bootstrap.dto.BootstrapListDTO;
import org.openpaas.ieda.web.deploy.bootstrap.service.BootstrapDeleteDeployAsyncService;
import org.openpaas.ieda.web.deploy.bootstrap.service.BootstrapDeployAsyncService;
import org.openpaas.ieda.web.deploy.bootstrap.service.BootstrapSaveService;
import org.openpaas.ieda.web.deploy.bootstrap.service.BootstrapService;
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
public class BootstrapController extends BaseController {

	@Autowired private BootstrapSaveService saveService;
	@Autowired private BootstrapService bootstrapService;
	@Autowired private BootstrapDeployAsyncService deployAsyncService;
	@Autowired private BootstrapDeleteDeployAsyncService deleteDeployService;

	private final static Logger LOGGER = LoggerFactory.getLogger(BootstrapController.class);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bootstrap 설치 화면 이동
	 * @title               : goBootstrap
	 * @return            : String
	***************************************************/
	@RequestMapping(value = "/deploy/bootstrap", method = RequestMethod.GET)
	public String goBootstrap() {
		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP 설치 화면 이동"); }
		return "/deploy/bootstrap/bootstrap";
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bootstrap 설치 화면 이동
	 * @title               : goBootstrapPopup
	 * @return            : String
	***************************************************/
	@RequestMapping(value = "/deploy/bootstrap/install/bootstrapPopup", method = RequestMethod.GET)
	public String goBootstrapPopup() {
		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP 설치 팝업 화면 이동"); }
		return "/deploy/bootstrap/bootstrapPopup";
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bootstrap 정보 목록 조회
	 * @title               : getBootstrapList
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value = "/deploy/bootstrap/list", method = RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> getBootstrapList() {

		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP 정보 목록 조회 요청"); }
		List<BootstrapListDTO> content = bootstrapService.bootstrapList();

		HashMap<String, Object> result = new HashMap<String, Object>();
		if (content.size() != 0) {
			result.put("total", content.size());
			result.put("records", content);
		}
		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP 정보 목록 조회 성공!!"); }

		return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bootstrap 상세 조회
	 * @title               : getBootstrapInfo
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value = "/deploy/bootstrap/install/detail/{id}", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getBootstrapInfo(@PathVariable int id) {

		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP 정보 상세 조회 요청"); }
		BootstrapVO vo = bootstrapService.getBootstrapInfo(id);
		Map<String, Object> result = new HashMap<>();
		result.put("contents", vo);

		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP 정보 상세 조회 성공"); }
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : AWS 정보 저장
	 * @title               : saveAwsInfo
	 * @return            : ResponseEntity<BootstrapVO>
	***************************************************/
	@RequestMapping(value = "/deploy/bootstrap/install/saveAwsInfo/{testFlag}", method = RequestMethod.PUT)
	public ResponseEntity<BootstrapVO> saveAwsInfo(@PathVariable String testFlag,
			@RequestBody @Valid BootStrapParamDTO.Aws dto) {

		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP AWS정보 저장 요청"); }
		BootstrapVO config = saveService.saveAwsInfo(dto, testFlag);
		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP AWS정보 저장 성공"); }

		return new ResponseEntity<BootstrapVO>(config, HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Openstack 정보 저장
	 * @title               : saveOpenstackInfo
	 * @return            : ResponseEntity<BootstrapVO>
	***************************************************/
	@RequestMapping(value = "/deploy/bootstrap/install/setOpenstackInfo/{testFlag}", method = RequestMethod.PUT)
	public ResponseEntity<BootstrapVO> saveOpenstackInfo(@RequestBody @Valid BootStrapParamDTO.Openstack dto,
			@PathVariable String testFlag) {

		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP OPENSTACK 정보 저장 요청"); }
		BootstrapVO config = saveService.saveOpenstackInfo(dto, testFlag);
		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP OPENSTACK 정보 저장 성공"); }

		return new ResponseEntity<BootstrapVO>(config, HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : vSphere 정보 저장
	 * @title               : saveVSphereInfo
	 * @return            : ResponseEntity<BootstrapVO>
	***************************************************/
	@RequestMapping(value = "/deploy/bootstrap/install/saveVSphereInfo/{testFlag}", method = RequestMethod.PUT)
	public ResponseEntity<BootstrapVO> saveVSphereInfo(@RequestBody @Valid BootStrapParamDTO.VSphere dto,
			@PathVariable String testFlag) {

		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP vSphere 정보 저장 요청"); }
		BootstrapVO config = saveService.saveVSphereInfo(dto, testFlag);
		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP vSphere 정보 저장 성공"); }

		return new ResponseEntity<BootstrapVO>(config, HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 정보 저장
	 * @title               : saveDefaultInfo
	 * @return            : ResponseEntity<BootstrapVO>
	***************************************************/
	@RequestMapping(value = "/deploy/bootstrap/install/setDefaultInfo", method = RequestMethod.PUT)
	public ResponseEntity<BootstrapVO> saveDefaultInfo(@RequestBody BootStrapParamDTO.Default dto) {

		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP 기본 정보 저장 요청"); }
		BootstrapVO config = saveService.saveDefaultInfo(dto);
		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP 기본 정보 저장 성공"); }

		return new ResponseEntity<>(config, HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 네트워크 정보 저장
	 * @title               : saveNetworkInfo
	 * @return            : ResponseEntity<BootstrapVO>
	***************************************************/
	@RequestMapping(value = "/deploy/bootstrap/install/setNetworkInfo", method = RequestMethod.PUT)
	public ResponseEntity<BootstrapVO> saveNetworkInfo(@RequestBody @Valid BootStrapParamDTO.Network dto) {

		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP 네트워크 정보 저장 요청"); }
		BootstrapVO config = saveService.saveNetworkInfo(dto);
		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP 네트워크 정보 저장 성공"); }

		return new ResponseEntity<BootstrapVO>(config, HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 리소스 정보 저장
	 * @title               : saveResourcesInfo
	 * @return            : ResponseEntity<BootstrapVO>
	***************************************************/
	@RequestMapping(value = "/deploy/bootstrap/install/setResourceInfo", method = RequestMethod.PUT)
	public ResponseEntity<BootstrapVO> saveResourcesInfo(@RequestBody @Valid BootStrapParamDTO.Resource dto) {

		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP 리소스 정보 저장 요청"); }
		BootstrapVO config = saveService.saveResourcesInfo(dto);
		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP 리소스 정보 저장 성공"); }

		return new ResponseEntity<BootstrapVO>(config, HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포 파일 생성
	 * @title               : makeDeploymentFile
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value = "/deploy/bootstrap/install/createSettingFile/{id}/{testFlag}", method = RequestMethod.POST)
	public ResponseEntity<?> makeDeploymentFile(@RequestBody BootstrapVO bootstrapVo, @PathVariable int id,
			@PathVariable String testFlag) {
		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP 배포 파일 생성 및 정보 저장 요청"); }
		// Manifest file Create
		bootstrapVo.setId(id);
		bootstrapService.createSettingFile(bootstrapVo, testFlag);
		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP 배포 파일 생성 및 정보 저장 성공"); }
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치
	 * @title               : installBootstrap
	 * @return            : ResponseEntity<BootstrapVO>
	***************************************************/
	@MessageMapping("/deploy/bootstrap/install/bootstrapInstall")
	@SendTo("/deploy/bootstrap/install/logs")
	public ResponseEntity<BootstrapVO> installBootstrap(@RequestBody @Valid BootStrapParamDTO.Install dto, Principal principal) {
		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP 설치 요청"); }
		deployAsyncService.deployAsync(dto, principal);
		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP 설치 성공"); }

		return new ResponseEntity<BootstrapVO>(HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 플랫폼 삭제 요청
	 * @title               : deleteBootstrap
	 * @return            : ResponseEntity<?>
	***************************************************/
	@MessageMapping("/deploy/bootstrap/delete/instance")
	@SendTo("/deploy/bootstrap/delete/logs")
	public ResponseEntity<?> deleteBootstrap(@RequestBody @Valid BootStrapParamDTO.Delete dto, Principal principal) {
		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP VM 삭제 요청"); }
		deleteDeployService.deleteDeployAsync(dto, principal);
		if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> BOOTSTRAP VM 삭제 성공"); }
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 단순 Bootstrap 레코드 삭제
	 * @title               : deleteJustOnlyBootstrapRecord
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value = "/deploy/bootstrap/delete/data", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteJustOnlyBootstrapRecord(@RequestBody @Valid BootStrapParamDTO.Delete dto) {

		if (LOGGER.isInfoEnabled()) { LOGGER.info("===================== BOOTSTRAP 단순 레코드 삭제 요청 요청 !!!"); }
		try {
			bootstrapService.deleteBootstrapInfoRecord(dto);
		} catch (SQLException e) {
			throw new CommonException("sqlException.bootstrap.delete.exception",
					"BOOTSTRAP 삭제 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (LOGGER.isInfoEnabled()) { LOGGER.info("===================== BOOTSTRAP 단순 레코드 삭제 요청 성공 !!!"); }

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포 로그 정보 조회
	 * @title               : getDeployLogMsg
	 * @return            : ResponseEntity<String>
	***************************************************/
	@RequestMapping(value = "/deploy/bootstrap/list/{id}", method = RequestMethod.GET)
	public ResponseEntity<String> getDeployLogMsg(@PathVariable int id) {

		if (LOGGER.isInfoEnabled()) { LOGGER.debug("====================================> 배포 이력 정보 조회 요청"); }
		BootstrapVO vo = bootstrapService.getBootstrapInfo(id);
		if (LOGGER.isInfoEnabled()) { LOGGER.debug("====================================> 배포 이력 정보 조회 성공"); }

		return new ResponseEntity<String>(vo.getDeployLog(), HttpStatus.OK);
	}

}