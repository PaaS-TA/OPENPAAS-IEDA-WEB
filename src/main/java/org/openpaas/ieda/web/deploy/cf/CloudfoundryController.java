package org.openpaas.ieda.web.deploy.cf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class CloudfoundryController {
	
	@Autowired
	private IEDACloudFoundryService cfService;
	
	@RequestMapping(value = "/deploy/cf", method=RequestMethod.GET)
	public String main() {
		return "/deploy/cf";
	}
	
	@RequestMapping(value="/cfs", method=RequestMethod.GET)
	public ResponseEntity listCfs() {
		List<CloudFoundryListDto> content = cfService.listCfs();
		
		Map<String, Object> result = new HashMap<>();
		
		result.put("total", (content == null) ? 0:content.size());
		result.put("records", content);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}
