package org.openpaas.ieda.web.config.bosh;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class BoshManagementController {

	@RequestMapping(value="/config/bosh")
	public String List(){
		return "/config/boshManagement";
	}
	
	@RequestMapping(value="/config/bosh/saveAwsInfo", method=RequestMethod.PUT)
	public ResponseEntity saveAwsInfo(@RequestBody @Valid BoshParam.AWS BoshParam){
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/config/bosh/saveBoshInfo", method=RequestMethod.PUT)
	public ResponseEntity saveBoshInfo(@RequestBody @Valid BoshParam.Bosh BoshParam){
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/config/bosh/saveNetworkInfo", method=RequestMethod.PUT)
	public ResponseEntity saveNetworkInfo(@RequestBody @Valid BoshParam.NetWork BoshParam){
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/config/bosh/saveResourceInfo", method=RequestMethod.PUT)
	public ResponseEntity saveResourceInfo(@RequestBody @Valid BoshParam.Resource BoshParam){
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
