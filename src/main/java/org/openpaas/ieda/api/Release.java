package org.openpaas.ieda.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Release {

	private String name;	
	@JsonProperty("release_versions")
	private List<ReleaseVersion> releaseVersions;
}
