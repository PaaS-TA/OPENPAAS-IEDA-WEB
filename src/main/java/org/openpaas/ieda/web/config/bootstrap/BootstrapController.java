/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.config.bootstrap;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigDto;
import org.openpaas.ieda.web.config.stemcell.StemcellManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Slf4j
@Controller
public class BootstrapController {
	@Autowired
	private IEDABootstrapService service;
	
	@Autowired
	private StemcellManagementService stemcellService;

	@RequestMapping(value = "/config/bootstrap", method = RequestMethod.GET)
	public String main() {
		return "/config/bootstrap";
	}
	
	/**
	 * 관리자 리스트 조회
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value="/bootstraps", method=RequestMethod.GET)
	public ResponseEntity listBootstrap(Pageable pageable) {
		List<IEDABootstrapAwsConfig> content = service.listBootstrap();
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		result.put("total", content.size());
		result.put("records", content);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/bootSetAwsSave", method = RequestMethod.POST)
	public ResponseEntity doBootSetAwsSave(@RequestBody  IDEABootStrapInfoDto.Aws data){
		log.info("### AwsData : " + data.toString());
		int seq = service.setAwsInfos(data);
		
		return new ResponseEntity(seq, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/bootSetNetworkSave", method = RequestMethod.POST)
	public ResponseEntity doBootSetNetworkSave(@RequestBody  IDEABootStrapInfoDto.Network data){
		log.info("### NetworkData : " + data.toString());
		service.setNetworkInfos(data);
		return new ResponseEntity(HttpStatus.OK);
	}	
	
	@RequestMapping(value="/bootstrap/bootSetResourcesSave", method = RequestMethod.POST)
	public ResponseEntity doBootSetResourcesSave(@RequestBody  IDEABootStrapInfoDto.Resources data){
		log.info("### ResourcesSave : " + data.toString());
		service.setReleaseInfos(data);
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/getLocalStemcellList", method = RequestMethod.GET)
	public ResponseEntity doBootSetStemcellList(){
		log.info("### doBootSetStemcellList : ");
		List<String> contents = stemcellService.getLocalStemcellList();
		
		return new ResponseEntity(contents, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/getBootStrapSettingInfo", method = RequestMethod.POST)
	public ResponseEntity getBootStrapSettingInfo(){
		String content = service.getBootStrapSettingInfo();
		HttpStatus status = (content != null) ? HttpStatus.OK: HttpStatus.NO_CONTENT;
		log.info("\n" +status + "\n");
		return new ResponseEntity(content, status);
	}
}
