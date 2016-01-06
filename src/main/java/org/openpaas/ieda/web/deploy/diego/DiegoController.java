package org.openpaas.ieda.web.deploy.diego;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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
public class DiegoController {

	@Autowired
	private IEDADiegoService diegoService; 

	@Autowired
	private IEDADiegoAwsService awsService;

	@Autowired
	private IEDADiegoOpenstackService openstackService;
	
	@Autowired
	private DiegoDeployAsyncService diegoDeployAsyncService;
	
	@Autowired
	private DiegoDeleteDeployAsyncService diegoDeleteDeployAsyncService;
	

	@RequestMapping(value = "/deploy/diego", method=RequestMethod.GET)
	public String main() {
		return "/deploy/diego";
	}	

	@RequestMapping(value="/deploy/diegoList", method=RequestMethod.GET)
	public ResponseEntity listDiego() {
		List<DiegoListDto> content = diegoService.getList();

		Map<String, Object> result = new HashMap<>();

		result.put("total", (content == null) ? 0:content.size());
		result.put("records", content);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	//AWS 상세정보
	@RequestMapping(value="/diego/aws/{id}", method=RequestMethod.GET)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	public ResponseEntity saveAwsDiegoInfo(@PathVariable int id){

		IEDADiegoAwsConfig config = awsService.getAwsInfo(id);
		Map<String, Object> result =  new HashMap<>();
		result.put("contents", config);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	//OPENSTACK 상세정보
	@RequestMapping(value="/diego/openstack/{id}", method=RequestMethod.GET)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	public ResponseEntity saveOpenstackDiegoInfo(@PathVariable int id){

		IEDADiegoOpenstackConfig config = openstackService.getOpenstackInfo(id);
		Map<String, Object> result =  new HashMap<>();
		result.put("contents", config);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	//AWS 기본정보 저장
	@RequestMapping(value="/diego/saveAws", method=RequestMethod.PUT)
	public ResponseEntity saveAwsDiegoInfo(@RequestBody @Valid DiegoParam.Default dto){

		IEDADiegoAwsConfig config = awsService.saveDefaultInfo(dto);

		return new ResponseEntity<>(config, HttpStatus.OK);
	}

	//AWS Diego 정보 저장
	@RequestMapping(value="/diego/saveAwsDiego", method=RequestMethod.PUT)
	public ResponseEntity saveAwsDiegoInfo(@RequestBody @Valid DiegoParam.Diego dto){

		IEDADiegoAwsConfig config = awsService.saveDiegoInfo(dto);

		return new ResponseEntity<>(config, HttpStatus.OK);
	}

	//AWS 네트워크 정보 저장
	@RequestMapping(value="/diego/saveAwsNetwork", method=RequestMethod.PUT)
	public ResponseEntity saveAwsNetworkInfo(@RequestBody @Valid DiegoParam.AwsNetwork dto){

		IEDADiegoAwsConfig config = awsService.saveNetworkInfo(dto);

		return new ResponseEntity<>(config, HttpStatus.OK);
	}

	//AWS 리소스 정보 저장
	@RequestMapping(value="/diego/saveAwsResource", method=RequestMethod.PUT)
	public ResponseEntity saveAwsResourceInfo(@RequestBody @Valid DiegoParam.Resource dto){

		IEDADiegoAwsConfig config = awsService.saveResourceInfo(dto);

		return new ResponseEntity<>(config, HttpStatus.OK);
	}


	//OPENSTACK 기본정보 저장
	@RequestMapping(value="/diego/saveOpenstack", method=RequestMethod.PUT)
	public ResponseEntity saveOpenstackDiegoInfo(@RequestBody @Valid DiegoParam.Default dto){

		IEDADiegoOpenstackConfig config = openstackService.saveDefaultInfo(dto);

		return new ResponseEntity<>(config, HttpStatus.OK);
	}

	//OPENSTACK Diego 정보 저장
	@RequestMapping(value="/diego/saveOpenstackDiego", method=RequestMethod.PUT)
	public ResponseEntity saveOpenstackDiegoInfo(@RequestBody @Valid DiegoParam.Diego dto){

		IEDADiegoOpenstackConfig config = openstackService.saveDiegoInfo(dto);

		return new ResponseEntity<>(config, HttpStatus.OK);
	}

	//OPENSTACK 네트워크 정보 저장
	@RequestMapping(value="/diego/saveOpenstackNetwork", method=RequestMethod.PUT)
	public ResponseEntity saveOpenstackNetworkInfo(@RequestBody @Valid DiegoParam.OpenstackNetwork dto){

		IEDADiegoOpenstackConfig config = openstackService.saveNetworkInfo(dto);

		return new ResponseEntity<>(config, HttpStatus.OK);
	}

	//OPENSTACK 리소스 정보 저장
	@RequestMapping(value="/diego/saveOpenstackResource", method=RequestMethod.PUT)
	public ResponseEntity saveOpenstackResourceInfo(@RequestBody @Valid DiegoParam.Resource dto){

		IEDADiegoOpenstackConfig config = openstackService.saveResourceInfo(dto);

		return new ResponseEntity<>(config, HttpStatus.OK);
	}

	@MessageMapping("/diegoInstall")
	@SendTo("/diego/diegoInstall")
	public ResponseEntity doBoshInstall(@RequestBody @Valid DiegoParam.Install dto){
		
		diegoDeployAsyncService.deployAsync(dto);
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping( value="/diego/delete", method=RequestMethod.DELETE)
	public ResponseEntity deleteJustOnlyDiegoRecord(@RequestBody @Valid DiegoParam.Delete dto){
		diegoService.deleteDiegoInfoRecord(dto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@MessageMapping("/diegoDelete")
	@SendTo("/diego/diegoDelete")
	public ResponseEntity deleteBosh(@RequestBody @Valid DiegoParam.Delete dto){
		
		diegoDeleteDeployAsyncService.deleteDeployAsync(dto);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
}
