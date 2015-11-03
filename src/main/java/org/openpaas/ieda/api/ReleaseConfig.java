package org.openpaas.ieda.api;

import lombok.Data;

@Data
public class ReleaseConfig {
	private Integer recid;
	private String name;
	private String version;
	private String commitHash;
	private String uncommittedChanges;//Boolean
	private String currentlyDeployed;//Boolean
}
