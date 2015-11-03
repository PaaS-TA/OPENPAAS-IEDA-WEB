package org.openpaas.ieda.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Stemcell {

	private Integer recid;
	private String name;
	
	@JsonProperty("operating_system")
	private String operatingSystem;
	
	private String version;	
	private String cid;
	private List<String> deployments; 
}
