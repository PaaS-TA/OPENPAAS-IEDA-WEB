package org.openpaas.ieda.web.deploy.cf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.api.ReleaseInfo;
import org.openpaas.ieda.web.common.BaseController;
import org.openpaas.ieda.web.common.CommonUtils;
import org.openpaas.ieda.web.deploy.release.ReleaseService;
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
		List<CfListDto> content = cfService.listCfs();
		
		Map<String, Object> result = new HashMap<>();
		
		result.put("total", (content == null) ? 0:content.size());
		result.put("records", content);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	// ====== AWS
	@RequestMapping(value="/cf/aws/{id}", method=RequestMethod.GET)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	public ResponseEntity saveAwsCfInfo(@PathVariable int id){
		
		IEDACfAwsConfig config = cfAwsService.getAwsCfInfo(id);
		Map<String, Object> result =  new HashMap<>();
		result.put("contents", config);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value="/cf/saveAws", method=RequestMethod.PUT)
	public ResponseEntity saveAwsCfInfo(@RequestBody @Valid CfParam.Aws dto){
		
		IEDACfAwsConfig config = cfAwsService.saveAwsCfInfo(dto);
		
		return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	@RequestMapping(value="/cf/saveAwsUaa", method=RequestMethod.PUT)
	public ResponseEntity saveAwsUaaCfInfo(@RequestBody @Valid CfParam.AwsUaa dto){
		
		IEDACfAwsConfig config = cfAwsService.saveAwsUaaCfInfo(dto);
		
		return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	@RequestMapping(value="/cf/saveAwsConsul", method=RequestMethod.PUT)
	public ResponseEntity saveAwsConsulCfInfo(@RequestBody @Valid CfParam.AwsConsul dto){
		
		IEDACfAwsConfig config = cfAwsService.saveAwsConsulCfInfo(dto);
		
		return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	@RequestMapping(value="/cf/saveAwsNetwork", method=RequestMethod.PUT)
	public ResponseEntity saveAwsNetworkCfInfo(@RequestBody @Valid CfParam.AwsNetwork dto){
		
		IEDACfAwsConfig config = cfAwsService.saveAwsNetworkInfo(dto);
		
		return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	@RequestMapping(value="/cf/saveAwsResource", method=RequestMethod.PUT)
	public ResponseEntity saveAwsResourceCfInfo(@RequestBody @Valid CfParam.AwsResource dto){
		
		IEDACfAwsConfig config = cfAwsService.saveAwsResourceInfo(dto);
		HttpStatus status = HttpStatus.OK;
		Map<String, Object> result = new HashMap<>();
		result.put("content", config);
		if( result.get("content") == null) status = HttpStatus.NO_CONTENT;
		return new ResponseEntity<>(result, status);
	}
	
	
	// ====== OPENSTACK
	@RequestMapping(value="/cf/openstack/{id}", method=RequestMethod.GET)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	public ResponseEntity getOpenstackCfInfo(@PathVariable int id){
		
		IEDACfOpenstackConfig config = cfOpenstackService.getOpenstackCfInfo(id);
		Map<String, Object> result =  new HashMap<>();
		result.put("contents", config);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/cf/saveOpenstack", method=RequestMethod.PUT)
	public ResponseEntity saveOpenstackCfInfo(@RequestBody @Valid CfParam.Openstack dto){
		
		IEDACfOpenstackConfig config = cfOpenstackService.saveOpenstackCfInfo(dto);
		
		return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	@RequestMapping(value="/cf/saveOpenstackUaa", method=RequestMethod.PUT)
	public ResponseEntity saveOpenstackUaaCfInfo(@RequestBody @Valid CfParam.OpenstackUaa dto){
		
		IEDACfOpenstackConfig config = cfOpenstackService.saveOpenstackUaaCfInfo(dto);
		
		return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	@RequestMapping(value="/cf/saveOpenstackConsul", method=RequestMethod.PUT)
	public ResponseEntity saveOpenstackConsulCfInfo(@RequestBody @Valid CfParam.OpenstackConsul dto){
		
		IEDACfOpenstackConfig config = cfOpenstackService.saveOpenstackConsulCfInfo(dto);
		
		return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	@RequestMapping(value="/cf/saveOpenstackNetwork", method=RequestMethod.PUT)
	public ResponseEntity saveOpenstackNetworkCfInfo(@RequestBody @Valid CfParam.OpenstackNetwork dto){
		
		IEDACfOpenstackConfig config = cfOpenstackService.saveOpenstackNetworkInfo(dto);
		
		return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	@RequestMapping(value="/cf/saveOpenstackResource", method=RequestMethod.PUT)
	public ResponseEntity saveOpenstackResourceCfInfo(@RequestBody @Valid CfParam.OpenstackResource dto){
		
		IEDACfOpenstackConfig config = cfOpenstackService.saveOpenstackResourceInfo(dto);
		HttpStatus status = HttpStatus.OK;
		Map<String, Object> result = new HashMap<>();
		result.put("content", config);
		if( result.get("content") == null) status = HttpStatus.NO_CONTENT;
		return new ResponseEntity<>(result, status);
	}
	
	@RequestMapping( value="/cf/releases", method =RequestMethod.GET)
	public ResponseEntity listRelease(){
		List<ReleaseInfo> contents = releaseService.listRelease();
		List<ReleaseInfo> releases = new ArrayList<>();
		if(contents != null ){
			for(ReleaseInfo releaseInfo: contents){
				if("bosh".equals(releaseInfo.getName())){
					log.info("@@@@@ " + releaseInfo.getName()+ "/" + releaseInfo.getVersion());
					releases.add(releaseInfo);
				}
			}
		}
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		if ( contents != null ) {
			result.put("total", releases.size());
			result.put("records", releases);
		} else
			result.put("total", 0);
		
		return new ResponseEntity( result, HttpStatus.OK);
	}
	
	@MessageMapping("/cfInstall")
	@SendTo("/cd/cfInstall")
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