package org.openpaas.ieda.web.config.stemcell;

import lombok.Data;

public class StemcellContentDto {
	
	@Data
	public static class query {
		private String os;
		private String osVersion;
		private String iaas;
	}
}
