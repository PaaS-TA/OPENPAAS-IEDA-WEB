package org.openpaas.ieda.web.config.release;

import java.util.List;
import java.util.Map;

import org.openpaas.ieda.web.config.stemcell.StemcellManagementService;
import org.openpaas.ieda.web.deploy.release.BootStrapSettingData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ReleaseManagementController {
	
	@Autowired
	private IEDAReleaseManagementService service;
	
	@Autowired
	private StemcellManagementService stemcellService;
	
	@RequestMapping(value="/config/releaseManagement", method=RequestMethod.GET)
	public String main() {
		return "/config/releaseManagement";
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
		//service.downloadSettingFile(data);
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping(value="/release/getLocalStemcellList", method = RequestMethod.GET)
	public ResponseEntity doBootSetStemcellList(){
		log.info("### doBootSetStemcellList : ");
		List<String> contents = stemcellService.getLocalStemcellList();
		
		return new ResponseEntity(contents, HttpStatus.OK);
	}
	
	@RequestMapping(value="/release/test3", method = RequestMethod.POST)
	public ResponseEntity test3(@RequestBody Map<String, String> fileInfo){
		log.info("### doBootSetStemcellList : " + fileInfo.get("filePath") );
		//List<String> contents = stemcellService.getLocalStemcellList();
		service.downloadSettingFile(fileInfo.get("filePath"));
		return new ResponseEntity(HttpStatus.OK);
	}
}
