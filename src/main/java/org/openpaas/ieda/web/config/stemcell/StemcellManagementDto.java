package org.openpaas.ieda.web.config.stemcell;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

public class StemcellManagementDto {

	@Data
	public static class Upload {
		@NotBlank
		private String fileName;
	}

	@Data
	public static class Delete {
		@NotBlank
		private String stemcellName;

		@NotBlank
		private String version;
	}
	
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
