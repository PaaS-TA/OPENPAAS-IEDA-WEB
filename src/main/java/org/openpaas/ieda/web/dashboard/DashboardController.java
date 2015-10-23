/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Controller
public class DashboardController {
	
	@RequestMapping(value="/dashboard", method=RequestMethod.GET)
	public String main(ModelAndView model) {
		return "/dashboard/dashboard";
	}	
}
