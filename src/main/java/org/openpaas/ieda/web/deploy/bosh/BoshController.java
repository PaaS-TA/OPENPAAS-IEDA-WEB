package org.openpaas.ieda.web.deploy.bosh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.api.ReleaseInfo;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BoshController extends BaseController {
	
	@Autowired
	private IEDABoshAwsService awsService;
	
	@Autowired
	private IEDABoshOpenstackService openstackService;
	
	@Autowired
	private IEDABoshService boshService;
	
	@Autowired
	private BoshDeployAsyncService boshDeployAsyncService;
	
	@Autowired
	private BoshDeleteDeployAsyncService boshDeleteDeployAsyncService;
	
	@Autowired
	private ReleaseService releaseService;
	
	@RequestMapping(value="/deploy/bosh")
	public String List(){
		return "/deploy/bosh";
	}
	
	@RequestMapping(value="/deploy/boshList", method=RequestMethod.GET)
	public ResponseEntity listBosh() {
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
	
	@RequestMapping(value="/bosh/openstack/{id}", method=RequestMethod.GET)
	public ResponseEntity getOpenstackInfo(@PathVariable int id){
		IEDABoshOpenstackConfig config = openstackService.getOpenstackInfo(id);
		
		Map<String, Object> result =  new HashMap<>();
		result.put("contents", config);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bosh/saveAwsInfo", method=RequestMethod.PUT)
	public ResponseEntity saveAwsInfo(@RequestBody @Valid BoshParam.AWS dto){
		IEDABoshAwsConfig awsInfo =  awsService.saveBoshAwsInfo(dto);	
		
		return new ResponseEntity<>(awsInfo.getId(), HttpStatus.OK);
	}
	
	@RequestMapping(value="/bosh/saveAwsDefaultInfo", method=RequestMethod.PUT)
	public ResponseEntity saveBoshInfo(@RequestBody BoshParam.AwsBosh dto){
		IEDABoshAwsConfig content = awsService.saveBoshInfo(dto);
			
		return new ResponseEntity<>(content, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bosh/saveAwsNetworkInfo", method=RequestMethod.PUT)
	public ResponseEntity saveNetworkInfo(@RequestBody @Valid BoshParam.AwsNetwork dto){
		HttpStatus status = HttpStatus.OK;
		Map<String, Object> result = new HashMap<>();
		result.put("content", awsService.saveBoshNetworkInfo(dto));			
			
		if( result.get("content") == null) status = HttpStatus.NO_CONTENT;
		
		return new ResponseEntity<>(result, status);
	}
	
	@RequestMapping(value="/bosh/saveAwsResourceInfo", method=RequestMethod.PUT)
	public ResponseEntity saveResourceInfo(@RequestBody @Valid BoshParam.AwsResource dto){
		
		HttpStatus status = HttpStatus.OK;
		Map<String, Object> result = new HashMap<>();
		result.put("content", awsService.saveBoshResourceInfo(dto));			
			
		if( result.get("content") == null) status = HttpStatus.NO_CONTENT;
		
		return new ResponseEntity<>(result, status);
	}

	@RequestMapping(value="/bosh/saveOpenstackInfo", method=RequestMethod.PUT)
	public ResponseEntity saveOpenstackInfo(@RequestBody @Valid BoshParam.Openstack dto){

		IEDABoshOpenstackConfig config = openstackService.saveOpenstackInfo(dto);	
		
		return new ResponseEntity<>(config, HttpStatus.OK);
	}

	@RequestMapping(value="/bosh/saveOpenstackDefaultInfo", method=RequestMethod.PUT)
	public ResponseEntity saveOpenstackBoshInfo(@RequestBody @Valid BoshParam.OpenstackBosh dto){

		IEDABoshOpenstackConfig config = openstackService.saveOpenstackBoshInfo(dto);	
		
		return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/bosh/saveOpenstackNetworkInfo", method=RequestMethod.PUT)
	public ResponseEntity saveOsNetworkInfo(@RequestBody @Valid BoshParam.OpenstackNetwork dto){
		
		IEDABoshOpenstackConfig config = openstackService.saveOpenstackNetworkInfo(dto);	
		
		return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bosh/saveOpenstackResourceInfo", method=RequestMethod.PUT)
	public ResponseEntity saveOsResourceInfo(@RequestBody @Valid BoshParam.OpenstackResource dto){
		
		HttpStatus status = HttpStatus.OK;
		Map<String, Object> result = new HashMap<>();
		result.put("content", openstackService.saveOpenstackResourceInfo(dto));			
			
		if( result.get("content") == null) status = HttpStatus.NO_CONTENT;
		
		return new ResponseEntity<>(result, status);
	}
	
	@MessageMapping("/boshInstall")
	@SendTo("/bosh/boshInstall")
	public ResponseEntity doBoshInstall(@RequestBody @Valid BoshParam.Install dto){
		
		boshDeployAsyncService.deployAsync(dto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping( value="/bosh/delete", method=RequestMethod.DELETE)
	public ResponseEntity deleteJustOnlyBoshRecord(@RequestBody @Valid BoshParam.Delete dto){
		boshService.deleteBoshInfoRecord(dto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@MessageMapping("/boshDelete")
	@SendTo("/bosh/boshDelete")
	public ResponseEntity deleteBosh(@RequestBody @Valid BoshParam.Delete dto){
		
		boshDeleteDeployAsyncService.deleteDeployAsync(dto);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping( value="/bosh/releases", method =RequestMethod.GET)
	public ResponseEntity listRelease(){
		List<ReleaseInfo> contents = releaseService.listRelease();
		List<ReleaseInfo> releases = new ArrayList<>();
		if(contents != null ){
			for(ReleaseInfo releaseInfo: contents){
				if("bosh".equals(releaseInfo.getName())){
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
		
		return new ResponseEntity<>( result, HttpStatus.OK);
	}
	
}