/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.deploy.stemcell;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Controller
public class StemcellController {
	@RequestMapping(value="/deploy/listStemcell", method=RequestMethod.GET)
	public String List() {
		return "/deploy/listStemcell";
	}
}
