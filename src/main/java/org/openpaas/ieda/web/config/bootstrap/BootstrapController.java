/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.config.bootstrap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.openpaas.ieda.web.config.stemcell.StemcellManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Slf4j
@Controller
public class BootstrapController {
	@Autowired
	private IEDABootstrapAwsService awsService;
	
	@Autowired
	private IEDABootstrapOpenstackService openstackService;
	
	@Autowired
	private IEDABootstrapService bootstrapService;
	
	@Autowired
	private StemcellManagementService stemcellService;
	
	@Autowired
	private ModelMapper modelMapper;

	@RequestMapping(value = "/config/bootstrap", method = RequestMethod.GET)
	public String main() {
		return "/config/bootstrap";
	}
	
	/**
	 * Bootstrap 리스트 조회
	 * @param pageable
	 * @return
	 */
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
	
	@RequestMapping(value="/bootstrap/aws/{id}", method=RequestMethod.DELETE)
	public ResponseEntity deleteAwsInfo(@PathVariable int id){
		awsService.deleteAwsInfo(id);		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/bootstrapSetAws", method = RequestMethod.PUT)
	public ResponseEntity doBootSetAwsSave(@RequestBody  IDEABootStrapInfoDto.Aws data){
		log.info("### AwsData : " + data.toString());
		int seq = awsService.saveAwsInfo(data);
		
		return new ResponseEntity(seq, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/bootstrapSetAwsNetwork", method = RequestMethod.PUT)
	public ResponseEntity doBootSetNetworkSave(@RequestBody  IDEABootStrapInfoDto.Network data){
		log.info("### NetworkData : " + data.toString());
		awsService.saveAwsNetworkInfos(data);
		return new ResponseEntity(HttpStatus.OK);
	}	
	
	@RequestMapping(value="/bootstrap/bootSetAwsResources", method = RequestMethod.PUT)
	public ResponseEntity doBootSetResourcesSave(@RequestBody IDEABootStrapInfoDto.Resources data){
		log.info("### ResourcesSave : " + data.toString());
		awsService.saveAwsReleaseInfos(data);
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/getLocalStemcellList", method = RequestMethod.GET)
	public ResponseEntity doBootSetStemcellList(){
		log.info("### doBootSetStemcellList : ");
		List<String> contents = stemcellService.getLocalStemcellList();
		
		return new ResponseEntity(contents, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/getBootstrapDeployInfo", method = RequestMethod.POST)
	public ResponseEntity getBootStrapSettingInfo(){
		String content = bootstrapService.getBootStrapSettingInfo();
		HttpStatus status = (content != null) ? HttpStatus.OK: HttpStatus.NO_CONTENT;
		log.info("\n" +status + "\n");
		return new ResponseEntity(content, status);
	}
}
