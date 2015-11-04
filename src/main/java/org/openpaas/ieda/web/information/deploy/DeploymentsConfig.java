package org.openpaas.ieda.web.information.deploy;

import lombok.Data;

@Data
public class DeploymentsConfig {
	private Integer recid;
	private String deployName;
	private String release;		//releaseName/releaseVersion 형식으로 Mapping
	private String stemcellName;

}
