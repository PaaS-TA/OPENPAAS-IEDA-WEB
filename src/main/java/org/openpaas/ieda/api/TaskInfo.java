package org.openpaas.ieda.api;

import lombok.Data;

@Data
public class TaskInfo {
	String id;
	String state;
	String description;
	String timestamp;
	String result;
	String user;
}
