package org.openpaas.ieda.api;

import lombok.Data;

@Data
public class StemcellInfo {
	private Integer recid;
	private String name;
	private String operatingSystem;
	private String version;
	private String cid;
	private String deploymentInfo;
}
