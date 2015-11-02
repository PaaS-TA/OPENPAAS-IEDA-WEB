package org.openpaas.ieda.api;

import lombok.Data;

@Data
public class Task {
	
	private Integer recid;
	
	private String id;
	
	private String state;
	
	private String description;
	
	private Long timestamp;
	
	private String result;
	
	private String user;
	
	private String runTime; 
}
