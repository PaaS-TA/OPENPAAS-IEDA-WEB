package org.openpaas.ieda.api;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskOutput {
	double time;
	String stage;
	List<String> tags;
	String total;
	String task;
	String index;
	String state;
	String progress;
	HashMap<String, String> data;
	HashMap<String, String> error;
}
