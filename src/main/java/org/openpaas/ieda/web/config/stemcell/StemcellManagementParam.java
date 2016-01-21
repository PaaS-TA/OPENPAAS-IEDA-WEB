package org.openpaas.ieda.web.config.stemcell;

import javax.validation.constraints.NotNull;

import lombok.Data;

public class StemcellManagementParam {

	@Data
	public static class Download {
		
		@NotNull
		private String recid;
		@NotNull
		private String key;
		@NotNull
		private String fileName;
		@NotNull
		private String fileSize;
	}
}
