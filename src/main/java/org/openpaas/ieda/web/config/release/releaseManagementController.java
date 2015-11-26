package org.openpaas.ieda.web.config.release;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class releaseManagementController {
	@RequestMapping(value="/config/releaseManagement", method=RequestMethod.GET)
	public String main() {
		return "/config/releaseManagement";
	}
}
