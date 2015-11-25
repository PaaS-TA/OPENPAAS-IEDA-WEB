/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.deploy.release;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.common.IEDAConfiguration;
import org.openpaas.ieda.web.config.stemcell.StemcellManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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
public class ReleaseController {
	

	@Autowired
	private IEDAConfiguration iedaConfiguration;

	@Autowired
	private IEDAReleaseService service;
	
	@Autowired
	private UploadReleasAsyncByScriptService uploadService;
	
	@Autowired
	private DeleteReleaseAsyncByScriptService deleteService;
	
	@Autowired
	private StemcellManagementService stemcellService;
	
	@RequestMapping(value="/deploy/listRelease", method=RequestMethod.GET)
	public String List() {
		return "/deploy/listRelease";
	}
	
	@RequestMapping( value="/releases", method =RequestMethod.GET)
	public ResponseEntity listRelease(){
		List<ReleaseConfig> contents = service.listRelease();
		
		int recid = 0;
		if(contents != null){
			for( ReleaseConfig config : contents ){
				config.setRecid(recid++);
			}
		}
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("total", contents.size());
		result.put("records", contents);
		return new ResponseEntity( result, HttpStatus.OK);
	}
	
	@RequestMapping( value="/localReleases", method =RequestMethod.GET)
	public ResponseEntity listLocalRelease(){
		List<ReleaseConfig> contents = service.listLocalRelease();
		
		int recid = 0;
		if(contents != null){
			for( ReleaseConfig config : contents ){
				config.setRecid(recid++);
				log.info("#### ::: "+ config.toString());
			}
		}
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("total", contents.size());
		result.put("records", contents);
		return new ResponseEntity( result, HttpStatus.OK);
	}
	
	/**
	 * 릴리즈 업로드
	 * @param dto
	 * @return
	 */
	@MessageMapping("/releaseUploading")
	@SendTo("/socket/uploadRelease")
	public ResponseEntity doUploadRelease(@RequestBody @Valid ReleaseContentDto.Upload dto) {
		log.info("### Release Upload Controller ###");
		
		uploadService.uploadReleaseAsync(iedaConfiguration.getReleaseDir()
				, dto.getFileName());
		
		
		return new ResponseEntity(HttpStatus.OK);
	}
	
	/**
	 * 릴리즈 삭제
	 * @param dto
	 * @return
	 */
	@MessageMapping("/releaseDelete")
	@SendTo("/socket/deleteRelease")
	public ResponseEntity doDeleteRelease(@RequestBody @Valid ReleaseContentDto.Delete dto) {
		log.info("### Release Delete Controller ###");
		deleteService.deleteReleaseAsync(iedaConfiguration.getReleaseDir()
				, dto.getFileName()
				, dto.getVersion());
		return new ResponseEntity(HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/test")
	public String popupTest1(){
		return "/test";
	}
	
	@RequestMapping(value="/test2")
	public String popupTest2(){
		return "/test2";
	}
	
	@RequestMapping(value="/test3")
	public String popupTest3(){
		return "/test3";
	}
	
	@RequestMapping(value="/release/bootSetAwsSave", method = RequestMethod.POST)
	public ResponseEntity doBootSetAwsSave(@RequestBody  BootStrapSettingData.Aws data){
		log.info("### AwsData : " + data.toString());
		
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping(value="/release/bootSetNetworkSave", method = RequestMethod.POST)
	public ResponseEntity doBootSetNetworkSave(@RequestBody  BootStrapSettingData.Network data){
		log.info("### NetworkData : " + data.toString());
		
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping(value="/release/bootSetResourcesSave", method = RequestMethod.POST)
	public ResponseEntity doBootSetResourcesSave(@RequestBody  BootStrapSettingData.Resources data){
		log.info("### ResourcesSave : " + data.toString());
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping(value="/release/getLocalStemcellList", method = RequestMethod.GET)
	public ResponseEntity doBootSetStemcellList(){
		log.info("### doBootSetStemcellList : ");
		List<Map<String, String>> contents = stemcellService.getLocalStemcellFileList();
		log.info("@@@@@@ : " + contents.size());
		Map<String, Object> result = new HashMap<>();
		result.put("total", contents.size());
		result.put("records", contents);
		
		return new ResponseEntity(result, HttpStatus.OK);
	}
}

