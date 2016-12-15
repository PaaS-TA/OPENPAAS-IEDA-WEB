package org.openpaas.ieda.web.information.vms.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.deploy.bootstrap.service.BootstrapService;
import org.openpaas.ieda.web.deploy.bosh.service.BoshService;
import org.openpaas.ieda.web.information.vms.dto.VmsListDTO;
import org.openpaas.ieda.web.information.vms.service.VmsJobAsyncService;
import org.openpaas.ieda.web.information.vms.service.VmsLogDownloadService;
import org.openpaas.ieda.web.information.vms.service.VmsService;
import org.openpaas.ieda.web.information.vms.service.VmsSnapshotAsyncService;
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
public class VmsController extends BaseController{
	
	@Autowired private VmsService service;
	@Autowired private VmsLogDownloadService logService;
	@Autowired private VmsJobAsyncService jobService;
	@Autowired private VmsSnapshotAsyncService snapshotService;
	@Autowired private BootstrapService bootstrapService;
	@Autowired private BoshService boshService;
	
	final private static Logger LOGGER = LoggerFactory.getLogger(VmsController.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : VM 정보 화면 호출
	 * @title               : goListVm
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/info/vms", method=RequestMethod.GET)
	public String goListVm() {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> VM 조회 화면 요청"); }
		return "/information/listVm";
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : VM 정보 목록 조회
	 * @title               : getVmList
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value="/info/vms/list/{deploymentName}", method=RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getVmList(@PathVariable String deploymentName){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> VM 조회 요청");  }
		
		List<VmsListDTO> contents = service.getVmList(deploymentName);
		HashMap<String, Object> result = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.OK;
		if( contents != null ){
			result.put("records", contents);
			result.put("total", contents.size());
		}else{
			status = HttpStatus.NO_CONTENT;
		}
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> VM 조회 요청 성공");  }
		return new ResponseEntity<Map<String, Object>>( result, status);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 사용 여부
	 * @title               : getSnapshotInfo
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value="/info/vms/list/snapshot", method=RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getSnapshotInfo(){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> 스냅샷 사용 정보 조회 요청"); }
		int count = bootstrapService.getSnapshotInfo();
		count += boshService.getSnapshotInfo();
		Map<String, Object> result =  new HashMap<>();
		result.put("contents", String.valueOf(count));
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> 스냅샷 사용 정보 조회 성공"); } 
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Agent/Job 로그 다운로드
	 * @title               : doDoenwloadLog
	 * @return            : void
	***************************************************/
	@RequestMapping(value="/info/vms/download/{jobName}/{index}/{deploymentName}/{type}", method=RequestMethod.GET)
	public void doDoenwloadLog( @PathVariable String jobName, @PathVariable String index, 
			@PathVariable String deploymentName, @PathVariable String type, HttpServletRequest request, HttpServletResponse response){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> VM 로그 다운로드 요청"); }
		logService.doDownloadLog(jobName, index, deploymentName, type, request, response);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> VM 로그 다운로드 요청 성공"); }
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Job 관리
	 * @title               : changeJobState
	 * @return            : ResponseEntity<?>
	***************************************************/
	@MessageMapping("/info/vms/vmLogs/job")
	@SendTo("/info/vms/vmLogs/socket")
	public ResponseEntity<?> changeJobState(@RequestBody @Valid VmsListDTO dto, Principal principal){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> Change Job State  Request!!"); }
		jobService.doGetJobLogAsync(dto, principal);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> Change Job State  Success!!"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 생성
	 * @title               : takeSnapshot
	 * @return            : ResponseEntity<?>
	***************************************************/
	@MessageMapping("/info/vms/snapshotLog/snapshotTaking")
	@SendTo("/info/vms/snapshotLog/socket")
	public ResponseEntity<?> takeSnapshot(@RequestBody @Valid VmsListDTO dto, Principal principal){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> Take Snapshot Request!!"); }
		snapshotService.doGetSnapshotLogAsync(dto, principal);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> Take Snapshot Success!!"); }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
