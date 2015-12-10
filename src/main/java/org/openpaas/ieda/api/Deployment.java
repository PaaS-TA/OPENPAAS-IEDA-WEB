package org.openpaas.ieda.api;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Deployment {
	private String name;
	
	private List<HashMap<String, String>> releases;
	
	private List<HashMap<String, String>> stemcells;
	
	@JsonProperty("cloud_config")
	private String cloudConfig;
}
