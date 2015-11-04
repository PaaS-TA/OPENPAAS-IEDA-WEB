package org.openpaas.ieda.api;

import java.util.List;

import org.openpaas.ieda.web.deploy.release.ReleaseConfig;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Deployment {
	private String name;
	private List<ReleaseConfig> releases;
	private List<Stemcell> stemcells;
	
	@JsonProperty("cloud_config")
	private String cloudConfig;
}
