package org.openpaas.ieda.web.deploy.cf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.web.common.BaseController;
import org.openpaas.ieda.web.information.release.ReleaseService;
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
public class CfController extends BaseController{
	
	@Autowired
	private IEDACfService cfService;
	
	@Autowired
	private IEDACfAwsService cfAwsService;
	
	@Autowired
	private IEDACfOpenstackService cfOpenstackService;
	
	@Autowired
	private CfDeployAsyncService cfDeployAsyncService;
	
	@Autowired
	private CfDeleteDeployAsyncService cfDeleteDeployAsyncService;
	
	@Autowired
	private ReleaseService releaseService;
	
	@RequestMapping(value = "/deploy/cf", method=RequestMethod.GET)
	public String main() {
		return "/deploy/cf";
	}
	
	
	@RequestMapping(value="/deploy/cfList", method=RequestMethod.GET)
	public ResponseEntity listCfs() {
		List<CfInfo> content = cfService.listCfs();
		Map<String, Object> result = new HashMap<>();
		
		result.put("total", (content == null) ? 0:content.size());
		result.put("records", content);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	// ====== AWS
	@RequestMapping(value="/cf/aws/{id}", method=RequestMethod.GET)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	public ResponseEntity getAwsCfInfo(@PathVariable int id){
		
		IEDACfAwsConfig config = cfAwsService.getAwsInfo(id);
		Map<String, Object> result =  new HashMap<>();
		result.put("contents", config);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	// ====== OPENSTACK
	@RequestMapping(value="/cf/openstack/{id}", method=RequestMethod.GET)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	public ResponseEntity getOpenstackCfInfo(@PathVariable int id){
		
		IEDACfOpenstackConfig config = cfOpenstackService.getOpenstackInfo(id);
		Map<String, Object> result =  new HashMap<>();
		result.put("contents", config);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/cf/saveDefaultInfo", method=RequestMethod.PUT)
	public ResponseEntity saveDefaultInfo(@RequestBody @Valid CfParam.Default dto){
		
		Map<String, Object> result  = new HashMap<>();
		if( "AWS".equals(dto.getIaas().toUpperCase()) ){
			IEDACfAwsConfig config = cfAwsService.saveAwsCfInfo(dto);
			result.put("content", config);
		}
		else if( "OPENSTACK".equals(dto.getIaas().toUpperCase()) ){
			IEDACfOpenstackConfig config = cfOpenstackService.saveOpenstackCfInfo(dto);
			result.put("content", config);
		}
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/cf/saveUaaInfo", method=RequestMethod.PUT)
	public ResponseEntity saveUaaCfInfo(@RequestBody @Valid CfParam.Uaa dto){
		Map<String, Object> result  = new HashMap<>();
		if( "AWS".equals(dto.getIaas().toUpperCase()) ){
			IEDACfAwsConfig config = cfAwsService.saveAwsUaaCfInfo(dto);
			result.put("content", config);
		}
		else if( "OPENSTACK".equals(dto.getIaas().toUpperCase()) ){
			IEDACfOpenstackConfig config = cfOpenstackService.saveOpenstackUaaCfInfo(dto);
			result.put("content", config);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/cf/saveConsulInfo", method=RequestMethod.PUT)
	public ResponseEntity saveConsulCfInfo(@RequestBody @Valid CfParam.Consul dto){
		Map<String, Object> result  = new HashMap<>();
		if( "AWS".equals(dto.getIaas().toUpperCase()) ){
			IEDACfAwsConfig config = cfAwsService.saveAwsConsulCfInfo(dto);
			result.put("content", config);
		}
		else if( "OPENSTACK".equals(dto.getIaas().toUpperCase()) ){
			IEDACfOpenstackConfig config = cfOpenstackService.saveOpenstackConsulCfInfo(dto);
			result.put("content", config);
		}
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/cf/saveAwsNetworkInfo", method=RequestMethod.PUT)
	public ResponseEntity saveAwsNetworkCfInfo(@RequestBody @Valid CfParam.AwsNetwork dto){
		Map<String, Object> result  = new HashMap<>();
		IEDACfAwsConfig config = cfAwsService.saveAwsNetworkInfo(dto);
		result.put("content", config);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/cf/saveOpenstackNetworkInfo", method=RequestMethod.PUT)
	public ResponseEntity saveOpenstackNetworkCfInfo(@RequestBody @Valid CfParam.OpenstackNetwork dto){
		Map<String, Object> result  = new HashMap<>();
		IEDACfOpenstackConfig config = cfOpenstackService.saveOpenstackNetworkInfo(dto);
		result.put("content", config);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/cf/saveResourceInfo", method=RequestMethod.PUT)
	public ResponseEntity saveResourceCfInfo(@RequestBody @Valid CfParam.Resource dto){
		Map<String, Object> result = new HashMap<>();
		HttpStatus status = HttpStatus.OK;
		
		if( "AWS".equals(dto.getIaas().toUpperCase()) ){
			IEDACfAwsConfig config = cfAwsService.saveAwsResourceInfo(dto);
			result.put("content", config);
		}else if( "OPENSTACK".equals(dto.getIaas().toUpperCase()) ){
			IEDACfOpenstackConfig config = cfOpenstackService.saveOpenstackResourceInfo(dto);
			result.put("content", config);
		}
		if( result.get("content") == null) status = HttpStatus.NO_CONTENT;
		return new ResponseEntity<>(result, status);
	}
	

	
//	@RequestMapping(value="/cf/saveOpenstack", method=RequestMethod.PUT)
//	public ResponseEntity saveOpenstackCfInfo(@RequestBody @Valid CfParam.Default dto){
//		
//		IEDACfOpenstackConfig config = cfOpenstackService.saveOpenstackCfInfo(dto);
//		
//		return new ResponseEntity<>(config, HttpStatus.OK);
//	}
	
//	@RequestMapping(value="/cf/saveOpenstackUaa", method=RequestMethod.PUT)
//	public ResponseEntity saveOpenstackUaaCfInfo(@RequestBody @Valid CfParam.Uaa dto){
//		
//		IEDACfOpenstackConfig config = cfOpenstackService.saveOpenstackUaaCfInfo(dto);
//		
//		return new ResponseEntity<>(config, HttpStatus.OK);
//	}
//	
//	@RequestMapping(value="/cf/saveOpenstackConsul", method=RequestMethod.PUT)
//	public ResponseEntity saveOpenstackConsulCfInfo(@RequestBody @Valid CfParam.Consul dto){
//		
//		IEDACfOpenstackConfig config = cfOpenstackService.saveOpenstackConsulCfInfo(dto);
//		
//		return new ResponseEntity<>(config, HttpStatus.OK);
//	}
//	@RequestMapping(value="/cf/saveOpenstackResource", method=RequestMethod.PUT)
//	public ResponseEntity saveOpenstackResourceCfInfo(@RequestBody @Valid CfParam.Resource dto){
//		
//		IEDACfOpenstackConfig config = cfOpenstackService.saveOpenstackResourceInfo(dto);
//		HttpStatus status = HttpStatus.OK;
//		Map<String, Object> result = new HashMap<>();
//		result.put("content", config);
//		if( result.get("content") == null) status = HttpStatus.NO_CONTENT;
//		return new ResponseEntity<>(result, status);
//	}
	
	@MessageMapping("/cfInstall")
	@SendTo("/cf/cfInstall")
	public ResponseEntity doBoshInstall(@RequestBody @Valid CfParam.Install dto){
		
		cfDeployAsyncService.deployAsync(dto);
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping( value="/cf/delete", method=RequestMethod.DELETE)
	public ResponseEntity deleteJustOnlyCfRecord(@RequestBody @Valid CfParam.Delete dto){
		cfService.deleteCfInfoRecord(dto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@MessageMapping("/cfDelete")
	@SendTo("/cf/cfDelete")
	public ResponseEntity deleteBosh(@RequestBody @Valid CfParam.Delete dto){
		
		cfDeleteDeployAsyncService.deleteDeployAsync(dto);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}