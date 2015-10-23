/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Controller
public class MainController {
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String main() {
		return "/main/layout";
	}
	
	@RequestMapping(value="/top", method=RequestMethod.GET)
	public String top(ModelAndView model) {
		return "/main/top";
	}
	
	@RequestMapping(value="/menu", method=RequestMethod.GET)
	public String menu(ModelAndView model) {
		return "/main/menu";
	}

}
