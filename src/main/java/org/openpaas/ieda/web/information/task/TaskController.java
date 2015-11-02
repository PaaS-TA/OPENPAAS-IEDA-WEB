/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.information.task;

import java.util.List;

import org.openpaas.ieda.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Controller
public class TaskController {
	
	@Autowired
	private TaskConfigService service;

	@RequestMapping(value="/information/listTaskHistory", method=RequestMethod.GET)
	public String List() {
		return "/information/listTaskHistory";
	}
	
	@RequestMapping( value="/tasks", method =RequestMethod.GET)
	public ResponseEntity listTaskHistory(){
//		List<Task> taskList = "";
		List<Task> contents = service.listTask();
		return new ResponseEntity(HttpStatus.OK);
	}

/*	@RequestMapping( value="/tasks/{taskId}", method =RequestMethod.GET)
	public ResponseEntity listTaskHistory(@PathVariable String taskId){
//		List<Task> taskList = "";
		List<Task> contents = service.listTask();
		return new ResponseEntity(HttpStatus.OK);
	}*/

}
