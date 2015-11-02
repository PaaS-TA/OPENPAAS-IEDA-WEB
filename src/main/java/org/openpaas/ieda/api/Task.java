package org.openpaas.ieda.api;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
public class Task {
	@Id @GeneratedValue
	private Integer id;
	
	private String state;
	
	private String description;
	
	private Integer timestamp;
	
	private String result;
	
	private String user;
}
