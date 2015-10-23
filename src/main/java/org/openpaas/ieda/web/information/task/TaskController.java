/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.information.task;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Controller
public class TaskController {

	@RequestMapping(value="/information/listTaskHistory", method=RequestMethod.GET)
	public String List() {
		return "/information/listTaskHistory";
	}
}
