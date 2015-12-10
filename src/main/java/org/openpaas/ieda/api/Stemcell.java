package org.openpaas.ieda.api;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Stemcell {
	private String name;

	@JsonProperty("operating_system")
	private String operatingSystem;

	private String version;
	private String cid;
	private List<HashMap<String, String>> deployments;
}
