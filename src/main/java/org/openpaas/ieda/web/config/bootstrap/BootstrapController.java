/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.config.bootstrap;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Controller
public class BootstrapController {

	@RequestMapping(value = "/config/bootstrap", method = RequestMethod.GET)
	public String main() {
		return "/config/bootstrap";
	}
}
