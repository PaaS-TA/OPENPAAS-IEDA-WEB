package org.openpaas.ieda.web.deploy.bootstrap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.web.common.BaseController;
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BootstrapController extends BaseController {
	@Autowired
	private IEDABootstrapAwsService awsService;
	
	@Autowired
	private IEDABootstrapOpenstackService openstackService;
	
	@Autowired
	private IEDABootstrapService bootstrapService;
	
	@Autowired
	private BootstrapDeployAsyncService bootstrapDeployAsyncService;
	
	@Autowired
	private BootstrapDeleteDeployAsyncService bootstrapDeleteDeployAsyncService;
	
	@RequestMapping(value = "/deploy/bootstrap", method=RequestMethod.GET)
	public String main() {
		return "/deploy/bootstrap";
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
	}
	
	@RequestMapping(value="/bootstrap/openstack/{id}", method=RequestMethod.GET)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	public ResponseEntity getOpenstackInfo(@PathVariable int id){
		IEDABootstrapOpenstackConfig config = openstackService.getOpenstackInfo(id);
		
		Map<String, Object> result =  new HashMap<>();
		result.put("contents", config);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/aws", method=RequestMethod.PUT)
	public ResponseEntity doBootstrapAwsSave(@RequestBody @Valid BootStrapDto.Aws dto){
		IEDABootstrapAwsConfig config = awsService.saveAwsInfo(dto);
		
		return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/awsDefault", method=RequestMethod.PUT)
	public ResponseEntity doBootstrapAwsDefaultSave(@RequestBody @Valid BootStrapDto.AwsDefault dto){
		IEDABootstrapAwsConfig config = awsService.saveAwsDefaultInfo(dto);
		
		return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/awsNetwork", method=RequestMethod.PUT)
	public ResponseEntity doBootstrapNetworkSave(@RequestBody @Valid  BootStrapDto.AwsNetwork dto){
		awsService.saveAwsNetworkInfos(dto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/awsResource", method=RequestMethod.PUT)
	public ResponseEntity doBootstrapResourcesSave(@RequestBody @Valid BootStrapDto.AwsResource dto){
		IEDABootstrapAwsConfig config = awsService.saveAwsResourcesInfos(dto);
		
		return new ResponseEntity(config, HttpStatus.OK);
	}
	
	@MessageMapping("/bootstrapInstall")
	@SendTo("/bootstrap/bootstrapInstall")
	public ResponseEntity doInstallBootstrap(@RequestBody @Valid BootStrapDto.Install dto){
		bootstrapDeployAsyncService.deployAsync(dto);
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@MessageMapping("/bootstrapDelete")
	@SendTo("/bootstrap/bootstrapDelete")
	public ResponseEntity deleteBootstrap(@RequestBody @Valid BootStrapDto.Delete dto){
		bootstrapDeleteDeployAsyncService.deleteDeployAsync(dto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping( value="/bootstrap/delete", method=RequestMethod.DELETE)
	public ResponseEntity deleteJustOnlyBootstrapRecord(@RequestBody @Valid BootStrapDto.Delete dto){
		bootstrapService.deleteBootstrapInfoRecord(dto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
		
	@RequestMapping(value="/bootstrap/setOpenstackInfo", method=RequestMethod.PUT)
	public ResponseEntity doOpenstackInfoSave(@RequestBody @Valid BootStrapDto.Openstack dto){
		IEDABootstrapOpenstackConfig config = openstackService.saveOpenstackInfoSave(dto);
		return new ResponseEntity(config, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/setOpenstackDefaultInfo", method=RequestMethod.PUT)
	public ResponseEntity doOpenstackBoshInfoSave(@RequestBody @Valid BootStrapDto.OpenstackDefault dto){
		IEDABootstrapOpenstackConfig config = openstackService.saveOpenstackDefaultInfoSave(dto);
		return new ResponseEntity(config, HttpStatus.OK);
	}

	
	@RequestMapping(value="/bootstrap/setOpenstackNetworkInfo", method=RequestMethod.PUT)
	public ResponseEntity doOpenstackNetworkInfoSave(@RequestBody @Valid BootStrapDto.OpenstackNetwork dto){
		IEDABootstrapOpenstackConfig config = openstackService.saveOpenstackNetworkInfoSave(dto);
		return new ResponseEntity(config, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/setOpenstackResourceInfo", method=RequestMethod.PUT)
	public ResponseEntity doOpenstackResourcesInfoSave(@RequestBody @Valid BootStrapDto.OpenstackResource dto){
		IEDABootstrapOpenstackConfig config = openstackService.saveOpenstackResourcesInfoSave(dto);
		return new ResponseEntity(config, HttpStatus.OK);
	}
	
}