package org.openpaas.ieda.web.config.stemcell;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

public class StemcellManagementParam {

	@Data
	public static class Download {
		
		@NotBlank
		private String recid;
		
		@NotBlank
		private String key;
		
		@NotBlank
		private String fileName;
		
		@NotBlank
		private String fileSize;
	}
}
