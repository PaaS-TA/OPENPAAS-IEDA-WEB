package org.openpaas.ieda.web.config.boshrelease;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class BoshReleaseController {
	
	@RequestMapping(value="/config/listBoshRelease", method=RequestMethod.GET)
	public String main() {
		return "/config/listBoshRelease";
	}
}
