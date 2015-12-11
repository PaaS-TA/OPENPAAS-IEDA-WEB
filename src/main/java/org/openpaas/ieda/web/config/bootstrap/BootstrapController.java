/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.config.bootstrap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.web.config.stemcell.StemcellManagementService;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BootstrapController {
	@Autowired
	private IEDABootstrapAwsService awsService;
	
	@Autowired
	private IEDABootstrapService bootstrapService;
	
	@Autowired
	private StemcellManagementService stemcellService;

	@RequestMapping(value = "/config/bootstrap", method = RequestMethod.GET)
	public String main() {
		return "/config/bootstrap";
	}
	
	@RequestMapping(value="/bootstraps", method=RequestMethod.GET)
	public ResponseEntity listBootstrap() {
		List<BootstrapListDto> content = bootstrapService.listBootstrap();
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		result.put("total", (content == null) ? 0:content.size());
		result.put("records", content);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/aws/{id}", method=RequestMethod.GET)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	public ResponseEntity getAwsInfo(@PathVariable int id){
		IEDABootstrapAwsConfig config = awsService.getAwsInfo(id);
		
		Map<String, Object> result =  new HashMap<>();
		result.put("contents", config);
		return new ResponseEntity<>(result, HttpStatus.OK);
		//return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/bootstrapSetAws", method = RequestMethod.PUT)
	public ResponseEntity doBootSetAwsSave(@RequestBody  IDEABootStrapInfoDto.Aws data){
		int seq = awsService.saveAwsInfo(data);
		
		return new ResponseEntity(seq, HttpStatus.OK);
	}	
	
	@RequestMapping(value="/bootstrap/keyPathFileUpload", method = RequestMethod.POST)
	public ResponseEntity doBootstrapKeyPathFileUpload(
			MultipartHttpServletRequest request
			){
		bootstrapService.uploadKeyPath(request);
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/bootstrapSetAwsNetwork", method = RequestMethod.PUT)
	public ResponseEntity doBootSetNetworkSave(@RequestBody @Valid  IDEABootStrapInfoDto.Network data){
		awsService.saveAwsNetworkInfos(data);
		return new ResponseEntity(HttpStatus.OK);
	}	
	
	@RequestMapping(value="/bootstrap/bootSetAwsResources", method = RequestMethod.PUT)
	public ResponseEntity doBootSetResourcesSave(@RequestBody @Valid IDEABootStrapInfoDto.Resources data){
		String deployFileName = awsService.saveAwsResourcesInfos(data);
		return new ResponseEntity(deployFileName, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/getLocalStemcellList", method = RequestMethod.GET)
	public ResponseEntity doBootSetStemcellList(){
		List<String> contents = stemcellService.getLocalStemcellList();
		
		return new ResponseEntity(contents, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/getBootstrapDeployInfo", method = RequestMethod.POST)
	public ResponseEntity getBootStrapSettingInfo(){
		String content = bootstrapService.getBootStrapSettingInfo();
		HttpStatus status = (content != null) ? HttpStatus.OK: HttpStatus.NO_CONTENT;
		return new ResponseEntity(content, status);
	}

	@MessageMapping("/bootstrapInstall")
	@SendTo("/bootstrap/bootstrapInstall")
	public ResponseEntity doInstallBootstrap(@RequestBody @Valid IDEABootStrapInfoDto.Install dto){
		log.info("$$$$ SOCKET :  "+ dto.getDeployFileName());
		bootstrapService.installBootstrap(dto.getDeployFileName());
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@MessageMapping("/bootstrapDelete")
	@SendTo("/bootstrap/bootstrapDelete")
	public ResponseEntity deleteBootstrap(@RequestBody @Valid IDEABootStrapInfoDto.Delete dto){
		if("AWS".equals(dto.getIaas())) awsService.deleteAwsInfo(dto.getId());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/getKeyPathFileList" , method=RequestMethod.GET)
	public ResponseEntity getKeyPathFileList (){
		List<String> keyPathFileList = bootstrapService.getKeyPathFileList();
		
		return new ResponseEntity<>(keyPathFileList, HttpStatus.OK);
	}
	
}