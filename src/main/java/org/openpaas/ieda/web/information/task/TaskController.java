/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.information.task;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */
@Slf4j
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
		List<Task> contents = service.listTask();
		int recid = 0;
		for( Task task : contents ){
			task.setRecid(recid++);
			long runTime = task.getTimestamp() * 1000;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			task.setRunTime(format.format(runTime));
		}
		
		HashMap<String, Object> d = new HashMap<String, Object>();
		d.put("total", contents.size());
		d.put("records", contents);
		
		return new ResponseEntity(d, HttpStatus.OK);
	}

/*	@RequestMapping( value="/tasks/{taskId}", method =RequestMethod.GET)
	public ResponseEntity listTaskHistory(@PathVariable String taskId){
//		List<Task> taskList = "";
		List<Task> contents = service.listTask();
		return new ResponseEntity(HttpStatus.OK);
	}*/

}
