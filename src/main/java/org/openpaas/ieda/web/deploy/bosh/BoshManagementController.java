package org.openpaas.ieda.web.deploy.bosh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.api.ReleaseInfo;
import org.openpaas.ieda.web.common.BaseController;
import org.openpaas.ieda.web.common.CommonUtils;
import org.openpaas.ieda.web.deploy.release.ReleaseService;
import org.openpaas.ieda.web.information.deploy.DeploymentService;
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
public class BoshManagementController extends BaseController {
	
	@Autowired
	private IEDABoshAwsRepository awsRepository;

	@Autowired
	private IEDABoshOpenstackRepository openstackRepository;

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
	private DeploymentService deploymentService;
	
	@Autowired
	private ReleaseService releaseService;
	
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
	
	@RequestMapping(value="/bosh/openstack/{id}", method=RequestMethod.GET)
	public ResponseEntity getOpenstackInfo(@PathVariable int id){
		IEDABoshOpenstackConfig config = openstackService.getBoshOpenstackInfo(id);
		
		Map<String, Object> result =  new HashMap<>();
		result.put("contents", config);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bosh/saveAwsInfo", method=RequestMethod.PUT)
	public ResponseEntity saveAwsInfo(@RequestBody @Valid BoshParam.AWS dto){
		log.info("### saveAwsInfo :: " + dto.toString());
		IEDABoshAwsConfig awsInfo =  awsService.saveBoshAwsInfo(dto);	
		
		return new ResponseEntity<>(awsInfo.getId(), HttpStatus.OK);
	}
	
	@RequestMapping(value="/bosh/saveAwsBoshInfo", method=RequestMethod.PUT)
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

	@RequestMapping(value="/bosh/saveOsBoshInfo", method=RequestMethod.PUT)
	public ResponseEntity saveOpenstackBoshInfo(@RequestBody @Valid BoshParam.OsBosh dto){

		IEDABoshOpenstackConfig config = openstackService.saveBoshInfo(dto);	
		
		return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/bosh/saveOsNetworkInfo", method=RequestMethod.PUT)
	public ResponseEntity saveOsNetworkInfo(@RequestBody @Valid BoshParam.OsNetwork dto){
		
		IEDABoshOpenstackConfig config = openstackService.saveOsNetworkInfo(dto);	
		
		return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bosh/saveOsResourceInfo", method=RequestMethod.PUT)
	public ResponseEntity saveOsResourceInfo(@RequestBody @Valid BoshParam.OsResource dto){
		
		//IEDABoshOpenstackConfig config = openstackService.saveOsResourceInfo(dto);
		
		HttpStatus status = HttpStatus.OK;
		Map<String, Object> result = new HashMap<>();
		result.put("content", openstackService.saveOsResourceInfo(dto));			
			
		if( result.get("content") == null) status = HttpStatus.NO_CONTENT;
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@MessageMapping("/boshInstall")
	@SendTo("/bosh/boshInstall")
	public ResponseEntity doBoshInstall(@RequestBody @Valid BoshParam.Install dto){
		
		boshDeployAsyncService.deployAsync(dto);
		return new ResponseEntity(HttpStatus.OK);
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
		
/*		IEDABoshAwsConfig aws = null;
		IEDABoshOpenstackConfig openstack = null;
		String deploymentName = "";
		
		if( "AWS".equals(dto.getIaas())) { 
			aws = awsRepository.findOne(Integer.parseInt(dto.getId()));
			if ( aws != null ) deploymentName = aws.getDeploymentName();

		} else {
			openstack = openstackRepository.findOne(Integer.parseInt(dto.getId()));
			if ( openstack != null ) deploymentName = openstack.getDeploymentName();
		}
		
		boolean bDeployed = false;
		List<DeploymentInfo> deploymentList = deploymentService.listDeployment();
		if ( deploymentList != null && deploymentList.size() > 0 ) {
			for ( DeploymentInfo deploymentInfo : deploymentList ) {
				if ( deploymentInfo.getName().equals(deploymentName) ) {
					bDeployed = true;
					break;
				}
			}
		}
		
		if ( bDeployed )
			boshDeleteDeployAsyncService.deleteDeployAsync(dto);
		else
			boshService.deleteBoshInfoRecord(dto);*/
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping( value="/bosh/releases", method =RequestMethod.GET)
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
	
}