package org.openpaas.ieda.api;

import lombok.Data;

@Data
public class DeploymentInfo {
	private Integer recid;
	private String name;
	private String releaseInfo;
	private String stemcellInfo;
}
