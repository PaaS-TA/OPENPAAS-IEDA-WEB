/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.deploy.release;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Controller
public class ReleaseController {

	@RequestMapping(value="/deploy/listRelease", method=RequestMethod.GET)
	public String List() {
		return "/deploy/listRelease";
	}
	
}
