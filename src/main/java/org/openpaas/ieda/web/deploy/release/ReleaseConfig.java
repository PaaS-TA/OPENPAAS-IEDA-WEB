package org.openpaas.ieda.web.deploy.release;

import lombok.Data;

@Data
public class ReleaseConfig {
	private Integer recid;
	private String name;
	private String version;
	private String commitHash;
	private String uncommittedChanges;//Boolean
	private String currentlyDeployed;//Boolean
	private String releaseType;
	private String fileName;
}
