package org.openpaas.ieda.web.deploy.bosh;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.web.config.bootstrap.IDEABootStrapInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BoshManagementController {

	@Autowired
	private IEDABoshAwsService awsService;
	
	@Autowired
	private IEDABoshService boshService;
	
	@RequestMapping(value="/deploy/bosh")
	public String List(){
		return "/deploy/boshManagement";
	}
	
	@RequestMapping(value="/deploy/boshList", method=RequestMethod.GET)
	public ResponseEntity listBootstrap() {
		List<BoshInfo> content = boshService.getBoshList();
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		result.put("total", (content == null) ? 0:content.size());
		result.put("records", content);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bosh/aws/{id}", method=RequestMethod.GET)
	public ResponseEntity getAwsInfo(@PathVariable int id){
		IEDABoshAwsConfig config = awsService.getAwsInfo(id);
		
		Map<String, Object> result =  new HashMap<>();
		result.put("contents", config);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/deploy/bosh/saveAwsInfo", method=RequestMethod.PUT)
	public ResponseEntity saveAwsInfo(@RequestBody @Valid BoshParam.AWS dto){
		log.info("### saveAwsInfo :: " + dto.toString());
		IEDABoshAwsConfig awsInfo =  awsService.saveBoshAwsInfo(dto);	
		
		return new ResponseEntity<>(awsInfo.getId(), HttpStatus.OK);
	}
	
	@RequestMapping(value="/deploy/bosh/saveBoshInfo", method=RequestMethod.PUT)
	public ResponseEntity saveBoshInfo(@RequestBody BoshParam.Bosh dto){
//		Map<String, Object> result = new HashMap<>();
//		if( "AWS".equals(dto.getIaas())){
//			result.put("content", awsService.saveBoshInfo(dto));			
//		}
		IEDABoshAwsConfig content = awsService.saveBoshInfo(dto);
			
		return new ResponseEntity<>(content, HttpStatus.OK);
	}
	
	@RequestMapping(value="/deploy/bosh/saveNetworkInfo", method=RequestMethod.PUT)
	public ResponseEntity saveNetworkInfo(@RequestBody @Valid BoshParam.NetWork dto){
		HttpStatus status = HttpStatus.OK;
		Map<String, Object> result = new HashMap<>();
		if( "AWS".equals(dto.getIaas())){
			result.put("content", awsService.saveBoshNetworkInfo(dto));			
		}
			
		if( result.get("content") == null) status = HttpStatus.NO_CONTENT;
		
		return new ResponseEntity<>(result, status);
	}
	
	@RequestMapping(value="/deploy/bosh/saveResourceInfo", method=RequestMethod.PUT)
	public ResponseEntity saveResourceInfo(@RequestBody @Valid BoshParam.Resource dto){
		HttpStatus status = HttpStatus.OK;
		Map<String, Object> result = new HashMap<>();
		if( "AWS".equals(dto.getIaas())){
			result.put("content", awsService.saveBoshResourceInfo(dto));			
		}
			
		if( result.get("content") == null) status = HttpStatus.NO_CONTENT;
		
		return new ResponseEntity<>(result, status);
	}
	
	@RequestMapping(value="/deploy/bosh/getBoshDeployInfo/{id}", method=RequestMethod.PUT)
	public ResponseEntity getBoshDeployInfo(@PathVariable int id){
		HttpStatus status = HttpStatus.OK;
		String content = "";
		content = awsService.getDeploymentInfos(id);
		if(StringUtils.isEmpty(content) ) {
			status = HttpStatus.NO_CONTENT;
		}
		
		return new ResponseEntity<>(content, status);
	}
	
	@MessageMapping("/boshInstall")
	@SendTo("/bosh/boshInstall")
	public ResponseEntity doInstallBootstrap(@RequestBody @Valid IDEABootStrapInfoDto.Install dto){
		log.info("$$$$ SOCKET :  "+ dto.getDeployFileName());
		boshService.installBootstrap(dto.getDeployFileName());
		return new ResponseEntity(HttpStatus.OK);
	}

	@MessageMapping("/boshDelete")
	@SendTo("/bosh/boshDelete")
	public ResponseEntity deleteBootstrap(@RequestBody @Valid BoshParam.Delete dto){
		log.info("$$$$ DELETE Connection :: " + dto.toString());
		if("AWS".equals(dto.getIaas())) awsService.deleteAwsInfo(dto.getId());
		//else openstackService.deleteOpenstackInfo(dto.getId());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}