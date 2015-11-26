package org.openpaas.ieda.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix="ieda")
public class IEDAConfiguration {

	private String stemcellDir;
	
	private String releaseDir;

	private String deploymentDir;
}
