package org.openpaas.ieda.web.config.release;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ReleaseManagementController {
	
	@Autowired
	private IEDAReleaseManagementService service;
	
	@RequestMapping(value="/config/releaseManagement", method=RequestMethod.GET)
	public String main() {
		return "/config/releaseManagement";
	}
	
}
