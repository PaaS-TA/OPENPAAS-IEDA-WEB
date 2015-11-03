package org.openpaas.ieda.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReleaseVersion {

	private String version;
	
	@JsonProperty("commit_hash")
	private String commitHash;
	
	@JsonProperty("uncommitted_changes")
	private Boolean uncommittedChanges;
	
	@JsonProperty("currently_deployed")
	private Boolean currentlyDeployed;
	
	@JsonProperty("job_names")
	private List<String> jobNames;
	
}
