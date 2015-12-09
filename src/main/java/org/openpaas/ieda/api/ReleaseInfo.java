package org.openpaas.ieda.api;

import lombok.Data;

@Data
public class ReleaseInfo {
	private Integer recid;
	private String name;
	private String version;
	private String currentDeployed;
	private String jobNames;
}
