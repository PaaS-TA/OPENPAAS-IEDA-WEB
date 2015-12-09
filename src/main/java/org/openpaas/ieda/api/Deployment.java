package org.openpaas.ieda.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Deployment {
	private String name;
	private List<Release> releases;
	private List<Stemcell> stemcells;
	
	@JsonProperty("cloud_config")
	private String cloudConfig;
}
